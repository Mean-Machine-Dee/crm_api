package com.crm.api.api.repository;

import com.crm.api.api.models.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;


@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query(value="SELECT * FROM users WHERE id = ?1",nativeQuery = true)
    Customer getByUserId(long id);

    @Query(value = "SELECT * FROM users WHERE verified = 1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Customer> getCustomers(Pageable pageable);


    Page<Customer> findByPhoneLike(String phone,Pageable pageable);

//    @Query(value = "SELECT * FROM users ORDER BY created_at DESC LIMIT 10", nativeQuery = true)
//    List<Customer> getCustomers();

    Customer findById(long id);
    @Query(value = "SELECT COUNT('id') FROM users WHERE iso = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer findTodaysSignUps(String iso,Timestamp timestamp, Timestamp stop);

    Customer findByPhone(String phone);

    @Query(value = "SELECT phone FROM users WHERE id IN ?1 ORDER BY created_at DESC", nativeQuery = true)
    List<String> findByIds(Set<Long> commonElems);

    @Query(value = "SELECT * FROM users WHERE verified = 1 AND iso = ?1 ORDER BY created_at DESC", nativeQuery = true)
    Page<Customer> getLocalizedCustomers(String iso,Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE id = ?1", nativeQuery = true)
    Customer findCustomerId(long id);

    @Query(value = "SELECT * FROM users WHERE iso = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    List<Customer> findByCreatedAtRange(String iso, Timestamp start, Timestamp from);

    @Query(value = "SELECT COUNT('id') FROM users WHERE iso = ?1 AND created_at BETWEEN ?2 AND ?3", nativeQuery = true)
    Integer findCustomersByIsoCode(String country, Timestamp from, Timestamp to);


    @Query(value = "SELECT COUNT('id') FROM users WHERE iso = ?1", nativeQuery = true)
    Long findCustomersByCountry(String country);


}
