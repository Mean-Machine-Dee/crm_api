package com.crm.api.api.repository;

import com.crm.api.api.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account,Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM accounts WHERE owner_id = ?1 LIMIT 1")
    Account findByOwnerId(long id);


    Optional<Account> findByName(String number);
}
