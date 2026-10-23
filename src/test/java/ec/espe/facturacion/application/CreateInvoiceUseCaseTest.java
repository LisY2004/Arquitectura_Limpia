package ec.espe.facturacion.application;
import ec.espe.facturacion.adapters.out.InMemoryInvoiceRepository;
import ec.espe.facturacion.domain.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
class CreateInvoiceUseCaseTest {
 private CreateInvoiceUseCase useCase(InMemoryInvoiceRepository repo){return new CreateInvoiceUseCase(repo,new BigDecimal("0.15"),new BigDecimal("1000"));}
 private CreateInvoiceCommand command(String id,String customer){return new CreateInvoiceCommand(id,customer,List.of(new InvoiceLine("Servicio",2,new BigDecimal("25.50"))));}
 @Test void calculatesTotalsAndApprovesInvoice(){var invoice=useCase(new InMemoryInvoiceRepository()).execute(command("op-1","CLI-1"));assertEquals(new BigDecimal("51.00"),invoice.subtotal());assertEquals(new BigDecimal("7.65"),invoice.tax());assertEquals(new BigDecimal("58.65"),invoice.total());assertEquals("APPROVED",invoice.status());}
 @Test void sameOperationIsIdempotent(){var useCase=useCase(new InMemoryInvoiceRepository());var first=useCase.execute(command("op-2","CLI-2"));var second=useCase.execute(command("op-2","CLI-2"));assertEquals(first.id(),second.id());}
 @Test void rejectsRiskCustomer(){assertThrows(BusinessRuleException.class,()->useCase(new InMemoryInvoiceRepository()).execute(command("op-3","RISK-9")));}
 @Test void marksLargeSubtotalForReview(){var uc=new CreateInvoiceUseCase(new InMemoryInvoiceRepository(),new BigDecimal("0.15"),new BigDecimal("1000"));var cmd=new CreateInvoiceCommand("op-4","CLI-4",List.of(new InvoiceLine("Equipo",1,new BigDecimal("1200"))));assertEquals("PENDING_REVIEW",uc.execute(cmd).status());}
}
