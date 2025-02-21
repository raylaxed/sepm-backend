package at.ac.tuwien.sepr.groupphase.backend.exception;

/**
 * Exception thrown when a requested resource cannot be found.
 * This is typically used when attempting to retrieve an entity by its identifier
 * and no matching record exists in the database.
 */
public class NotFoundException extends RuntimeException {

    /**
     * Constructs a new NotFoundException with no detail message.
     */
    public NotFoundException() {
    }

    /**
     * Constructs a new NotFoundException with the specified detail message.
     *
     * @param message the detail message explaining the reason for the exception
     */
    public NotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new NotFoundException with the specified detail message and cause.
     *
     * @param message the detail message explaining the reason for the exception
     * @param cause the cause of the exception
     */
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NotFoundException with the specified cause.
     *
     * @param e the cause of the exception
     */
    public NotFoundException(Exception e) {
        super(e);
    }
}
