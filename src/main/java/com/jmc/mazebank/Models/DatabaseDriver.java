package com.jmc.mazebank.Models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDriver {
    private static final String DB_URL = "jdbc:sqlite:mazebank.db";

    // Inisialisasi database saat pertama kali dijalankan
    public DatabaseDriver() {
        initializeDatabase();
    }

    // Method untuk mendapatkan koneksi
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    // Inisialisasi struktur database
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

    // Validasi login client
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

    // Mendapatkan data client
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

    // Menyimpan transaksi
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

    // Mendapatkan semua transaksi
    public List<String> getAllTransactions() {
        List<String> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transactions ORDER BY Date DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String record = String.format("%s mengirim %,.2f ke %s pada %s",
                        rs.getString("Sender"),
                        rs.getDouble("Amount"),
                        rs.getString("Receiver"),
                        rs.getString("Date"));
                transactions.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error mengambil transaksi: " + e.getMessage());
        }
        return transactions;
    }
}