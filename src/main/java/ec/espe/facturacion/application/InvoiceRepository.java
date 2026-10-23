package ec.espe.facturacion.application;
import ec.espe.facturacion.domain.Invoice; import java.util.Optional;
public interface InvoiceRepository { Optional<Invoice> findByOperationId(String id); void save(Invoice invoice,CreateInvoiceCommand command); }