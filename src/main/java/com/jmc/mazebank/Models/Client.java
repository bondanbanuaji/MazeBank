package com.jmc.mazebank.Models;

import com.jmc.mazebank.Views.AccountType;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Client {
    private final StringProperty firstName ;
    private  final StringProperty lastName ;
    private final StringProperty payeeAddress ;
    private final ObjectProperty<Account> checkingAccount;
    private final ObjectProperty<Account> savingsAccount;
    private final ObjectProperty<LocalDate> dateCreated;

    public Client(String fName , String lName , String pAddress , Account cAccount , Account sAccount , LocalDate date) {
        this.firstName = new SimpleStringProperty(this,"FirstName",fName);
        this.lastName = new SimpleStringProperty(this,"LastName",lName);
        this.payeeAddress = new SimpleStringProperty(this,"Payee Address",pAddress);
        this.checkingAccount = new SimpleObjectProperty<>(this,"Checking Account",cAccount);
        this.savingsAccount = new SimpleObjectProperty<>(this,"Savings Account",sAccount);
        this.dateCreated = new SimpleObjectProperty<>(this,"Date",date);
    }

    public Client(String payeeAddress, String fullName, int balance, StringProperty firstName, StringProperty lastName, StringProperty payeeAddress1, ObjectProperty<Account> checkingAccount, ObjectProperty<Account> savingsAccount, ObjectProperty<LocalDate> dateCreated) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.payeeAddress = payeeAddress1;
        this.checkingAccount = checkingAccount;
        this.savingsAccount = savingsAccount;
        this.dateCreated = dateCreated;
    }

    public Client(String payeeAddress, String firstName, String lastName, String password, String date) {
        this.firstName = new SimpleStringProperty(this, "FirstName", firstName);
        this.lastName = new SimpleStringProperty(this, "LastName", lastName);
        this.payeeAddress = new SimpleStringProperty(this, "Payee Address", payeeAddress);
        this.checkingAccount = new SimpleObjectProperty<>(this, "Checking Account", null);
        this.savingsAccount = new SimpleObjectProperty<>(this, "Savings Account", null);
//        this.dateCreated = new SimpleObjectProperty<>(this, "Date", LocalDate.parse(date));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate parsedDate = LocalDate.parse(date, formatter);
        this.dateCreated = new SimpleObjectProperty<>(this, "Date", parsedDate);

    }

//
//    public Client(String payeeAddress, String fullName, int balance, StringProperty firstName, StringProperty lastName, StringProperty payeeAddress1, ObjectProperty<Account> checkingAccount, ObjectProperty<Account> savingsAccount, ObjectProperty<LocalDate> dateCreated) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.payeeAddress = payeeAddress1;
//        this.checkingAccount = checkingAccount;
//        this.savingsAccount = savingsAccount;
//        this.dateCreated = dateCreated;
//    }

    public StringProperty firstNameProperty() {return firstName;}

    public StringProperty lastNameProperty() {return lastName;}

    public StringProperty pAddressProperty() {return payeeAddress;}

    public ObjectProperty<Account> checkingAccountProperty() {return checkingAccount;}

    public ObjectProperty<Account> savingsAccountProperty() {return savingsAccount;}

    public ObjectProperty<LocalDate> dateProperty() {return dateCreated;}

//    public Client(String pAddress, String fName, String lName, String date) {
//    }

}
