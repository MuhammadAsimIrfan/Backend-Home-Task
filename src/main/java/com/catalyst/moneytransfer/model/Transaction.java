package com.catalyst.moneytransfer.model;

public class Transaction {

    private String user;
    private String fromAccount;
    private String toAccount;
    private double amount;

    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public String getFromAccount() {
        return fromAccount;
    }
    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount;
    }
    public String getToAccount() {
        return toAccount;
    }
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
}
