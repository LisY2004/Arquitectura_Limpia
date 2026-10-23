package ec.espe.facturacion.adapters.in;
import ec.espe.facturacion.application.*; import ec.espe.facturacion.domain.Invoice;
public record InvoiceService(CreateInvoiceUseCase useCase) { public Invoice create(CreateInvoiceCommand c){return useCase.execute(c);} }