package com.crm.api.services.impl;


import com.crm.api.api.models.*;
import com.crm.api.api.repository.*;
import com.crm.api.crm.models.*;
import com.crm.api.crm.repository.AgentActivityRepository;
import com.crm.api.crm.repository.TicketRepository;
import com.crm.api.crm.repository.UserRepository;
import com.crm.api.dtos.ActivityDTO;
import com.crm.api.dtos.CustomerDTO;
import com.crm.api.dtos.PlayersOnly;
import com.crm.api.dtos.TicketDTO;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.CustomerService;
import com.crm.api.services.WalletService;
import com.crm.api.utils.AppUtils;
import com.crm.api.utils.ThreadExecutor;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private ThreadExecutor executorService = new ThreadExecutor();
    private Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

    @Autowired
    BetRepository betRepository;

    AppUtils appUtils = new AppUtils();


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    OTPSRepository otpsRepository;

    @Autowired
    PicksRepository picksRepository;


    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    AgentActivityRepository agentActivityRepository;


    @Autowired
    DepositRepository depositRepository;


    @Autowired
    BonusRepository bonusRepository;

    @Autowired
    WithdrawRepository withdrawRepository;

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WalletService walletService;

    @Autowired
    TicketRepository ticketRepository;



    @Override
    @Transactional
    public GlobalResponse getUserBets(long id, Pageable pageable) {
        Page<Bet> bets = betRepository.getPagedClientBets(id, pageable);
        if (!bets.getContent().isEmpty()) {
            Map<String, Object> response = appUtils.dataFormatter(bets.getContent(), bets.getNumber(), bets.getTotalElements(), bets.getTotalPages());
            return new GlobalResponse(response, true, false, "bets");
        }
        return new GlobalResponse(null, true, false, "bets");
    }

    @Override
    public GlobalResponse getUserPayments(long id) {

        Map<String, Object> response = new HashMap<>();
        Customer customer = customerRepository.findById((int) id);

        logger.info("Customer {} {} {}", customer.getId(), customer.getPhone(), id);


        List<Deposit> deposits = depositRepository.userDeposits(customer.getId());
        List<Withdrawals> withdrawals = withdrawRepository.userB2c(customer.getPhone());
        response.put("main", customer.getAccount().getMain());
        response.put("bonus", customer.getAccount().getBonus());

        if (!deposits.isEmpty()) {
            response.put("c2b", deposits.size());
            response.put("c2bs", deposits);

        } else {
            response.put("c2b", 0);
            response.put("c2bs", null);
        }

        if (!withdrawals.isEmpty()) {
            response.put("b2c", withdrawals.size());
            response.put("b2cs", withdrawals);
        } else {
            response.put("b2c", 0);
            response.put("b2cs", null);
        }

        return new GlobalResponse(response, true, false, "bets");
    }

    @Override
    public GlobalResponse getClients(Pageable pageable, String phone, Principal principal) {
        Page<Customer> customers = null;
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if(!user.isPresent()){
            return new GlobalResponse(null,false,true,"No user found");
        }
        boolean isCustomerCare = checkAdmin(user.get());
        if (phone.equals("all")) {
           if(isCustomerCare){
               customers = customerRepository.getLocalizedCustomers(user.get().getIso(),pageable);
           }else{
               customers = customerRepository.getCustomers(pageable);
           }
        } else {
            customers = customerRepository.findByPhoneLike(phone, pageable);
        }

        if (customers != null) {
            List<CustomerDTO> data = customers.getContent().stream().map(customer -> {
                Account account = customer.getAccount();
                return CustomerDTO.builder()
                        .blocked(account.isBlocked())
                        .canWithdraw(account.isBlock_withdraw())
                        .phone(customer.getPhone())
                        .main(account.getMain())
                        .id(customer.getId())
                        .bonus(account.getBonus())
                        .country(customer.getIso())
                        .joined(String.valueOf(customer.getCreatedAt()))
                        .build();
            }).collect(Collectors.toList());
            Map<String, Object> response = appUtils.dataFormatter(data, customers.getNumber(),
                    customers.getTotalElements(),
                    customers.getTotalPages());
            return new GlobalResponse(response, true, false, "clients");
        }

        return new GlobalResponse(null, false, true, "Failed to get clients");


    }

    private boolean checkAdmin(User user) {
        for (Role role: user.getRoles()){
            if(role.getName() == ERole.ROLE_CUSTOMER_CARE){
               return  true;
            }
        }

        return false;
    }

    @Override
    public GlobalResponse getClientDetails(long id) {
        Customer customer = customerRepository.findById(id);
        OTPS otps = otpsRepository.findByUser(customer.getId());
        logger.info("customer is {} and is {} otp is {}", customer.getPhone(),customer.getId(), otps);
        Map<String, Object> response = new HashMap<>();
        List<ActivityDTO> activityDTOS = null;
        List<Bet> bets = betRepository.getClientBets((int) id);
        List<Activity> activities = activityRepository.findByUserId(id);
        if (!activities.isEmpty()) {
            activityDTOS = activities.stream().map(activity -> ActivityDTO.builder()
                    .type(activity.getType())
                    .activity(activity.getActivity())
                    .from(activity.getSystem())
                    .createdAt(activity.getCreatedAt())
                    .build()).collect(Collectors.toList());
        }
        int active = 0, lost = 0, total = 0, won = 0;
        if (!bets.isEmpty()) {
            for (Bet bet : bets) {
                total++;
                if (bet.isWon()) {
                    won++;
                } else if (bet.isStatus()) {
                    lost++;
                } else {
                    active++;
                }
            }

        }

        try {
            List<Withdrawals> withdrawals = withdrawRepository.userB2c(customer.getPhone());
            double totalB2c = withdrawals.stream().map(Withdrawals::getAmount).reduce(0.0, Double::sum);
            List<Deposit> deposits = depositRepository.userDeposits(customer.getId());
            double totalC2B = deposits.stream().map(Deposit::getAmount).reduce(0.0, Double::sum);
            Account account = customer.getAccount();

            CustomerDTO customerDTO = CustomerDTO.builder()
                    .blocked(account.isBlocked())
                    .canWithdraw(account.isBlock_withdraw())
                    .phone(customer.getPhone())
                    .main(account.getMain())
                    .id(customer.getId())
                    .verified(customer.isVerified())
                    .bonus(account.getBonus())
                    .country(customer.getIso())
                    .joined(String.valueOf(customer.getCreatedAt()))
                    .build();
            response.put("active", active);
            response.put("lost", lost);
            response.put("total", total);
            response.put("won", won);
            response.put("b2c", totalB2c);
            response.put("c2b", totalC2B);
            response.put("activities", activityDTOS);
            response.put("details", customerDTO);
            response.put("otp",otps);

            return new GlobalResponse(response, true, false, "client");
        } catch (Exception e) {
            return new GlobalResponse(e.getMessage(), false, true, "client data failed");
        }

    }

    @Override
    public GlobalResponse getClientPayments(int id, Pageable pageable) {
        Customer customer = customerRepository.getByUserId(id);
        if (customer != null) {
            Page<Withdrawals> withdrawals = withdrawRepository.getClientWithdrawals(customer.getPhone(), pageable);
            if (!withdrawals.getContent().isEmpty()) {
                Map<String, Object> response = appUtils.dataFormatter(withdrawals.getContent(), withdrawals.getNumber(),
                        withdrawals.getTotalElements(),
                        withdrawals.getTotalPages());
                return new GlobalResponse(response, true, false, "b2c");
            }
        }

        return new GlobalResponse(null, false, true, "withdrawals failed");
    }

    @Override
    public GlobalResponse getDeposits(int id, Pageable pageable, String type) {


        Customer customer = customerRepository.getByUserId(id);
        if (customer != null) {
            logger.info("Telco is {}  customer {}", type, customer.getPhone());
            Page<Deposit> deposits = null;
            if(type.equalsIgnoreCase("deposits")){
                deposits = depositRepository.userDepositsPaged(customer.getId(), pageable);
            }else{
                deposits = depositRepository.userPagedDeposits(customer.getId(),"crm", pageable);
            }

            if (!deposits.getContent().isEmpty()) {
                Map<String, Object> response = appUtils.dataFormatter(deposits.getContent(), deposits.getNumber(),
                        deposits.getTotalElements(),
                        deposits.getTotalPages());
                return new GlobalResponse(response, true, false, "c2b");
            } else {
                logger.info("No deposits found");
            }
        } else {
            logger.info("Customer not found");
        }
        return new GlobalResponse(null, false, true, "c2b failed");


    }

    @Override
    public GlobalResponse clientDeposit(DepositRequest depositRequest, Principal principal) {
        logger.info("Deposit {} -- principal {}", depositRequest, principal.getName());
        String txId = appUtils.getRefCode();
        String refCode = String.valueOf(System.currentTimeMillis());
        Customer customer = customerRepository.findByPhone(depositRequest.getPhone());
        Optional<User> user = userRepository.findByUsername(principal.getName());
        if (user.isPresent() && customer != null) {
            Deposit deposit
                    = new Deposit(
                    1L, txId, "CRM", refCode,
                    depositRequest.getAmount(),
                    "Complete",
                    "n/a",
                    "BIF",
                    0.00,
                    depositRequest.getPhone(),
                    customer.getId(), user.get().getId(),
                    "Deposit Successful", "crm", new Timestamp(System.currentTimeMillis())

            );

            try {
                Account account = accountRepository.findByOwnerId(customer.getId());
                walletService.deposit(deposit, account);
                depositRepository.save(deposit);
            } catch (Exception e) {
                logger.info("Error depositing {}", e.getMessage());
                return new GlobalResponse(e.getMessage(), false, true, "c2b failed");
            }
            return new GlobalResponse(deposit, true, false, "c2b successful");
        }
        return new GlobalResponse(null, false, true, "c2b failed");
    }

    @Override
    public GlobalResponse b2c(WithdrawRequest request, Principal principal) {
//        String txId = appUtils.getRefCode();
//        String refCode = String.valueOf(System.currentTimeMillis());
//        Customer customer = customerRepository.findByPhone(request.getPhone());
//        Optional<User> user = userRepository.findByEmail(principal.getName());

        return null;

    }

    @Transactional
    @Override
    public GlobalResponse profile(ProfileRequest request) {
        Customer customer = customerRepository.findById(request.getId());
        if (customer != null) {
            Account account = accountRepository.findByOwnerId(customer.getId());
            account.setBlocked(request.isCanBet());
            account.setBlock_withdraw(request.isCanWithdraw());
            logger.info("acc {}", account);
            accountRepository.save(account);
            return new GlobalResponse(null, true, false, "profile updated");
        }
        return new GlobalResponse(null, false, true, "user not found");
    }


    @Override
    public GlobalResponse createTicket(TicketRequest ticketDTO, Principal principal) {
        try {
            User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Ticket ticket = new Ticket(0L, ticketDTO.getName(), "open", ticketDTO.getDescription(), ticketDTO.getPhone(), ticketDTO.getIssueType(), user.getId(), now, now);
            ticketRepository.save(ticket);
            AgentActivity activity = new AgentActivity(0L,ticketDTO.getDescription(),"crm","Ticket Creation", "00000000",new Timestamp(System.currentTimeMillis()));
            agentActivityRepository.save(activity);
            return new GlobalResponse(ticket, true, false, "Ticket created successfully");
        } catch (Exception e) {
            logger.info("Exception is ticket creation {}", e.getMessage());
            return new GlobalResponse(null, false, true, "Ticket creation failed");
        }
    }

    @Override
    public GlobalResponse updateTicket(TicketRequest ticketDTO, long id) {
        try {
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Ticket ticket = ticketRepository.findById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
            ticket.setStatus(ticketDTO.getStatus());
            ticket.setDescription(ticketDTO.getDescription());
            ticket.setIssueType(ticketDTO.getIssueType());
            ticket.setName(ticketDTO.getName());
            ticket.setPhone(ticketDTO.getPhone());
            ticket.setUpdatedAt(now);
            ticketRepository.save(ticket);
            return new GlobalResponse(ticket, true, false, "Ticket updated successfully");
        } catch (Exception e) {
            logger.info("Exception is ticket update {}", e.getMessage());
            return new GlobalResponse(e.getMessage(), false, true, "Ticket update failed");
        }

    }

    @Override
    public GlobalResponse getTickets(Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.getPagedTickets(pageable);
        List<TicketDTO> ticketDTOS = tickets.getContent().stream().map(
                ticket -> TicketDTO.builder()
                        .name(ticket.getName())
                        .description(ticket.getDescription())
                        .status(ticket.getStatus())
                        .issueType(ticket.getIssueType())
                        .phone(ticket.getPhone())
                        .id(ticket.getId())

//                        .createdBy(ticket.getCreatedBy())
                        .build()).collect(Collectors.toList());
        Map<String, Object> response = appUtils.dataFormatter(ticketDTOS, tickets.getNumber(), tickets.getTotalElements(), tickets.getTotalPages());
        return new GlobalResponse(response, true, false, "Ticket list");
    }

    @Override
    public GlobalResponse deleteTicket(long id) {
        try {
            ticketRepository.deleteById(id);
            return new GlobalResponse(null, true, false, "Ticket deleted successfully");
        } catch (Exception e) {
            logger.info("delete ticket failed {}", e.getMessage());
            return new GlobalResponse(null, false, true, e.getMessage());
        }

    }

    @Override
    public GlobalResponse getBetPicks(int id) {
        List<Picks> picks = picksRepository.getPicks((long) id);
        return new GlobalResponse(picks, true, false, "Picks");
    }

    @Override
    public GlobalResponse verifyClient(long id) {
        Customer customer = customerRepository.findCustomerId(id);
        customer.setVerified(true);
        customerRepository.save(customer);
        AgentActivity activity = new AgentActivity(0L,"Verified users " + customer.getPhone(),"crm","Verification", customer.getPhone(),new Timestamp(System.currentTimeMillis()));
        agentActivityRepository.save(activity);
        return new GlobalResponse(null, true, false, "Account verified");
    }

    @Override
    public GlobalResponse settleBulk(MultipartFile filename, String type) {
        logger.info("File uploaded successfully");
        List<String> betCodes = new ArrayList<>();
            Runnable task = () ->{
                Reader reader = null;
                try {
                    reader = new InputStreamReader(filename.getInputStream());
                    CSVReader csvReader = new CSVReaderBuilder(reader).build();
                    List<String[]> records = csvReader.readAll();
                    for (String[] record : records) {
                        String[] codes = record[1].trim().split(",");
                        for (String code : codes) {
                            logger.info("BetCode is {}", code);
                            if (code != null) {
                                betCodes.add(code);
                            }
                        }
                    }
                    if(type.equalsIgnoreCase("active")){
                        // settleBets(betCodes);
                    }else if(type.equalsIgnoreCase("postponed")){
                        settlePostPoned(betCodes);
                    }

                } catch (IOException | CsvException e) {
                    logger.info("Service interrupted by::: {}", e.getMessage());
                    executorService.shutDown();
                    throw new RuntimeException(e);
                }

            };
            executorService.settleBetsExecutor(task);
            logger.info("Total Codes are {}", betCodes.size());
        return new GlobalResponse(null, true, false, "Bets Settled successfully");

    }

    private void settlePostPoned(List<String> betCodes) {
        //TODO::impliment code for postponed bets
    }

    @Override
    public GlobalResponse agentsData(Pageable pageable) {
        Page<AgentActivity> activities = agentActivityRepository.findAll(pageable);
       if(!activities.isEmpty()){
        Map<String, Object> data = appUtils.dataFormatter(activities.getContent(), activities.getNumber(), activities.getTotalElements(), activities.getTotalPages());
        return new GlobalResponse(data, true, false, "Agents Data");
    }else{
        return new GlobalResponse(null, true, false, "No data availalbe");
    }
    }



    private void settleBets(List<String> betCodes) {
     for(String code: betCodes){
         Bet bet = betRepository.findByBetCode(code);
         if(bet != null){
             Customer customer = customerRepository.findById(bet.getUserId());
             double payout = bet.getPayout();
             bet.setStatus(true);
             bet.setWon(true);
             bet.setDeleted_at(new Timestamp(System.currentTimeMillis()));
             betRepository.save(bet);
             walletService.creditCustomer(payout, bet.getUserId());
         }else{
             logger.info("BET {} not found", code);
         }
     }
    }

    @Override
    public GlobalResponse getPlayers(String category,String country, HttpServletResponse response) {
        //TODO:: make this to be downloadable via csv file
        Timestamp now = appUtils.getBurundiTime();
       Timestamp to = appUtils.minusDays(1);

        //Active users ...
        //VIP users
        //Dormant Users
        logger.info("search from  {} -- {} -- iso code --- {}",now,to,country);
        List<Bet> betList = betRepository.findBetsWithinAWeek(country,to,now);
        logger.info("betList is {}", betList.size());
        Map<Long, Double> collected = betList.stream().collect(Collectors.groupingBy(Bet::getUserId, Collectors.summingDouble(Bet::getAmount)));
        Map<Long, Double> vip = new HashMap<>();
        Map<Long,Double> activeUsers = new HashMap<>();
        for (Map.Entry<Long,Double> entry: collected.entrySet()){
            if(entry.getValue() >= 500000){
                vip.put(entry.getKey(), entry.getValue());
            }else{
                activeUsers.put(entry.getKey(), entry.getValue());
            }

        }




        //TODO:: all filters should be in a week
        //TODO:: Bets worth above 500k, Top winners and casinos
        //TODO:: users have value 500k add a bonus amount stake after tax
        //TODO:: active users anyone placed a bet within a week
        //TODO:: not placed a bet in a months time:: idle list
        Map<String, Object> stringObjectMap = new HashMap<>();
        List<String> byIds = new ArrayList<>();
        if(category.equalsIgnoreCase("vip")){
            stringObjectMap = appUtils.dataFormatter(vip, 1, byIds.size(), byIds.size());
//            byIds = customerRepository.findByIds(vip.keySet());
        }else if(category.equalsIgnoreCase("active")){
            stringObjectMap = appUtils.dataFormatter(activeUsers, 1, byIds.size(), byIds.size());
        }else{
            logger.info("Dormant users");
        }
        return new GlobalResponse(stringObjectMap,true,false,"Data");

    }

    @Override
    public GlobalResponse bulkDeposit(MultipartFile file) {
        logger.info("File uploaded successfully");
        Map<String,String> clientDeposit = new HashMap<>();
        Runnable task = () ->{
            Reader reader = null;
            try {
                reader = new InputStreamReader(file.getInputStream());
                CSVReader csvReader = new CSVReaderBuilder(reader).build();
                List<String[]> records = csvReader.readAll();
                for (String[] record : records) {
                    String phoneNumber = record[0];
                    String amount = record[2];
                    if(phoneNumber != null && amount != null){
                        clientDeposit.put(phoneNumber, amount);
                    }
                }
                // settleBets(betCodes);
            } catch (IOException | CsvException e) {
                logger.info("Service interrupted by::: {}", e.getMessage());
                executorService.shutDown();
                throw new RuntimeException(e);
            }

        };
        executorService.bulkDepositExecutor(task);
        //TODO:: impliment code to increment account
        return new GlobalResponse(null, true, false, "Deposit updated successfully");
    }

    @Override
    public GlobalResponse awardBonus(BonusWinnersRequest winnersRequest) {

            String[] numberArr = winnersRequest.getWinners().split(",");
            Arrays.stream(numberArr).forEach(number->{
            Optional<Account> account = accountRepository.findByName(number);
                account.ifPresent(value -> topUpBonus(value, winnersRequest.getAmount()));
            });
            return null;

    }

    @Override
    public GlobalResponse getRate(String from, String to,String iso) {
        Timestamp start = appUtils.startOfDayTimestamp(from);
        Timestamp finish = appUtils.endOfDayTimestamp(to);
        List<Customer> customers = customerRepository.findByCreatedAtRange(iso,start,finish);
        List<Long> customerIds = customers.stream().map(Customer::getId).collect(Collectors.toList());
        List<Deposit> deposits = depositRepository.getByIds(customerIds);
        Map<Long, List<Deposit>> collected = deposits.stream().collect(Collectors.groupingBy(Deposit::getUser_id));
        //customers vs those who have deposited
        double conversionRate = 0.0;
        if(!customers.isEmpty() && !collected.isEmpty()){
            conversionRate = (double) customers.size() / collected.size();
        }
        Double amountDeposited = deposits.stream().mapToDouble(Deposit::getAmount).sum();
        //List<Bet> bets = betRepository.findByIds(customerIds);
        //customerSize
        Map<String,Object> response = new HashMap<>();
        response.put("customers", customers.size());
        response.put("deposits", collected.size());
//        response.put("depositPerUser",collected);
        response.put("deposited",amountDeposited);
        response.put("conversion", conversionRate);
        return new GlobalResponse(response, true, false, "Conversion rate");

    }

    @Override
    public GlobalResponse getBetsPerSport(String id, String from, String to, Pageable pageable, String country) {
        String sport = getSportFromId(id);

            Timestamp start = appUtils.startOfDayTimestamp(from);
            Timestamp finish = appUtils.endOfDayTimestamp(to);
            logger.info("Get {} {} {} {} ", id, sport,start,finish);
            Page<Bet> bets = betRepository.getPaginatedBetsByDate(country,start,finish, pageable);
            List<Bet> betsData = new ArrayList<>();
            Map<String, Object> response = new HashMap<>();
            logger.info("Filtering.... size is {}",bets.getContent().size());
            if(!bets.isEmpty()){
                            for (Bet bet: bets.getContent()){
                logger.info("Inside");
                List<Picks> matched = bet.getPicks().stream().filter(pick -> pick.getSport().equalsIgnoreCase(sport)).collect(Collectors.toList());
                logger.info("finished");
                if(!matched.isEmpty()){
                    betsData.add(bet);
                }
                }

                response.put("data", betsData);
                response.put("currentPage", bets.getNumber());
                response.put("totalItems", bets.getTotalElements());
                response.put("totalPages", bets.getTotalPages());
                response.put("nextPage", bets.getNumber()+ 1);
                return new GlobalResponse(response,true,false,"Bets per sport");
            }else{
                return new GlobalResponse(null,false,true,"No Bets per sport");
            }


        }

    @Override
    public GlobalResponse unlock(Long id, String type) {
        Account account = accountRepository.findByOwnerId(id);
        if(account != null){
            if(type.equalsIgnoreCase("betting")){
                account.setBlocked(false);
            }else{
                account.setBlock_withdraw(false);
            }
            accountRepository.save(account);
            return new GlobalResponse(null,true,false,"user blocked successfully");
        }
        return new GlobalResponse(null,false,true,"No user found");
    }

    private String getSportFromId(String id) {
        String name;
         switch (id){
             case "2":
                name = "Basketball";
                break;
            case "3":
                name = "Baseball";
                break;

            case "4":
                name = "Ice Hockey";
                break;

            case "5":
                name = "Tennis";
                break;
            case "6":
                name = "Handball";
                break;
            case "10":
                name = "Boxing";
                break;
            case "21":
                name = "Cricket";
                break;
            case "12":
                name = "Rugby";
                break;
             default:
                 name = "Football";
        }

        return name;
    }


    private void topUpBonus(Account account, Integer amount) {
       Timestamp now = new Timestamp(System.currentTimeMillis());
        Bonus bonus = new Bonus(0L,account.getCustomer().getId(),false,now,"BONUS_JACKPOT","JACKPOT-"+account.getName(),amount,true);
        bonusRepository.save(bonus);
        double bonusBalance = account.getBonus() +(double) amount;
        account.setBonus( bonusBalance);
        accountRepository.save(account);
    }

    public static <T> void exportCsv(String fileName, HttpServletResponse response, List<T> respList, Class<T> reqClass) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + fileName + "\"");
        try {
            StatefulBeanToCsv<T> writer = new StatefulBeanToCsvBuilder<T>(response.getWriter())
                    .withQuotechar(CSVWriter.DEFAULT_QUOTE_CHARACTER)
                    .withSeparator(CSVWriter.DEFAULT_SEPARATOR)
                    .withOrderedResults(false)
                    .build();

            writer.write(respList);
        }catch (Exception e){
            throw new RuntimeException("Exception while Exporting csv file");
        }
    }

}
