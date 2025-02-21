package at.ac.tuwien.sepr.groupphase.backend.service;

import at.ac.tuwien.sepr.groupphase.backend.entity.ApplicationUser;

public interface PasswordResetTokenService {

    /**
     * Deletes the password reset token for the given user.
     *
     * @param user the user for which the token should be deleted
     */
    void deleteTokenByUser(ApplicationUser user);
}
