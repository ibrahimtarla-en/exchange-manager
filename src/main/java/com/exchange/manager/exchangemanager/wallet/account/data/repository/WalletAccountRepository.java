package com.exchange.manager.exchangemanager.wallet.account.data.repository;

import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {

    List<WalletAccount> getAllByUser(User user);

    WalletAccount findByWalletTypeAndUser(CurrencyType currencyType,User user);

}
