package com.exchange.manager.exchangemanager.wallet.account.model.dto;

import com.exchange.manager.exchangemanager.user.data.entity.User;
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
public class WalletAccountDto {

    private CurrencyType walletType;
    private BigDecimal amount;
    private User user;
}
