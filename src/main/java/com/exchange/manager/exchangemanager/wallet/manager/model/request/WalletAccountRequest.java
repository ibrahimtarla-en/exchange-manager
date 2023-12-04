package com.exchange.manager.exchangemanager.wallet.manager.model.request;

import com.exchange.manager.exchangemanager.transaction.enums.TransactionTypes;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class WalletAccountRequest {
    @JsonProperty(value = "currencyType")
    private CurrencyType currencyType;

    @JsonProperty(value = "count")
    private BigDecimal count;

    @JsonProperty(value = "transactionType")
    private TransactionTypes transactionType;

    @JsonProperty(value = "userName")
    private String userName;

}
