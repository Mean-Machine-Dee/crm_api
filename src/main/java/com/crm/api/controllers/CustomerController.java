package com.crm.api.controllers;



import com.crm.api.api.models.Account;
import com.crm.api.dtos.BonusWinner;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.websocket.server.PathParam;

import java.security.Principal;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/crm/")
public class CustomerController {
    private static final String[] ALLOWED_MIME_TYPES = {"image/jpeg", "image/png", "application/pdf"};
    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "jpeg", "png", "pdf"};

    @Autowired
    CustomerService customerService;


    @GetMapping("clients")
    public GlobalResponse clients(

            @RequestParam(name = "page",required = false, defaultValue = "0") Integer page,
            @RequestParam(name = "size",required = false, defaultValue = "20") Integer size,
            @RequestParam(name = "phone", required = false, defaultValue = "all") String phone, Principal principal){

             Pageable pageable = PageRequest.of(page,size);

        return customerService.getClients(pageable, phone, principal);
    }

    @GetMapping("client/{id}")
    public GlobalResponse client(@PathVariable(name = "id") long id){
        return customerService.getClientDetails(id);
    }

    @GetMapping("client/payments/{id}")
    public GlobalResponse clientPayments(@PathVariable(name = "id") int id,
                                         @RequestParam(name = "size", defaultValue = "15", required = true) int size,
                                         @RequestParam(name = "page", defaultValue = "0", required = true) int page
                                         ){
        Pageable pageable = PageRequest.of(page,size);
        return customerService.getClientPayments(id,pageable);


    }

    @GetMapping("client/deposits/{id}")
    public GlobalResponse getDeposits(
            @PathVariable(name = "id") int id,
            @RequestParam(name = "size", defaultValue = "15", required = true) int size,
            @RequestParam(name = "page", defaultValue = "0", required = true) int page,
            @RequestParam(name = "type", defaultValue = "deposit", required = false) String type

    ){

        Pageable pageable = PageRequest.of(page,size);
        return customerService.getDeposits(id, pageable,type);
    }


    @GetMapping("client/bets/{id}")
    public GlobalResponse userBets(@PathVariable("id") long id,
                                   @RequestParam(name = "size", defaultValue = "15") int size,
                                   @RequestParam(name = "page", defaultValue = "0") int page
    ){
        Pageable pageable = PageRequest.of(page,size);
        return customerService.getUserBets(id, pageable);
    }


    @PostMapping("client/c2b")
    public GlobalResponse deposit(@RequestBody DepositRequest depositRequest, Principal principal){
        return customerService.clientDeposit(depositRequest,principal);
    }


    @PostMapping("client/b2c")
    public GlobalResponse withdraw(@RequestBody WithdrawRequest request, Principal principal){
        return customerService.b2c(request,principal);
    }

    @PostMapping("client/profile")
    public GlobalResponse profile( @RequestBody ProfileRequest request){
        return customerService.profile(request);
    }


    @PostMapping("tickets")
    public GlobalResponse createTicket(@RequestBody TicketRequest ticketDTO, Principal principal){
        return customerService.createTicket(ticketDTO,principal);
    }

    @PutMapping("tickets/{id}")
    public GlobalResponse updateTicket(@PathVariable int id, @RequestBody TicketRequest ticketDTO, Principal principal){
        return customerService.updateTicket(ticketDTO, id);
    }

    @GetMapping("tickets")
    public GlobalResponse getTickets(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
                                     @RequestParam(name = "size", defaultValue = "15", required = false) int size){
        Pageable pageable = PageRequest.of(page,size);
        return customerService.getTickets(pageable);
    }

    @DeleteMapping("tickets/{id}")
    public GlobalResponse deleteUser(@PathVariable long id){
        return customerService.deleteTicket(id);
    }

    @GetMapping("client/bet/{id}")
    public GlobalResponse betDetails(@PathVariable int id){
        return customerService.getBetPicks(id);
    }

    @PutMapping("user/{id}")
    public GlobalResponse verifyUser(@PathVariable(name = "id") long id){
        return customerService.verifyClient(id);
    }

    @PostMapping("settle/bulk")
    public GlobalResponse uploadAndSettleBets(@RequestParam("file") MultipartFile file, @RequestParam("type") String type){
        if(file.isEmpty()){
            return new GlobalResponse(null, false,true,"Upload file");
        }
        String filename = file.getOriginalFilename();
        if(filename == null || !filename.toLowerCase().endsWith(".csv")){
            return new GlobalResponse(null, false,true,"Upload csv file");
        }

        return customerService.settleBulk(file,type);


    }


    @PostMapping("bulk/deposits")
    public GlobalResponse bulkDeposit(@RequestParam("file") MultipartFile file){
        if(file.isEmpty()){
            return new GlobalResponse(null, false,true,"Upload file");
        }
        String filename = file.getOriginalFilename();
        if(filename == null || !filename.toLowerCase().endsWith(".csv")){
            return new GlobalResponse(null, false,true,"Upload csv file");
        }

        return customerService.bulkDeposit(file);


    }

    @GetMapping("agents")
    public GlobalResponse agents(@RequestParam(name = "page") int page, @RequestParam(name = "size") int size){
        Pageable pageable = PageRequest.of(page,size, Sort.by("created_at").descending());
        return customerService.agentsData(pageable);
    }

    @GetMapping("players")
    public GlobalResponse players(@RequestParam(name = "category") String category,@RequestParam(name = "country") String country, HttpServletResponse response){
        return customerService.getPlayers(category,country, response);
    }

    @GetMapping("conversion/rate")
    public GlobalResponse getConversion(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to, @RequestParam(name = "country") String country){
        return customerService.getRate(from,to,country);
    }

    @PostMapping("award/bonus")
    public GlobalResponse bonusWinners(@RequestBody BonusWinnersRequest winnersRequest){
        return customerService.awardBonus(winnersRequest);
    }

    @GetMapping("bets/sport/{id}")
    public GlobalResponse betsPerSport(@PathVariable(name = "id") String id,@RequestParam(name = "page") int page, @RequestParam(name = "size") int size,
                                       @RequestParam(name = "from") String from, @RequestParam(name = "to") String to,@RequestParam(name = "country") String country){
        Pageable pageable = PageRequest.of(page,size);
        return customerService.getBetsPerSport(id,from,to,pageable,country);
    }

    @GetMapping("unlock/user/{id}")
    public GlobalResponse unlock(@PathVariable(name = "id") Long id,@RequestParam(name = "type") String type){
        return customerService.unlock(id,type);
    }



}
