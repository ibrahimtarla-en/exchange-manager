package com.exchange.manager.exchangemanager.transaction.service.impl;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.exceptions.transaction.TransactionNotCreatedException;
import com.exchange.manager.exchangemanager.transaction.data.entity.Transaction;
import com.exchange.manager.exchangemanager.transaction.data.repository.TransactionRepository;
import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import com.exchange.manager.exchangemanager.transaction.service.ITransactionService;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements ITransactionService {

    private final TransactionRepository repository;

    @Override
    public Transaction createTransaction(TransactionDto transactionDto, WalletAccount walletAccount) {

        try {
            Transaction transaction = Transaction.builder()
                    .currencyType(transactionDto.getCurrencyType())
                    .day(transactionDto.getDay())
                    .month(transactionDto.getMonth())
                    .year(transactionDto.getYear())
                    .amount(transactionDto.getAmount())
                    .transactionDate(transactionDto.getTransactionDate())
                    .description(transactionDto.getDescription())
                    .walletAccount(walletAccount)
                    .transactionType(transactionDto.getTransactionType())
                    .build();
            return repository.save(transaction);
        } catch (Exception exception) {
            throw new TransactionNotCreatedException(Language.TR, FriendlyMessageCodes.TRANSACTION_NOT_CREATED_EXCEPTION, "user request: " + transactionDto);

        }

    }

    @Override
    public List<Transaction> getAllTransactionsByWalletAccount(WalletAccount walletAccount) {

        return repository.getAllByWalletAccount(walletAccount);
    }

    @Override
    public Optional<Transaction> getTransactionByDate(Long transactionDate) {
        Optional<Transaction> transaction = repository.findByTransactionDate(transactionDate);
        return transaction;

    }

    @Override
    public Transaction createChargeTransaction(Transaction transaction) {
        return repository.save(transaction);
    }
}
