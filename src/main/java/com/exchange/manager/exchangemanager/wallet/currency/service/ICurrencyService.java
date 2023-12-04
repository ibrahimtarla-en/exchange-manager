package com.exchange.manager.exchangemanager.wallet.currency.service;

import com.exchange.manager.exchangemanager.wallet.currency.model.CurrencyDto;
import com.exchange.manager.exchangemanager.wallet.enums.Currencies;

import java.util.List;

public interface ICurrencyService {

    CurrencyDto getCurrencyInfosFromCentralBank(String url, Currencies currencies);
    List<CurrencyDto> getAllCurrenciesInfosFromCentralBank(String url);

}
