package com.catalyst.moneytransfer.model;

import java.util.UUID;
public class WithdrawalRequest {

    private String userId;
    private UUID id;
    private String address;
    private double amount;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }

    public WithdrawalRequest() {

    }


}
