package com.crm.api.controllers;

import com.crm.api.crm.repository.JackpotMatchRepository;
import com.crm.api.crm.repository.JackpotRepository;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.BookieService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@CrossOrigin(maxAge = 3600, origins = "*")
@RequestMapping("/api/bookie/")
public class BookieController {

    @Autowired
    private BookieService bookieService;


    @PostMapping("tournament")
    public GlobalResponse getTournament(@RequestBody TournamentSearchRequest tournament, @RequestParam(name = "page",defaultValue = "0") int page,
                                        @RequestParam(name = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return bookieService.searchTournament(tournament, pageable);
    }

    @GetMapping("sports")
    GlobalResponse getSports(){
        return bookieService.getSports();
    }

    @PostMapping("country/tournaments")
    public GlobalResponse getGames(@RequestBody SportTournament sportTournament){
        log.info("Tournaments sport search {}", sportTournament);
        return bookieService.getTournaments(sportTournament);
    }

    @GetMapping("tournament/games/{id}")
    public GlobalResponse getGames(@PathVariable long id){
        return bookieService.getTournamentGames(id);
    }

    @GetMapping("tournament/remove/{id}")
    public GlobalResponse removeGames(@PathVariable long id){
        return bookieService.removeGame(id);
    }

    @PostMapping("update/games")
    public GlobalResponse setGames(@RequestBody TournamentRequest tournamentRequest){
        return bookieService.highlightGames(tournamentRequest);
    }

    @GetMapping("bets")
    public GlobalResponse bets(@RequestParam(name = "page",defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "15") int size,
                               @RequestParam(name = "code",defaultValue = "all") String code){
        Pageable pageable = PageRequest.of(page,size);
        return bookieService.bets(pageable,code);
    }

    @GetMapping("jackpot/bets")
    public GlobalResponse jackpotBets(@RequestParam(name = "page",defaultValue = "0") int page,
                               @RequestParam(name = "size", defaultValue = "15") int size,
                               @RequestParam(name = "code",defaultValue = "all") String code){
        Pageable pageable = PageRequest.of(page,size);
        return bookieService.getJackpotBets(pageable,code);
    }

//    @PostMapping("search/bet/{code}")
//    public GlobalResponse getBet(@PathVariable int code){
//        return bookieService.searchBet(code);
//    }

    @GetMapping("tournaments")
    public GlobalResponse getTournaments(@RequestParam(name="page", defaultValue = "0") int page,
                                         @RequestParam(name = "size", defaultValue = "20") int size,
                                         @RequestParam(name = "tournament", defaultValue = "all") String country){
        Pageable pageable = PageRequest.of(page,size);
        return bookieService.tournaments(pageable,country);
    }

    @PostMapping("tournaments")
    public GlobalResponse updateTournament(@RequestBody CompetitionUpdateRequest request){
        return bookieService.updateTournament(request);
    }

    @PatchMapping("tournaments/game/{id}")
    public GlobalResponse updateSingleGame(@PathVariable(name = "id") long id, @RequestParam(name = "priority") int priority){
        return  bookieService.highlightSingleGame(id,priority);
    }


    @PostMapping("jackpot")
    public GlobalResponse createJackpot(@RequestBody JackpotRequest jackpotRequest){
        return bookieService.createJackpot(jackpotRequest);
    }

    @GetMapping("jackpot")
    public GlobalResponse jackpots(){
        return bookieService.jackpots();
    }

    @GetMapping("jackpot/{id}")
    public GlobalResponse jackpotGames(@PathVariable(name = "id") long id){
        return bookieService.getGames(id);
    }


    @DeleteMapping("jackpot/{id}")
    public GlobalResponse deleteJackpot(@PathVariable(name = "id") long id){
        return bookieService.deleteJackpot(id);
    }

    @PatchMapping("jackpot/{id}")
    public GlobalResponse activate(@PathVariable(name = "id") long id,@RequestParam(name = "status") String status){
        return bookieService.activateJackpot(id,status);
    }

    @GetMapping("slides")
    public GlobalResponse slides(@RequestParam(name = "page", defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size);
        return  bookieService.getSlides(pageable);
    }
    @PostMapping(value = "slides")
    public GlobalResponse createSlide(@RequestParam("file") MultipartFile file, @RequestParam("model") SlideRequest slideRequest){
        return bookieService.createSlide(file,slideRequest);
    }

    @GetMapping(value = "slides/{id}")
    public GlobalResponse updateSlide(@PathVariable(name = "id") long id){
        return  bookieService.updateSlide(id);
    }

    @GetMapping(value = "slides/image/{filename}")
    public ResponseEntity<Resource> getSlide(@PathVariable(name = "filename") String filename){
        return  bookieService.getImage(filename);
    }

    @DeleteMapping(value = "slides/{id}")
    public GlobalResponse deleteSlide(@PathVariable(name = "id") long id){
        return bookieService.deleteSlide(id);
    }

        @GetMapping("risky/bets")
        public GlobalResponse risky(@RequestParam(name = "page",defaultValue = "0") int page, @RequestParam(name = "size", defaultValue = "15") int size){
            Pageable pageable = PageRequest.of(page,size);
            return bookieService.getRiskyBets(pageable);
        }

        @GetMapping("settle/bet/{id}")
        public GlobalResponse settleBet(@PathVariable(name = "id") long id){
            return bookieService.settleBet(id);
        }

    @PostMapping("tournaments/priority")
        public GlobalResponse updatePriority(@RequestBody LeaguePriority leagues){
            return bookieService.updateTopLeagues(leagues);
    }

    @GetMapping("bonus/bets")
    public GlobalResponse bonusBets( @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "15") int size,
                                     @RequestParam(name = "from", defaultValue = "0") String from,
                                     @RequestParam(name = "to", defaultValue = "15") String to,
                                     @RequestParam(name = "country", defaultValue = "BI") String country
    ){
        Pageable pageable = PageRequest.of(page,size, Sort.by("created_at").descending());
            return bookieService.getBonusBets(country,from, to,pageable);
    }

