package com.exchange.manager.exchangemanager.user.service.authentication;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.exceptions.user.UserNotAuthenticationException;
import com.exchange.manager.exchangemanager.exception.exceptions.user.UserNotCreatedException;
import com.exchange.manager.exchangemanager.transaction.enums.TransactionTypes;
import com.exchange.manager.exchangemanager.transaction.model.dto.TransactionDto;
import com.exchange.manager.exchangemanager.transaction.service.ITransactionService;
import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.user.data.repository.UserRepository;
import com.exchange.manager.exchangemanager.user.model.dto.UserDto;
import com.exchange.manager.exchangemanager.user.model.enums.Role;
import com.exchange.manager.exchangemanager.user.model.request.UserRequest;
import com.exchange.manager.exchangemanager.user.model.response.UserResponse;
import com.exchange.manager.exchangemanager.user.service.jwt.JwtService;
import com.exchange.manager.exchangemanager.wallet.account.data.entity.WalletAccount;
import com.exchange.manager.exchangemanager.wallet.account.model.dto.WalletAccountDto;
import com.exchange.manager.exchangemanager.wallet.account.service.IWalletAccountService;
import com.exchange.manager.exchangemanager.wallet.enums.CurrencyType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;

    private final IWalletAccountService iWalletAccountService;

    private final ITransactionService iTransactionService;


    public UserResponse save(UserDto userDto) {

        log.debug("[{}][createUser] -> request: {}", this.getClass().getSimpleName(), userDto);

        try {
            User user = User.builder()
                    .username(userDto.getUsername())
                    .password(passwordEncoder.encode(userDto.getPassword()))
                    .nameSurname(userDto.getNameSurname())
                    .role(Role.USER).build();
            User userResponse = userRepository.save(user);

            WalletAccount walletAccount = createADefaultWalletAccountForUser(userResponse);
            createATransactionForDefaultWalletAccount(walletAccount);

            var token = jwtService.generateToken(user);
            return UserResponse.builder().token(token).build();
        } catch (Exception exception) {
            throw new UserNotCreatedException(Language.TR, FriendlyMessageCodes.USER_NOT_CREATED_EXCEPTION, "user request: " + userDto);
        }
    }

    public WalletAccount createADefaultWalletAccountForUser(User user) {

        WalletAccountDto walletAccount = WalletAccountDto.builder()
                .walletType(CurrencyType.TURKISH_LIRA)
                .amount(BigDecimal.valueOf(1000))
                .user(user)
                .build();
        return iWalletAccountService.createWalletAccount(walletAccount);
    }

    public void createATransactionForDefaultWalletAccount(WalletAccount walletAccount) {

        TransactionDto transaction = TransactionDto.builder()
                .transactionDate(System.currentTimeMillis())
                .day(LocalDate.now().getDayOfMonth())
                .month(LocalDate.now().getMonthValue())
                .year(LocalDate.now().getYear())
                .currencyType(CurrencyType.TURKISH_LIRA)
                .transactionType(TransactionTypes.BUYING)
                .description("When the user account was first created, 100 TL was loaded ")
                .amount(walletAccount.getAmount())
                .build();
        iTransactionService.createTransaction(transaction,walletAccount);

    }

    public UserResponse auth(UserRequest userRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword()));
            User user = userRepository.findByUsername(userRequest.getUsername()).orElseThrow();
            String token = jwtService.generateToken(user);
            return UserResponse.builder().token(token).build();
        } catch (Exception exception) {
            throw new UserNotAuthenticationException(Language.TR, FriendlyMessageCodes.USER_NOT_AUTHENTICATION_EXCEPTION, "user request: " + userRequest);

        }


    }
}
