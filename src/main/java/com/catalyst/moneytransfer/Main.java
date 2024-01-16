package com.catalyst.moneytransfer;

import com.catalyst.moneytransfer.Utilities.InMemoryStore;
import com.catalyst.moneytransfer.controller.ExternalTransferController;
import com.catalyst.moneytransfer.controller.MoneyTransferController;
import com.catalyst.moneytransfer.externalWithdrawlService.WithdrawalService;
import com.catalyst.moneytransfer.externalWithdrawlService.WithdrawalServiceStub;
import spark.ResponseTransformer;
import spark.Spark;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static spark.Spark.*;

public class Main {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(2);
    static InMemoryStore inMemoryStore = InMemoryStore.GetObject();
    static WithdrawalService externalService = new WithdrawalServiceStub();

    static {
        //Creating Dummy Users in memory structure
        inMemoryStore.AddUser("001");
        inMemoryStore.AddUser("002");
        inMemoryStore.AddUser("003");

        //Creating and assigning dummy accounts to users with initial balances in memory structure
        inMemoryStore.AddAccount("BK0000345", "001", 3000.00);
        inMemoryStore.AddAccount("BK0000399", "002", 10000.00);
        inMemoryStore.AddAccount("BK0004589", "003", 15000);
    }

    public static void main(String[] args) {

        Spark.port(4568);

        MoneyTransferController transferController = new MoneyTransferController(executorService);
        path("/accountTransfer", () -> {
            post("", transferController::Transfer, toJson());
        });
        path("/checkBalance/:userid", () -> {
            get("", transferController::CheckBalance, toJson());
        });

        ExternalTransferController withdrawalController = new ExternalTransferController(executorService, externalService);
        path("/requestWithdrawal", () -> {
            post("", withdrawalController::RequestWithdrawal, toJson());
        });
        path("/getWithdrawalState/:id", () -> {
            get("", withdrawalController::GetWithdrawalState, toJson());
        });
    }

    private static ResponseTransformer toJson() {
        return Object::toString;
    }
}
