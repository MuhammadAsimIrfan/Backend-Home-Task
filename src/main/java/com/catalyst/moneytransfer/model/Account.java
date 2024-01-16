package com.catalyst.moneytransfer.model;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {

    private String userID;
    private String accountNo;
    private double accountBalance;

    private final Lock lock = new ReentrantLock();

    public String getUserID() {
        return userID;
    }
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getAccountNo() {
        return accountNo;
    }
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    public double getAccountBalance() {
        lock.lock();
        try {
            return accountBalance;
        } finally {
            lock.unlock();
        }
    }
    public  void setAccountBalance(double _accountBalance) {
        lock.lock();
        try {
            accountBalance = _accountBalance;
        } finally {
            lock.unlock();
        }
    }

    public void debit(double amount) {
        lock.lock();
        try {
            double newBalance = getAccountBalance() - amount;
            setAccountBalance(newBalance);
        } finally {
            lock.unlock();
        }
    }

    public void credit(double amount) {
        lock.lock();
        try {
            double newBalance = getAccountBalance() + amount;
            setAccountBalance(newBalance);
        } finally {
            lock.unlock();
        }
    }

}
