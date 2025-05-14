package com.jmc.mazebank.Views;

import com.jmc.mazebank.Controllers.Admin.ClientCellController;
import com.jmc.mazebank.Models.Client;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class ClientCellFactory extends ListCell<Client> {
    @Override
    protected void updateItem(Client client, boolean empty) {
        super.updateItem(client, empty);

        if(empty) {
            setText(null);
            setGraphic(null);
        }else {
            String s = "Fxml/Admin/ClientCell.fxml";
            FXMLLoader loader = new FXMLLoader(getClass().getResource(s));
            ClientCellController controller = new ClientCellController(client);
            loader.setController(controller);
            setText(null);
            try {
                setGraphic(loader.load());
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
