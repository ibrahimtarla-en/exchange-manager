package com.exchange.manager.exchangemanager.transaction.data.entity;

import com.exchange.manager.exchangemanager.transaction.enums.TransactionTypes;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction {


    @Id
    @Column(name = "transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type")
    TransactionTypes transactionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency_type")
    CurrencyType currencyType;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "description")
    private String description;


    @Column(name = "year")
    private int year;

    @Column(name = "month")
    private int month;

    @Column(name = "day")
    private int day;


    @Column(name = "transaction_date")
    private Long transactionDate;


    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "wallet_id")
    @JsonBackReference
    private WalletAccount walletAccount;

}
