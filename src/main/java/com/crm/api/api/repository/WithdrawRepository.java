package com.crm.api.api.repository;

import com.crm.api.api.models.Withdrawals;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface WithdrawRepository extends JpaRepository<Withdrawals, Long> {
    @Query(value="SELECT * FROM payments WHERE telco = ?1 AND trans_date between ?2 AND ?3",nativeQuery = true)
    List<Withdrawals> getWithdrawals(String iso,Timestamp startOfToday, Timestamp stop);

    @Query(value="SELECT * FROM payments WHERE phone = ?1 ORDER BY created_at DESC",nativeQuery = true)
    List<Withdrawals> userB2c(String phone);

    @Query(value="SELECT * FROM payments WHERE phone = ?1 ORDER BY created_at DESC",nativeQuery = true)
    Page<Withdrawals> getClientWithdrawals(String phone, Pageable pageable);

    @Query(value = "SELECT SUM(`amount`) FROM payments where trans_date BETWEEN ?1 AND ?2", nativeQuery = true)
    Double findByDateBetween(Timestamp start, Timestamp finish);

    @Query(value = "SELECT SUM(`amount`) FROM payments where telco = ?1 AND trans_date BETWEEN ?2 AND ?3", nativeQuery = true)
    Double findByTelcoBetween(String name, Timestamp start, Timestamp finish);

    @Query(value="SELECT * FROM payments WHERE telco = ?1 AND trans_date between ?2 AND ?3 ORDER BY amount ASC LIMIT 15",nativeQuery = true)
    List<Withdrawals> getWithdrawalTimeFrame(String prsp,Timestamp startOfToday, Timestamp stop);

    @Query(value="SELECT * FROM payments WHERE trans_date between ?1 AND ?2 ",nativeQuery = true)
    List<Withdrawals> getWithdrawalBetween(Timestamp startOfToday, Timestamp stop);

    @Query(value = "SELECT SUM(`amount`) FROM payments where telco = ?1 AND trans_date BETWEEN ?2 AND ?3", nativeQuery = true)
    Double findByCurrency(String currency, Timestamp start, Timestamp finish);
    @Query(value = "SELECT SUM(`amount`) FROM payments", nativeQuery = true)
    Double findAllTime();

    @Query(value = "SELECT SUM(`amount`) FROM payments where trans_date BETWEEN ?1 AND ?2", nativeQuery = true)
    Double findTotalBetween(Timestamp timestamp, Timestamp stopDate);
}