    @PostMapping("change/payments")
    public GlobalResponse changePayments(@RequestBody PaymentSettingRequest paymentSettingRequest){
        return bookieService.setPayments(paymentSettingRequest);
    }

    @PostMapping(value = "campaign")
    public GlobalResponse createCampaign(@RequestParam("file") MultipartFile file, @RequestParam("model") CampaignRequest campaignRequest){
       log.info("Campaign is {}", campaignRequest);
         return bookieService.createCampaign(file,campaignRequest);

    }

    @GetMapping("campaign")
    public GlobalResponse Campaigns( @RequestParam(name = "page", defaultValue = "0") int page,
                                     @RequestParam(name = "size", defaultValue = "15") int size){
        Pageable pageable = PageRequest.of(page,size,Sort.by("created_at").descending());
        return  bookieService.campaigns(pageable);
    }

    @PutMapping("campaign/{id}")
    public GlobalResponse editCampaign(@PathVariable(name = "id") long id,@RequestParam("model") CampaignRequest campaignRequest){
        return bookieService.editCampaign(campaignRequest,id);
    }


    @PatchMapping("campaign/{id}")
    public GlobalResponse manageCampaign(@PathVariable(name = "id") long id, @RequestParam(name = "status") String status){
        return  bookieService.activate(id,status);
    }

    @DeleteMapping("campaign/{id}")
    public GlobalResponse deleteCampaign(@PathVariable(name = "id") long id){
        return  bookieService.delete(id);
    }

    @GetMapping("/smses")
    public GlobalResponse smses(
            @RequestParam(name = "origin") String origin,
            @RequestParam(name = "from") String from,
            @RequestParam(name = "to") String to,
            @RequestParam(name = "page") int page, @RequestParam(name = "size") int size
            ){
        Pageable pageable = PageRequest.of(page,size,Sort.by("created_at").descending());

        return bookieService.getSmses(origin, from,to, pageable);
    }


    @GetMapping("/sms/report")
    public GlobalResponse aggregateSmes(@RequestParam(name = "from") String from, @RequestParam(name = "to") String to) {
        return bookieService.aggregateSmses(from,to);
    }

    @GetMapping("/country/bets")
    public GlobalResponse betsPerCountry(@RequestParam(name = "country", defaultValue = "BI") String country){
        return bookieService.wonPerCountry(country);
    }


}
