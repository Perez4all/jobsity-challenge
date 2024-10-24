# Jobsity Challenge

This application executes third party api requests and acts as an aggregator service returning all the contacts from kenectlabs or if needed by page to the end user.

## Requirements

For building and running the application you need:

- [Java 21](https://adoptium.net/es/temurin/releases/?version=21)
- [Maven 3](https://maven.apache.org) (3.9.6 was used in this project)

## Running the application locally

There are several ways to run a Spring Boot application on your local machine. One way is to execute the `main` method in the `com.jobsity.JobsityChallengeApplication` class from your IDE.

Alternatively you can use the [Spring Boot Maven plugin](https://docs.spring.io/spring-boot/docs/current/reference/html/build-tool-plugins-maven-plugin.html) like so:

```shell
mvn spring-boot:run
```

## Sample Requests

To get all contacts:

```curl --location 'http://localhost:8080/contacts'```

To get contacts by page:

```curl --location 'http://localhost:8080/contacts?page=1'```

There are edge test cases eg. No contacts found, this is a sample request:

```curl --location 'http://localhost:8080/contacts?page=999'```