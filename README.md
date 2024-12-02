# Loan API for ING HUB

# How to use

1) Clone the project to your local system
2) Use JDK 17
3) Run `mvnn clean install`
4) Application can be started now
5) By-default application will use `localhost:8080`
6) Application uses H2: `http://localhost:8080/h2-console`
    ```properties
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: root
    password: pass
   ```
7) Unit tests for `LoanService` are written, decision and line coverage can be find at
   `http://localhost:63342/inghub-loan/loan/target/site/jacoco/index.html` (you need to execute `mvn verify` beforehand)

# API

- All API related info is listed on `http://localhost:8080/swagger-ui/index.html`
- There are 3 APIs: User, Customer, Loan
- All the tables in DB have audit columns such as createdBy and updatedBy; when a loan/installment is saved/updated,
  you can see the responsible user easily.
- API endpoints accepts `Accept-Language` as a request header, you can set this to **tr** or **en**. The response of 
  APIs will change based on this header. If omitted, English is used

# Start Using APIs

1) All the endpoints are protected by **JWT**. Thus, you must first register then authenticate.
    1) You can set your role as ***ADMIN*** or ***CUSTOMER***.
    2) ***username*** is unique.
    3) **POST** `http://localhost:8080/user`
       ```json
       {
          "username": "CUSTOMER1",
          "password": "qwerty",
          "role": "CUSTOMER"
       }
       ```
    4) Now you can authenticate your registered user and get your **JWT**. Please use the username that you have
       registered with:
       **POST** `http://localhost:8080/user/authenticate`
       ```json
       {
         "username": "CUSTOMER1",
         "password": "qwerty"
       }
       ```
2) After JWT is generated, all your subsequent requests should be sent with the generated JWT. 
   1) **A single generated JWT has a lifetime of 6 hours. You can use the same JWT even if you restart the server. 
      However, you will still need to register with the same username!!** 
   2) On `Postman`, you can do this by: 
      Authorization->Auth Type-> select Bearer Token->insert your JWT.
3) Now that we have JWT and a user, we can create a `Customer`. Be aware that users who has ***CUSTOMER*** role can
   use this endpoint. Once `creditLimit` is set in this request, it cannot be changed:
   **POST** `http://localhost:8080/customer`
    ```json
    {
        "name":"ege",
        "surname":"de",
        "creditLimit":900
    }
    ```
    1) The response is your customer ID. You should save/note it.

4) Now that we have user, JWT and customer, we can create `Loan`:
   **POST** `http://localhost:8080/loan`
    ```json
    {
        "customerId": 1,
        "loanAmount": 10,
        "rate":0.20,
        "numberOfInstallment": 6
    }
    ```
    1) If your role is ***ADMIN***, `customerId` corresponds to the customer that you want to create a loan.
    2) If your role is ***CUSTOMER***, `customerId` corresponds to your id.
    3) The response is your loan ID. You should save/note it.

5) You can view the loan of a customer with pagination: **GET** `http://localhost:8080/loan?customerId={
   {customerId}}&pageIndex={{pageIndex}}&size={{pageSize}}`.
    1) Ex: `http://localhost:8080/loan?customerId=1&pageIndex=0&size=4`. This will get all the loans
       of `Customer(id=1)`, that is in the first page, with at most 4 results in the response.

6) Now you can pay for your loan: **POST** http://localhost:8080/loan/installment
   ```json
   {
      "loanId":1,
      "amount": 180,
      "customerId": 1
   }
   ```
    1) If your role is ***ADMIN***, `customerId` corresponds to the customer that you want to create a loan.
    2) If your role is ***CUSTOMER***, `customerId` corresponds to your id.
    3) At most 3 subsequent installments can be paid
    4) An installment can be paid as a whole or none at all
    5) When all the installments of a loan are paid, customer limit is increased by the amount of loan, loan status
       is set to paid
    6) The response will inform you on how many installments you have paid and how many are remaining and also if the
       total loan is paid fully. You can also find how much is deducted from you:
         ```json
         {
            "numberOfInstallmentsPaid": 3,
            "numberOfInstallmentsRemaining": 3,
            "paidAmount": 6.00,
            "isLoanPaid": false
         }
         ```
7) You can view the installments of a loan with pagination:
   **GET** `http://localhost:8080/loan/installment?loanId=1&pageIndex=0&size=4`

## More to play

1) Register a user as admin
2) Register a user as customer with username `Customer1`
3) Register a user as customer with username `Customer2`
4) Create JWT for each user
5) Create customer for `Customer1` and `Customer2`
6) Apply for a loan for `Customer1` using JWT of ADMIN
7) Apply for a loan for `Customer1` using JWT of `Customer2` --> should throw HTTP 403
8) Pay installments of `Customer1` using JWT of ADMIN
9) Pay installments of `Customer1` using JWT `Customer2` --> should throw HTTP 403