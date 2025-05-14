package com.jmc.mazebank.Models;

import java.sql.*;

public class DatabaseDriver {
    private Connection conn;

    public DatabaseDriver() {
        try {
            String s = "jdbc:sqlite:mazebank.db";
            this.conn = DriverManager.getConnection(s);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     /*
    Bagian Client
      */
    public ResultSet getClientData(String pAddress,String password) {
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement = this.conn.createStatement();
            String c ="Select * FROM Clients WHERE PayeeAddress='"+pAddress+"' AND Password='"+password+"';";
            resultSet = statement.executeQuery(c);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
    /*
    Bagian Admin
     */

    /*

    Utility Methods
     */
}
