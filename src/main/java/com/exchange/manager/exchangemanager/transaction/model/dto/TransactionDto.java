package com.exchange.manager.exchangemanager.transaction.model.dto;

import com.exchange.manager.exchangemanager.transaction.enums.TransactionTypes;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {

    private Long transactionDate;
    private int day;
    private int month;
    private int year;
    private BigDecimal amount;
    private TransactionTypes transactionType;
    private String description;
    private CurrencyType currencyType;


}
