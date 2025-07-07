package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.DatabaseDriver;
import com.jmc.mazebank.Models.Transaction;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML public Text user_name;
    @FXML public Label login_date;
    @FXML public Label checking_bal;
    @FXML public Label checking_acc_num;
    @FXML public Label savings_bal;
    @FXML public Label savings_acc_num;
    @FXML public Label income_lbl;
    @FXML public Label expense_lbl;
    @FXML public ListView<Transaction> transaction_listview;
    @FXML public TextField payee_fld;
    @FXML public TextField amount_fld;
    @FXML public TextArea message_fld;
    @FXML public Button send_money_btn;

    private final String currentUser = "You";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        login_date.setText("Today, " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        user_name.setText("Mr.Heru");
        loadTransactions();
    }

    @FXML
    public void onSendMoney() {
        String payee = payee_fld.getText().trim();
        String amountText = amount_fld.getText().trim();
        String message = message_fld.getText().trim();
        String sender = currentUser;
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        if (payee.isEmpty() || amountText.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Payee dan Amount wajib diisi.").show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                new Alert(Alert.AlertType.ERROR, "Jumlah harus lebih dari 0.").show();
                return;
            }

            DatabaseDriver db = new DatabaseDriver();
            boolean success = db.insertTransaction(sender, payee, amount, message, date);

            if (success) {
                new Alert(Alert.AlertType.INFORMATION, "Transaksi berhasil dikirim!").show();

                payee_fld.clear();
                amount_fld.clear();
                message_fld.clear();

                loadTransactions();
            } else {
                new Alert(Alert.AlertType.ERROR, "Gagal menyimpan transaksi ke database.").show();
            }

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Format jumlah tidak valid.").show();
        }
    }

    private void loadTransactions() {
        DatabaseDriver db = new DatabaseDriver();
        List<Transaction> transactions = db.getTransactionsForUser(currentUser);

        transaction_listview.getItems().setAll(transactions);

        transaction_listview.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Transaction transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (empty || transaction == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader();
                        loader.setLocation(getClass().getResource("/Fxml/Client/TransactionCell.fxml"));
                        Parent root = loader.load();
                        TransactionCellController controller = loader.getController();

                        boolean isIncoming = transaction.getReceiver().equalsIgnoreCase(currentUser);
                        controller.setTransaction(transaction, isIncoming);

                        setGraphic(root);

                    } catch (IOException e) {
                        e.printStackTrace();
                        setGraphic(new Label("Error loading transaction"));
                    }
                }
            }
        });
    }
}
