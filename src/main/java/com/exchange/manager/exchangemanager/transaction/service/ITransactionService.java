package com.exchange.manager.exchangemanager.transaction.service;

import com.exchange.manager.exchangemanager.transaction.data.entity.Transaction;
import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;

import java.util.List;
import java.util.Optional;

public interface ITransactionService {

    Transaction createTransaction(TransactionDto transactionDto,WalletAccount walletAccount);

    List<Transaction> getAllTransactionsByWalletAccount(WalletAccount walletAccount);

    Optional<Transaction> getTransactionByDate(Long transactionDate);

    Transaction createChargeTransaction(Transaction transaction);

}
