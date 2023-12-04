package com.exchange.manager.exchangemanager.wallet.account.data.entity;

import com.exchange.manager.exchangemanager.transaction.data.entity.Transaction;
import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallet_accounts")
public class WalletAccount {


    @Id
    @Column(name = "wallet_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "wallet_type")
    private CurrencyType walletType;

    @Column(name = "amount")
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "walletAccount")
    @JsonManagedReference
    private List<Transaction> transactions;

}
