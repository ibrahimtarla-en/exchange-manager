package com.exchange.manager.exchangemanager.wallet.manager.delegate;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.exceptions.exchange.CurrencyExchangeRateValidationException;
import com.exchange.manager.exchangemanager.exception.exceptions.wallet.WalletAccountNotEnoughMoneyException;
import com.exchange.manager.exchangemanager.transaction.data.entity.Transaction;
import com.exchange.manager.exchangemanager.transaction.enums.ReportRangeType;
import com.exchange.manager.exchangemanager.transaction.enums.TransactionTypes;
import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import com.exchange.manager.exchangemanager.transaction.service.ITransactionService;
import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.user.service.authentication.UserService;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.account.model.dto.WalletAccountDto;
import com.exchange.manager.exchangemanager.wallet.account.service.IWalletAccountService;
import com.exchange.manager.exchangemanager.wallet.currency.model.CurrencyDto;
import com.exchange.manager.exchangemanager.wallet.currency.service.ICurrencyService;
import com.exchange.manager.exchangemanager.wallet.enums.Currencies;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import com.exchange.manager.exchangemanager.wallet.manager.model.request.WalletAccountRequest;
import com.exchange.manager.exchangemanager.wallet.manager.model.request.WalletChargeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletAccountDelegateService {

    @Value("${central.bank.api.url}")
    private String url;

    private final UserService userService;
    private final IWalletAccountService iWalletAccountService;
    private final ITransactionService iTransactionService;
    private final ICurrencyService currencyService;


    public TransactionDto buyCurrency(WalletAccountRequest accountRequest) {
        TransactionDto transactionDto;
        Currencies currency = mapCurrencies(accountRequest.getCurrencyType());

        CurrencyDto currencyDto = currencyService.getCurrencyInfosFromCentralBank(url, currency);


        User user = userService.getUserByUserName(accountRequest.getUserName());
        List<WalletAccount> walletAccountList = user.getWalletAccounts();

        WalletAccount turkishAccount = iWalletAccountService.getWalletAccountByCurrencyType(CurrencyType.TURKISH_LIRA, user);
        BigDecimal buyAmount = multiplyAmount(BigDecimal.valueOf(currencyDto.getCurrencySellingPrice()), accountRequest.getCount());


        if (!hasEnoughAmountForTransaction(buyAmount, turkishAccount)) {

            throw new WalletAccountNotEnoughMoneyException(Language.TR, FriendlyMessageCodes.WALLET_ACCOUNT_NOT_ENOUGH_MONEY_EXCEPTION, "user request: " + accountRequest);

        } else {

            //create wallet
            if (!isExistWalletAccount(accountRequest.getCurrencyType(), walletAccountList)) {
                try {
                    Thread.sleep(5000);
                    verifyExchangeRate(currencyDto, accountRequest.getCurrencyType());
                    turkishAccount.setAmount(turkishAccount.getAmount().subtract(buyAmount));
                    iWalletAccountService.updateWalletAccount(turkishAccount);
                    WalletAccount walletAccount = createWalletAccount(accountRequest, user, accountRequest.getCount());
                    Transaction transaction = createTransaction(accountRequest, walletAccount, accountRequest.getCount());
                    transactionDto = mapTransactionDto(transaction);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
            //Update Wallet Account
            else {

                try {
                    Thread.sleep(5000);
                    verifyExchangeRate(currencyDto, accountRequest.getCurrencyType());
                    WalletAccount walletAccount = iWalletAccountService.getWalletAccountByCurrencyType(accountRequest.getCurrencyType(), user);
                    turkishAccount.setAmount(turkishAccount.getAmount().subtract(buyAmount));
                    iWalletAccountService.updateWalletAccount(turkishAccount);
                    walletAccount.setAmount(accountRequest.getCount().add(walletAccount.getAmount()));
                    iWalletAccountService.updateWalletAccount(walletAccount);
                    Transaction transaction = createTransaction(accountRequest, walletAccount, accountRequest.getCount());
                    transactionDto = mapTransactionDto(transaction);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }


            }
        }

        return transactionDto;
    }

    public TransactionDto chargeWallet(WalletChargeRequest request) {
        User user = userService.getUserByUserName(request.getUserName());
        WalletAccount turkishAccount = iWalletAccountService.getWalletAccountByCurrencyType(CurrencyType.TURKISH_LIRA, user);
        turkishAccount.setAmount(request.getAmount().add(turkishAccount.getAmount()));
        TransactionDto transactionDto= TransactionDto.builder()
                .currencyType(CurrencyType.TURKISH_LIRA)
                .day(LocalDate.now().getDayOfMonth())
                .month(LocalDate.now().getMonthValue())
                .year(LocalDate.now().getYear())
                .amount(request.getAmount())
                .transactionDate(System.currentTimeMillis())
                .description(initializeTransactionMessage(CurrencyType.TURKISH_LIRA, TransactionTypes.CHARGE, request.getAmount()))
                .transactionType(TransactionTypes.CHARGE)
                .build();

        Transaction responseTransaction = iTransactionService.createTransaction(transactionDto,turkishAccount);

        return mapTransactionDto(responseTransaction);

    }

    private void verifyExchangeRate(CurrencyDto tempCurrencyDto, CurrencyType currencyType) {

        Currencies currency = mapCurrencies(currencyType);

        CurrencyDto currencyDto = currencyService.getCurrencyInfosFromCentralBank(url, currency);

        if (tempCurrencyDto.getCurrencySellingPrice() != currencyDto.getCurrencySellingPrice()) {
            throw new CurrencyExchangeRateValidationException(Language.TR, FriendlyMessageCodes.CURRENCY_EXCHANGE_RATE_VALIDATE_EXCEPTION, "user request: " + currency);

        }
    }

    public TransactionDto sellCurrency(WalletAccountRequest accountRequest) {
        TransactionDto transactionDto;
        User user = userService.getUserByUserName(accountRequest.getUserName());
        Currencies currency = mapCurrencies(accountRequest.getCurrencyType());

        CurrencyDto currencyDto = currencyService.getCurrencyInfosFromCentralBank(url, currency);

        WalletAccount turkishAccount = iWalletAccountService.getWalletAccountByCurrencyType(CurrencyType.TURKISH_LIRA, user);
        WalletAccount selectedWalletaccount = iWalletAccountService.getWalletAccountByCurrencyType(accountRequest.getCurrencyType(), user);
        BigDecimal totalAmount = multiplyAmount(BigDecimal.valueOf(currencyDto.getCurrencyBuyingPrice()), accountRequest.getCount());

        if (!hasEnoughAmountForTransaction(accountRequest.getCount(), selectedWalletaccount)) {
            throw new WalletAccountNotEnoughMoneyException(Language.TR, FriendlyMessageCodes.WALLET_ACCOUNT_NOT_ENOUGH_MONEY_EXCEPTION, "user request: " + accountRequest);
        } else {
            try {
                Thread.sleep(5000);
                verifyExchangeRate(currencyDto, accountRequest.getCurrencyType());
                turkishAccount.setAmount(totalAmount.add(turkishAccount.getAmount()));
                iWalletAccountService.updateWalletAccount(turkishAccount);
                selectedWalletaccount.setAmount(selectedWalletaccount.getAmount().subtract(accountRequest.getCount()));
                iWalletAccountService.updateWalletAccount(selectedWalletaccount);
                Transaction transaction = createTransaction(accountRequest, selectedWalletaccount, accountRequest.getCount());
                transactionDto = mapTransactionDto(transaction);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


        }


        return transactionDto;
    }

    public List<TransactionDto> getTransactions(WalletAccountRequest accountRequest, ReportRangeType rangeType) {

        List<TransactionDto> transactionDtoList = new ArrayList<>();
        User user = userService.getUserByUserName(accountRequest.getUserName());
        WalletAccount walletAccount = iWalletAccountService.getWalletAccountByCurrencyType(accountRequest.getCurrencyType(), user);
        List<Transaction> transactionList = walletAccount.getTransactions();
        transactionList.forEach(item -> {
            if (rangeType.equals(ReportRangeType.DAILY)) {
                if (item.getDay() == LocalDate.now().getDayOfMonth() && item.getMonth() == LocalDate.now().getMonthValue()) {
                    transactionDtoList.add(mapTransactionDto(item));
                }
            }
            if (rangeType.equals(ReportRangeType.MONTHLY)) {
                if (item.getMonth() == LocalDate.now().getMonthValue() && item.getYear() == LocalDate.now().getYear()) {
                    transactionDtoList.add(mapTransactionDto(item));
                }
            }
            if (rangeType.equals(ReportRangeType.YEARLY)) {
                if (item.getYear() == LocalDate.now().getYear()) {
                    transactionDtoList.add(mapTransactionDto(item));
                }
            }
        });

        return transactionDtoList;
    }

    public List<CurrencyDto> getAllCurrencyInfosFromCentralBank() {

        return currencyService.getAllCurrenciesInfosFromCentralBank(url);

    }


    TransactionDto mapTransactionDto(Transaction transaction) {

        return TransactionDto.builder()
                .currencyType(transaction.getCurrencyType())
                .transactionDate(transaction.getTransactionDate())
                .day(transaction.getDay())
                .month(transaction.getMonth())
                .year(transaction.getYear())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .transactionType(transaction.getTransactionType())
                .build();
    }


    private BigDecimal multiplyAmount(BigDecimal amount, BigDecimal count) {
        return count.multiply(amount);
    }

    private BigDecimal divideAmount(BigDecimal amount, BigDecimal count) {
        return amount.divide(count);
    }

    private boolean isExistWalletAccount(CurrencyType currencyType, List<WalletAccount> walletAccountList) {
        Boolean isExist = false;

        for (int i = 0; i < walletAccountList.size(); i++) {
            if (walletAccountList.get(i).getWalletType().equals(currencyType)) {
                isExist = true;
            }
        }
        return isExist;

    }

    public boolean hasEnoughAmountForTransaction(BigDecimal amount, WalletAccount walletAccount) {

        int result = amount.compareTo(walletAccount.getAmount());
        return result != 1;
    }

    public WalletAccount createWalletAccount(WalletAccountRequest accountRequest, User user, BigDecimal amount) {
        WalletAccountDto walletAccount = WalletAccountDto.builder()
                .walletType(accountRequest.getCurrencyType())
                .amount(amount)
                .user(user)
                .build();
        return iWalletAccountService.createWalletAccount(walletAccount);
    }

    public Transaction createTransaction(WalletAccountRequest accountRequest, WalletAccount walletAccount, BigDecimal amount) {
        TransactionDto transaction = TransactionDto.builder()
                .transactionDate(System.currentTimeMillis())
                .year(LocalDate.now().getYear())
                .month(LocalDate.now().getMonthValue())
                .day(LocalDate.now().getDayOfMonth())
                .currencyType(accountRequest.getCurrencyType())
                .transactionType(accountRequest.getTransactionType())
                .description(initializeTransactionMessage(accountRequest.getCurrencyType(), accountRequest.getTransactionType(), amount))
                .amount(amount)
                .build();
        return iTransactionService.createTransaction(transaction, walletAccount);
    }

    private Currencies mapCurrencies(CurrencyType currencyType) {

        if (currencyType.equals(CurrencyType.US_DOLLAR)) {
            return Currencies.US_DOLLAR;
        } else if (currencyType.equals(CurrencyType.AUSTRALIAN_DOLLAR)) {
            return Currencies.AUSTRALIAN_DOLLAR;
        } else if (currencyType.equals(CurrencyType.DANISH_KRONE)) {
            return Currencies.DANISH_KRONE;
        } else if (currencyType.equals(CurrencyType.EURO)) {
            return Currencies.EURO;
        } else if (currencyType.equals(CurrencyType.POUND_STERLING)) {
            return Currencies.POUND_STERLING;
        } else if (currencyType.equals(CurrencyType.SWISS_FRANK)) {
            return Currencies.SWISS_FRANK;
        } else if (currencyType.equals(CurrencyType.SWEDISH_KRONA)) {
            return Currencies.SWEDISH_KRONA;
        } else if (currencyType.equals(CurrencyType.CANADIAN_DOLLAR)) {
            return Currencies.CANADIAN_DOLLAR;
        } else if (currencyType.equals(CurrencyType.KUWAITI_DINAR)) {
            return Currencies.KUWAITI_DINAR;
        } else if (currencyType.equals(CurrencyType.NORWEGIAN_KRONE)) {
            return Currencies.NORWEGIAN_KRONE;
        } else if (currencyType.equals(CurrencyType.SAUDI_RIYAL)) {
            return Currencies.SAUDI_RIYAL;
        } else if (currencyType.equals(CurrencyType.JAPANESE_YEN)) {
            return Currencies.JAPANESE_YEN;
        } else if (currencyType.equals(CurrencyType.BULGARIAN_LEV)) {
            return Currencies.BULGARIAN_LEV;
        } else if (currencyType.equals(CurrencyType.NEW_LEU)) {
            return Currencies.NEW_LEU;
        } else if (currencyType.equals(CurrencyType.RUSSIAN_ROUBLE)) {
            return Currencies.RUSSIAN_ROUBLE;
        } else if (currencyType.equals(CurrencyType.IRANIAN_RIAL)) {
            return Currencies.IRANIAN_RIAL;
        } else if (currencyType.equals(CurrencyType.CHINESE_RENMINBI)) {
            return Currencies.CHINESE_RENMINBI;
        } else if (currencyType.equals(CurrencyType.PAKISTANI_RUPEE)) {
            return Currencies.PAKISTANI_RUPEE;
        } else if (currencyType.equals(CurrencyType.QATARI_RIAL)) {
            return Currencies.QATARI_RIAL;
        }
        return Currencies.TURKISH_LIRA;
    }

    private String initializeTransactionMessage(CurrencyType currencyType, TransactionTypes transactionTypes, BigDecimal amount) {

        return amount.toString() + " " + currencyType + " " + transactionTypes.toString() + " işlemi gerçekleşmiştir";
    }


}
