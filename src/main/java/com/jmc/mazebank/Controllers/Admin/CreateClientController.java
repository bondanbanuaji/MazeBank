package com.jmc.mazebank.Controllers.Admin;

import com.jmc.mazebank.Models.DatabaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class CreateClientController implements Initializable {
    public TextField fName_fld;
    public TextField lName_fld;
    public TextField password_fld;
    public CheckBox pAddress_box;
    public Label pAddress_lbl;
    public CheckBox ch_acc_box;
    public TextField ch_amount_fld;
    public CheckBox sv_acc_box;
    public TextField sv_amount_fld;
    public Button create_client_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        create_client_btn.setOnAction(event -> onCreateClient());
        pAddress_box.setOnAction(event -> {
            if (pAddress_box.isSelected()) {
                String payee = fName_fld.getText().toLowerCase() + (int)(Math.random() * 1000);
                pAddress_lbl.setText("@" + payee);
            } else {
                pAddress_lbl.setText("");
            }
        });
    }

    private void onCreateClient() {
        String firstName = fName_fld.getText().trim();
        String lastName = lName_fld.getText().trim();
        String password = password_fld.getText().trim();
        String payeeAddress = pAddress_lbl.getText().trim();
        boolean hasChecking = ch_acc_box.isSelected();
        boolean hasSaving = sv_acc_box.isSelected();

        double chAmount = 0;
        double svAmount = 0;

        if (firstName.isEmpty() || lastName.isEmpty() || password.isEmpty() || payeeAddress.isEmpty()) {
            error_lbl.setText("Semua field wajib diisi!");
            return;
        }

        try {
            if (hasChecking) {
                chAmount = Double.parseDouble(ch_amount_fld.getText().trim());
            }
            if (hasSaving) {
                svAmount = Double.parseDouble(sv_amount_fld.getText().trim());
            }
        } catch (NumberFormatException e) {
            error_lbl.setText("Nominal harus angka valid!");
            return;
        }

        try (Connection conn = new DatabaseDriver().connect()) {
            conn.setAutoCommit(false); // Mulai transaksi

            // Insert ke tabel Clients
            String insertClient = "INSERT INTO Clients (PayeeAddress, FirstName, LastName, Password, Date) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertClient)) {
                stmt.setString(1, payeeAddress);
                stmt.setString(2, firstName);
                stmt.setString(3, lastName);
                stmt.setString(4, password);
                stmt.setString(5, LocalDate.now().toString());
                stmt.executeUpdate();
            }

            // Insert ke CheckingAccounts jika dicentang
            if (hasChecking) {
                String insertCheck = "INSERT INTO CheckingAccounts (Owner, AccountNumber, TransactionLimit, Balance) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertCheck)) {
                    stmt.setString(1, payeeAddress);
                    stmt.setString(2, "CHK" + System.currentTimeMillis());
                    stmt.setDouble(3, 5000);
                    stmt.setDouble(4, chAmount);
                    stmt.executeUpdate();
                }
            }

            if (hasSaving) {
                String insertSave = "INSERT INTO SavingsAccounts (Owner, AccountNumber, WithdrawalLimit, Balance) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertSave)) {
                    stmt.setString(1, payeeAddress);
                    stmt.setString(2, "SVG" + System.currentTimeMillis());
                    stmt.setDouble(3, 3);
                    stmt.setDouble(4, svAmount);
                    stmt.executeUpdate();
                }
            }

            conn.commit();
            error_lbl.setText("Client berhasil dibuat!");
            clearFields();

        } catch (SQLException e) {
            error_lbl.setText("Gagal membuat client: " + e.getMessage());
        }
    }

    private void clearFields() {
        fName_fld.clear();
        lName_fld.clear();
        password_fld.clear();
        ch_amount_fld.clear();
        sv_amount_fld.clear();
        ch_acc_box.setSelected(false);
        sv_acc_box.setSelected(false);
    }

}
