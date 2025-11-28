package com.crm.api.services.impl;

import com.crm.api.api.models.Bet;
import com.crm.api.api.models.OddBooster;
import com.crm.api.api.models.SmsDelivery;
import com.crm.api.api.repository.BetRepository;
import com.crm.api.api.repository.OddBoosterRepository;
import com.crm.api.api.repository.SmsDeliverlyRepository;
import com.crm.api.crm.models.*;
import com.crm.api.crm.repository.*;
import com.crm.api.dtos.*;
import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.sdk.entities.SrCompetition;
import com.crm.api.sdk.entities.SrSport;
import com.crm.api.sdk.entities.SrTournament;
import com.crm.api.sdk.repositories.SrCategoryRepository;
import com.crm.api.sdk.repositories.SrCompetitionRepository;
import com.crm.api.sdk.repositories.SrSportRepository;
import com.crm.api.sdk.repositories.SrTourmentRepository;
import com.crm.api.services.BookieService;
import com.crm.api.utils.AppUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookieServiceImpl implements BookieService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    @Value("${file.upload-dir}")
    private String uploadDir;
    @Autowired
    private SrCompetitionRepository competitionRepository;

    @Autowired
    private OddBoosterRepository oddBoosterRepository;

    @Autowired
    private CampaignRepository campaignRepository;

    @Autowired
    private SrCategoryRepository categoryRepository;

    @Autowired
    private SlideRepository slideRepository;

    @Autowired
    JackpotRepository jackpotRepository;

    @Autowired
    SrSportRepository sportRepository;
    @Autowired
    JackpotMatchRepository jackpotMatchRepository;

    @Autowired
    UserRepository userRepository;
    @Autowired
    private BetRepository betRepository;

    @Autowired
    private AgentActivityRepository agentActivityRepository;


    @Autowired
    private SmsDeliverlyRepository smsDeliverlyRepository;

    AppUtils appUtils = new AppUtils();

    @Autowired
    private SrTourmentRepository srTourmentRepository;
    @Override
    public GlobalResponse searchTournament(TournamentSearchRequest tournament, Pageable pageable) {
        Timestamp now = appUtils.getBurundiTime();
        GlobalResponse globalResponse = null;
        log.info("tourn {}",tournament);
        Page<SrTournament> competitions = srTourmentRepository.findByNameIgnoreCaseContaining(tournament.getTournament(), pageable);
        log.info("Tournaments are {}", competitions.getContent());
       if(!competitions.isEmpty()){
           List<Long> ids = competitions.stream().map(SrTournament::getId).collect(Collectors.toList());
           Page<SrCompetition> fixtures = competitionRepository.findByTournamentIdList(ids,now,pageable);
           log.info("Fixtures are {}",fixtures.getContent());
           if(!fixtures.isEmpty()){
               List<FixtureDTO> games = fixtures.getContent().stream().map(fixture -> FixtureDTO.builder()
                               .id(fixture.getId())
                               .sport(fixture.getSport().getName())
                               .name(fixture.getName())
                               .country(fixture.getCategory().getCountry())
                               .scheduled(fixture.getScheduled())
                               .build())
                       .collect(Collectors.toList());
               Map<String, Object> response = appUtils.dataFormatter(games, fixtures.getNumber(), fixtures.getTotalElements(), fixtures.getTotalPages());
               globalResponse = new GlobalResponse(response,true,false, tournament +" games" );
           }else {
               globalResponse = new GlobalResponse(null, false, true, tournament + " has no active games");
           }
         }else{
           globalResponse = new GlobalResponse(null,false,true, tournament.getTournament() +" games not found" );
       }
       return globalResponse;
    }

    @Override
    public GlobalResponse highlightGames(TournamentRequest tournamentRequest) {
       try{
                String json = objectMapper.writeValueAsString(tournamentRequest);
               Map<String,Object> map = new HashMap<>();
               map.put("type","games");
               map.put("data",json);
               String data = objectMapper.writeValueAsString(map);
               String results = sendPostRequest(data);
               log.info("results are {}", results);

//           if(tournamentRequest.getType().equalsIgnoreCase("featured")){
//               tournamentRequest.getIds().forEach(game->{
//                   log.info("To update is {} and {}", game.getPriority(), game.getId());
//                   try {
//                       String json = objectMapper.writeValueAsString(tournamentRequest);
//                       sendPostRequest(json);
//                   } catch (JsonProcessingException e) {
//                      log.info("error updating games {}", e.getMessage());
//                   }
//
//               });
//
//           }
//
//           if(tournamentRequest.getType().equalsIgnoreCase("highlights")){
//               tournamentRequest.getIds().forEach(game->{
//                   log.info("To update highlights is {} and {}", game.getPriority(), game.getId());
//                   sendPostRequest();
//               });
//
//           }
           return new GlobalResponse(null,true,false, "Games updated");
       }catch (Exception e){
           log.info("Error on updating fixtures {}",e.getMessage());
           return new GlobalResponse(null,false,true, "Error updating matches" );

       }


    }


    @Override
    @Transactional(transactionManager = "apiDbTransactionManager")
    public GlobalResponse bets(Pageable pageable, String code, String country, String type, String to, String from) {

        Timestamp timestampFrom = appUtils.startOfDayTimestamp(from);
        Timestamp timestampEnd = appUtils.endOfDayTimestamp(to);
        if(type.equalsIgnoreCase("landing")){
            timestampFrom = appUtils.startOfToday();
            timestampEnd = appUtils.getBurundiTime();
        }
        Page<Bet> paginatedBets = null;
        if(code.equalsIgnoreCase("all")){
            paginatedBets = betRepository.getPaginatedBets(country,"normal",timestampFrom,timestampEnd,pageable);
        }else{
            paginatedBets = betRepository.findByBetCodeAndCountry(country,code,timestampFrom,timestampEnd, pageable);
        }

        log.info("Got Data {}", paginatedBets);

        if(!paginatedBets.isEmpty()){
            Map<String,Object> response = appUtils.dataFormatter(paginatedBets.getContent(),paginatedBets.getNumber(),paginatedBets.getTotalElements(),paginatedBets.getTotalPages());
            return new GlobalResponse(response,true,false, "Bets list" );
        }
        return new GlobalResponse(null,false,true, "Error getting bets" );
    }

    @Transactional
    @Override
    public GlobalResponse tournaments(Pageable pageable, String country) {
        Page<SrTournament> tournaments;
        if(country.equalsIgnoreCase("all")){
            tournaments = srTourmentRepository.findAll(pageable);
        }else{
            log.info("searching for {}", country);
            tournaments = srTourmentRepository.findByNameLike(country+"%", pageable);
            log.info("searching for {}", tournaments.getTotalElements());
        }
       if(!tournaments.isEmpty()){
           List<TournamentDTO> tournamentDTOS = tournaments.getContent().stream()
                   .map(tournament-> TournamentDTO.builder()
                           .featured(tournament.getFeatured() == null ? 0 : tournament.getFeatured())
                           .tournament(tournament.getName())
                           .id(tournament.getId())
//                           .country(tournament.getCategory().getCountry())
                           .sport(tournament.getSport().getName())
                           .build())
                   .collect(Collectors.toList());
          Map<String,Object> response =  appUtils.dataFormatter(tournamentDTOS, tournaments.getNumber(),tournaments.getTotalElements(),tournaments.getTotalPages());
           return new GlobalResponse(response,true,false, "Competitions" );
       }
        return new GlobalResponse(null,false,true, "No competitions found" );
    }

    @Override
    public GlobalResponse updateTournament(CompetitionUpdateRequest request) {
        Optional<SrTournament> competition =  srTourmentRepository.findById(request.getId());

        if(competition.isPresent()){
       try{
           String json = objectMapper.writeValueAsString(request);
           Map<String,Object> map = new HashMap<>();
           map.put("type","tournament");
           map.put("data",json);
           String data = objectMapper.writeValueAsString(map);
           String results = sendPostRequest(data);
           log.info("results are {}", results);
       }catch (Exception e){
        log.info("Error sending post data {} --- {} ",e);
       }

            return new GlobalResponse(null,true,false, "Competition updated successfully" );
        }
        return new GlobalResponse(null,false,true, "Competition with id "+ request.getId() + " not found" );
    }

    private String sendPostRequest(String json) {
        String url = "https://games.rahisibet.com/api/update/games";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
        // Process the response
        if (response.getStatusCode().is2xxSuccessful()) {
            return "Request successful. Response body: " + response.getBody();
        } else {
            return "Request failed. Status code: " + response.getStatusCode();
        }
    }

    @Override
    public GlobalResponse getSlides(Pageable pageable) {
        Page<Slide> slides = slideRepository.findAll(pageable);
        if(!slides.isEmpty()){
          Map<String, Object>  response = appUtils.dataFormatter(slides.getContent(), slides.getNumber(), slides.getTotalElements(), slides.getTotalPages());
            return new GlobalResponse(response,true,false, "Slides");
        }
        return new GlobalResponse(null,false,true, "Slides not found" );
    }

    @Override
    public GlobalResponse createSlide(MultipartFile file, SlideRequest slideRequest) {
        try{
            String name = file.getOriginalFilename();
            if(name != null){
                name = UUID.randomUUID() + "." +name.substring(name.lastIndexOf(".") + 1);
            }
            saveImage(file, name);
            String slideId = UUID.randomUUID().toString().substring(0,10);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            StringBuilder sb = new StringBuilder();
            StringBuilder isos = new StringBuilder();
            for (String s: slideRequest.getCategory()){
                sb.append(s).append(",");
            } for (String s: slideRequest.getIso()){
                isos.append(s).append(",");
            }
            Slide slide = new Slide(0L,slideId,name,now,now,false,slideRequest.getLang(),sb.toString(),isos.toString());
            slideRepository.save(slide);
            return new GlobalResponse(null,true,false, "Slide uploaded successfully" );
        }catch (Exception e){
            log.error("upload error {}", e.getMessage());
            return new GlobalResponse(null,false,true, "Image upload failed" );
        }
    }

    @Override
    public GlobalResponse updateSlide(long id) {

        Optional<Slide> slide = slideRepository.findById(id);
        if(slide.isPresent()){
            boolean active =  slide.get().isActive();
            slide.get().setActive(!active);
            slideRepository.save(slide.get());
            return new GlobalResponse(null,true,false, "Slide updated succesfully" );
        }
        return new GlobalResponse(null,false,true, "Slide update failed" );
    }



    @Override
    public ResponseEntity<Resource> getImage(String filename) {
     try{
         Path filePath = Paths.get(uploadDir).resolve(filename);
         Resource resource = new UrlResource(filePath.toUri());
         if(resource.exists()){
             return ResponseEntity.ok()
                     .contentType(MediaType.IMAGE_JPEG)
                     .body(resource);
         }else{
             return ResponseEntity.notFound().build();
         }
     } catch (MalformedURLException e) {
        log.info("Exception thrown {}", e.getMessage());
         return ResponseEntity.notFound().build();
     }
    }

    @Override
    public GlobalResponse deleteSlide(long id) {
        //TODO::delete file in server
        try{
            slideRepository.deleteById(id);
            return new GlobalResponse(null,true,false, "Slide updated succesfully" );
        }catch (Exception e){
            return new GlobalResponse(null,false,true, "Error deleting slide");
        }

    }

    @Override
    public GlobalResponse getRiskyBets(Pageable pageable, String country) {
        Page<Bet> bets = betRepository.getRiskyBets(country,pageable);
        if(!bets.isEmpty()){
          Map<String, Object> response = appUtils.dataFormatter(bets.getContent(),bets.getNumber(),bets.getTotalElements(),bets.getTotalPages());
          return new GlobalResponse(response,true,false, "bets");
        }
        return new GlobalResponse(null,false,true, "No bets found");
    }

    @Override
    public GlobalResponse settleBet(long id) {
        Optional<Bet> bet = betRepository.findById(id);
        if(bet.isPresent()){
            bet.get().setClean(true);
            betRepository.save(bet.get());
            return new GlobalResponse(null,true,false, "bet settled");
        }
        return new GlobalResponse(null,true,false, "No bet found");
    }

    @Override
    public GlobalResponse updateTopLeagues(LeaguePriority leagues) {
        try{
            String json = objectMapper.writeValueAsString(leagues);
            Map<String,Object> map = new HashMap<>();
            map.put("type","leagues");
            map.put("data",json);
            String data = objectMapper.writeValueAsString(map);
            String results = sendPostRequest(data);
            log.info("results are {}", results);
            return new GlobalResponse(null, true, false, "Games updated successfully");
        }catch (Exception e){
            return new GlobalResponse(null, false, true, e.getMessage());
        }
       }



    private String saveImage(MultipartFile file, String name) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if(!Files.exists(uploadPath)){
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(name);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }


