## Clustered Data Warehouse

File format is CSV contains the following fields (Deal Unique Id, From Currency ISO Code "Ordering Currency", To Currency ISO Code, Deal timestamp, Deal Amount in ordering currency).

Spring boot is used to manage imported files and store CSV formatted data into MySQL tables. JPA is selected to access DB.

### How To Run:
Go to the project folder and execute the below command line: 
```sh
$ cd /path/to/bloomberg_warehouse
$ mvn clean package
```
The command will package the project into */target* directory
