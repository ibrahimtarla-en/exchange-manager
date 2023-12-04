package com.exchange.manager.exchangemanager.wallet.account.service.impl;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.exceptions.wallet.WalletAccountNotCreatedException;
import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.account.data.repository.WalletAccountRepository;
import com.exchange.manager.exchangemanager.wallet.account.model.dto.WalletAccountDto;
import com.exchange.manager.exchangemanager.wallet.account.service.IWalletAccountService;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WalletAccountServiceImpl implements IWalletAccountService {

    private final WalletAccountRepository repository;

    @Override
    public WalletAccount createWalletAccount(WalletAccountDto walletAccountDto) {

        try {
            WalletAccount walletAccount = WalletAccount.builder()
                    .walletType(walletAccountDto.getWalletType())
                    .amount(walletAccountDto.getAmount())
                    .user(walletAccountDto.getUser())
                    .build();
            return repository.save(walletAccount);
        } catch (Exception exception) {
            throw new WalletAccountNotCreatedException(Language.TR, FriendlyMessageCodes.WALLET_ACCOUNT_CREATED_EXCEPTION, "user request: " + walletAccountDto);

        }
    }

    @Override
    public WalletAccount updateWalletAccount(WalletAccount walletAccount) {
        try {
            return repository.save(walletAccount);
        } catch (Exception exception) {
            throw new WalletAccountNotCreatedException(Language.TR, FriendlyMessageCodes.WALLET_ACCOUNT_CREATED_EXCEPTION, "user request: " + walletAccount);

        }
    }

    @Override
    public WalletAccount getWalletAccountByCurrencyType(CurrencyType currencyType,User user) {

        return repository.findByWalletTypeAndUser(currencyType,user);
    }

    @Override
    public List<WalletAccount> getAllWalletAccountsByUser(User user) {

        return repository.getAllByUser(user);
    }
}
