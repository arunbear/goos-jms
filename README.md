# GOOS with JMS

This project is a new version of the Auction Sniper case study developed in the book
[Growing Object-Oriented Software Guided by Tests](http://www.growing-object-oriented-software.com)
by Steve Freeman and Nat Pryce.

## Features

Granular commits, to make it easier to follow the evolution of the project.

JMS instead of XMPP. The availability of embeddable JMS servers makes it much easier to build the project
and run the tests.

Spring Boot - makes it easier to manage JMS and even dependencies between the various project components.


## Build and run all Tests

To run the tests

```bash
./mvnw clean compile test
```

## Other Helpful Projects

[goos](https://github.com/belano/goos)
: This is (mostly) the code from the book with support for Maven, and with an XMPP
server provided via Docker. Helpfully each chapter has its own git commit making it easier to follow the progress
of the project.

[spring-boot-swing](https://github.com/mightychip/spring-boot-swing)
: An example of how to run a Swing application via Spring Boot.

[spring-boot-4-boot-notes](https://github.com/marconak-matej/spring-boot-4-boot-notes)
: Examples of Spring Boot 4.1 features, including JmsClient
