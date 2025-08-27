package com.crm.api.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadExecutor {

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    public void settleBetsExecutor(Runnable task){
        executorService.submit(task);
    }

    public void bulkDepositExecutor(Runnable task){
        executorService.submit(task);
    }
    public void shutDown(){
        executorService.shutdown();
    }
}
