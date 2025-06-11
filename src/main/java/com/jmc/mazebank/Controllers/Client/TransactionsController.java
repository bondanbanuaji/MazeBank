package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.DatabaseDriver;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class TransactionsController implements Initializable {
    public ListView transactions_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    private DatabaseDriver db = new DatabaseDriver();

    public void onSendMoney(String sender, String receiver, double amount, String message) {
        String date = LocalDateTime.now().toString();
        boolean success = db.insertTransaction(sender, receiver, amount, message, date);
        if (success) {
            System.out.println("Transaksi berhasil!");
        } else {
            System.out.println("Transaksi gagal.");
        }
    }
}
