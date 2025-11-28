package com.crm.api.api.repository;

import com.crm.api.api.models.Deposit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface DepositRepository extends JpaRepository<Deposit,Long> {
    @Query(value="SELECT * FROM deposits WHERE currency = ?1 AND created_at between ?2 AND ?3",nativeQuery = true)
    List<Deposit> getDeposits(String currency,Timestamp startOfToday, Timestamp stop);
    @Query(value="SELECT * FROM deposits WHERE user_id = ?1",nativeQuery = true)
    List<Deposit> userDeposits(Long id);

    @Query(value="SELECT * FROM deposits WHERE user_id = ?1 AND telco = ?2 ORDER BY created_at DESC",nativeQuery = true)
    Page<Deposit> userPagedDeposits(Long id, String telco,Pageable pageable);

    @Query(value="SELECT * FROM deposits WHERE user_id = ?1 ORDER BY created_at DESC",nativeQuery = true)
    Page<Deposit> userDepositsPaged(Long id,Pageable pageable);

    @Query(value = "SELECT SUM(`amount`) FROM deposits where created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    Double findByDateBetween(Timestamp start, Timestamp finish);

    @Query(value = "SELECT SUM(`amount`) FROM deposits where telco = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Double findByTelcoBetween(String telco,Timestamp start, Timestamp finish);

    @Query(value="SELECT * FROM deposits WHERE telco = ?1 AND created_at between ?2 AND ?3 ORDER BY amount DESC LIMIT 15",nativeQuery = true)
    List<Deposit> getDepositTimeFrame(String telco,Timestamp startOfToday, Timestamp stop);

    @Query(value="SELECT * FROM deposits WHERE created_at between ?1 AND ?2 ",nativeQuery = true)
    List<Deposit> getDepositBetween(Timestamp startOfToday, Timestamp stop);

    @Query(value = "SELECT SUM(`amount`) FROM deposits where currency = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Double findByCurrency(String currency, Timestamp start, Timestamp finish);


    @Query(value = "SELECT SUM(`amount`) FROM deposits where created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    Double findTotalBetween(Timestamp start, Timestamp finish);

    @Query(value="SELECT * FROM deposits WHERE currency = ?1 AND created_at between ?2 AND ?3 ORDER BY created_at DESC LIMIT 15",nativeQuery = true)
    List<Deposit> getDepositTimeFrameCurrency(String currency, Timestamp start, Timestamp finish);

    @Query(value = "SELECT SUM(`amount`) FROM deposits", nativeQuery = true)
    Double findAllTime();

    @Query(value = "SELECT * FROM deposits WHERE agent_source =?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Deposit> getCrmDeposits(int id, Pageable pageable);

    @Query(value = "SELECT user_id FROM deposits where created_at BETWEEN ?1 AND ?2", nativeQuery = true)
    List<String> findUserPhones(Timestamp from, Timestamp to);

    @Query(value = "SELECT * FROM deposits where user_id in ?1 ", nativeQuery = true)
    List<Deposit> getByIds(List<Long> customerIds);

    @Query(value="SELECT * FROM deposits WHERE user_id = ?1 ORDER BY created_at ASC LIMIT 1",nativeQuery = true)
   Deposit getFirstDeposit(Long id);

    @Query(value = "SELECT SUM(`amount`) FROM deposits where currency = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Double getDepositCurrency(String currency, Timestamp start, Timestamp stop);


    @Query(value = "SELECT SUM(`amount`) FROM deposits where currency = ?1", nativeQuery = true)
    Double allTimeCurrency(String currency);


    @Query(value = "SELECT SUM(`amount`) FROM deposits where telco = ?1 ", nativeQuery = true)
    Double allTimePrsp(String prsp);
}
