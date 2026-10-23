package ec.espe.facturacion.application;
import ec.espe.facturacion.domain.InvoiceLine; import java.util.List;
public record CreateInvoiceCommand(String operationId,String customerId,List<InvoiceLine> lines){public CreateInvoiceCommand{lines=lines==null?List.of():List.copyOf(lines);}}