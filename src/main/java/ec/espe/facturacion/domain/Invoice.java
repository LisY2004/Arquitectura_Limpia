package ec.espe.facturacion.domain;
import java.math.BigDecimal; import java.time.Instant; import java.util.UUID;
public record Invoice(UUID id,String operationId,String customerId,BigDecimal subtotal,BigDecimal tax,BigDecimal total,String status,Instant createdAt) {}