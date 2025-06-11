package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.DatabaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Text user_name;
    public Label login_date;
    public Label checking_bal;
    public Label checking_acc_num;
    public Label savings_bal;
    public Label savings_acc_num;
    public Label income_lbl;
    public Label expense_lbl;
    public ListView transaction_listview;
    public TextField payee_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void onSendMoney() {
        String payee = payee_fld.getText();
        String amountText = amount_fld.getText();
        String message = message_fld.getText();
        String sender = user_name.getText(); // pastikan Text ini berisi Payee pengirim
        String date = java.time.LocalDate.now().toString();

        if (payee.isEmpty() || amountText.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Payee dan Amount wajib diisi.");
            alert.show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            DatabaseDriver db = new DatabaseDriver();
            boolean success = db.insertTransaction(sender, payee, amount, message, date);

            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transaksi berhasil dikirim!");
                alert.show();

                payee_fld.clear();
                amount_fld.clear();
                message_fld.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Gagal menyimpan transaksi ke database.");
                alert.show();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Format nominal tidak valid.");
            alert.show();
        }
    }
}
