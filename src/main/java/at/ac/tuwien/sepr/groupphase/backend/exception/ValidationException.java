package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

/**
 * Exception that signals, that data,
 * that came from outside the backend, is invalid.
 * The data violates some invariant constraint
 * (rather than one, that is imposed by the current data in the system).
 * Contains a list of all validations that failed when validating the piece of
 * data in question.
 */
public class ValidationException extends ErrorListException {
    /**
     * Constructs a new ValidationException with the specified message summary and
     * list of errors.
     *
     * @param messageSummary a brief summary of the validation failure
     * @param errors         a list of detailed error messages describing each
     *                       validation failure
     */
    public ValidationException(String messageSummary, List<String> errors) {
        super("Failed validations", messageSummary, errors);
    }
}
