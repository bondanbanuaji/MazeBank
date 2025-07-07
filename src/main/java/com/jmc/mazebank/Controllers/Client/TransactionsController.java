package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.DatabaseDriver;
import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Models.Transaction;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TransactionsController implements Initializable {

    private final DatabaseDriver db = new DatabaseDriver();
    private static final Logger LOGGER = Logger.getLogger(TransactionsController.class.getName());

    @FXML
    public ListView<Transaction> transactions_listview;
    @FXML
    public ListView<Transaction> latestTransactions_listview;

    private String currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentUser = Model.getInstance().getClient().pAddressProperty().get();

        loadAllTransactions();
        setupLatestTransactions();
        setupCellFactory();
    }

    private void loadAllTransactions() {
        try {
            List<Transaction> transactions = db.getTransactionsForUser(currentUser);
            transactions_listview.getItems().setAll(transactions);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error loading transactions", e);
        }
    }

    private void setupLatestTransactions() {
        try {
            ObservableList<Transaction> latest = Model.getInstance().getLatestTransactions(5);
            latestTransactions_listview.getItems().setAll(latest);
            latestTransactions_listview.setFixedCellSize(40);
            latestTransactions_listview.setPrefHeight(latest.size() * 60 + 10);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error setting up latest transactions", e);
        }
    }

    private void setupCellFactory() {
        // Set cell factory untuk kedua ListView
        transactions_listview.setCellFactory(this::createTransactionCell);
        latestTransactions_listview.setCellFactory(this::createTransactionCell);
    }

    private ListCell<Transaction> createTransactionCell(ListView<Transaction> listView) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Transaction transaction, boolean empty) {
                super.updateItem(transaction, empty);
                if (empty || transaction == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader();
//                        loader.setLocation(getClass().getResource("/com.jmc.mazebank/Fxml/Client/TransactionCell.fxml"));
                        loader.setLocation(getClass().getResource("/Fxml/Client/TransactionCell.fxml"));
                        Parent root = loader.load();
                        TransactionCellController controller = loader.getController();

                        boolean isIncoming = transaction.getReceiver().equalsIgnoreCase(currentUser);
                        controller.setTransaction(transaction, isIncoming);

                        setGraphic(root);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Failed to load TransactionCell.fxml", e);
                        setText("Load Error");
                    }
                }
            }
        };
    }
}