package com.jmc.mazebank.Models;

import com.jmc.mazebank.Views.AccountType;
import com.jmc.mazebank.Views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class Model {
    private static Model model;
    private final ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    private AccountType loginAccountType = AccountType.CLIENT;
    private final Client client;
    private boolean clientLoginSuccessFlag;

    private Model() {

        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver();
        //Client Data Section
        this.clientLoginSuccessFlag = false;
        String fName = "", lName = "", pAddress = "";
        Account cAccount = null, sAccount = null;
        LocalDate date = null;
        this.client = new Client(fName, lName, pAddress, null, null, null);
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
                CheckingAccount cAccount = databaseDriver.getCheckingAccount(pAddress);
                SavingsAccount sAccount = databaseDriver.getSavingsAccount(pAddress);
                this.client.checkingAccountProperty().set(cAccount);
                this.client.savingsAccountProperty().set(sAccount);

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

    public ObservableList<Transaction> getTransactions() {
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            List<Transaction> dbTransactions = databaseDriver.getTransactionsForUser(client.pAddressProperty().get());
            transactions.addAll(dbTransactions);
        } catch (Exception e) {
            System.err.println("Error getting transactions: " + e.getMessage());
        }
        return transactions;
    }
    public ObservableList<Transaction> getLatestTransactions(int limit) {
        ObservableList<Transaction> latestTransactions = FXCollections.observableArrayList();
        String payeeAddress = this.client.pAddressProperty().get();

        if (payeeAddress == null || payeeAddress.isEmpty()) {
            System.err.println("Payee address is not set!");
            return createDefaultTransactionList();
        }

        String query = "SELECT * FROM transactions " +
                "WHERE sender = ? OR receiver = ? " +
                "ORDER BY date DESC, id DESC " +
                "LIMIT ?";

        try (Connection conn = databaseDriver.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, payeeAddress);
            pstmt.setString(2, payeeAddress);
            pstmt.setInt(3, limit);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                latestTransactions.add(createTransactionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            return createDefaultTransactionList(payeeAddress);
        }

        if (latestTransactions.isEmpty()) {
            return createDefaultTransactionList(payeeAddress);
        }

        return latestTransactions;
    }

    private Transaction createTransactionFromResultSet(ResultSet rs) throws SQLException {
        return new Transaction(
                rs.getString("sender"),
                rs.getString("receiver"),
                rs.getDouble("amount"),
                rs.getDate("date").toLocalDate(),
                rs.getString("message")
        );
    }

    private ObservableList<Transaction> createDefaultTransactionList() {
        return FXCollections.observableArrayList(
                new Transaction("System", "User", 0, LocalDate.now(),
                        "No transactions available")
        );
    }

    private ObservableList<Transaction> createDefaultTransactionList(String payeeAddress) {
        return FXCollections.observableArrayList(
                new Transaction("System", payeeAddress, 0, LocalDate.now(),
                        "No transactions available")
        );
    }

}
