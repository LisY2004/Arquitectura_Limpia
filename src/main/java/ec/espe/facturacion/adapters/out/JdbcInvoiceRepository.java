package ec.espe.facturacion.adapters.out;
import ec.espe.facturacion.application.*; import ec.espe.facturacion.domain.Invoice; import java.sql.*; import java.util.*; import javax.sql.DataSource;
public class JdbcInvoiceRepository implements InvoiceRepository {
 private final DataSource ds; public JdbcInvoiceRepository(DataSource ds){this.ds=ds;}
 public Optional<Invoice> findByOperationId(String id){try(var c=ds.getConnection();var s=c.prepareStatement("SELECT id,operation_id,customer_id,subtotal,tax,total,status,created_at FROM invoices WHERE operation_id=?")){s.setString(1,id);try(var r=s.executeQuery()){if(!r.next())return Optional.empty();return Optional.of(new Invoice(UUID.fromString(r.getString(1)),r.getString(2),r.getString(3),r.getBigDecimal(4),r.getBigDecimal(5),r.getBigDecimal(6),r.getString(7),r.getTimestamp(8).toInstant()));}}catch(SQLException e){throw new IllegalStateException("No se pudo consultar factura",e);}}
 public void save(Invoice i,CreateInvoiceCommand cmd){
  try(var c=ds.getConnection()){
   c.setAutoCommit(false);
   try{
    try(var s=c.prepareStatement("INSERT INTO invoices(id,operation_id,customer_id,subtotal,tax,total,status,created_at) VALUES(?,?,?,?,?,?,?,?)")){
     s.setString(1,i.id().toString());s.setString(2,i.operationId());s.setString(3,i.customerId());s.setBigDecimal(4,i.subtotal());s.setBigDecimal(5,i.tax());s.setBigDecimal(6,i.total());s.setString(7,i.status());s.setTimestamp(8,Timestamp.from(i.createdAt()));s.executeUpdate();
    }
    try(var s=c.prepareStatement("INSERT INTO invoice_lines(invoice_id,description,quantity,unit_price) VALUES(?,?,?,?)")){
     for(var l:cmd.lines()){s.setString(1,i.id().toString());s.setString(2,l.description());s.setInt(3,l.quantity());s.setBigDecimal(4,l.unitPrice());s.addBatch();}s.executeBatch();
    }
    c.commit();
   }catch(SQLException e){c.rollback();throw e;}
  }catch(SQLException e){throw new IllegalStateException("No se pudo guardar factura",e);}
 }
}