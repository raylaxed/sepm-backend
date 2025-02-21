package at.ac.tuwien.sepr.groupphase.backend.repository;

import at.ac.tuwien.sepr.groupphase.backend.entity.CancellationInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for managing {@link CancellationInvoice} entities.
 * Provides methods for CRUD operations related to cancellation invoices.
 * Cancellation invoices are generated when users cancel their ticket orders,
 * documenting the refund amount and cancellation details.
 */
@Repository
public interface CancellationInvoiceRepository extends JpaRepository<CancellationInvoice, Long> {
}