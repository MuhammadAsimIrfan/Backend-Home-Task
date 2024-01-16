**Money Transfer RESTful API**

**Overview:**
This project is a simple implementation of a RESTful API for money transfers, designed to meet the functional requirements specified. 
The API allows users to send money from their account to other users' accounts and to an external withdrawal address. Additionally, users can track the progress of their operations.
REST Architecture is followed, as well as Singleton Pattern is used for InMemoryDataStructure to keep it static across the application.
Also CompletableFuture in combination with ExecutorService is used to make the service available to multiple systems concurrently, and atomicity has been maintained through proper locking where necessary to avoid potential race conditions.
This build manager used is maven and framework used is Spark. Please refer to pom.xml for dependencies

**Functional API Endpoints Exposed:**
-------------------------------------------------------------------------------------------------------------
1) User-to-User Transfer:
  To perform a user-to-user transfer, make a POST request to the following endpoint:
  POST http://localhost:4568/accountTransfer
  The request body should be a JSON object with the following model:
  json
  {
    "user": "string",
    "fromAccount": "string",
    "toAccount": "string",
    "amount": double
  }
  user: UserId initiating the transfer.
  fromAccount: The source account from which the money will be transferred.
  toAccount: The destination account to which the money will be transferred.
  amount: The amount to be transferred (double).

  Example Request
  {
    "user": "001",
    "fromAccount": "BK0000345",
    "toAccount": "BK0000399",
    "amount": 100.0
  }
  Response is in String. For e.g.
  "Transaction Successful" with Response Code 200,
  "Insufficient Funds, request failed" with Response Code 402,
  "Invalid Source Account" with Response Code 403.
----------------------------------------------------------------------------------------------------------------
2) Check Balance
  To check the balance in real time before or after transfering or withdrawal for a specific user, make a GET request to the following endpoint:
  GET http://localhost:4568/checkBalance/{userId}
  Replace `{userId}` with the actual user ID for which you want to check the balance.

  Example Request:
  GET http://localhost:4568/checkBalance/001
  Response will be in String. For e.g.
  "Account Balance is 3000.0"
-----------------------------------------------------------------------------------------------------------------

3) External Withdrawal Service
   The withdrawal service allows users to initiate withdrawals to external addresses through a provided API stub.
   To initiate a withdrawal, make a POST request to the following endpoint:
   POST http://localhost:4568/requestWithdrawal
   The request body should be a JSON object with the following model:
    json
    {
      "userId": "string",
      "id": "string",
      "address": "string",
      "amount": double
    }
   userId: Id of any user stored in inmemory structure.
   id: A unique UUID in String format.
   address: The external withdrawal address.
   amount: The amount to be withdrawn (double).
   Example Request
    {
      "userId": "001",
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "address": "external_address_xyz",
      "amount": 50.0
    }
  Response is in String. For e.g.
  "Transaction Successful" with Response Code 200,
  "Insufficient Funds, request failed" with Response Code 402,
  "Invalid Source Account" with Response Code 403.
----------------------------------------------------------------------------------------------------------------
4) Get Withdrawal Request State
   Users can check the status of their withdrawal requests using the following endpoint:
   GET http://localhost:4568/checkWithdrawalStatus/{withdrawalId}
   Replace {withdrawalId} with the actual withdrawal ID for which you want to check the status.
   http://localhost:4568/getWithdrawalState/550e8400-e29b-41d4-a716-446655440000
----------------------------------------------------------------------------------------------------------------


For any further assistance,
please feel free to reach at asimirfan007@gmail.com
