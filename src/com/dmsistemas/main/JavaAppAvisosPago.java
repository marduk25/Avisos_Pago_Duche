package com.dmsistemas.main;

import com.dmsistemas.conexion.Conexion;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class JavaAppAvisosPago extends Conexion {

    private String CVE_PROV;
    private String NO_FACTURA;
    private String REFER;
    private String DOCTO;
    private String FECHA_APLI;
    private String IMPORTE;
    private String FACTURA;
    private String UUID;
    private String REFERENCIA;
    private String NOMBRE_E;
    private String RFC_E;
    private String CORREO;

    public String getCVE_PROV() {
        return CVE_PROV;
    }

    public void setCVE_PROV(String CVE_PROV) {
        this.CVE_PROV = CVE_PROV;
    }

    public String getNO_FACTURA() {
        return NO_FACTURA;
    }

    public void setNO_FACTURA(String NO_FACTURA) {
        this.NO_FACTURA = NO_FACTURA;
    }

    public String getREFER() {
        return REFER;
    }

    public void setREFER(String REFER) {
        this.REFER = REFER;
    }

    public String getDOCTO() {
        return DOCTO;
    }

    public void setDOCTO(String DOCTO) {
        this.DOCTO = DOCTO;
    }

    public String getFECHA_APLI() {
        return FECHA_APLI;
    }

    public void setFECHA_APLI(String FECHA_APLI) {
        this.FECHA_APLI = FECHA_APLI;
    }

    public String getIMPORTE() {
        return IMPORTE;
    }

    public void setIMPORTE(String IMPORTE) {
        this.IMPORTE = IMPORTE;
    }

    public String getFACTURA() {
        return FACTURA;
    }

    public void setFACTURA(String FACTURA) {
        this.FACTURA = FACTURA;
    }

    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getREFERENCIA() {
        return REFERENCIA;
    }

    public void setREFERENCIA(String REFERENCIA) {
        this.REFERENCIA = REFERENCIA;
    }

    public String getNOMBRE_E() {
        return NOMBRE_E;
    }

    public void setNOMBRE_E(String NOMBRE_E) {
        this.NOMBRE_E = NOMBRE_E;
    }

    public String getRFC_E() {
        return RFC_E;
    }

    public void setRFC_E(String RFC_E) {
        this.RFC_E = RFC_E;
    }

    public String getCORREO() {
        return CORREO;
    }

    public void setCORREO(String CORREO) {
        this.CORREO = CORREO;
    }

    public static void main(String[] args) {
        JavaAppAvisosPago p = new JavaAppAvisosPago();
        p.Pago();

    }

    public void Pago() {
        try {
            this.OpenSae();
            this.OpenPortal();
            Statement st = this.getCnsae().createStatement();
            ResultSet rs = st.executeQuery("SELECT CVE_PROV, REFER, FECHA_APLI FROM INSOFTEC_AVISOS_PAGO WHERE PROCESADO=0 AND REFER LIKE '%WCXP%'");
            if (!rs.isBeforeFirst()) {
                System.out.println("Ningún pago detectado en sistema...");
            } else {
                while (rs.next()) {
                    System.out.println("Pago detectado en sistema...");
                    this.CVE_PROV = rs.getString("CVE_PROV");
                    this.REFER = rs.getString("REFER");
                    this.FECHA_APLI = rs.getString("FECHA_APLI");
                    System.out.println(this.CVE_PROV + "| " + this.REFER + "| " + this.FECHA_APLI);

                    System.out.println("Buscando información del importe pagado...");
                    Statement st1 = this.getCnsae().createStatement();
                    ResultSet rs1 = st1.executeQuery("SELECT IMPORTE FROM PAGA_M01 WHERE REFER='" + this.REFER + "' AND CVE_PROV='" + this.CVE_PROV + "'");
                    if (!rs1.isBeforeFirst()) {
                        System.out.println("____________________________________");
                    } else {
                        while (rs1.next()) {
                            this.IMPORTE = rs1.getString("IMPORTE");
                            System.out.println(this.IMPORTE);
                        }
                    }

                    System.out.println("Buscando datos de la factura pagada...");
                    Statement st2 = this.getCnportal().createStatement();
                    ResultSet rs2 = st2.executeQuery("SELECT FACTURA, UUID, REFERENCIA, NOMBRE_E, RFC_E  FROM FACTURA WHERE FOLIOWCXP='" + this.REFER.replace("WCXP", "") + "'");
                    if (!rs2.isBeforeFirst()) {
                        System.out.println("____________________________________");
                    } else {
                        while (rs2.next()) {
                            this.FACTURA = rs2.getString("FACTURA");
                            this.UUID = rs2.getString("UUID");
                            this.REFERENCIA = rs2.getString("REFERENCIA");
                            this.NOMBRE_E = rs2.getString("NOMBRE_E");
                            this.RFC_E = rs2.getString("RFC_E");
                            System.out.println(this.FACTURA + "| " + this.UUID + "| " + this.REFERENCIA + "| " + this.NOMBRE_E + "| " + this.RFC_E);
                        }
                    }

                    System.out.println("Buscando correo de la factura pagada...");
                    Statement st3 = this.getCnportal().createStatement();
                    ResultSet rs3 = st3.executeQuery("SELECT TOP(1) CORREO FROM USUARIO WHERE RFC='" + this.RFC_E + "'");
                    if (!rs3.isBeforeFirst()) {
                        System.out.println("____________________________________");
                    } else {
                        while (rs3.next()) {
                            this.CORREO = rs3.getString("CORREO");
                            System.out.println(this.CORREO);
                        }
                    }
                    System.out.println("Enviando aviso de pago a proveedor...");
                    try {
                        EnviarCorreo();
                    } catch (MessagingException ex) {
                        Logger.getLogger(JavaAppAvisosPago.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    System.out.println("Actualizando estatus en el portal de la factura pagada...");
                    PreparedStatement ps = this.getCnportal().prepareStatement("UPDATE FACTURA SET ESTATUS='PAGADA', FECHA_PAGO='" + this.FECHA_APLI + "' WHERE FOLIOWCXP='" + this.REFER.replace("WCXP", "") + "'");
                    ps.executeUpdate();

                    PreparedStatement ps1 = this.getCnsae().prepareStatement("UPDATE INSOFTEC_AVISOS_PAGO SET PROCESADO=1  WHERE CVE_PROV='" + this.CVE_PROV + "' AND REFER ='" + this.REFER + "'");
                    ps1.executeUpdate();
                    System.out.println("Proceso actualizado correctamente...");
                    Limpiar();
                }
            }

            this.ClosePortal();
            this.CloseSae();
        } catch (SQLException ex) {
            Logger.getLogger(JavaAppAvisosPago.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void EnviarCorreo() throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.alestraune.net.mx");
        props.setProperty("mail.smtp.starttls.enable", "true");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.user", "portalproveedores@duche.com");
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);

        BodyPart texto = new MimeBodyPart();
        texto.setContent("<html><head><title></title></head>"
                + "<body>"
                + "<table width='878' height='315' border='0' bordercolor='#0000FF' bgcolor='#FFFFFF'>"
                + "<tr>"
                + "<td height='50' colspan='3' bordercolor='#FFFFFF'><br><br></td>"
                + "</tr>"
                + "<tr>"
                + "<td colspan='3' bordercolor='#FFFFFF'><p align='left' style='font-family:calibri; font-size:17px'><font color='#004177'>ESTIMADO PROVEEDOR:  </font><br>"
                + "<font color='#17202a'></font><i><font color='#004177'> " + this.NOMBRE_E + " | " + this.RFC_E + "</font></i><br><br>"
                + "<font color='#17202a'></font><i><font color='#17202a'><b>Nuestro sistema ha pagado el siguiente documento:</b></font></i> <br>"
                + "<font color='#17202a'></font> <font color='#086A87'><i></i></font><br>"
                + "<font color='#17202a'>Factura/Folio fiscal:</font> <font color='#004177'><i> " + this.FACTURA + " | " + this.UUID + " </i></font><br>"
                + "<font color='#17202a'>Recepci&oacute;n:</font> <font color='#004177'><i> " + this.REFERENCIA + " </i></font><br>"
                + "<font color='#17202a'>Cuenta por pagar no:</font> <font color='#004177'><i> " + this.REFER + " </i></font><br>"
                + "<font color='#17202a'>Monto pagado:</font> <font color='#004177'><i> $" + this.IMPORTE + " </i></font><br>"
                + "<font color='#17202a'></font> <font color='#004177'><i> </i></font><br>"
                + "<font color='#17202a'></font> <font color='#004177'><i></i></font><br><br>"
                + "<font color='#17202a'><p></p></font><br>"
                + "<font color='#004177'><b>PORTAL PROVEEDORES | </font><font color='#004177'>COLOIDALES DUCH&Eacute;, S.A. DE C.V.</b></font></td>"
                + "</tr>"
                + "<tr>"
                + "<td width='725' bordercolor='#FFFFFF'><p align='left' style='font-family:calibri; font-size:17px'><br><br>"
                + "<a href='http://ducheproveedores.dyndns.info:9088/proveedores/' target='_blank'><img src='cid:image' width='20%'/></a></td>"
                + "<td width='422' bordercolor='#FFFFFF'></td>"
                + "<td width='422' rowspan='2' bordercolor='#FFFFFF'></td>"
                + "</tr>"
                + "<tr>"
                + "<td colspan='2' bordercolor='#17202a'><br><br><p align='justify' style='font-family:calibri; font-size:16px'>"
                + "<font color='#004177'><br> Favor de no responder a este correo ya que es un aviso del sistema, "
                + "si tiene alguna duda favor de contactar al &aacute;rea de Atenci&oacute;n a proveedores:<br>cuentasporpagartoluca@duche.com<br> "
                + "cuentasporpagarmexico@duche.com<br> amendoza@duche.com<br> bcarrillo@duche.com<br></font></p></td>"
                + "</tr>"
                + "</table>"
                + "</body></html>", "text/html");

        MimeMultipart multiParte = new MimeMultipart();
        BodyPart imagen = new MimeBodyPart();
        DataSource fds = new FileDataSource("C:\\img\\duche.png");
//        DataSource fds = new FileDataSource("/home/dmsistemas/Escritorio/logo2.png");
        imagen.setDataHandler(new DataHandler(fds));
        imagen.setHeader("Content-ID", "<image>");

        multiParte.addBodyPart(texto);
        // multiParte.addBodyPart(adjunto);
        multiParte.addBodyPart(imagen);

        MimeMessage message = new MimeMessage(session);

// Se rellena el From
        message.setFrom(new InternetAddress("portalproveedores@duche.com"));

// Se rellenan los destinatarios
        //message.addRecipients(Message.RecipientType.TO, us.getCorreo());
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(this.CORREO));
        message.addRecipient(Message.RecipientType.CC, new InternetAddress("duche.proveedores@gmail.com"));
//        message.addRecipient(Message.RecipientType.CC, new InternetAddress("cuentasporpagartoluca@duche.com"));
//        message.addRecipient(Message.RecipientType.CC, new InternetAddress("cuentasporpagarmexico@duche.com"));
//        message.addRecipient(Message.RecipientType.CC, new InternetAddress("amendoza@duche.com"));
//        message.addRecipient(Message.RecipientType.CC, new InternetAddress("bcarrillo@duche.com"));

// Se rellena el subject
        message.setSubject("AVISO DE PAGO DE FACTURA");

// Se mete el texto y la foto adjunta.
        message.setContent(multiParte);

        Transport t = session.getTransport("smtp");
        t.connect("portalproveedores@duche.com", "ML310gen11");
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }

    public void Limpiar() {
        CVE_PROV = "";
        NO_FACTURA = "";
        REFER = "";
        DOCTO = "";
        FECHA_APLI = "";
        IMPORTE = "";
        FACTURA = "";
        UUID = "";
        REFERENCIA = "";
        NOMBRE_E = "";
        RFC_E = "";
        CORREO = "";
    }

}
