package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.Transaction;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class TransactionCellController implements Initializable {

    @FXML
    private Label trans_date_lbl;

    @FXML
    private Label sender_lbl;

    @FXML
    private Label receiver_lbl;

    @FXML
    private Label amount_lbl;

    @FXML
    private FontAwesomeIconView in_icon;

    @FXML
    private FontAwesomeIconView out_icon;

    @FXML
    private FontAwesomeIconView money_icon;

    private Transaction transaction;

    public TransactionCellController() {
    }

    public void setTransaction(Transaction transaction, boolean isIncoming) {
        this.transaction = transaction;
        updateUI(isIncoming);
    }

    private void updateUI(boolean isIncoming) {
        if (transaction == null) {
            return;
        }

        trans_date_lbl.setText(transaction.getFormattedDate());
        sender_lbl.setText(transaction.getSender());
        receiver_lbl.setText(transaction.getReceiver());
        amount_lbl.setText(String.format("$%.2f", transaction.getAmount()));

        if (isIncoming) {
            in_icon.setIcon(FontAwesomeIcon.ARROW_DOWN);
            in_icon.setVisible(true);
            out_icon.setVisible(false);
        } else {
            out_icon.setIcon(FontAwesomeIcon.ARROW_UP);
            out_icon.setVisible(true);
            in_icon.setVisible(false);
        }

        money_icon.setIcon(FontAwesomeIcon.MONEY);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialization if needed
        in_icon.setIcon(FontAwesomeIcon.QUESTION);
        out_icon.setIcon(FontAwesomeIcon.QUESTION);
        money_icon.setIcon(FontAwesomeIcon.MONEY);
    }
}
