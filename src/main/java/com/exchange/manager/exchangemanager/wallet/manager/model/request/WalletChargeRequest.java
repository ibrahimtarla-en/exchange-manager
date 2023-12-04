package com.exchange.manager.exchangemanager.wallet.manager.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class WalletChargeRequest {

    @JsonProperty(value = "amount")
    private BigDecimal amount;

    @JsonProperty(value = "userName")
    private String userName;
}