//    @Override
//    public GlobalResponse searchBet(int code) {
//        Optional<Bet> bet = betRepository.findByBetCode(code);
//        if(bet.isPresent()){
//            return new GlobalResponse(bet,true,false, "Bet" );
//        }
//        return new GlobalResponse(null,false,true, "Bet not found" );
//    }



    @Override
    public GlobalResponse getBonusBets(String country, String from, String to, Pageable pageable) {
        Timestamp start = appUtils.startOfDayTimestamp(from);
        Timestamp end = appUtils.endOfDayTimestamp(to);
        Page<Bet> bets = betRepository.getBonusBetsPerCountry("bonus", country,start, end,pageable);
        if(bets != null){
//            Map<String, List<Bet>> map = bets.getContent().stream().collect(Collectors.groupingBy(Bet::getIso));
            Map<String, Object> response = appUtils.dataFormatter(bets.getContent(),bets.getNumber(),bets.getTotalElements(),bets.getTotalPages());
            return  new GlobalResponse(response,true,false,"bonus Bets");
        }else{
            return  new GlobalResponse(null,true,false,"No bonus Bets");
        }
    }



    @Override
    public GlobalResponse setPayments(PaymentSettingRequest paymentSettingRequest) {
        return null;
    }

    @Override
    public GlobalResponse createCampaign(MultipartFile file, CampaignRequest campaignRequest, Principal principal) {
        User user = userRepository.findByUsername(principal.getName()).orElseThrow(() -> new RuntimeException("User not found"));
        log.info("Campaign data parsed");
        try{
            String name = file.getOriginalFilename();
            if(name != null){
                name = UUID.randomUUID() + "." + name.substring(name.lastIndexOf(".") + 1);
            }
            LocalDateTime expiryDate = campaignRequest.getExpiryDay() != null ? appUtils.parseDate(campaignRequest.getExpiryDay()) : null;

            saveImage(file, name);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            Campaign campaign = new Campaign(0L,1,campaignRequest.getDescription(),false,now,now,
                    appUtils.parseDate(campaignRequest.getActionDay()),campaignRequest.getType(),campaignRequest.getCta(),campaignRequest.getLang(),
                    name,
                    campaignRequest.getCtaLink(),
                    campaignRequest.getHeader(),
                    campaignRequest.getSubHeader(),expiryDate);
            campaignRepository.save(campaign);
            AgentActivity agentActivity = new AgentActivity(0L,"Created campaign " + campaign.getId(),"n/a",now,user.getId());
            agentActivityRepository.save(agentActivity);
            return new GlobalResponse(null,true,false, "Campaign uploaded successfully" );
        }catch (Exception e){
            log.error("upload error {}", e.getMessage());
            return new GlobalResponse(null,false,true, "Image upload failed" );
        }
    }

    @Override
    public GlobalResponse activate(long id, String status) {
        Optional<Campaign> campaign = campaignRepository.findById(id);
        if(campaign.isPresent()){
            campaign.get().setStatus(status.equals("active"));
            campaignRepository.save(campaign.get());
        }
        return new GlobalResponse(null,true,false, "Campaign updated successfully" );
    }

    @Override
    public GlobalResponse getSmses(String origin, String from, String to, Pageable pageable) {
        Timestamp start = appUtils.startOfDayTimestamp(from);
        Timestamp end = appUtils.endOfDayTimestamp(to);

        log.info("Searching for smses {} between {} and {} --- {} --- {}", origin,start,end,from,to);
        Page<SmsDelivery> data = smsDeliverlyRepository.getSmses(origin,start, end,pageable);
        if(data.hasContent()){
            Map<String, Object> mapped = appUtils.dataFormatter(data.getContent(), data.getNumber(), data.getTotalElements(), data.getTotalPages());
            return new GlobalResponse(mapped,true,false,"Smeses");
        }
        return new GlobalResponse(null,false,true,"No data for period specified");

    }

    @Override
    public GlobalResponse aggregateSmses(String from, String to) {
        Timestamp start = appUtils.startOfDayTimestamp(from);
        Timestamp end = appUtils.endOfDayTimestamp(to);
        Long ats = smsDeliverlyRepository.aggregateSmses("ATS",start, end);
        Long lumitel = smsDeliverlyRepository.aggregateSmses("Lumitel",start, end);
        Map<String, Object> response = new HashMap<>();
        response.put("ats", ats);
        response.put("lumitel", lumitel);
        return new GlobalResponse(response, true, false, "Sms Agrregates");
    }

    @Override
    public GlobalResponse createJackpot(JackpotRequest jackpotRequest) {
        if(jackpotRequest.getGameIds().size() < 17){
            return new GlobalResponse(null, false, true, "Games must be 17");
        }
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        Timestamp starts = appUtils.startOfDayTimestamp(jackpotRequest.getStarts());
        Timestamp ends = appUtils.endOfDayTimestamp(jackpotRequest.getCompletes());
        Jackpot jackpot = Jackpot.builder()
                .jackpotCode(UUID.randomUUID().toString().substring(0,10))
                .status(true)
                .starts(starts)
                .completes(ends)
                .createdAt(timestamp)
                .build();
        Jackpot created = jackpotRepository.save(jackpot);
        for (FeaturedDTO game: jackpotRequest.getGameIds()){
            JackpotGame match = JackpotGame.builder()
                    .jackpotId(created.getId())
                    .matchId(game.getId())
                    .createdAt(timestamp)
                    .updatedAt(timestamp)
                    .build();
            jackpotMatchRepository.save(match);
        }

        return new GlobalResponse(null, true, false, "Created jackpot");
    }

    @Override
    public GlobalResponse wonPerCountry(String country) {
        Timestamp start = appUtils.startOfToday();
        Timestamp stop = appUtils.getStopDate();
        List<Bet> bets;
        if(country.equals("all")){
            bets = betRepository.getWonBetsByDate(start,stop);
        }else {
            bets = betRepository.getWonBetsByDateAndCountry(country,start,stop);
        }

        log.info("Searching for {} country{}", start, country);
       if(!bets.isEmpty()){
           Map<String, List<Double>> data = bets.stream().collect(Collectors.groupingBy(
                   Bet::getAccount,
                   Collectors.mapping(
                           bet -> Arrays.asList((double) bet.getAmount(), bet.getPayout()),
                           Collectors.reducing(
                                   Arrays.asList(0.0, 0.0),
                                   (acc, elem) -> Arrays.asList(acc.get(0) + elem.get(0), acc.get(1) + elem.get(1))
                           )
                   )
           ));
           return new GlobalResponse(data, true, false, "Bets Per Country");
       }else{
           return new GlobalResponse(null, false, true, "No bets Found");

       }


    }

    @Override
    public GlobalResponse campaigns(Pageable pageable) {
        Page<Campaign> data = campaignRepository.findCampaigns(pageable);
        if(data.hasContent()){
            Map<String, Object> response = appUtils.dataFormatter(data.getContent(), data.getNumber(), data.getTotalElements(), data.getTotalPages());
            return new GlobalResponse(response,true,false,"Campaigns");
        }else{
            return new GlobalResponse(null,false,false,"No Campaigns found");
        }
    }

    @Override
    public GlobalResponse delete(long id) {
        Optional<Campaign> campaign = campaignRepository.findById(id);
        if(campaign.isPresent()){
            campaignRepository.delete(campaign.get());
            return new GlobalResponse(null,true,false,"Campaign Deleted");
        }else{
        return new GlobalResponse(null,false,false,"No Campaign found");
        }
    }

    @Override
    public GlobalResponse editCampaign(CampaignRequest campaignRequest, long id) {
        Optional<Campaign> campaign = campaignRepository.findById(id);
        if(campaign.isPresent()){
            try{

                Timestamp now = new Timestamp(System.currentTimeMillis());
                Campaign exists = campaign.get();
                exists.setDescription(campaignRequest.getDescription());
                exists.setDispatchDate(appUtils.parseDate(campaignRequest.getActionDay()));
                exists.setExpiryDate(appUtils.parseDate(campaignRequest.getExpiryDay()));
                exists.setCta(campaignRequest.getCta());
                exists.setLang(campaignRequest.getLang());
                exists.setStatus(campaignRequest.getStatus().equals("active"));
                exists.setHeader(campaignRequest.getHeader());
                exists.setSubHeader(campaignRequest.getSubHeader());
                exists.setCtaLink(campaignRequest.getCtaLink());
                campaignRepository.save(exists);
                return new GlobalResponse(null,true,false, "Campaign updated successfully" );
            }catch (Exception e){
                log.error("upload error {}", e.getMessage());
                return new GlobalResponse(null,false,true, "Image upload failed" );
            }
        }else{
            return new GlobalResponse(null,false,true, "No Campaign found" );
        }
    }

    @Override
    public GlobalResponse highlightSingleGame(long id, int priority) {
        SrCompetition competition = competitionRepository.findGame(id);
        if(competition != null){
//            competitionRepository.highlightSingle(priority,id);
            return new GlobalResponse(null,true,false, "Match updated successfully" );
        }
        return new GlobalResponse(null,false,true, "No Game found" );
    }



    @Override
    @Transactional(transactionManager = "apiDbTransactionManager")
    public GlobalResponse getJackpotBets(Pageable pageable, String code, String country, String type, String to, String from) {
        Timestamp timestampFrom = appUtils.startOfDayTimestamp(from);
        Timestamp timestampEnd = appUtils.endOfDayTimestamp(to);
        if(type.equalsIgnoreCase("landing")){
            timestampFrom = appUtils.startOfToday();
            timestampEnd = appUtils.getBurundiTime();
        }
        Page<Bet> paginatedBets = null;
        log.info("Getting all jackpots {}", code);
        if(code.equalsIgnoreCase("all")){
            paginatedBets = betRepository.getPaginatedJackpotBets("jackpot",timestampFrom,timestampEnd,pageable);
        }else{
            paginatedBets = betRepository.findByBetCode(country,code, pageable);
        }

        if(!paginatedBets.isEmpty()){
            Map<String,Object> response = appUtils.dataFormatter(paginatedBets.getContent(),paginatedBets.getNumber(),paginatedBets.getTotalElements(),paginatedBets.getTotalPages());
            return new GlobalResponse(response,true,false, "Bets list" );
        }
        return new GlobalResponse(null,false,true, "Error getting bets" );
    }


    @Override
    public GlobalResponse jackpots() {
        List<Jackpot> jackpots = jackpotRepository.findAll(Sort.by("createdAt").descending());
        return new GlobalResponse(jackpots, true,false,"Jackpots");
    }

    @Override
    public GlobalResponse activateJackpot(Long id, String status) {
        Optional<Jackpot> byId = jackpotRepository.findById(id);
        if(byId.isPresent()){
            Jackpot jackpot =  byId.get();
            jackpot.setStatus(status.equals("active"));
            jackpotRepository.save(jackpot);
            return new GlobalResponse(null, true,false,"Jackpot " + byId.get().getJackpotCode() + status + " successfully");
        }
        return new GlobalResponse(null, false,true,"Jackpot not found");
    }

    @Override
    public GlobalResponse getGames(long id) {
        Optional<Jackpot> jackpot = jackpotRepository.findById(id);
        List<JackpotGameDTO> dtos = new ArrayList<>();
        if(jackpot.isPresent()){
            Jackpot jp = jackpot.get();
            for (JackpotGame game : jp.getGames()) {
                SrCompetition fixture = competitionRepository.findGame(game.getMatchId());
                if(fixture != null){
                    JackpotGameDTO dto = JackpotGameDTO
                            .builder()
                            .country(fixture.getCategory().getCountry())
                            .id(fixture.getId())
                            .name(fixture.getName())
                            .scheduled(fixture.getScheduled())
                            .build();
                    dtos.add(dto);
                }

            }
        }
        Map<String, Object> stringObjectMap = appUtils.dataFormatter(dtos, 1, dtos.size(), 1);
        return new GlobalResponse(stringObjectMap, true,false,"Games");
    }

    @Override
    public GlobalResponse deleteJackpot(long id) {
        Optional<Jackpot> jackpot = jackpotRepository.findById(id);
        if(jackpot.isPresent()){
            Jackpot jp = jackpot.get();
            jackpotMatchRepository.deleteAll(jp.getGames());
            jackpotRepository.delete(jp);
        }

        return new GlobalResponse(null, true,false,"Jackpot deleted");
    }

    @Override
    public GlobalResponse getSports() {
        List<SrSport> sports = sportRepository.findSports();
        return new GlobalResponse(sports , true,false,"All Sports");
    }

    @Override
    public GlobalResponse getTournamentGames(long id) {
        Timestamp now = appUtils.getBurundiTime();
        List<SrCompetition> games = competitionRepository.findByTournamentId(id, now);
        if(games != null){
         return new GlobalResponse(games,true,false, "Games for tournament" );
        }
        return new GlobalResponse(null,false,true, "No games for tournament "+ id );
    }

    @Override
    public GlobalResponse getTournaments(SportTournament sportTournament) {
        List<TournamentSport> tournaments = srTourmentRepository.findAllBySportAndCategory(sportTournament.getSportId(),sportTournament.getCategoryId());
        if(!tournaments.isEmpty()){
            return new GlobalResponse(tournaments,true,false, "Tournaments" );
        }
        return new GlobalResponse(null,false,true, "No tournaments found ");

    }

    @Override
    public GlobalResponse removeGame(long id) {
        SrCompetition competition = competitionRepository.findGame(id);
        if(competition != null){
            competition.setIsHighLight(0);
            competition.setPriority(0);
            competitionRepository.save(competition);
            return new GlobalResponse(null,true,false, "Game updated" );
        }
        return new GlobalResponse(null,false,true, "Game not found");
    }

    @Override
    public GlobalResponse setLiveTournament(long id, int priority) {
       Optional<SrTournament> tournament = srTourmentRepository.findById(id);
       if(tournament.isPresent()){
           Map<String,Object> map = new HashMap<>();
           Map<String,Object> data = new HashMap<>();
           data.put("id",id);
           data.put("priority",priority);
           map.put("type","liveTournament");
           map.put("data",data);
         try{
             String info = objectMapper.writeValueAsString(map);
             String results = sendPostRequest(info);
             log.info("results are {}", results);
         } catch (JsonProcessingException e) {
             throw new RuntimeException(e);
         }
           return new GlobalResponse(null,true,false, "Tournament updated" );
       }
        return new GlobalResponse(null,false,true, "Tournament not found");
    }

    @Override
    public GlobalResponse boostOdds(OddBoostRequest request) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        OddBooster oddBooster = new OddBooster(0L, false, request.getPercentage(), now);
        oddBoosterRepository.save(oddBooster);
        return new GlobalResponse(null, true, false, "Odd Boost Created Successfully");
    }

    @Override
    public GlobalResponse activateBoostOdds(long id) {
        Optional<OddBooster> booster = oddBoosterRepository.findById(id);
        if(booster.isPresent()){
            OddBooster oddBooster = booster.get();
            oddBooster.setStatus(!oddBooster.isStatus());
            oddBoosterRepository.save(oddBooster);
        }
        return new GlobalResponse(null, true, false, "Odd Boost updated Successfully");
    }

    @Override
    public GlobalResponse boostedOdds() {
        List<OddBooster> boosters = oddBoosterRepository.findAll();
        return new GlobalResponse(boosters, true, false, "Odd Boost updated Successfully");
    }


}
