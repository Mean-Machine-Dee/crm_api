package com.crm.api.utils;

import com.crm.api.api.models.Bet;
import com.crm.api.controllers.AuthController;
import com.crm.api.crm.models.ERole;
import com.crm.api.crm.models.Role;
import com.crm.api.crm.repository.RoleRepository;
import com.crm.api.dtos.CustomerDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AppUtils {
    private final Logger logger = LoggerFactory.getLogger(AppUtils.class);

    @Autowired
    private RoleRepository roleRepository;
    public Date addHoursToJavaUtilDate(Date date, int hours) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hours);
        return calendar.getTime();
    }


    public String[] pathArray() {
        return new String[]{
                "/api/v2/dashboard/count",};
    }

    public ZonedDateTime getStartOfTommorow() {
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        LocalDate today = LocalDate.now(zoneId);
        return today.plusDays( 1 ).atStartOfDay( zoneId );
    }
    public  Timestamp getStopDate() {
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        ZonedDateTime zonedDateTime = LocalDateTime.now().toLocalDate().plusDays(1).atStartOfDay(zoneId);
        return Timestamp.valueOf(zonedDateTime.toLocalDateTime());
    }


    public Timestamp startOfToday() {
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        LocalDate today = LocalDate.now(zoneId);
        return Timestamp.valueOf(today.atStartOfDay());
    }

    public Timestamp minusDays(int days) {
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        LocalDateTime today = LocalDateTime.now(zoneId);
        return Timestamp.valueOf(today.minusDays(days).toLocalDate().atStartOfDay());
    }


    public Timestamp getBurundiTime(){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:s");
        sdf.setTimeZone(TimeZone.getTimeZone("Africa/Bujumbura"));
        calendar.setTime(new Date());
        return Timestamp.valueOf(sdf.format(calendar.getTime()));
    }

    public Object formatDate(ZonedDateTime zdtStop) {
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyy-MM-dd HH:mm:ss");
        return zdtStop.format(formatter2);

    }

    public Map<String,Object> dataFormatter(Object data, int number, long totalElements, long pages){
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("currentPage", number);
        response.put("totalItems", totalElements);
        response.put("totalPages", pages);
        response.put("nextPage", number+ 1);
        return response;
    }


    public String getRefCode() {
        return "CRM-" + System.currentTimeMillis();
    }

    public Set<Role> getRoles(Set<String> strRoles) {
        Set<Role> roles = new HashSet<>();
        logger.info("Roles are roles {} -- {}" , strRoles,strRoles.size());
        List<Role> rolesAll = roleRepository.findAll();
        logger.info("All Roles are {}", rolesAll);
//        strRoles.forEach(role ->{
//
//            switch (role){
//                case "admin":
//                    Role roleAdmin = roleRepository.findByName(ERole.ROLE_ADMIN)
//                            .orElseThrow(()-> new RuntimeException("Error: Role admin not found"));
//                    roles.add(roleAdmin);
//                    logger.info("searching role {} - {} - {}" , role, ERole.ROLE_ADMIN, roleAdmin);
//                    break;
//                case "bookie":
//                    Role modRole = roleRepository.findByName(ERole.ROLE_BOOKIE)
//                            .orElseThrow(() -> new RuntimeException("Error: Role Bookie is not found."));
//                    roles.add(modRole);
//                    break;
//
//                case "customer_care":
//                    Role csRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
//                            .orElseThrow(() -> new RuntimeException("Error: Role Customer Care is not found."));
//                    logger.info("Role cs is {}", csRole);
//                    roles.add(csRole);
//                    break;
//                default:
//                    Role userRole = roleRepository.findByName(ERole.ROLE_CUSTOMER_CARE)
//                            .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                    roles.add(userRole);
//            }
//        });

        return roles;
    }

    public LocalDateTime parseDate(String actionDay) {
        return LocalDateTime.parse(actionDay);
    }

    public Timestamp formatStringToTimestamp(String date) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
        Locale locale = Locale.US;
      try{
          DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", locale);
          LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
          return Timestamp.valueOf(dateTime);
      }catch (Exception e){
          e.printStackTrace();
          return null;
      }

    }


    public Timestamp startOfDayTimestamp(String date){
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime today = LocalDate.parse(date,formatter).atStartOfDay(zoneId);
        return Timestamp.valueOf(today.toLocalDateTime());
    }

    public Timestamp endOfDayTimestamp(String date){
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime today = LocalDate.parse(date,formatter).atStartOfDay(zoneId);
        LocalDateTime tm = today.toLocalDateTime();
        LocalDateTime localDateTime = tm.toLocalDate().atTime(LocalTime.MAX);
        return Timestamp.valueOf(localDateTime);
    }

    public String getPRSP(String service, String name) {
        String result = "";
        if(service.equalsIgnoreCase("deposit")){
            switch (name){
                case "crm":
                    result = "crm";
                    break;
                case "centwise":
                    result = "centwise";
                    break;
                case "astropay":
                    result = "ASTROPAY";
                    break;
                case "virtual":
                    result = "vp";
                    break;
                default:
                    result = "lumitel";
                    break;
            }
        }

        if(service.equalsIgnoreCase("payment")){
            switch (name){
                case "astropay":
                    result = "ASTROPAY";
                    break;
                case "centwise":
                    result = "centwise";
                    break;
                case "virtual":
                    result = "vp";
                    break;
                default:
                    result = "BI";
                    break;
            }
        }

        return result;
    }


    public Map<String, Object> bonusBets(Map<String, List<Bet>> bets, int number, long totalElements, int totalPages) {
        Map<String, Object> map = new HashMap<>();
        map.put("bets", bets);
        map.put("currentPage", number);
        map.put("totalPages", totalPages);
        map.put("totalItems", totalElements);
        map.put("nextPage", number + 1 > totalPages ? number : number +1);
        return map;

    }

    public String getCurrency(String iso){
        String currency;
        switch (iso){
            case "KE":
                currency ="KES";
                break;
            case "UG":
                currency ="UGX";
                break;
            case "TZ":
                currency ="TZS";
                break;
            case "RW":
                currency ="RWF";
                break;
            case "ZM":
                currency ="ZMW";
                break;
            case "BJ":
            case "CI":
                currency ="XOF";
                break;
            case "CM":
                currency ="XAF";
                break;
            case "CD":
                currency ="CDF";
                break;
            case "GH":
                currency ="GHS";
                break;
            default:
                currency ="BIF";
        }

        return currency;
    }
}
