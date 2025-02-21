package at.ac.tuwien.sepr.groupphase.backend.service;

public interface SecurityService {

    /**
     * Validates a password reset token.
     *
     * @param token the token to validate
     * @return null if the token is valid, an error message otherwise
     */
    String validatePasswordResetToken(String token);
}
