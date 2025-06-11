package com.jmc.mazebank.Models;

import com.jmc.mazebank.Views.AccountType;
import com.jmc.mazebank.Views.ViewFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    private AccountType loginAccountType = AccountType.CLIENT;
    //Client Data section
    private final Client client;
    private boolean clientLoginSuccessFlag;
    //Admin data Section


    private Model() {

        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        //Client Data Section
        this.clientLoginSuccessFlag = false;
        String fName = "", lName = "", pAddress = "";
        Account cAccount = null, sAccount = null;
        LocalDate date = null;
        this.client = new Client(fName, lName, pAddress, null, null, null);
        //Admin data section
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    public AccountType getLoginAccountType() {
        return loginAccountType;
    }

    public void setLoginAccountType(AccountType loginAccountType) {
        this.loginAccountType = loginAccountType;
    }

    /*
      Clients Method Section
    */
    public boolean getClientLoginSuccessFlag() {
        return this.clientLoginSuccessFlag;
    }

    public void setClientLoginSuccessFlag(boolean flag) {
        this.clientLoginSuccessFlag = flag;
    }

    public Client getClient() {
        return client;
    }

    public void evaluateClientCred(String pAddress, String password) {
        ResultSet resultSet = databaseDriver.getClientData(pAddress, password);
        try {
            if (resultSet != null && resultSet.next()) {
                this.client.firstNameProperty().set(resultSet.getString("FirstName"));
                this.client.lastNameProperty().set(resultSet.getString("LastName"));
                this.client.pAddressProperty().set(resultSet.getString("PayeeAddress"));

                String dateStr = resultSet.getString("Date");
                if (dateStr != null) {
                    String[] dateParts = dateStr.split("-");
                    LocalDate date = LocalDate.of(
                            Integer.parseInt(dateParts[0]),
                            Integer.parseInt(dateParts[1]),
                            Integer.parseInt(dateParts[2])
                    );
                    this.client.dateProperty().set(date);
                }

                this.clientLoginSuccessFlag = true;
            } else {
                this.clientLoginSuccessFlag = false;
            }
        } catch (Exception e) {
            System.err.println("Error evaluasi kredensial: " + e.getMessage());
            this.clientLoginSuccessFlag = false;
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.getStatement().getConnection().close();
                }
            } catch (SQLException e) {
                System.err.println("Error menutup koneksi: " + e.getMessage());
            }
        }
    }
}
