package com.exchange.manager.exchangemanager.wallet.manager.controller;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.utils.FriendlyMessageUtils;
import com.exchange.manager.exchangemanager.transaction.enums.ReportRangeType;
import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import com.exchange.manager.exchangemanager.wallet.currency.model.CurrencyDto;
import com.exchange.manager.exchangemanager.wallet.manager.delegate.WalletAccountDelegateService;
import com.exchange.manager.exchangemanager.wallet.manager.model.request.WalletAccountRequest;
import com.exchange.manager.exchangemanager.wallet.manager.model.request.WalletChargeRequest;
import com.exchange.manager.exchangemanager.wallet.manager.model.response.FriendlyMessage;
import com.exchange.manager.exchangemanager.wallet.manager.model.response.InternalApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/1.0/exchange")
public class WalletAccountManagementController {
    private final WalletAccountDelegateService service;

    public WalletAccountManagementController(WalletAccountDelegateService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<String> helloWorld() {
        return ResponseEntity.ok("Welcome Dashboard");
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{language}/buy")
    public InternalApiResponse<TransactionDto> buyBanknote(@PathVariable("language") Language language,
                                                           @RequestBody WalletAccountRequest request) {
        TransactionDto transactionDto = service.buyCurrency(request);


        return InternalApiResponse.<TransactionDto>builder()
                .friendlyMessage(FriendlyMessage.builder()
                        .title(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.SUCCESS))
                        .description(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.WALLET_ACCOUNT_SUCCESSFULLY_UPDATED))
                        .build())
                .httpStatus(HttpStatus.OK)
                .hasError(false)
                .payload(transactionDto)
                .build();

    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{language}/charge")
    public InternalApiResponse<TransactionDto> chargeWallet(@PathVariable("language") Language language,
                                                            @RequestBody WalletChargeRequest request) {
        TransactionDto transactionDto = service.chargeWallet(request);


        return InternalApiResponse.<TransactionDto>builder()
                .friendlyMessage(FriendlyMessage.builder()
                        .title(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.SUCCESS))
                        .description(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.WALLET_ACCOUNT_SUCCESSFULLY_UPDATED))
                        .build())
                .httpStatus(HttpStatus.OK)
                .hasError(false)
                .payload(transactionDto)
                .build();

    }


    @ResponseStatus(HttpStatus.OK)
    @PostMapping(value = "/{language}/sell")
    public InternalApiResponse<TransactionDto> sellBanknote(@PathVariable("language") Language language,
                                                            @RequestBody WalletAccountRequest request) {
        TransactionDto transactionDto = service.sellCurrency(request);


        return InternalApiResponse.<TransactionDto>builder()
                .friendlyMessage(FriendlyMessage.builder()
                        .title(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.SUCCESS))
                        .description(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.WALLET_ACCOUNT_SUCCESSFULLY_UPDATED))
                        .build())
                .httpStatus(HttpStatus.OK)
                .hasError(false)
                .payload(transactionDto)
                .build();

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{language}/{rangeType}/report")
    public InternalApiResponse<List<TransactionDto>> reportTransactions(@PathVariable("language") Language language,
                                                                        @PathVariable("rangeType") ReportRangeType rangeType,
                                                                        @RequestBody WalletAccountRequest request) {

        List<TransactionDto> transactionDtoList = service.getTransactions(request, rangeType);


        return InternalApiResponse.<List<TransactionDto>>builder()
                .friendlyMessage(FriendlyMessage.builder()
                        .title(FriendlyMessageUtils.getFriendlyMessage(Language.TR, FriendlyMessageCodes.SUCCESS))
                        .description(FriendlyMessageUtils.getFriendlyMessage(Language.TR, FriendlyMessageCodes.WALLET_TRANSACTION_INFORMATION_RETRIEVE))
                        .build())
                .httpStatus(HttpStatus.OK)
                .hasError(false)
                .payload(transactionDtoList)
                .build();

    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping(value = "/{language}/currencies/view")
    public InternalApiResponse<List<CurrencyDto>> viewAllCurrenciesInfos(@PathVariable("language") Language language) {

        List<CurrencyDto> currencyDtoList = service.getAllCurrencyInfosFromCentralBank();


        return InternalApiResponse.<List<CurrencyDto>>builder()
                .friendlyMessage(FriendlyMessage.builder()
                        .title(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.SUCCESS))
                        .description(FriendlyMessageUtils.getFriendlyMessage(language, FriendlyMessageCodes.CURRENT_EXCHANGE_RATE_INFORMATION_RETRIEVE))
                        .build())
                .httpStatus(HttpStatus.OK)
                .hasError(false)
                .payload(currencyDtoList)
                .build();

    }
}
