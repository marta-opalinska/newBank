# GitHub Project - Group 8

## Application setup

In order to test application it is neccessary to run first **NewBankServer.java** and after that **ExampleClient.java**. 

## Available commands
Commands in our application follows the pattern:

**commandName [-arg] paramenerValue**

Our application have following commands available:

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
