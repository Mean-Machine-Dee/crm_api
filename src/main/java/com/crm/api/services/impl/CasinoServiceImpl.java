package com.crm.api.services.impl;

import com.crm.api.api.models.Aviatrix;
import com.crm.api.api.models.JetX;
import com.crm.api.api.models.Plagmatic;
import com.crm.api.api.repository.AviatrixRepository;
import com.crm.api.api.repository.JetXRepository;
import com.crm.api.api.repository.PlagmaticRepository;
import com.crm.api.dtos.CasinoDTO;
import com.crm.api.dtos.PlagmaticInterface;
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
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class CasinoServiceImpl implements CasinoService {

    private final AppUtils appUtils = new AppUtils();

    @Autowired
    private PlagmaticRepository plagmaticRepository;

    @Autowired
    private JetXRepository jetXRepository;

    @Autowired
    private AviatrixRepository aviatrixRepository;
    @Override
    public GlobalResponse casinoBets(String provider, Pageable pageable, String country, String from, String to, String type) {
        log.info("Changing {} from {} to {} and type {}", provider,from,to,type);
        Timestamp timestampStart = appUtils.startOfDayTimestamp(from);
        Timestamp timestampStop = appUtils.endOfDayTimestamp(to);
        if(type.equalsIgnoreCase("landing")){
            timestampStart = appUtils.startOfToday();
            timestampStop = appUtils.getBurundiTime();
        }
       if(provider.equalsIgnoreCase("plagmatic"))
       {
           return getPlagmatic(pageable, country,timestampStart,timestampStop);
          }else if(provider.equalsIgnoreCase("jetx")){
           return getJetx(country,timestampStart,timestampStop,pageable);
          }else{
           return getAviatrix(pageable,country,timestampStart,timestampStop);
       }
     }

    @Override
    public GlobalResponse getUserCasinos(long id, String provider, Pageable pageable) {
        if(provider.equalsIgnoreCase("plagmatic")){
            Page<Plagmatic> plagmatics = plagmaticRepository.findByUserBets(id, pageable);
            if (!plagmatics.isEmpty()){
                List<CasinoDTO> casinoDTOS = formatPlagmatics(plagmatics);
                Map<String, Object> mapped = appUtils.dataFormatter(casinoDTOS, plagmatics.getNumber(), plagmatics.getTotalElements(), plagmatics.getTotalPages());
                return new GlobalResponse(mapped,true,false,"User Bets");
            }

        }else if(provider.equalsIgnoreCase("jetx")){
            Page<JetX> jetXES = jetXRepository.findByUser(id, pageable);
            if (!jetXES.isEmpty()){
                List<CasinoDTO> casinoDTOS = formatJetX(jetXES);
                Map<String, Object> mapped = appUtils.dataFormatter(casinoDTOS, jetXES.getNumber(), jetXES.getTotalElements(), jetXES.getTotalPages());
                return new GlobalResponse(mapped,true,false,"User Bets");
            }

        }else{
            Page<Aviatrix> aviatrixes = aviatrixRepository.findByUserBet(id, pageable);
            if (!aviatrixes.isEmpty()){
                List<CasinoDTO> casinoDTOS = formatAviatrix(aviatrixes);
                Map<String, Object> mapped = appUtils.dataFormatter(casinoDTOS, aviatrixes.getNumber(), aviatrixes.getTotalElements(), aviatrixes.getTotalPages());
                return new GlobalResponse(mapped,true,false,"User Bets");
            }

            List<CasinoDTO> casinoDTOS = formatAviatrix(aviatrixes);
            return new GlobalResponse(casinoDTOS,true,false,"User Bets");

        }
        return new GlobalResponse(null,false,true,"No User Bets");
    }


    private GlobalResponse getJetx(String country,Timestamp timestampStart,Timestamp timestampStop,Pageable pageable) {
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<JetX> jetXBets = jetXRepository.findBets(country,timestampStart,timestampStop,pageable);

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
                        jetx.getAmountWon(), jetx.getStatus(), jetx.getWon(),
                        jetx.getCreatedAt(), jetx.getResultedAt(), 1.0,
                        jetx.getCurrency(), jetx.getUserId()))
                .collect(Collectors.toList());
    }


    private GlobalResponse getPlagmatic(Pageable pageable,String country, Timestamp timestampStart, Timestamp timestampStop){
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<Plagmatic> plagmatics =  plagmaticRepository.findBets(country,timestampStart,timestampStop,pageable);

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
                        plagmatic.getGameId(), plagmatic.getAmount(),
                        plagmatic.getAmountWon() / 100, plagmatic.getStatus(), plagmatic.getWon(),
                        plagmatic.getCreatedAt(), plagmatic.getResultedAt(), 1.0,
                        "BIF", plagmatic.getUserId()))
                .collect(Collectors.toList());


    }

    private GlobalResponse getAviatrix(Pageable pageable,String country, Timestamp timestampStart, Timestamp timestampStop){
        int currentPage = 0;
        int totalPages = 0;
        int nextPage = 0;
        long totalItems = 0;
        Page<Aviatrix> aviatorBets =  aviatrixRepository.findBets(country,timestampStart,timestampStop,pageable);
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
                        aviator.getAmountWon() ,aviator.isStatus() ? "resulted":"pending", aviator.getWon(),
                        aviator.getCreatedAt(),aviator.getResultedAt(), aviator.getOdds(), aviator.getCurrency(),aviator.getUserId()))
                .collect(Collectors.toList());
    }


}
