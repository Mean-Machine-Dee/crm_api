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
import com.crm.api.dtos.LonaRemit;
import com.crm.api.lona.models.Lona;
import com.crm.api.lona.respositories.LonaRepository;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.FinanceService;
import com.crm.api.utils.AppUtils;
import com.crm.api.utils.Constants;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FinanceServiceImpl implements FinanceService {

    AppUtils appUtils = new AppUtils();
    @Autowired
    private ObjectMapper objectMapper;


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
        Timestamp start = appUtils.startOfDayTimestamp(search.getFrom());
        Timestamp finish = appUtils.endOfDayTimestamp(search.getTo());
        Timestamp startToday = appUtils.startOfToday();
        Timestamp finishToday = appUtils.getBurundiTime();
        log.info("Dates are {} and {} search is {}", start,finish, search);
        GlobalResponse globalResponse = null;
        //deposits
        try {
            if(search.getType().equalsIgnoreCase("payments")){
                Double searched = paymentRepository.findByDateBetween(start, finish);
                Double todays = paymentRepository.findByDateBetween(startToday, finishToday);
                Double total = paymentRepository.findAllTime();
                Map<String, Object> response = new HashMap<>();
                response.put("searched", searched);
                response.put("todays", todays);
                response.put("total", total);
                globalResponse = new GlobalResponse(response,true,false,"payments");
            }else{
                Double searched = depositRepository.findByDateBetween(start, finish);
                Double todays = depositRepository.findByDateBetween(startToday, finishToday);
                Double payouts = depositRepository.findAllTime();
                Map<String, Object> response = new HashMap<>();
                response.put("searched", searched);
                response.put("todays", todays);
                response.put("total", payouts);
                globalResponse = new GlobalResponse(response,true,false,"deposits");
            }

            return globalResponse;
        }catch (Exception e){
            return new GlobalResponse(e.getMessage(),false,true,"finances");
        }

    }



    @Override
    public GlobalResponse filterPaymentsByPSP(PSPRequest search) {
        Map<String,Object> response = new HashMap<>();
        Timestamp start = appUtils.startOfDayTimestamp(search.getFrom());
        Timestamp finish = appUtils.endOfDayTimestamp(search.getTo());

        if(!search.getName().equalsIgnoreCase("n/a") && search.getCurrency().equalsIgnoreCase("n/a") ){
            String prsp = appUtils.getPRSP("payment", search.getName());
            try{
                Double paid = paymentRepository.findPrspSum(search.getName(),start,finish);
                log.info("paid is {}", paid);
                List<Withdrawals> withdrawals = paymentRepository.getWithdrawalTimeFrame(prsp,start,finish);
                response.put("payments", withdrawals);
                response.put("total", paid);
                return new GlobalResponse(response, true,false,"success");
            }catch (Exception e){
                log.info("prsp not found is {}", e.getMessage());
                return new GlobalResponse(e.getMessage(), false,true,"failed");
            }
        }else{
            try{
                Double paid = paymentRepository.findPrspByCurrency(search.getCurrency(),start,finish);
                log.info("currency paid is {}", paid);
                List<Withdrawals> withdrawals = paymentRepository.getCurrencyWithdrawalBetween(search.getCurrency(),start,finish);
                response.put("payments", withdrawals);
                response.put("total", paid);
                return new GlobalResponse(response, true,false,"success");
            }catch (Exception e){
                log.info("currency not found is {}", e.getMessage());
                return new GlobalResponse(e.getMessage(), false,true,"failed");
            }
        }










//        try{
//            String prsp = appUtils.getPRSP("payment", search.getName());
//            log.info("PRSP is {} from {} to {}", prsp,start,finish);
//            Double paid = paymentRepository.findByTelcoBetween(prsp,start,finish);
//            List<Withdrawals> withdrawals = paymentRepository.getWithdrawalTimeFrame(prsp,start,finish);
//            response.put("payments", withdrawals);
//            response.put("total", paid);
//            return new GlobalResponse(response, true,false,"success");
//        }catch (Exception e){
//            log.info("Logger {}", e.getMessage());
//            return new GlobalResponse(e.getMessage(), false,true,"failed");
//        }
    }

    @Override
    public GlobalResponse filterLona(LonaRequest lonaRequest, Pageable pageable) {
        Timestamp start = appUtils.startOfDayTimestamp(lonaRequest.getFrom());
        Timestamp finish = appUtils.endOfDayTimestamp(lonaRequest.getTo());
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
    public GlobalResponse lonaTaxes(LonaRequest lonaRequest, String type) {
        Map<String,Object> response = new HashMap<>();
        Timestamp start = appUtils.startOfDayTimestamp(lonaRequest.getFrom());
        Timestamp finish = appUtils.endOfDayTimestamp(lonaRequest.getTo());
        if(type.equalsIgnoreCase("landing")){
           start = appUtils.minusDays(1);
           finish = appUtils.startOfToday();
        }

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
        LonaRemit remit = new LonaRemit();
        remit.setDate(String.valueOf(dt));


        try{
            String json = objectMapper.writeValueAsString(remit);
            log.info("Lona remittance response {}", json);
//            String response = restTemplate.postForObject(url, json, String.class);

//            log.info("Lona remittance response {}", response);
            return new GlobalResponse(null, true,false, "remited");
        }catch (Exception e){
            return new GlobalResponse(e.getMessage(), false,true, null);
        }
    }

    @Override
    public GlobalResponse remitted(Pageable pageable) {
        Page<Submission> submissions = submissionRepository.findAllData(pageable);
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
//       List<Setting> settings = settingRepository.findByPRSPName(request.getPrsp());
//       List<Setting> list = settingRepository.getAll();
//       log.info("All are {}", list);
//
//        if(settings != null){
//            for (Setting setting : settings){
//                log.info("Checking request{} and setting {}", request, setting);
//                if(setting.getPrsp().equalsIgnoreCase(request.getPrsp()) && setting.getService().equalsIgnoreCase(request.getService()) && setting.getCountry().equalsIgnoreCase(request.getCountry())){
//                    log.info("Its a match");
//                    try{
//                        String statusRequest = request.getStatus().equalsIgnoreCase("Deactivate") ? "DeActivate":request.getStatus();
//                        setting.setStatus(statusRequest);
//                        setting.setSpan(request.getSpan());
//                        setting.setPrsp(request.getPrsp());
//                        settingRepository.save(setting);
//                    }catch (Exception e){
//                        return new GlobalResponse(e.getMessage(),false,true,"error");
//                    }
//                }else{
//                    log.info("No match found");
//                }
//            }
//            return new GlobalResponse(null,true,false,"success");
//        }
        Optional<Setting> setting = settingRepository.findById(request.getId());
        String status = request.getStatus().equalsIgnoreCase("active") ? "Activate" : "DeActivate";
        if(setting.isPresent()){
            Setting service = setting.get();
            service.setStatus(status);
            settingRepository.save(service);
            return new GlobalResponse(null,true,false,"Setting set successfully");
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

    @Override
    public GlobalResponse payDirect(DirectTransferRequest request) {
        try{

            request.setCurrency("BIF");
            request.setProvider("lumicash");
            log.info("Transfer {}", request);
            String json = objectMapper.writeValueAsString(request);
            String response = restTemplate.postForObject(Constants.TRANSFER_URL, json, String.class);
            log.info("Direct Transfer response {}",response);
            return new GlobalResponse(response, true,false,"Withdrawal successfull");
        } catch (JsonProcessingException e) {
            return new GlobalResponse(e.getMessage(), true,false,"Withdrawal successfull");
        }

    }

    @Override
    public GlobalResponse filterCashflow(String type) {
        Timestamp start = appUtils.startOfToday();
        Timestamp finish = appUtils.getStopDate();
        log.info("Between {} and {} ", start,finish);
        Double todays = 0.0;
       if(type.equalsIgnoreCase("deposit")){
          Integer deposits = depositRepository.findByTelcoBetween("Lumitel", start, finish);
           todays = deposits != 0 ? Double.valueOf(deposits) : 0.0;
       }else{
          todays = paymentRepository.findByCurrency("BI",start,finish);
       }
        return new GlobalResponse(todays, true,false,"Today's Payments");
    }

    @Override
    public GlobalResponse getPaymentSettings() {
        List<Setting> settings = settingRepository.getAll();
        return new GlobalResponse(settings, true,false,"Payments service");
    }
    @Override
    public GlobalResponse filterPaymentsToDay(String prsp, String currency) {

        Timestamp start = appUtils.startOfToday();
        Timestamp stop = appUtils.getBurundiTime();
        Timestamp months = appUtils.minusDays(90);
        log.info("Filter payments {} {} {} {}",prsp,currency,start,stop);
        Double today;
        Double total;
        String td = "today";
        String all = "allTime";

        Map<String,Double> response  = new HashMap<>();
        if(!currency.equalsIgnoreCase("na") && prsp.equalsIgnoreCase("na")){
            today = paymentRepository.getWithdrawByCurrency(currency,start,stop);
            total = paymentRepository.findAllTimeCurrency(currency,months,start);
            response.put(td,today);
            response.put(all,total);
        }

        if(!prsp.equalsIgnoreCase("na") && currency.equalsIgnoreCase("na")){
            today = paymentRepository.findPrspSum(prsp,start,stop);
            total = paymentRepository.findAllTimePrsp(prsp,months,start);
            response.put(td,today);
            response.put(all,total);
        }
        return new GlobalResponse(response, true,false,"Todays Payments");
    }
    @Override
    public GlobalResponse filterTodays(String prsp, String currency) {
        Timestamp start = appUtils.startOfToday();
        Timestamp stop = appUtils.getBurundiTime();
        double today = 0;
        double total = 0;
        Map<String,Double> response  = new HashMap<>();
        if(!currency.equalsIgnoreCase("na") && prsp.equalsIgnoreCase("na")){
            today = depositRepository.getDepositCurrency(currency,start,stop);
            total = depositRepository.allTimeCurrency(currency);
            response.put("today",today);
            response.put("total",total);
        }

        if(!prsp.equalsIgnoreCase("na") && currency.equalsIgnoreCase("na")){
           today = depositRepository.findByTelcoBetween(prsp,start,stop);
           total = depositRepository.allTimePrsp(prsp);
            response.put("today",today);
            response.put("total",total);
        }
        return new GlobalResponse(response, true,false,"Todays Deposit");
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
        Timestamp start = appUtils.startOfDayTimestamp(search.getFrom());
        Timestamp finish = appUtils.endOfDayTimestamp(search.getTo());

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
