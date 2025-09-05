package com.example.bank_rest.repository;

import com.example.bank_rest.entity.Transaction;
import com.example.bank_rest.entity.enums.TransactionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findAllBySender_User_IdOrReceiver_User_IdOrderByCreatedAtDesc(Long senderUserId, Long receiverUserId, Pageable pageable);

    Page<Transaction> findAllBySender_User_IdOrReceiver_User_IdAndStatusOrderByCreatedAtDesc(Long senderUserId, Long receiverUserId, TransactionStatus status, Pageable pageable);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);

    Page<Transaction> findAllByStatusOrderByCreatedAtDesc(TransactionStatus status, Pageable pageable);

}
