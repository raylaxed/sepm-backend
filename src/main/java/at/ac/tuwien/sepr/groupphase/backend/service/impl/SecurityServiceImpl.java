package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.PasswordResetToken;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.SecurityService;
import org.springframework.stereotype.Service;
import java.util.Calendar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SecurityServiceImpl implements SecurityService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityService.class);

    private final PasswordTokenRepository passwordTokenRepository;

    public SecurityServiceImpl(PasswordTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    public String validatePasswordResetToken(String token) {
        LOGGER.info("Validating token: {}", token);
        PasswordResetToken passToken = passwordTokenRepository.findByToken(token);

        if (passToken == null) {
            LOGGER.warn("Token not found in database: {}", token);
            return "Invalid token.";
        }

        if (passToken.getExpiryDate().before(Calendar.getInstance().getTime())) {
            LOGGER.warn("Token has expired: {}", token);
            return "Token has expired.";
        }

        LOGGER.info("Token is valid: {}", token);
        return null;
    }
}
