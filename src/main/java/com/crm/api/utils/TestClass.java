package com.crm.api.utils;

import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;

public class TestClass {
//    AppUtils appUtils = new AppUtils();
//    public static void main(String[] args) {
//        System.out.println(new AppUtils().formatStringToTimestamp("2024-10-08T08:29:08.504Z"));
//    }

    public static void main(String[] args) {
        String date = "2025-09-10";
        ZoneId zoneId = ZoneId.of( "Africa/Bujumbura" ) ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        ZonedDateTime today = LocalDate.parse(date,formatter).atStartOfDay(zoneId);
        LocalDateTime localDateTime = today.plusDays(1).toLocalDate().atStartOfDay();
        LocalDateTime tm = today.toLocalDateTime();
        LocalDateTime localDateTime1 = tm.toLocalDate().atTime(LocalTime.MAX);
        Timestamp start = Timestamp.valueOf(today.toLocalDateTime());
        Timestamp stop = Timestamp.valueOf(localDateTime);
        System.out.println(start + " " + stop + " " + localDateTime1);

    }
}
