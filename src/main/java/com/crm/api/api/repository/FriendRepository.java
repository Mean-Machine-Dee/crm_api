package com.crm.api.api.repository;

import com.crm.api.api.models.Customer;
import com.crm.api.api.models.Friend;
import com.crm.api.dtos.AffiliateSerializer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
   @Query(value = "SELECT A.id,A.phone,A.iso,A.created_at FROM `users` A left join `friends` B on A.`id` = B.`invite` where A.`iso` = ?1 and B.`created_at` between ?2 and ?3 order by B.created_at", nativeQuery = true)
    Page<AffiliateSerializer> findByCountryAndDate(String country, Timestamp start, Timestamp finish, Pageable pageable);

    @Query(value = "SELECT * FROM friends WHERE invite = ?1 ORDER BY created_at DESC", nativeQuery = true)
    List<Friend> getAllInvitees(Long id);
}