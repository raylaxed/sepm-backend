package at.ac.tuwien.sepr.groupphase.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * Exception thrown when a requested operation is not allowed in the current context.
 * This exception maps to HTTP 405 Method Not Allowed status code.
 * Used when an operation is technically valid but not permitted due to business rules
 * or application state.
 */
@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class MethodNotAllowedException extends RuntimeException {

    /**
     * Constructs a new MethodNotAllowedException with the specified detail message and list of errors.
     *
     * @param message the detail message explaining why the method is not allowed
     * @param errors list of specific error messages providing additional context
     */
    public MethodNotAllowedException(String message, List<String> errors) {
        super(message);
    }
}