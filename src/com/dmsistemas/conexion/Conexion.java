package com.dmsistemas.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private Connection cnportal;
    private Connection cnsae;

    public Connection getCnportal() {
        return cnportal;
    }

    public void setCnportal(Connection cnportal) {
        this.cnportal = cnportal;
    }

    public Connection getCnsae() {
        return cnsae;
    }

    public void setCnsae(Connection cnsae) {
        this.cnsae = cnsae;
    }

    private final String URLSAE = "jdbc:sqlserver://192.168.1.37\\SQLEXPRESS;databaseName=SAE70Empre01";
    private final String URLPORTAL = "jdbc:sqlserver://FTP-DUCHE;databaseName=PortalProvNac";
    private final String PWSAE = "Aspel**2013";
    private final String PWPORTAL = "vxml}}2014";
//    private final String URLSAE = "jdbc:sqlserver://localhost;databaseName=SAE70Empre01";
//    private final String URLPORTAL = "jdbc:sqlserver://localhost;databaseName=PortalProvNac";
//    private final String PWSAE = "dmsis2019*#";
//    private final String PWPORTAL = "dmsis2019*#";
    private final String USR = "sa";

    public void OpenPortal() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cnportal = DriverManager.getConnection(URLPORTAL, USR, PWPORTAL);
        } catch (ClassNotFoundException | SQLException e) {
        }

    }

    public void ClosePortal() throws SQLException {
        try {
            if (cnportal != null) {
                if (cnportal.isClosed() == false) {
                    cnportal.close();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }

    public void OpenSae() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            cnsae = DriverManager.getConnection(URLSAE, USR, PWSAE);
        } catch (ClassNotFoundException | SQLException e) {
        }

    }

    public void CloseSae() throws SQLException {
        try {
            if (cnsae != null) {
                if (cnsae.isClosed() == false) {
                    cnsae.close();
                }
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
