package com.exchange.manager.exchangemanager.wallet.currency.model;

import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyDto {

    private String currencyType;
    private  float currencySellingPrice;
    private  float currencyBuyingPrice;

}
