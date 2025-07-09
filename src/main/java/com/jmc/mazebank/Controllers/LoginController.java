package com.jmc.mazebank.Controllers;

import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Views.AccountType;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public ChoiceBox<AccountType> acc_selector;
    public Label payee_address_lbl;
    public TextField payee_address_field;
    public TextField password_fld;
    public Button login_btn;
    public Label error_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        acc_selector.setItems(FXCollections.observableArrayList(AccountType.CLIENT, AccountType.ADMIN));
        acc_selector.setValue(Model.getInstance().getViewFactory().getLoginAccountType());
        acc_selector.valueProperty().addListener(observable -> {
            Model.getInstance().getViewFactory().setLoginAccountType(acc_selector.getValue());
            if (acc_selector.getValue() == AccountType.CLIENT) {
                payee_address_lbl.setText("Alamat Payee:");
            } else {
                payee_address_lbl.setText("Username Admin:");
            }
        });
        login_btn.setOnAction(event -> onLogin());
    }

    private void onLogin() {
        Stage stage = (Stage) error_lbl.getScene().getWindow();
        if (payee_address_field.getText().isEmpty() || password_fld.getText().isEmpty()) {
            error_lbl.setText("Harap isi semua field!");
            return;
        }

        if (Model.getInstance().getViewFactory().getLoginAccountType() == AccountType.CLIENT) {
            Model.getInstance().evaluateClientCred(
                    payee_address_field.getText(),
                    password_fld.getText()
            );

            if (Model.getInstance().getClientLoginSuccessFlag()) {
                Model.getInstance().getViewFactory().showClientWindow();
                Model.getInstance().getViewFactory().closeStage(stage);
            } else {
                payee_address_field.setText("");
                password_fld.setText("");
                error_lbl.setText("Kredensial tidak valid atau tidak ditemukan");
            }
        } else {
            Model.getInstance().getViewFactory().showAdminWindow();
        }
    }
}