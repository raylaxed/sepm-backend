package at.ac.tuwien.sepr.groupphase.backend.exception;

import java.util.List;

/**
 * Exception that signals, that data,
 * that came from outside the backend, conflicts with the current state of the
 * system.
 * The data violates some constraint on relationships
 * (rather than an invariant).
 * Contains a list of all conflict checks that failed when validating the piece
 * of data in question.
 */
public class ConflictException extends ErrorListException {
    /**
     * Constructs a new ConflictException with the specified message summary and
     * list of errors.
     *
     * @param messageSummary a brief summary of the conflict
     * @param errors         a list of detailed error messages describing each
     *                       conflict
     */
    public ConflictException(String messageSummary, List<String> errors) {
        super("Conflicts", messageSummary, errors);
    }

}