package com.crm.api.services;


import com.crm.api.api.models.Account;
import com.crm.api.api.models.Customer;
import com.crm.api.api.models.Deposit;
import com.crm.api.api.models.Ledger;
import com.crm.api.api.repository.AccountRepository;
import com.crm.api.api.repository.CustomerRepository;
import com.crm.api.api.repository.LedgerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private AccountRepository accountRepository;
    @Transactional
    public void deposit(Deposit deposit, Account account){
        double balance = account.getMain();

        double amount = balance + deposit.getAmount();
        account.setMain(amount);
        log.info(" {} after {}", balance,account);
        accountRepository.save(account);
        String ref = "CRM-DEPOSIT";
        String code = "CRM0000";
        writeLedger(account.getCustomer().getId(),ref,deposit.getAmount(), amount,code);
    }



    public void writeLedger(long userId, String ref, double amount, double balance, String code){

        String uuid = UUID.randomUUID().toString();
        Ledger ledger = new Ledger(0L,code, (int) userId,ref,amount,0.00,balance,uuid,new Timestamp(System.currentTimeMillis()));
        ledgerRepository.save(ledger);
    }

    public void creditCustomer(double payout, Long id) {
        String ref = "CRM-BETSETTLEMENT";
        String code = "CRMBST";
            Account account = accountRepository.findByOwnerId(id);
            double balance = account.getMain();
            double amount = balance + payout;
            account.setMain(amount);
            accountRepository.save(account);
           writeLedger(account.getCustomer().getId(),ref, payout,amount,code);
        }


//    public void withdrawal()
}
