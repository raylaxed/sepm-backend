package at.ac.tuwien.sepr.groupphase.backend.exception;

/**
 * Exception used to signal unexpected and unrecoverable errors.
 */
public class FatalException extends RuntimeException {
    /**
     * Constructs a new FatalException with the specified detail message.
     *
     * @param message the detail message
     */
    public FatalException(String message) {
        super(message);
    }

    /**
     * Constructs a new FatalException with the specified cause.
     *
     * @param cause the cause of the exception
     */
    public FatalException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new FatalException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of the exception
     */
    public FatalException(String message, Throwable cause) {
        super(message, cause);
    }
}
