package com.crm.api.api.repository;

import com.crm.api.api.models.Customer;
import com.crm.api.api.models.Friend;
import com.crm.api.crm.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
   @Query(value = "SELECT A.* FROM users A left join `friends` B on A.`id` = B.`invite` where B.`created_at` between ?1 and ?2", nativeQuery = true)
    Page<Customer> findByCountryAndDate(Timestamp start, Timestamp finish, String country, Pageable pageable);

    @Query(value = "SELECT * FROM friends WHERE invite = ?1", nativeQuery = true)
    List<Friend> getAllInvitees(Long id);
}