# GitHub Project - Group 8

## Application setup

In order to test application it is neccessary to run first **NewBankServer.java** and after that **ExampleClient.java**. 

## Available commands
Commands in our application follows the pattern:

**commandName [-arg] parameterValue**

Our application has the following commands available:

* pay 
  * [-h] - printing help
  * [-a] - choosing amount to send
  * [-s] - choosing source account name
  * [-d] - choosing destination (customer id number)
  * [-t] - (optional) transaction title
* showaccounts
  * [-h] - printing help
* moveaccounts
  * [-h] - printing help
  * [-a] - choosing amount to send
  * [-s] - choosing source account name
  * [-d] - choosing destination internal account name 
  * [-t] - (optional) transaction title
* addaccount
  * [-h] - printing help
  * [-n] - choosing new account name
* requestloan
  * [-h] - printing help
  * [-a] - amount requested as loan
  * [-d] - days until repayment 
* offerloan
  * [-h] - printing help
  * [-a] - amount offered as loan
  * [-d] - days until repayment  
* getoffers | getrequests
  * [-h] -  printing help
  * [-x] - (optional) filter minimum days
  * [-y] - (optional) filter maximum days
  * [-p] - (optional) filter minimum amount
  * [-q] - (optional) filter maximum amount  
* matchloan
  * [-h] - printing help
  * [-i] - select ID of offer or request to accept
* payloan
  * [-h] - printing help
  * [-i] - select ID of loan to repay
  * [-a] - amount to repay  
* getloans
  * [-h] - printing help
* retract
  * [-h] - printing help
  * [-i] - select ID of offer or request to repay
* exit/logout
  
Server response can be divided into three categories:
- requests (blue in command line) - bank app needs information from the customer
- information (white in command line) - information for the customer
- error (red in command line) - errors during processing request
- warnings (orange in command line) - warning for the user
