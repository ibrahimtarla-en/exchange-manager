package com.exchange.manager.exchangemanager.user.service.authentication;

import com.exchange.manager.exchangemanager.exception.enums.FriendlyMessageCodes;
import com.exchange.manager.exchangemanager.exception.enums.Language;
import com.exchange.manager.exchangemanager.exception.exceptions.user.UserNotFoundException;
import com.exchange.manager.exchangemanager.user.data.entity.User;
import com.exchange.manager.exchangemanager.user.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public User getUserByUserName(String userName) {

        return userRepository.findByUsername(userName)
                .orElseThrow(() -> new UserNotFoundException(Language.TR, FriendlyMessageCodes.USER_NOT_FOUND_EXCEPTION, "user request: " + userName));

    }
}
