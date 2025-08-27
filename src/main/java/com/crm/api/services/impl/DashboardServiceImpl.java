package com.crm.api.services.impl;

import com.crm.api.api.models.Bet;
import com.crm.api.api.models.Deposit;
import com.crm.api.api.models.Withdrawals;
import com.crm.api.api.repository.*;
import com.crm.api.crm.models.Dispatch;
import com.crm.api.crm.models.User;
import com.crm.api.crm.repository.DispatchRepository;
import com.crm.api.crm.repository.UserRepository;
import com.crm.api.payload.requests.DispatchRequest;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.DashboardService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {


     AppUtils appUtils = new AppUtils();

     @Autowired
     private DispatchRepository dispatchRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private DepositRepository depositRepository;


    @Autowired
    private WithdrawRepository withdrawRepository;

    @Autowired
    private BetRepository betRepository;

    @Autowired
    private UserRepository userRepository;


    public Map<String, Object> getTodayBets() {

        Map<String,Object> response = new HashMap<>();

        log.info("FROM start {} to end at {} ",appUtils.startOfToday(), appUtils.getBurundiTime() );
        List<Bet> bets = betRepository.getBetsByDate(appUtils.startOfToday(), appUtils.getBurundiTime());
        log.info("bets {} ", bets.size());
        int won = 0;
        int lost = 0;
        int active = 0;
        double risky = 0;
        if(!bets.isEmpty()){
            for(Bet bet : bets){
               if(bet.isStatus() && bet.isWon() && bet.getDeleted_at() != null){
                   won+=1;
               }else if(bet.isStatus() && !bet.isWon() && bet.getDeleted_at() != null){
                   lost += 1;
               }else{
                   active += 1;
               }

               if(bet.getPayout() > risky){
                   risky = bet.getPayout();
                }
            }
        }
        response.put("count", bets.size());
        response.put("won", won);
        response.put("lost",lost);
        response.put("active",active);
        response.put("risky",risky);
        return response;
    }


    public Map<String, Object> getPayments() {
        log.info("Entering");
        ZonedDateTime zdtStop = appUtils.getStartOfTommorow();
        String stop = appUtils.formatDate(zdtStop).toString();
        System.out.println("FROM " + stop + " start " +appUtils.startOfToday());
        List<Withdrawals> withdrawals = withdrawRepository.getWithdrawals(appUtils.startOfToday(),stop);
        List<Deposit> deposits = depositRepository.getDeposits(appUtils.startOfToday(),stop);
        double depositedAmount = 0;
        double paidOut = 0;
        Map<String,Object> response = new HashMap<>();
        if(!deposits.isEmpty()){
            for (Deposit deposit : deposits) {
                depositedAmount += deposit.getAmount();
            }
        }

        if(!withdrawals.isEmpty()){
            for (Withdrawals paid : withdrawals) {
                paidOut += paid.getAmount();
            }
        }
        response.put("depositTotal",depositedAmount);
        response.put("depositSize", deposits.size());
        response.put("withDraw",paidOut);
        response.put("withDrawSize", withdrawals.size());
        return response;
    }

    @Override
    public GlobalResponse getTodaysAggregates() {
        Map<String,Object> response = new HashMap<>();
        response.put("payments",getPayments());
        log.info("Today payments");
        response.put("signups", getTodaysSignups());
        log.info("Today signups");
        response.put("bets",getTodayBets());
        log.info("Today bets");
        return new GlobalResponse(response,true,false,"aggregation");
    }

    private Object getTodaysSignups() {
        ZonedDateTime zdtStop = appUtils.getStartOfTommorow();
        String stop = appUtils.formatDate(zdtStop).toString();
        log.info("Sign ups start {} and end {}",appUtils.startOfToday(), appUtils.getBurundiTime());
        return customerRepository.findTodaysSignUps(appUtils.startOfToday(), appUtils.getBurundiTime());
    }

    @Override
    public GlobalResponse getAggregates() {
        Map<String,Object> response = new HashMap<>();
        response.put("clients",customerRepository.count());
        response.put("active",betRepository.activeBets(false));
        return new GlobalResponse(response,true,false,"Aggregation");
    }

    @Override
    public GlobalResponse dispatchMessage(DispatchRequest dispatchRequest, Principal principal) {
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if(user.isPresent()){
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            Dispatch dispatch = new Dispatch(
                    0L,user.get().getId(),dispatchRequest.getMessage(),false,
                    timestamp,timestamp, appUtils.formatStringToTimestamp(dispatchRequest.getDate()),
                    dispatchRequest.getType(),
                    dispatchRequest.getLang()
                    );
            dispatchRepository.save(dispatch);
            return new GlobalResponse(null,true,false,"success");
        }
        return new GlobalResponse(null,false,true,"User not present");
    }

    @Override
    public GlobalResponse sendNotification(long id) {
        Optional<Dispatch> message = dispatchRepository.findById(id);
        if(message.isPresent()){
          // TODO:: implement rest template to dispatch notifications, kim needs to add this feature on app
            return new GlobalResponse(null,true,false,"Notification dispatched");
        }
        return new GlobalResponse(null,false,true,"Message not active");
    }

    @Override
    public GlobalResponse getDispatches(Pageable pageable) {
        Page<Dispatch> dispatches = dispatchRepository.findAllDispatches(pageable);
        if(dispatches != null){
            Map<String, Object> mapped = appUtils.dataFormatter(dispatches.getContent(), dispatches.getNumber(), dispatches.getTotalElements(), dispatches.getTotalPages());
            return new GlobalResponse(mapped,true,false,"Notifications");
        }
        return new GlobalResponse(null,false,true,"No notifications found");
    }

    @Override
    public GlobalResponse getBonusAbusers(String from, String to) {
       List<Bet> bets = betRepository.getBonusBets("bonus",from,to);
       if(!bets.isEmpty()){
           Map<Long, Integer> mapped = bets.stream().collect(Collectors.groupingBy(Bet::getUserId, Collectors.reducing(0, Bet::getAmount, Integer::sum)));
           Map<String, Object> stringObjectMap = appUtils.dataFormatter(mapped, 0, 0, 0);
           return new GlobalResponse(stringObjectMap,true,false,"Bonus abusers");
       }
           return new GlobalResponse(null,false,true,"No Bonus abusers for this range");

    }


}
