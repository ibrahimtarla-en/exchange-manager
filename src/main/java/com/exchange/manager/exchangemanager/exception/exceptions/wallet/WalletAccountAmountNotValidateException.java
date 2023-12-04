package com.exchange.manager.exchangemanager.exception.exceptions.wallet;

import com.exchange.manager.exchangemanager.exception.enums.IFriendlyMessageCode;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.utils.FriendlyMessageUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class WalletAccountAmountNotValidateException extends RuntimeException {

    private final Language language;
    private final IFriendlyMessageCode friendlyMessageCode;

    public WalletAccountAmountNotValidateException(Language language, IFriendlyMessageCode friendlyMessageCode, String message) {
        super(FriendlyMessageUtils.getFriendlyMessage(language, friendlyMessageCode));
        this.language = language;
        this.friendlyMessageCode = friendlyMessageCode;
        log.error("[WalletAccountAmountNotValidateException] -> message: {} developer message: {}", FriendlyMessageUtils.getFriendlyMessage(language, friendlyMessageCode), message);
    }
}
