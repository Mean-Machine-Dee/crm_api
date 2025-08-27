package com.crm.api.services.impl;

import com.crm.api.api.models.Aviatrix;
import com.crm.api.api.models.JetX;
import com.crm.api.api.models.Plagmatic;
import com.crm.api.api.repository.AviatrixRepository;
import com.crm.api.api.repository.JetXRepository;
import com.crm.api.api.repository.PlagmaticRepository;
import com.crm.api.dtos.CasinoDTO;
import com.crm.api.payload.response.CasinoResponse;
import com.crm.api.payload.response.GlobalResponse;
import com.crm.api.services.CasinoService;
import com.crm.api.utils.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CasinoServiceImpl implements CasinoService {

    private final AppUtils appUtils = new AppUtils();
   private  CasinoResponse casinoResponses = null;
    @Autowired
    private PlagmaticRepository plagmaticRepository;

    @Autowired
    private JetXRepository jetXRepository;

    @Autowired
    private AviatrixRepository aviatrixRepository;
    @Override
    public GlobalResponse casinoBets(String provider, Pageable pageable) {
        log.info("Changing {}", provider);
       if(provider.equalsIgnoreCase("plagmatic"))
       {
           return getPlagmatic(pageable, 0);
          }else if(provider.equalsIgnoreCase("jetx")){
           return getJetx(pageable, 0);
          }else{
           return getAviatrix(pageable, 0);
       }
     }

    @Override
    public GlobalResponse getUserCasinos(long id, String provider, Pageable pageable) {
        if(provider.equalsIgnoreCase("plagmatic")){
            return getPlagmatic(pageable, id);
        }else if(provider.equalsIgnoreCase("jetx")){
            return getJetx(pageable,id);
        }else{
            return getAviatrix(pageable,id);
        }
    }

    @Override
    public GlobalResponse filterCasinos(String provider, String from, String to, Pageable pageable) {
        Timestamp start = appUtils.formatStringToTimestamp(from);
        Timestamp finish = appUtils.formatStringToTimestamp(to);
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        List<CasinoDTO> casinos = new ArrayList<>();
        if(provider.equalsIgnoreCase("plagmatic")){
             Page<Plagmatic> plagmatics = plagmaticRepository.findByDate(pageable, start,finish);
             if(plagmatics != null){
                 List<CasinoDTO> casinoDTOS = formatPlagmatics(plagmatics);
                 currentPage = plagmatics.getNumber();
                 totalPages = plagmatics.getTotalPages();
                 nextPage = plagmatics.getNumber()+1;
                 totalItems = plagmatics.getTotalElements();
                 CasinoResponse casinoResponse = new CasinoResponse(casinos, currentPage, totalPages, nextPage, totalItems);
                 return new GlobalResponse(casinoResponse, true, false, "plagmatics");
            }else{
                 return new GlobalResponse(null, false, true, "No plagmatics");
             }
        }else if(provider.equalsIgnoreCase("jetx")){
            Page<JetX> jetXES = jetXRepository.findByDate(pageable, start,finish);
       if(jetXES != null){
           List<CasinoDTO> casinoDTOS = formatJetX(jetXES);
           currentPage = jetXES.getNumber();
           totalPages = jetXES.getTotalPages();
           nextPage = jetXES.getNumber()+1;
           totalItems = jetXES.getTotalElements();
           CasinoResponse casinoResponse = new CasinoResponse(casinoDTOS, currentPage, totalPages, nextPage, totalItems);
           return new GlobalResponse(casinoResponse, true, false, "JetX");
       }else{
           return new GlobalResponse(null, false, true, "No JetX Bets ");
       }

        }else{
            Page<Aviatrix> aviatrixes = aviatrixRepository.findByDate(pageable, start,finish);
            if(aviatrixes != null){
                List<CasinoDTO> casinoDTOS = formatAviatrix(aviatrixes);
                currentPage = aviatrixes.getNumber();
                totalPages = aviatrixes.getTotalPages();
                nextPage = aviatrixes.getNumber()+1;
                totalItems = aviatrixes.getTotalElements();
                CasinoResponse casinoResponse = new CasinoResponse(casinoDTOS, currentPage, totalPages, nextPage, totalItems);
                return new GlobalResponse(casinoResponse, true, false, "Aviatrix");
            }else{
                return new GlobalResponse(null, false, true, "No aviatrix bets");
            }
        }

    }

    private GlobalResponse getJetx(Pageable pageable, long userId) {
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<JetX> jetXBets = null;
        if (userId == 0) {
            jetXBets = jetXRepository.findBets(pageable);
        } else {
            jetXBets = jetXRepository.findByUser(userId,pageable);
        }
        if (jetXBets != null) {
            List<CasinoDTO> casinos = formatJetX(jetXBets);
            currentPage = jetXBets.getNumber();
            totalPages = jetXBets.getTotalPages();
            nextPage = jetXBets.getNumber() + 1;
            totalItems = jetXBets.getTotalElements();
            CasinoResponse casinoResponse = new CasinoResponse(casinos, currentPage, totalPages, nextPage, totalItems);
            return new GlobalResponse(casinoResponse, true, false, "plagmatics");
        }
        return new GlobalResponse(null,true, true, "plagmatics");
    }

    private List<CasinoDTO> formatJetX(Page<JetX> jetXBets) {
        return jetXBets.getContent().stream()
                .map(jetx -> new CasinoDTO("JetX",
                        jetx.getGameName(), jetx.getAmount(),
                        jetx.getAmountWon()/100, jetx.getStatus(), jetx.getWon(),
                        jetx.getCreatedAt(), jetx.getResultedAt(), 1.0,
                        jetx.getCurrency(), jetx.getUserId()))
                .collect(Collectors.toList());
    }


    private GlobalResponse getPlagmatic(Pageable pageable, long userId){
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<Plagmatic> plagmatics = null;
        if(userId == 0){
           plagmatics = plagmaticRepository.findBets(pageable);
        }else{
            plagmatics = plagmaticRepository.findByUserBets(userId, pageable);
        }
       if(plagmatics != null){
          List<CasinoDTO> casinos = formatPlagmatics(plagmatics);
           currentPage = plagmatics.getNumber();
           totalPages = plagmatics.getTotalPages();
           nextPage = plagmatics.getNumber()+1;
           totalItems = plagmatics.getTotalElements();
           CasinoResponse casinoResponse = new CasinoResponse(casinos,currentPage,totalPages,nextPage,totalItems);
           return new GlobalResponse(casinoResponse,true, false, "plagmatics");

       }
        return new GlobalResponse(null,true, true, "No Data");
    }

    private List<CasinoDTO> formatPlagmatics(Page<Plagmatic> plagmatics) {
       return plagmatics.getContent().stream()
                .map(plagmatic -> new CasinoDTO("Plagmatic",
                        plagmatic.getGameId(),plagmatic.getAmount(),
                        plagmatic.getAmountWon()/100,plagmatic.getStatus(), plagmatic.getWon(),
                        plagmatic.getCreatedAt(),plagmatic.getResultedAt(), 1.0,
                        "BIF",plagmatic.getUserId()))
                .collect(Collectors.toList());
    }

    private GlobalResponse getAviatrix(Pageable pageable, long userId){
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<Aviatrix> aviatorBets = null;

        if(userId == 0){
            log.info("Aviator bets {}",userId);

            aviatorBets = aviatrixRepository.findBets(pageable);
        }else{
            aviatorBets = aviatrixRepository.findByUserBet(userId, pageable);
        }

        log.info("Aviator {}", aviatorBets.isEmpty());

       if(!aviatorBets.isEmpty()){
           List<CasinoDTO> casinos = formatAviatrix(aviatorBets);
           currentPage = aviatorBets.getNumber();
           totalPages = aviatorBets.getTotalPages();
           nextPage = aviatorBets.getNumber()+1;
           totalItems = aviatorBets.getTotalElements();
           CasinoResponse casinoResponse = new CasinoResponse(casinos,currentPage,totalPages,nextPage,totalItems);
           return new GlobalResponse(casinoResponse,true, false, "Aviatirx");

       }
        return new GlobalResponse(null,true, false, "Aviatrix");
    }

    private List<CasinoDTO> formatAviatrix(Page<Aviatrix> aviatorBets) {
        log.info("Formatting Aviator {}", aviatorBets.getContent());
        return aviatorBets.getContent().stream().map(aviator -> new CasinoDTO("Aviatrix",
                        aviator.getGameId(),aviator.getAmount(),
                        aviator.getAmountWon() / 100 ,aviator.isStatus() ? "resulted":"pending", aviator.getWon(),
                        aviator.getCreatedAt(),aviator.getResultedAt(), aviator.getOdds(), aviator.getCurrency(),aviator.getUserId()))
                .collect(Collectors.toList());
    }


}
