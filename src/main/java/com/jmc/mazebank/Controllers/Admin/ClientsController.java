package com.jmc.mazebank.Controllers.Admin;

import com.jmc.mazebank.Models.Client;
import com.jmc.mazebank.Models.DatabaseDriver;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.ListCell;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ClientsController implements Initializable {

    public ListView<Client> clients_listview;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadClients();
    }

    private void loadClients() {
        clients_listview.getItems().clear();
        DatabaseDriver db = new DatabaseDriver();
        String sql = "SELECT * FROM Clients";

        try (Connection conn = db.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Client client = new Client(
                        rs.getString("PayeeAddress"),
                        rs.getString("FirstName"),
                        rs.getString("LastName"),
                        rs.getString("Password"),
                        rs.getString("Date")
                );
                clients_listview.getItems().add(client);
            }

            clients_listview.setCellFactory(listView -> new ListCell<>() {
                @Override
                protected void updateItem(Client client, boolean empty) {
                    super.updateItem(client, empty);
                    if (empty || client == null) {
                        setGraphic(null);
                    } else {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Fxml/Admin/ClientCell.fxml"));
                            loader.setController(new ClientCellController(client));
                            AnchorPane pane = loader.load();
                            setGraphic(pane);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

        } catch (SQLException e) {
            System.err.println("Gagal memuat data client: " + e.getMessage());
        }
    }
}
