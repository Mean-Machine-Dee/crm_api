package com.crm.api.services.impl;

import com.crm.api.api.models.AppCounter;
import com.crm.api.api.models.Deposit;
import com.crm.api.api.models.Setting;
import com.crm.api.api.models.Withdrawals;
import com.crm.api.api.repository.AppCounterRepository;
import com.crm.api.api.repository.DepositRepository;
import com.crm.api.api.repository.SettingRepository;
import com.crm.api.api.repository.WithdrawRepository;
import com.crm.api.crm.models.Submission;
import com.crm.api.crm.repository.SubmissionRepository;
import com.crm.api.lona.models.Lona;
import com.crm.api.lona.respositories.LonaRepository;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.FinanceService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {

    AppUtils appUtils = new AppUtils();

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCounterRepository appCounterRepository;

    @Autowired
    private LonaRepository lonaRepository;
    @Autowired
    private WithdrawRepository paymentRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SubmissionRepository submissionRepository;

    @Override
    public GlobalResponse search(PaymentsSearchRequest search) {
        Timestamp start = appUtils.formatStringToTimestamp(search.getFrom());
        Timestamp finish = appUtils.formatStringToTimestamp(search.getTo());
        log.info("Dates are {} and {}", start,finish);
        GlobalResponse globalResponse = null;
        //deposits
        try {
            if(search.getType().equalsIgnoreCase("payments")){
                Double todays = paymentRepository.findByDateBetween(start, finish);
                Double total = paymentRepository.findAllTime();
                Map<String, Object> response = new HashMap<>();
                response.put("searched", todays);
                response.put("total", total);
                globalResponse = new GlobalResponse(response,true,false,"finances");
            }else{
                Double deposits = depositRepository.findByDateBetween(start, finish);
                Double payouts = depositRepository.findAllTime();
                Map<String, Object> response = new HashMap<>();
                response.put("searched", deposits);
                response.put("total", payouts);
                globalResponse = new GlobalResponse(response,true,false,"finances");
            }

            return globalResponse;
        }catch (Exception e){
            return new GlobalResponse(e.getMessage(),false,true,"finances");
        }

    }



    @Override
    public GlobalResponse filterPaymentsByPSP(PSPRequest search) {
        Map<String,Object> response = new HashMap<>();
        Timestamp start = appUtils.formatStringToTimestamp(search.getFrom());
        Timestamp finish = appUtils.formatStringToTimestamp(search.getTo());
//        GlobalResponse globalResponse;

//        if(search.getCurrency() != null){
//            try{
//                Double deposit = paymentRepository.findByCurrency(search.getCurrency(),start,finish);
//                List<Deposit> list = depositRepository.getDepositTimeFrameCurrency(search.getCurrency(), start,finish);
//                response.put("deposits", list);
//                response.put("total", deposit);
//                globalResponse = new GlobalResponse(response, true,false,"success");
//            }catch (Exception e){
//                log.info("currency is {}", e.getMessage());
//                globalResponse =  new GlobalResponse(e.getMessage(), false,true,"failed");
//            }
//        }else{
//            try{
//                String prspDeposit = appUtils.getPRSP("deposit", search.getName());
//                log.info("PRSP is {}", prspDeposit);
//                deposits = depositRepository.findByTelcoBetween(prspDeposit,start,finish);
//                List<Deposit> list = depositRepository.getDepositTimeFrame(prspDeposit,start,finish);
//                response.put("deposits", list);
//                response.put("total", deposits);
//                globalResponse = new GlobalResponse(response, true,false,"success");
//            }catch (Exception e){
//                log.info("Logger {}", e.getMessage());
//                globalResponse =  new GlobalResponse(e.getMessage(), false,true,"failed");
//            }
//        }



        try{
            String prsp = appUtils.getPRSP("payment", search.getName());
            log.info("PRSP is {}", prsp);
            Double paid = paymentRepository.findByTelcoBetween(prsp,start,finish);

            List<Withdrawals> withdrawals = paymentRepository.getWithdrawalTimeFrame(prsp,start,finish);
            response.put("payments", withdrawals);
            response.put("total", paid);
            return new GlobalResponse(response, true,false,"success");
        }catch (Exception e){
            log.info("Logger {}", e.getMessage());
            return new GlobalResponse(e.getMessage(), false,true,"failed");
        }
    }

    @Override
    public GlobalResponse filterLona(LonaRequest lonaRequest, Pageable pageable) {
        Timestamp start = appUtils.formatStringToTimestamp(lonaRequest.getFrom());
        Timestamp finish = appUtils.formatStringToTimestamp(lonaRequest.getTo());
        try {
            Page<Lona> lonas = lonaRepository.filterBets(start, finish, pageable);
            if(!lonas.isEmpty()){
                Map<String,Object> data = appUtils.dataFormatter(lonas.getContent(),lonas.getNumber(),
                        lonas.getTotalElements(),lonas.getTotalPages());
                return new GlobalResponse(data, true,false,"success");
            }
            return new GlobalResponse(null, false,true,"failed");
        }catch (Exception e){
            return new GlobalResponse("error", false,true,"failed");
        }

    }

    @Override
    public GlobalResponse lonaTaxes(LonaRequest lonaRequest) {
        Map<String,Object> response = new HashMap<>();
        Timestamp start = appUtils.formatStringToTimestamp(lonaRequest.getFrom());
        Timestamp finish = appUtils.formatStringToTimestamp(lonaRequest.getTo());
        double excise = 0;
        double holding = 0;
        double won = 0;
        double lost = 0;
        double stake = 0;
        double stakeWithoutTax = 0;
        double payout = 0;
        double payoutWithoutTax = 0;
        double loyalty = 0;
        List<Lona> lonas = lonaRepository.filterBetRequests(start, finish);
        if(lonas.isEmpty()){
            return new GlobalResponse(null, false,true,"No data for this date");
        }

        try {

            for (Lona lona: lonas) {
                loyalty += lona.getLoyalty();
                if (lona.isStatus()) {
                    won += lona.getPayout();
                    holding += lona.getWithHolding();
                    payout += lona.getPayout();
                    payoutWithoutTax += withoutTaxWinnings(lona.getPayout(), lona.getWithHolding(), lona.getStake(), lona.getExcise());
                }
                if (!lona.isStatus()) {
                    lost += lona.getStake();
                }
                excise += lona.getExcise();
                stake += lona.getStake();
                stakeWithoutTax += (lona.getStake() + lona.getExcise());
            }


            double gross = (stake - payout);
            double grossWithout = (stake - excise) - payout - (holding);

           //TODO::continue with forming the response
            response.put("excise",excise);
            response.put("loyalty",loyalty);
            response.put("lost", lost);
            response.put("withHolding", holding);
            response.put("won", won);
            response.put("stake",stake);
            response.put("stakeWithoutTax", stakeWithoutTax);
            response.put("payout", payout);
            response.put("payoutWithoutTax", payoutWithoutTax);
            response.put("gross",gross);
            response.put("grossWithoutTax", grossWithout);
            return new GlobalResponse(response, true,false,"success");
        }catch(Exception e){
            log.error("Failed to collect tax {}", e.getMessage());
            return new GlobalResponse(null, false,true,"Failed");
        }
    }
    private double withoutTaxWinnings(Double payout, Double withHolding, int stake, Double wager)
    {
        double payoutWithoutWithHolding = payout + withHolding;
        double odds = payoutWithoutWithHolding / stake;
        return (stake + wager) * odds;
    }

    @Override
    public GlobalResponse remit(RemitDate remitDate) {
        Timestamp dt = appUtils.formatStringToTimestamp(remitDate.getDate());
        String url = "https://remit.rahisibet.com/api/lona";
        Map<String, String> request = new HashMap<>();
        request.put("date", String.valueOf(dt));
        try{
            //String response = restTemplate.postForObject(url, request, String.class);
            return new GlobalResponse(null, true,false, "remited");
        }catch (Exception e){
            return new GlobalResponse(e.getMessage(), false,true, null);
        }
    }

    @Override
    public GlobalResponse remitted(Pageable pageable) {
        Page<Submission> submissions = submissionRepository.findAll(pageable);
        if(!submissions.isEmpty()){
           Map<String,Object> response =  appUtils.dataFormatter(submissions.getContent(),
                    submissions.getNumber(),
                    submissions.getTotalElements(),
                    submissions.getTotalPages());
           return new GlobalResponse(response,true,false,"remitted list");
        }
        return new GlobalResponse(null,false,true,"remitted list is empty");
    }

    @Override
    public GlobalResponse setSettings(SettingRequest request) {
       List<Setting> settings = settingRepository.findByPRSPName(request.getPrsp());
       List<Setting> list = settingRepository.getAll();
       log.info("All are {}", list);
        log.info("Setting request is {} and {} setting is {}", request.getPrsp(), request.getService(), settings);
        if(settings != null){
            for (Setting setting : settings){
                if(setting.getService().equals(request.getService())){
                    try{
                        setting.setStatus(request.getStatus());
                        setting.setSpan(request.getSpan());
                        setting.setPrsp(request.getPrsp());
                        settingRepository.save(setting);

                    }catch (Exception e){
                        return new GlobalResponse(e.getMessage(),false,true,"error");
                    }
                }
            }
            return new GlobalResponse(null,true,false,"success");
        }
        return new GlobalResponse(null,false,true,"No setting found");
    }

    @Override
    public GlobalResponse getAppCounter() {
        Map<String, Object> response = new HashMap<>();
        List<AppCounter> counters = appCounterRepository.findTop();
        long total = appCounterRepository.count();
        response.put("count", total);
        response.put("data", counters);
        return new GlobalResponse(response, true,false,"App Counter List");
    }


    private Double withoutTax(double payout, double withHolding, int stake, double excise) {
        double withoutWH = payout + withHolding;
        double odds = withoutWH / stake;
        return (stake + excise) * odds;
    }

    @Override
    public GlobalResponse filterDepositsByPSP(PSPRequest search) {
        int deposits = 0;
        Map<String,Object> response = new HashMap<>();
        GlobalResponse globalResponse = null;
        Timestamp start = appUtils.formatStringToTimestamp(search.getFrom());
        Timestamp finish = appUtils.formatStringToTimestamp(search.getTo());

            if(search.getCurrency() != null){
                try{
                    Double deposit = depositRepository.findByCurrency(search.getCurrency(),start,finish);
                    List<Deposit> list = depositRepository.getDepositTimeFrameCurrency(search.getCurrency(), start,finish);
                    response.put("deposits", list);
                    response.put("total", deposit);
                    globalResponse = new GlobalResponse(response, true,false,"success");
                }catch (Exception e){
                    log.info("currency is {}", e.getMessage());
                    globalResponse =  new GlobalResponse(e.getMessage(), false,true,"failed");
                }
            }else{
                try{
                    String prspDeposit = appUtils.getPRSP("deposit", search.getName());
                    log.info("PRSP is {}", prspDeposit);
                    deposits = depositRepository.findByTelcoBetween(prspDeposit,start,finish);
                    List<Deposit> list = depositRepository.getDepositTimeFrame(prspDeposit,start,finish);
                    response.put("deposits", list);
                    response.put("total", deposits);
                    globalResponse = new GlobalResponse(response, true,false,"success");
                }catch (Exception e){
                    log.info("Logger {}", e.getMessage());
                   globalResponse =  new GlobalResponse(e.getMessage(), false,true,"failed");
                }
            }
            return globalResponse;
    }


}
