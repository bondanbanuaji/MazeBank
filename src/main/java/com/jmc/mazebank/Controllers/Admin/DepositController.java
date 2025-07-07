package com.jmc.mazebank.Controllers.Admin;

import com.jmc.mazebank.Models.DatabaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DepositController implements Initializable {


    public TextField pAddress_fld;
    public Button search_btn;
    public ListView result_listview;
    public TextField amount_fld;
    public Button deposit_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_btn.setOnAction(e -> searchClient());
        deposit_btn.setOnAction(e -> performDeposit());
    }

    private void searchClient() {
        String pAddress = pAddress_fld.getText().trim();
        if (pAddress.isEmpty()) return;

        try {
            DatabaseDriver db = new DatabaseDriver();
            ResultSet rs = db.getClientData(pAddress, "*");
            if (rs != null && rs.next()) {
                String display = rs.getString("FirstName") + " " + rs.getString("LastName");
                result_listview.getItems().clear();
                result_listview.getItems().add(display);
            } else {
                result_listview.getItems().clear();
                result_listview.getItems().add("Client tidak ditemukan");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void performDeposit() {
        String pAddress = pAddress_fld.getText().trim();
        String amountText = amount_fld.getText().trim();

        if (pAddress.isEmpty() || amountText.isEmpty()) return;

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) return;

            DatabaseDriver db = new DatabaseDriver();
            db.insertTransaction("ADMIN", pAddress, amount, "Deposit dari Admin", LocalDate.now().toString());
            amount_fld.clear();

            result_listview.getItems().add("Berhasil deposit " + amount);

        } catch (NumberFormatException e) {
            result_listview.getItems().add("Jumlah tidak valid");
        }
    }


}
