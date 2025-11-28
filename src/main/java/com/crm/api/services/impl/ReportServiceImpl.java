package com.crm.api.services.impl;

import com.crm.api.api.models.*;
import com.crm.api.api.repository.*;
import com.crm.api.crm.models.Jackpot;
import com.crm.api.crm.repository.JackpotRepository;
import com.crm.api.dtos.AffiliateDepositDTO;
import com.crm.api.dtos.AffiliateSerializer;
import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.ReportService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private BetRepository betRepository;



    @Autowired
    private JackpotRepository jackpotRepository;


    @Autowired
    private DepositRepository depositRepository;


    @Autowired
    private FriendRepository friendRepository;


    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private AppUtils appUtils = new AppUtils();

    @Override
    public GlobalResponse dashboard(LonaRequest reportRequest) {
        Timestamp from = appUtils.startOfDayTimestamp(reportRequest.getFrom());
        Timestamp to = appUtils.endOfDayTimestamp(reportRequest.getTo());
        GlobalResponse globalResponse;
        String currency = appUtils.getCurrency(reportRequest.getCountry());
        Double deposits = depositRepository.findByCurrency(currency,from,to);
        Double withdrawals = withdrawRepository.findByCurrency(currency,from,to);
        List<Bet> bets = betRepository.getBetsByDateIso(reportRequest.getCountry(),from, to);
        globalResponse = reportResponse(bets,deposits,withdrawals);

        return globalResponse;
    }

    @Override
    public GlobalResponse providerReport(LonaRequest providerRequest) {
        GlobalResponse globalResponse = null;
        Timestamp from = appUtils.startOfDayTimestamp(providerRequest.getFrom());
        Timestamp to = appUtils.endOfDayTimestamp(providerRequest.getTo());

            //search todays
           try{
               String currency = appUtils.getCurrency(providerRequest.getCountry());
               List<Deposit> deposits = depositRepository.getDepositTimeFrameCurrency(currency,from,to);
               List<Withdrawals> withdrawals = withdrawRepository.getPaymentByIso(providerRequest.getCountry(),from,to);
               globalResponse = providerCollection(deposits, withdrawals);
           }catch (Exception e){
               log.info("Error on providerReport {}", e.getMessage());
               globalResponse = new GlobalResponse(e.getMessage(), false,true, "error");
           }


        return globalResponse;
    }

    @Override
    public GlobalResponse getAffiliates(String from, String to, String type, Pageable pageable, String country) {
        Timestamp start = appUtils.startOfDayTimestamp(from);
        Timestamp finish = appUtils.endOfDayTimestamp(to);
        if(type.equalsIgnoreCase("landing")){
            start = appUtils.minusDays(1);
            finish = appUtils.startOfToday();
        }
        Page<AffiliateSerializer> friends = friendRepository.findByCountryAndDate(country,start,finish,pageable);
//        Map<String, List<AffiliateSerializer>> data = friends.getContent().stream().collect(Collectors.groupingBy(AffiliateSerializer::getIso));
        Map<String, Object> dataFormatter = appUtils.dataFormatter(friends.getContent(), friends.getNumber(), friends.getTotalElements(), friends.getTotalPages());
        return new GlobalResponse(dataFormatter, true,false, "Data");
    }

    @Override
    public GlobalResponse getAffiliate(Long id) {

        List<Friend> friendList = friendRepository.getAllInvitees(id);
        log.info("User Id {} xxx {}", id,friendList);
        List<AffiliateDepositDTO> depositDTOS = new ArrayList<>();
        for (Friend friend: friendList){
            Deposit deposit = depositRepository.getFirstDeposit(friend.getInvitee());
            Customer customer = customerRepository.getByUserId(friend.getInvitee());
            if(deposit != null){
                AffiliateDepositDTO dto = new AffiliateDepositDTO(customer.getPhone(),friend.getInvitee(), (int) deposit.getAmount(), (int) (0.1 *  deposit.getAmount()),deposit.getDate_deposited(),friend.getInvite(),true);
                depositDTOS.add(dto);
            }else{
              depositDTOS.add(new AffiliateDepositDTO(customer.getPhone(),friend.getInvitee(), 0, 0,null,friend.getInvite(),false));
            }
        }
        return new GlobalResponse(depositDTOS, true,false, "Data");
    }

    @Override
    public GlobalResponse getJackpotReport() {
        Jackpot jackpot = jackpotRepository.findByActive();
        Map<String,Object> response = new HashMap<>();
        if(jackpot != null){
            List<Bet> bets = betRepository.findByJackpotId(jackpot.getId());
            Map<String, Object> betReports = betReport(bets);
            response.put("data",betReports);
            return new GlobalResponse(response, true,false, "jackpot data");
        }
        return new GlobalResponse(response, false,true, "No active jackpot");
    }

    private GlobalResponse providerCollection(List<Deposit> deposits, List<Withdrawals> withdrawals) {
        //TODO:: add crm and astropay deposits
        Map<String, List<Deposit>>  depositCollection = deposits.stream()
                .collect(Collectors.groupingBy(Deposit::getTelco));


        Map<String, Double> depositMap = new HashMap<>();
        Map<String, Double> withDrawMap = new HashMap<>();

        for (Map.Entry<String, List<Deposit>> entry: depositCollection.entrySet()){
            Double reduced = entry.getValue().stream().
                    map(ob -> (ob.getAmount() + ob.getAmount()))
                    .reduce(0.0, Double::sum);
            depositMap.put(entry.getKey(), reduced);
        }


        Map<String, List<Withdrawals>>  withDrawCollection = withdrawals.stream().parallel()
                .collect(Collectors.groupingBy(Withdrawals::getTelco));
        for (Map.Entry<String, List<Withdrawals>> entry: withDrawCollection.entrySet()){
            Double reduced = entry.getValue().stream().
                    map(ob -> (ob.getAmount() + ob.getAmount()))
                    .reduce(0.0, Double::sum);
            withDrawMap.put(entry.getKey(), reduced);
        }
        Map<String, Object> response = new HashMap<>();
        response.put("deposits", depositMap);
        response.put("payments",withDrawMap);
        return new GlobalResponse(response, true,false, "providers");
    }


    private GlobalResponse reportResponse(List<Bet> bets, Double deposits, Double withdrawals) {
        GlobalResponse globalResponse;
        Map<String,Object> map = new HashMap<>();

        if(!bets.isEmpty()){
            map.put("bets", betReport(bets));
            map.put("deposits", deposits);
            map.put("payments",withdrawals);
            globalResponse =  new GlobalResponse(map, true, false, "dashboard report");
        }else{
            globalResponse =  new GlobalResponse(null, false, true, "No bets found for this period");
        }
        return globalResponse;
    }

    private Map<String, Object> betReport(List<Bet> bets){
        Map<String, Object> response = new HashMap<>();
        int totalBets = 0;
        int active = 0;
        int lost = 0;
        int staked = 0;
        double payout = 0;
        int won = 0;
        double totalWon = 0;
        int totalLost = 0;
        for (Bet bet: bets){
            if(!bet.isStatus()){
                active++;
            }
            if(bet.isStatus() && !bet.isWon()){
                lost++;
                totalLost += bet.getAmount();
            }
            if(bet.isWon() && bet.isStatus()){
                won++;
                totalWon += bet.getPayout();
            }
            payout+= bet.getPayout();
            staked += bet.getAmount();
            totalBets++;
        }

        response.put("allBets", totalBets);
        response.put("active", active);
        response.put("lost", lost);
        response.put("won", won);
        response.put("staked", staked);
        response.put("payout", payout);
        response.put("totalWon", totalWon);
        response.put("totalLost", totalLost);
        return response;
    }
}
