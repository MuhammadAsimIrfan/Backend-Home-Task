package com.catalyst.moneytransfer.Utilities;

import com.catalyst.moneytransfer.model.Account;
import com.catalyst.moneytransfer.model.User;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStore {

    private static InMemoryStore instance;
    private final ConcurrentHashMap<String, User> userMap; //Key = UserID, Value = User Bean
    private final ConcurrentHashMap<String, Account> accountMap; //Key = AccountNo, Value = Account Bean
    private final ConcurrentHashMap<String, String> userAccountMapping; //Key = UserID, Value = AccountNo
    private InMemoryStore() {
        userMap = new ConcurrentHashMap<String, User>();
        accountMap = new ConcurrentHashMap<String, Account>();
        userAccountMapping = new ConcurrentHashMap<String, String>();
    }
    public static InMemoryStore GetObject() {
        if (instance == null)
            instance = new InMemoryStore();

        return instance;
    }
    public void AddUser(String id) {
        if (id != null && !id.trim().isEmpty()) {
            User newUser = new User();
            newUser.setUserID(id);

            var exists = userMap.putIfAbsent(id, newUser);
            if (exists != null && exists.getUserID().equals(newUser.getUserID()))
                throw new IllegalArgumentException("Exception: Duplicate Record, User with same ID already exists");
        }
        else
            throw new IllegalArgumentException("Exception: UserID cannot be null");
    }
    public void AddAccount(String accountNo, String userID, double initialBalance) {
        if (accountNo != null && !accountNo.trim().isEmpty() && userID != null && !userID.trim().isEmpty()) {
            if (!accountMap.containsKey(accountNo)) {
                Account newAccount = new Account();
                newAccount.setAccountNo(accountNo);
                newAccount.setUserID(userID);
                newAccount.setAccountBalance(initialBalance);

                if (userMap.containsKey(userID)) {
                    if (!userAccountMapping.containsKey(userID)) {
                        userMap.get(userID).setUserAccountNo(accountNo);
                        accountMap.put(accountNo, newAccount);
                        userAccountMapping.put(userID, accountNo);
                    }
                    else
                        throw new IllegalStateException("User is already bind with another account");
                }
                else
                    throw new IllegalArgumentException("Exception: User does not exist, create user first");
            }
            else
                throw new IllegalStateException("Duplicate Account No, account already exists");
        }
        else
            throw new IllegalArgumentException("Exception: AccountNo or/and UserID cannot be null");
    }
    public User GetUserByID(String _id) {
        try {
            if (_id != null && !_id.trim().isEmpty() && userMap.containsKey(_id) && userMap.get(_id) != null)
                return userMap.get(_id);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public Account GetAccountByNo(String _accountNo) {
        try {
            if (_accountNo != null && !_accountNo.trim().isEmpty() && accountMap.containsKey(_accountNo) && accountMap.get(_accountNo) != null)
                return accountMap.get(_accountNo);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }
    public Account GetUserAccount(String _userID) {
        if (_userID != null && !_userID.trim().isEmpty() && userAccountMapping.containsKey(_userID) && userAccountMapping.get(_userID) != null) {
            String accountNo = userAccountMapping.get(_userID);
            if (accountMap.containsKey(accountNo) && accountMap.get(accountNo) != null)
                return accountMap.get(accountNo);
        }

        return null;
    }
}