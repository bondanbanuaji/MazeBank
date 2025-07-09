package com.jmc.mazebank.Models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDriver {
    private static final String DB_URL = "jdbc:sqlite:mazebank.db";

    public DatabaseDriver() {
        initializeDatabase();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void initializeDatabase() {
        String createClientsTable = "CREATE TABLE IF NOT EXISTS Clients (" +
                "PayeeAddress TEXT PRIMARY KEY, " +
                "FirstName TEXT NOT NULL, " +
                "LastName TEXT NOT NULL, " +
                "Password TEXT NOT NULL, " +
                "Date TEXT NOT NULL)";

        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS Transactions (" +
                "Id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "Sender TEXT NOT NULL, " +
                "Receiver TEXT NOT NULL, " +
                "Amount REAL NOT NULL, " +
                "Date TEXT NOT NULL, " +
                "Message TEXT)";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createClientsTable);
            stmt.execute(createTransactionsTable);
        } catch (SQLException e) {
            System.err.println("Gagal inisialisasi database: " + e.getMessage());
        }
    }
    public boolean isValidClient(String pAddress, String password) {
        String sql = "SELECT 1 FROM Clients WHERE PayeeAddress = ? AND Password = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, pAddress);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Error validasi client: " + e.getMessage());
            return false;
        }
    }

    public ResultSet getClientData(String pAddress, String password) {
        String sql = "SELECT * FROM Clients WHERE PayeeAddress = ? AND Password = ?";
        try {
            Connection conn = getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, pAddress);
            stmt.setString(2, password);
            return stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println("Error mendapatkan data client: " + e.getMessage());
            return null;
        }
    }

    public boolean insertTransaction(String sender, String receiver, double amount, String message, String date) {
        String sql = "INSERT INTO Transactions (Sender, Receiver, Amount, Date, Message) VALUES (?, ?, ?, datetime('now'), ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, sender);
            stmt.setString(2, receiver);
            stmt.setDouble(3, amount);
            stmt.setString(4, message);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error menyimpan transaksi: " + e.getMessage());
            return false;
        }
    }

//    public List<String> getAllTransactions() {
//        List<String> transactions = new ArrayList<>();
//        String sql = "SELECT * FROM Transactions ORDER BY Date DESC";
//
//        try (Connection conn = getConnection();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//
//            while (rs.next()) {
//                String record = String.format("%s mengirim %,.2f ke %s pada %s",
//                        rs.getString("Sender"),
//                        rs.getDouble("Amount"),
//                        rs.getString("Receiver"),
//                        rs.getString("Date"));
//                transactions.add(record);
//            }
//        } catch (SQLException e) {
//            System.err.println("Error mengambil transaksi: " + e.getMessage());
//        }
//        return transactions;
//    }
//public List<Transaction> getAllTransactions() {
//    List<Transaction> transactions = new ArrayList<>();
//    String sql = "SELECT * FROM Transactions ORDER BY Date DESC";
//
//    try (Connection conn = getConnection();
//         Statement stmt = conn.createStatement();
//         ResultSet rs = stmt.executeQuery(sql)) {
//
//        while (rs.next()) {
//            Transaction tx = new Transaction(
//                    rs.getString("Sender"),
//                    rs.getString("Receiver"),
//                    rs.getDouble("Amount"),
//                    rs.getString("Message"),
//                    rs.getString("Date")
//            );
//            transactions.add(tx);
//        }
//
//    } catch (SQLException e) {
//        System.err.println("Error mengambil semua transaksi: " + e.getMessage());
//    }
//    return transactions;
//}
public CheckingAccount getCheckingAccount(String payeeAddress) {
    String sql = "SELECT * FROM CheckingAccounts WHERE Owner = ?";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, payeeAddress);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return new CheckingAccount(
                    rs.getString("Owner"),
                    rs.getString("AccountNumber"),
                    rs.getDouble("Balance"),
                    rs.getInt("TransactionLimit")
            );
        }
    } catch (SQLException e) {
        System.err.println("Error getCheckingAccount: " + e.getMessage());
    }
    return null;
}

    public SavingsAccount getSavingsAccount(String payeeAddress) {
        String sql = "SELECT * FROM SavingsAccounts WHERE Owner = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, payeeAddress);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new SavingsAccount(
                        rs.getString("Owner"),
                        rs.getString("AccountNumber"),
                        rs.getDouble("Balance"),
                        rs.getDouble("WithdrawalLimit")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error getSavingsAccount: " + e.getMessage());
        }
        return null;
    }


    public List<Transaction> getTransactionsForUser(String username) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions WHERE Sender = ? OR Receiver = ? ORDER BY Date DESC";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, username);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("Sender"),
                        rs.getString("Receiver"),
                        rs.getDouble("Amount"),
                        rs.getString("Date"),
                        rs.getString("Message")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil transaksi untuk user: " + e.getMessage());
        }

        return transactions;
    }
    public List<Transaction> getLatestTransactionsForUser(String payeeAddress, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions " +
                "WHERE Sender = ? OR Receiver = ? " +
                "ORDER BY Date DESC " +
                "LIMIT ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, payeeAddress);
            stmt.setString(2, payeeAddress);
            stmt.setInt(3, limit);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Transaction transaction = new Transaction(
                        rs.getString("Sender"),
                        rs.getString("Receiver"),
                        rs.getDouble("Amount"),
                        rs.getString("Date"),
                        rs.getString("Message")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil transaksi terbaru: " + e.getMessage());
        }

        return transactions;
    }

    public ResultSet getLatestTransactionsResultSet(String payeeAddress, int limit) throws SQLException {
        String sql = "SELECT * FROM Transactions " +
                "WHERE Sender = ? OR Receiver = ? " +
                "ORDER BY Date DESC " +
                "LIMIT ?";

        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, payeeAddress);
        stmt.setString(2, payeeAddress);
        stmt.setInt(3, limit);

        return stmt.executeQuery();
    }
    public Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    public boolean updateCheckingBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE CheckingAccounts SET Balance = ? WHERE AccountNumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateCheckingBalance: " + e.getMessage());
            return false;
        }
    }

    public boolean updateSavingsBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE SavingsAccounts SET Balance = ? WHERE AccountNumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updateSavingsBalance: " + e.getMessage());
            return false;
        }
    }

}