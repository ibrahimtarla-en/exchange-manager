package com.exchange.manager.exchangemanager.wallet.manager.model.response;

import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class WalletAccountResponse {

    TransactionDto transactionDto;

}
