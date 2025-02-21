package at.ac.tuwien.sepr.groupphase.backend.service.impl;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;
import at.ac.tuwien.sepr.groupphase.backend.repository.PasswordTokenRepository;
import at.ac.tuwien.sepr.groupphase.backend.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private final PasswordTokenRepository passwordTokenRepository;

    @Autowired
    public PasswordResetTokenServiceImpl(PasswordTokenRepository passwordTokenRepository) {
        this.passwordTokenRepository = passwordTokenRepository;
    }

    @Override
    @Transactional
    public void deleteTokenByUser(ApplicationUser user) {
        passwordTokenRepository.deleteByUser(user);
    }
}