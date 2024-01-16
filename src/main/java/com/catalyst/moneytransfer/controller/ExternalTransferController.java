package com.catalyst.moneytransfer.controller;

import com.catalyst.moneytransfer.Utilities.InMemoryStore;
import com.catalyst.moneytransfer.externalWithdrawlService.WithdrawalService;
import com.catalyst.moneytransfer.model.Account;
import com.catalyst.moneytransfer.model.User;
import com.catalyst.moneytransfer.model.WithdrawalRequest;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ExternalTransferController {

    private final ExecutorService executorService;
    private final WithdrawalService externalService;

    public ExternalTransferController(ExecutorService _executorService, WithdrawalService _externalService) {
        this.executorService = _executorService;
        this.externalService = _externalService;
    }

    public Object RequestWithdrawal(Request req, Response res) {
        try {
            WithdrawalRequest request = new Gson().fromJson(req.body(), WithdrawalRequest.class);

            if (!isValidRequest(request)) {
                res.status(400);
                return "Invalid Withdrawal Request";
            }

            return CompletableFuture.supplyAsync(() -> {
                try {
                    User user = InMemoryStore.GetObject().GetUserByID(request.getUserId());
                    if (user != null) {
                        String accNo = user.getUserAccountNo();
                        if (accNo != null && !accNo.trim().isEmpty()) {
                            Account account = InMemoryStore.GetObject().GetAccountByNo(accNo);
                            if (account != null) {
                                if (account.getAccountBalance() >= request.getAmount()) {
                                    account.debit(request.getAmount());
                                    WithdrawalService.WithdrawalId withdrawalId = new WithdrawalService.WithdrawalId(request.getId());
                                    WithdrawalService.Address withdrawalAddress = new WithdrawalService.Address(request.getAddress());
                                    try {
                                        this.externalService.requestWithdrawal(withdrawalId, withdrawalAddress, request.getAmount());
                                        res.status(200);
                                        return "Withdrawal Request Successful";
                                    }
                                    catch (IllegalStateException e) { // Id is already present in external service so crediting back the amount deducted earlier
                                        account.credit(request.getAmount());
                                        res.status(400);
                                        return e.getMessage();
                                    }
                                }
                                else {
                                    res.status(402);
                                    return "Insufficient Funds, withdrawal request failed";
                                }
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
                        return "User not found";
                    }
                }
                catch (Exception ex) {
                    System.out.println(ex.getMessage());
                    res.status(500); // Internal Server Error
                    return "Internal Server Error";
                }
            }, this.executorService).join();
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            res.status(500); // Internal Server Error
            return "Internal Server Error";
        }
    }

    public Object GetWithdrawalState(Request req, Response res) {
        try {
            String id = req.params(":id");
            if (id != null && !id.trim().isEmpty()) {
                return CompletableFuture.supplyAsync(() -> {
                    WithdrawalService.WithdrawalId withdrawalId = new WithdrawalService.WithdrawalId(UUID.fromString(id));
                    try {
                        WithdrawalService.WithdrawalState state = externalService.getRequestState(withdrawalId);
                        res.status(200);
                        return state;
                    }
                    catch (IllegalArgumentException e) { //thrown from external service in case of wrong id
                        res.status(404);
                        return e.getMessage();
                    }
                }, this.executorService).join();
            }
            else {
                res.status(400);
                return "Invalid Withdrawal Request";
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            res.status(500); // Internal Server Error
            return "Internal Server Error";
        }
    }

    private boolean isValidRequest(WithdrawalRequest request) {
        return
                request != null &&
                        request.getUserId() != null && !request.getUserId().trim().isEmpty() &&
                        request.getId() != null &&
                        request.getAddress() != null && !request.getAddress().trim().isEmpty() &&
                        request.getAmount() != 0.0;
    }
}
