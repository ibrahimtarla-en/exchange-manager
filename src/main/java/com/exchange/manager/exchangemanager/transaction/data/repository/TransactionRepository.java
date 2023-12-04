package com.exchange.manager.exchangemanager.transaction.data.repository;

import com.exchange.manager.exchangemanager.transaction.data.entity.Transaction;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByTransactionId(Long id);

    Optional<Transaction> findByTransactionDate(Long date);

    List<Transaction> getAllByWalletAccount(WalletAccount walletAccount);
}
