package com.jmc.mazebank.Controllers.Admin;

import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Views.AdminMenuOptions;
import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminMenuController implements Initializable {
    public Button create_client_btn;
    public Button clients_btn;
    public Button deposit_btn;
    public Button logout_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        addListeners();
    }

    private void addListeners(){
        create_client_btn.setOnAction(Event ->onCreateClient());
        clients_btn.setOnAction(Event ->onClients());
        deposit_btn.setOnAction(Event -> onDeposit());
        logout_btn.setOnAction(Event -> onLogout());
    }

    private void onCreateClient(){
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.CREATE_CLIENT);
    }
    private void onClients () {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.CLIENTS);
    }

    private void onDeposit() {
        Model.getInstance().getViewFactory().getAdminSelectedMenuItem().set(AdminMenuOptions.DEPOSIT);
    }
    private void onLogout() {
        Model.getInstance().getViewFactory().closeAdminWindow();
        Model.getInstance().getViewFactory().showLoginWindow();
    }
}

