package com.crm.api.services.impl;

import com.crm.api.api.models.Bet;
import com.crm.api.api.models.Deposit;
import com.crm.api.api.models.Withdrawals;
import com.crm.api.api.repository.BetRepository;
import com.crm.api.api.repository.DepositRepository;
import com.crm.api.api.repository.WithdrawRepository;
import com.crm.api.payload.requests.LonaRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.ReportService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
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
    private DepositRepository depositRepository;


    @Autowired
    private WithdrawRepository withdrawRepository;

    private AppUtils appUtils = new AppUtils();

    @Override
    public GlobalResponse dashboard(LonaRequest reportRequest) {
        Timestamp from = appUtils.formatStringToTimestamp(reportRequest.getFrom());
        Timestamp to = appUtils.formatStringToTimestamp(reportRequest.getTo());
        GlobalResponse globalResponse;
        Double deposits = depositRepository.findTotalBetween(from,to);
        Double withdrawals = withdrawRepository.findTotalBetween(from,to);

            List<Bet> bets = betRepository.getBetsByDate(from, to);
            globalResponse = reportResponse(bets,deposits,withdrawals);

        return globalResponse;
    }

    @Override
    public GlobalResponse providerReport(LonaRequest providerRequest) {
        GlobalResponse globalResponse = null;
        Timestamp from = appUtils.formatStringToTimestamp(providerRequest.getFrom());
        Timestamp to = appUtils.formatStringToTimestamp(providerRequest.getTo());

            //search todays
           try{
               List<Deposit> deposits = depositRepository.getDepositBetween(from,to);
               List<Withdrawals> withdrawals = withdrawRepository.getWithdrawalBetween(from,to);
               globalResponse = providerCollection(deposits, withdrawals);
           }catch (Exception e){
               log.info("Error on providerReport {}", e.getMessage());
               globalResponse = new GlobalResponse(e.getMessage(), false,true, "error");
           }


        return globalResponse;
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
