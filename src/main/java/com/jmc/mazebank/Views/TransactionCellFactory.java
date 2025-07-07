package com.jmc.mazebank.Views;

import com.jmc.mazebank.Controllers.Client.TransactionCellController;
import com.jmc.mazebank.Models.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

public class TransactionCellFactory extends ListCell<Transaction> {

    private final String currentUser;

    public TransactionCellFactory(String currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    protected void updateItem(Transaction transaction, boolean empty) {
        super.updateItem(transaction, empty);

        if (empty || transaction == null) {
            setText(null);
            setGraphic(null);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/jmc/mazebank/Fxml/Client/TransactionCell.fxml"));
            AnchorPane root = loader.load();

            TransactionCellController controller = loader.getController();
            boolean isIncoming = transaction.getReceiver().equalsIgnoreCase(currentUser);
            controller.setTransaction(transaction, isIncoming); // ✅ method tunggal

            setText(null);
            setGraphic(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
