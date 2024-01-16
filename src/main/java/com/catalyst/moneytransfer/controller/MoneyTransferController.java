package com.catalyst.moneytransfer.controller;

import com.catalyst.moneytransfer.Utilities.InMemoryStore;
import com.catalyst.moneytransfer.model.Account;
import com.catalyst.moneytransfer.model.Transaction;
import com.catalyst.moneytransfer.model.User;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class MoneyTransferController {

    private final ExecutorService executorService;

    public MoneyTransferController(ExecutorService _executorService) {
        this.executorService = _executorService;
    }

    public Object Transfer(Request req, Response res) {
        try {
            Transaction transaction = new Gson().fromJson(req.body(), Transaction.class);

            if (!isValidTransaction(transaction)) {
                res.status(400); // Bad Request
                return "Invalid Transaction Request";
            }

            return CompletableFuture.supplyAsync(() -> {
                try {
                    User user = InMemoryStore.GetObject().GetUserByID(transaction.getUser());
                    if (user != null) {
                        Account sourceAccount = InMemoryStore.GetObject().GetUserAccount(user.getUserID());
                        if (sourceAccount != null && sourceAccount.getAccountNo().equals(transaction.getFromAccount())) {
                            Account destAccount = InMemoryStore.GetObject().GetAccountByNo(transaction.getToAccount());
                            if (destAccount != null) {
                                if (sourceAccount.getAccountBalance() >= transaction.getAmount()) {
                                    sourceAccount.debit(transaction.getAmount());
                                    destAccount.credit(transaction.getAmount());
                                    res.status(200);
                                    return "Transaction Successful";
                                } else {
                                    res.status(402);
                                    return "Insufficient Funds, request failed";
                                }
                            } else {
                                res.status(403);
                                return "Invalid Destination Account";
                            }
                        } else {
                            res.status(403);
                            return "Invalid Source Account";
                        }
                    } else {
                        res.status(404);
                        return "User Not Found";
                    }
                } catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    res.status(500); // Internal Server Error
                    return "Internal Server Error";
                }
            }, this.executorService).join();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            res.status(500); // Internal Server Error
            return "Internal Server Error";
        }
    }

    public Object CheckBalance(Request req, Response res) {
        try {
            String userId = req.params(":userid");
            return CompletableFuture.supplyAsync(() -> {
                if (userId != null && !userId.trim().isEmpty()) {
                    User user = InMemoryStore.GetObject().GetUserByID(userId);
                    if (user != null) {
                        String accNo = user.getUserAccountNo();
                        if (accNo != null && !accNo.trim().isEmpty()) {
                            Account account = InMemoryStore.GetObject().GetAccountByNo(accNo);
                            if (account != null) {
                                return "Account balance is " +account.getAccountBalance();
                            }
                            else {
                                res.status(400);
                                return "Account details not found in cache";
                            }
                        }
                        else {
                            res.status(403);
                            return "User is not associated with any account";
                        }
                    }
                    else {
                        res.status(404);
                        return "User Not Found";
                    }
                }
                else {
                    res.status(400);
                    return "Invalid Request";
                }
            }, this.executorService).join();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            res.status(500); // Internal Server Error
            return "Internal Server Error";
        }
    }

    private boolean isValidTransaction(Transaction transaction) {
        return
                (transaction != null) &&
                        transaction.getUser() != null && !transaction.getUser().trim().isEmpty() &&
                        transaction.getFromAccount() != null && !transaction.getFromAccount().trim().isEmpty() &&
                        transaction.getToAccount() != null && !transaction.getToAccount().trim().isEmpty()
                        && transaction.getAmount() != 0.0;
    }
}
