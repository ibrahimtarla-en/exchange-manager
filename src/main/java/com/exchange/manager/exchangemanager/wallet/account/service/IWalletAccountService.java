package com.exchange.manager.exchangemanager.wallet.account.service;

import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.account.model.dto.WalletAccountDto;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;

import java.util.List;
import java.util.Optional;

public interface IWalletAccountService {

    WalletAccount createWalletAccount(WalletAccountDto walletAccountDto);

    WalletAccount updateWalletAccount(WalletAccount walletAccount);

   WalletAccount getWalletAccountByCurrencyType(CurrencyType currencyType,User user);

    List<WalletAccount> getAllWalletAccountsByUser(User user);
}
