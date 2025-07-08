package com.jmc.mazebank.Models;

import javafx.beans.property.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction {
    private final StringProperty sender;
    private final StringProperty receiver;
    private final DoubleProperty amount;
    private final ObjectProperty<LocalDate> date;
    private final StringProperty message;

//    public Transaction(String sender, String receiver, double amount, String date, String message) {
//        this.sender = new SimpleStringProperty(this, "Sender", sender);
//        this.receiver = new SimpleStringProperty(this, "Receiver", receiver);
//        this.amount = new SimpleDoubleProperty(this, "Amount", amount);
////        this.date = new SimpleObjectProperty<>(this, "Date",
////                LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate());
////        this.date = new SimpleObjectProperty<>(this, "Date",
////                LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
////        );
//        DateTimeFormatter formatter;
//        if (date.length() > 10) {
//            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//            this.date = new SimpleObjectProperty<>(this, "Date",
//                    LocalDateTime.parse(date, formatter).toLocalDate()
//            );
//        } else {
//            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//            this.date = new SimpleObjectProperty<>(this, "Date",
//                    LocalDate.parse(date, formatter)
//            );
//        }
//
//        this.message = new SimpleStringProperty(this, "Message", message);
//    }
public Transaction(String sender, String receiver, double amount, String date, String message) {
    this.sender = new SimpleStringProperty(this, "Sender", sender);
    this.receiver = new SimpleStringProperty(this, "Receiver", receiver);
    this.amount = new SimpleDoubleProperty(this, "Amount", amount);

    LocalDate parsedDate;
    try {
        if (date.length() > 10) {
            parsedDate = LocalDateTime.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).toLocalDate();
        } else {
            parsedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
    } catch (Exception e) {
        System.out.println("⚠️ Gagal parse tanggal: " + date);
        parsedDate = LocalDate.now(); // fallback biar nggak crash
    }

    this.date = new SimpleObjectProperty<>(this, "Date", parsedDate);
    this.message = new SimpleStringProperty(this, "Message", message);
}


    public Transaction(String sender, String receiver, double amount, LocalDate date, String message) {
        this.sender = new SimpleStringProperty(this, "Sender", sender);
        this.receiver = new SimpleStringProperty(this, "Receiver", receiver);
        this.amount = new SimpleDoubleProperty(this, "Amount", amount);
        this.date = new SimpleObjectProperty<>(this, "Date", date);
        this.message = new SimpleStringProperty(this, "Message", message);
    }

    // Getters
    public String getSender() {
        return sender.get();
    }

    public String getReceiver() {
        return receiver.get();
    }

    public double getAmount() {
        return amount.get();
    }

    public String getDate() {
        return date.get().toString(); // yyyy-MM-dd
    }

    public String getMessage() {
        return message.get();
    }

    public String getFormattedDate() {
        return date.get().format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
    }

    public StringProperty senderProperty() { return sender; }
    public StringProperty receiverProperty() { return receiver; }
    public DoubleProperty amountProperty() { return amount; }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    public StringProperty messageProperty() { return message; }


}
