# ChatApp

ChatApp allows you to create an account (or login anonymously) and chat with others on a public chat, with the option to send up to 100 messages for free or unlimited for premium accounts.

## Requirements

- Node.js 16.0
- Maven 3.1+
- MySQL 8.0

Alternatively download the latest version of Docker Engine and skip the installation process.

## Installation

1. Clone this repository to a folder of your choice.
2. Go into the `frontend` folder and run `npm install` to install all the Frontend dependencies.
3. Go into the `backend` folder and run `mvn clean install` to install all the Backend dependencies.
4. Create a new MySQL connection, using MySQL Workbench if you're using Windows.

## Setup

First go to the `application.properties` file at `backend/src/main/resources/` and add the following lines:

```properties
spring.mail.username={NotificationMailAddress}
spring.mail.password={NotificationMailPassword}
```

Where {NotificationMailAddress} and {NotificationMailPassword} are replaced with an email credentials of your choice. That will allow premium renewal notifications via email.

Optionally, if you want to can also disable the JobRunr Dashboard by modifying the following line:

```properties
org.jobrunr.dashboard.enabled=false
```

After that, create a `secrets.kt` file at `backend/src/main/kotlin/com/example/demo/static/`, in there add the following line and change the SECRET_KEY value to a random value of your choice, it will be used when signing a JWT.

```kotlin
package com.example.demo.static
const val SECRET_KEY = "{StringOfYourChoice}"
```

### Additional steps for non Docker users

Add the following lines to the `application.properties` file:

```properties
spring.datasource.url = jdbc:mysql://localhost:{MySQLPort}/chat_app?useSSL=true
spring.datasource.username={MySQLUsername}
spring.datasource.password={MySQLPassword}
```

Replace {MySQLPort}, {MySQLUsername}, {MySQLPassword} with the relevant values for your machine.

### Optional steps for Docker users

You can choose a different MySQL username and password as well as different ports that the app will use, by modifying the `.env` inside of the project's root folder.

## Running the application

### With Docker

Run ```docker-compose up``` inside the project's root folder. It will download and install all the necessary dependencies inside of three containers, and then run the whole project for you.

### Without docker

Go into the `backend` folder and run `mvn spring-boot:run` to get the Backend running.

Then, go into the `frontend` folder and run `npm start` to get the Frontend running.

## Usage

Go to [http://localhost:4200](http://localhost:4200) to access the website.

In case you enabled the JobRunr Dashboard you can access it on [http://localhost:8000/dashboard](http://localhost:8000/dashboard) (note that it might use a different port if you modified it on the .env file).
