package com.airline.reservation.repository;

import com.airline.reservation.entity.Wallet;
import com.airline.reservation.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletOrderByCreatedAtDesc(Wallet wallet);
}
