/**
 * Pr&aacute;ctricas de Sistemas Inform&aacute;ticos II
 * VisaCancelacionJMSBean.java
 */

package ssii2.visa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.ejb.MessageDrivenContext;
import javax.ejb.ActivationConfigProperty;
import javax.jms.MessageListener;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.jms.JMSException;
import javax.annotation.Resource;
import java.util.logging.Logger;

/**
 * @author jaime
 */
@MessageDriven(mappedName = "jms/VisaPagosQueue")
public class VisaCancelacionJMSBean extends DBTester implements MessageListener {
  static final Logger logger = Logger.getLogger("VisaCancelacionJMSBean");
  @Resource
  private MessageDrivenContext mdc;

  private static final String UPDATE_CANCELA_QRY = 
                              "update tarjeta" +
                              " set saldo=?" +
                              " where numeroTarjeta = ?";
   // TODO : Definir UPDATE sobre la tabla pagos para poner
   // codRespuesta a 999 dado un código de autorización

  private static final String UPDATE_CODIGO_QRY = 
                    "update pago" +
                    " set codRespuesta=999" +
                    " where idAutorizacion=?";

  private static final String SELECT_TARJETA_IDAUT_QRY = 
                    "select numeroTarjeta" +
                    " from pago" +
                    " where idAutorizacion = ?";

  private static final String SELECT_PAGO_IMPORTE_QRY = 
                    "select importe" +
                    " from pago" +
                    " where idAutorizacion = ?";

  private static final String SELECT_TARJETA_SALDO_QRY = 
                    "select saldo" +
                    " from tarjeta" +
                    " where numeroTarjeta = ?";



  public VisaCancelacionJMSBean() {
  }

  // TODO : Método onMessage de ejemplo
  // Modificarlo para ejecutar el UPDATE definido más arriba,
  // asignando el idAutorizacion a lo recibido por el mensaje
  // Para ello conecte a la BD, prepareStatement() y ejecute correctamente
  // la actualización
  public void onMessage(Message inMessage) {
      TextMessage msg = null;
      Connection con = null;
      ResultSet rs = null;
      String query;
      PreparedStatement pstmt = null;
      Boolean ret = false;

      int idAutorizacion = 0;
      String numTarjeta = null;
      double importe = 0;
      double saldo = 0;

      try {
          con = getConnection();

          if (inMessage instanceof TextMessage) {
              msg = (TextMessage) inMessage;
              logger.info("MESSAGE BEAN: Message received: " + msg.getText());
              // Lanzamos la query que cambia el codRespuesta en la bd
              query = UPDATE_CODIGO_QRY;
              logger.info(query);
              pstmt = con.prepareStatement(query);
              idAutorizacion = Integer.parseInt(msg.getText());
              pstmt.setInt(1, idAutorizacion);
              if (!pstmt.execute() && pstmt.getUpdateCount() == 1) {
                  ret = true;
              }

              // Query que obtiene el numtarjeta del idaut
              query = SELECT_TARJETA_IDAUT_QRY;
              logger.info(query);
              pstmt = con.prepareStatement(query);
              pstmt.setInt(1, idAutorizacion);
              rs = pstmt.executeQuery();
              if(rs.next()){
                numTarjeta = rs.getString("numeroTarjeta");
              }


              query = SELECT_TARJETA_SALDO_QRY;
              logger.info(query);
              pstmt = con.prepareStatement(query);
              pstmt.setString(1, numTarjeta);
              rs = pstmt.executeQuery();
              if(rs.next()){
                saldo = rs.getDouble("saldo");
              }


              query = SELECT_PAGO_IMPORTE_QRY;
              logger.info(query);
              pstmt = con.prepareStatement(query);
              pstmt.setInt(1, idAutorizacion);
              rs = pstmt.executeQuery();
              if(rs.next()){
                importe = rs.getDouble("importe");
              }

              saldo += importe;

              query = UPDATE_CANCELA_QRY;
              logger.info(query);
              pstmt = con.prepareStatement(query);
              pstmt.setDouble(1, saldo);
              pstmt.setString(2, numTarjeta);
              pstmt.executeUpdate();

          } else {
              logger.warning(
                      "Message of wrong type: "
                      + inMessage.getClass().getName());
          }
      } catch (JMSException e) {
          e.printStackTrace();
          mdc.setRollbackOnly();
      } catch (Throwable te) {
          te.printStackTrace();
      }
  }


}
