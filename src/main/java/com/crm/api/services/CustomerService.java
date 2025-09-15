package com.crm.api.services;

import com.crm.api.payload.requests.*;
import com.crm.api.payload.response.GlobalResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

public interface CustomerService {
    GlobalResponse getUserBets(long id, Pageable pageable);


    GlobalResponse getUserPayments(long id);

    GlobalResponse getClients(Pageable pageable, String phone, Principal principal);

    GlobalResponse getClientDetails(long id);

    GlobalResponse getClientPayments(int id, Pageable pageable);

    GlobalResponse getDeposits(int id, Pageable pageable, String type);

    GlobalResponse clientDeposit(DepositRequest depositRequest, Principal principal);

    GlobalResponse b2c(WithdrawRequest request, Principal principal);

    GlobalResponse profile(ProfileRequest request);

    GlobalResponse createTicket(TicketRequest ticketDTO, Principal principal);

    GlobalResponse updateTicket(TicketRequest ticketDTO, long id);

    GlobalResponse getTickets(Pageable pageable);

    GlobalResponse deleteTicket(long id);

    GlobalResponse getBetPicks(int id);

    GlobalResponse verifyClient(long id);

    GlobalResponse settleBulk(MultipartFile file, String type);

    GlobalResponse agentsData(Pageable pageable);

    GlobalResponse getPlayers(String category, String country, HttpServletResponse response);

    GlobalResponse bulkDeposit(MultipartFile file);

    GlobalResponse awardBonus(BonusWinnersRequest winnersRequest);

    GlobalResponse getRate(String from, String to,String iso);

    GlobalResponse getBetsPerSport(String id, String from, String to, Pageable pageable, String country);

    GlobalResponse unlock(Long id, String type);
}
