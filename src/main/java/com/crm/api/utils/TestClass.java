package com.crm.api.utils;

public class TestClass {
    AppUtils appUtils = new AppUtils();
    public static void main(String[] args) {
        System.out.println(new AppUtils().formatStringToTimestamp("2024-10-08T08:29:08.504Z"));
    }
}
