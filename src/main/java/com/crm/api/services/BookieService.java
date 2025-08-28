package com.crm.api.services;


import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface BookieService {
    GlobalResponse searchTournament(TournamentSearchRequest tournament, Pageable pageable);

    GlobalResponse highlightGames(TournamentRequest tournamentRequest);

    GlobalResponse bets(Pageable pageable,String code);

    GlobalResponse tournaments(Pageable pageable,String country);

    GlobalResponse updateTournament(CompetitionUpdateRequest request);

    GlobalResponse getSlides(Pageable pageable);

    GlobalResponse createSlide(MultipartFile file, SlideRequest slideRequest);

    GlobalResponse updateSlide(long id);

    ResponseEntity<Resource> getImage(String filename);

    GlobalResponse deleteSlide(long id);

    GlobalResponse getRiskyBets(Pageable pageable);

    GlobalResponse settleBet(long id);

    GlobalResponse updateTopLeagues(LeaguePriority leagues);

    GlobalResponse getBonusBets(String country,String from, String to,Pageable pageable);

    GlobalResponse setPayments(PaymentSettingRequest paymentSettingRequest);

    GlobalResponse createCampaign(MultipartFile file, CampaignRequest campaignRequest);

    GlobalResponse activate(long id, String status);

    GlobalResponse getSmses(String origin, String from, String to, Pageable pageable);

    GlobalResponse aggregateSmses(String from, String to);

    GlobalResponse createJackpot(JackpotRequest jackpotRequest);

    GlobalResponse wonPerCountry(String country);

    GlobalResponse campaigns(Pageable pageable);

    GlobalResponse delete(long id);

    GlobalResponse editCampaign(CampaignRequest slideRequest, long id);

    GlobalResponse highlightSingleGame(long id, int priority);

    GlobalResponse getJackpotBets(Pageable pageable, String code);

    GlobalResponse jackpots();

    GlobalResponse activateJackpot(Long id,String status);

    GlobalResponse getGames(long id);

    GlobalResponse deleteJackpot(long id);

    GlobalResponse getSports();

    GlobalResponse getTournamentGames(long id);

    GlobalResponse getTournaments(SportTournament sportTournament);

    GlobalResponse removeGame(long id);

    GlobalResponse setLiveTournament(long id, int priority);


//    GlobalResponse searchBet(int code);
}
