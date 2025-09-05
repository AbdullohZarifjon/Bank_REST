package com.example.bank_rest.repository;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.enums.StatusCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findAllByExpiryDateBeforeAndStatusNot(LocalDate expiryDateBefore, StatusCard status);

    Page<Card> findByUser_Id(Long userId, Pageable pageable);

    Page<Card> findByUser_IdAndNameContainingIgnoreCase(Long id, String filter, Pageable pageable);

    Page<Card> findAllByOrderByExpiryDateDesc(Pageable pageable);

    Page<Card> findAllByStatusOrderByExpiryDateDesc(StatusCard status, Pageable pageable);
}
