## Clustered Data Warehouse

File format is CSV contains the following fields (Deal Unique Id, From Currency ISO Code "Ordering Currency", To Currency ISO Code, Deal timestamp, Deal Amount in ordering currency).

Spring boot is used to manage imported files and store CSV formatted data into MySQL tables. JPA is selected to access DB.

### How To Run:
Go to the project folder and execute the below command line: 
```sh
$ cd /path/to/bloomberg_warehouse
$ mvn clean package
```
The command will create an executable *jar* file in the */target* folder of the project base directory.
Run the **bloomberg.jar**, to test it out you can go to http://localhost:8080/broken.
Application creates all needed tables in your DB while initialization process. Make sure you have MySQL database run your machine with a default port number.

### Alternative ways
You can use Docker container. 
Verify the docker installation by running
```sh
$ docker -v
Docker version 17.09.0-ce, build afdb6d4
```
You can find full instructions in the Docker [Getting Started](https://docs.docker.com/get-started/#a-brief-explanation-of-containers) documentation.

Run API docker, tagged as **snavruzov/bloomberg**.
Let’s first start by setting up the container for the MySQL database. This is quite simple, because MySQL already provided a public image for us to use on the Docker hub.
All we have to do is run a container, it will automatically download the mysql image if it doesn’t already have it installed.
```sh
$ docker run -d \
    --name warehouse \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=bloomberg \
    -e MYSQL_USER=sardor \
    -e MYSQL_PASSWORD=root \
    mysql:latest
```
Now you can run Spring Boot application, the application will be linked to MySQL container.
```sh
$ docker run -d \
    --name bloomberg \
    --link warehouse:mysql \ 
    -p 8080:8080 \ 
    -e DATABASE_HOST=warehouse \
    -e DATABASE_PORT=3306 \
    -e DATABASE_NAME=bloomberg \
    -e DATABASE_USER=sardor \
    -e DATABASE_PASSWORD=root \
    snavruzov/bloomberg
```
Technicaly the project is all set except a front-end part, let's deploy our web interface implemented on Angular using Docker container.

```sh
$ docker run -d \
    --name frontweb \
    -p 4200:4200 \ 
    snavruzov/bloomberg_web
```
Docker will Angular application listening 4200 port. To check if everything set OK go to the link http://localhost:4200
Front-end Angular source codes can be found here: https://github.com/snavruzov/bloomberg_web


