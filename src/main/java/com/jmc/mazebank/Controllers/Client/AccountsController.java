package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.CheckingAccount;
import com.jmc.mazebank.Models.Client;
import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Models.SavingsAccount;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label ch_acc_num;
    public Label transaction_limit;
    public Label ch_acc_date;
    public Label ch_acc_bal;
    public Label sv_acc_num;
    public Label withdrawal_limit;
    public Label sv_acc_date;
    public Label sv_acc_bal;
    public TextField amount_to_sv;
    public Button trans_to_sv_btn;
    public TextField amount_to_ch;
    public Button trans_to_cv_btn;

    private CheckingAccount checkingAccount;
    private SavingsAccount savingsAccount;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client client = Model.getInstance().getClient();

        checkingAccount = client.getCheckingAccount();
        savingsAccount = client.getSavingsAccount();

        ch_acc_num.setText(checkingAccount.getAccountNumber());
        transaction_limit.setText(String.valueOf(checkingAccount.transactionLimitProp().get()));
        ch_acc_date.setText(client.getDateCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        ch_acc_bal.setText(String.format("$%,.2f", checkingAccount.balanceProperty().get()));

        sv_acc_num.setText(savingsAccount.getAccountNumber());
        withdrawal_limit.setText(String.format("%,.0f", savingsAccount.withdrawalLimitProp().get()));
        sv_acc_date.setText(client.getDateCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        sv_acc_bal.setText(String.format("$%,.2f", savingsAccount.balanceProperty().get()));

        trans_to_sv_btn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amount_to_sv.getText());
                if (amount <= 0 || amount > checkingAccount.balanceProperty().get()) {
                    showAlert("Saldo tidak cukup atau nominal tidak valid.");
                    return;
                }

                double newCheckingBal = checkingAccount.balanceProperty().get() - amount;
                double newSavingsBal = savingsAccount.balanceProperty().get() + amount;

                checkingAccount.balanceProperty().set(newCheckingBal);
                savingsAccount.balanceProperty().set(newSavingsBal);

                Model.getInstance().getDatabaseDriver()
                        .updateCheckingBalance(checkingAccount.getAccountNumber(), newCheckingBal);
                Model.getInstance().getDatabaseDriver()
                        .updateSavingsBalance(savingsAccount.getAccountNumber(), newSavingsBal);

                Model.getInstance().getDatabaseDriver().insertTransaction(
                        checkingAccount.getOwner(),
                        savingsAccount.getOwner(),
                        amount,
                        "Transfer to Savings",
                        LocalDate.now().toString()
                );

                refreshBalances();
                amount_to_sv.clear();
            } catch (NumberFormatException ex) {
                showAlert("Masukkan jumlah yang valid.");
            }
        });
        trans_to_cv_btn.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amount_to_ch.getText());
                if (amount <= 0 || amount > savingsAccount.balanceProperty().get()) {
                    showAlert("Saldo tidak cukup atau nominal tidak valid.");
                    return;
                }

                double newSavingsBal = savingsAccount.balanceProperty().get() - amount;
                double newCheckingBal = checkingAccount.balanceProperty().get() + amount;

                savingsAccount.balanceProperty().set(newSavingsBal);
                checkingAccount.balanceProperty().set(newCheckingBal);

                Model.getInstance().getDatabaseDriver()
                        .updateSavingsBalance(savingsAccount.getAccountNumber(), newSavingsBal);
                Model.getInstance().getDatabaseDriver()
                        .updateCheckingBalance(checkingAccount.getAccountNumber(), newCheckingBal);

                Model.getInstance().getDatabaseDriver().insertTransaction(
                        savingsAccount.getOwner(),
                        checkingAccount.getOwner(),
                        amount,
                        "Transfer to Checking",
                        LocalDate.now().toString()

                );
                boolean checkingUpdated = Model.getInstance().getDatabaseDriver()
                        .updateCheckingBalance(checkingAccount.getAccountNumber(), newCheckingBal);

                boolean savingsUpdated = Model.getInstance().getDatabaseDriver()
                        .updateSavingsBalance(savingsAccount.getAccountNumber(), newSavingsBal);

                if (!checkingUpdated || !savingsUpdated) {
                    System.out.println(" Update database gagal. Periksa AccountNumber.");
                }

                refreshBalances();
                amount_to_ch.clear();
            } catch (NumberFormatException ex) {
                showAlert("Masukkan jumlah yang valid.");
            }
        });
    }

    private void refreshBalances() {
        ch_acc_bal.setText(String.format("$%,.2f", checkingAccount.balanceProperty().get()));
        sv_acc_bal.setText(String.format("$%,.2f", savingsAccount.balanceProperty().get()));
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Peringatan");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
