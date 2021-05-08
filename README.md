# Trivia
## Main idea
We are going to create a multyplayer android game. One player create a game and share room code with his friend. They connect and play together. Host can start game and the players complete tasks and get their points. The player with the most points wins.
## Risks
- Players can lose connection in any time and we need to react correctly.
## Technology Stack
Server - Java, Spring Boot, Postgresql.
Android application - Kotlin, Android Studio, xml.
## MVP
Date: mid-may.
Features: simple tasks such as select an answer and enter a nearest answer (years, length, size, ect).
## Release
Date: 20th may.
## Team Members
- V. Chernikov
- E. Dubinskaya

# Architecture
## Server
The server is capable of running multiple games simultaneously. Tasks, user tokens, and nicknames are stored in a PostgreSQL database. Lobbies are stored in application memory. The server is responsible for the logic of connecting/joining to lobbies, generating tasks, processing answers received from players, and notifying players if the lobby state has changed.

## Client
The user interacts with the application. Application receive token from server and store it in data storage. Aplication didn't store any other information about user. Application use Asynk Tasks to do requests to the server and background bounded service to do longpolling connection.

## Communication
Client-server communication is done using HTTP, objects are serialized as JSON. All requests require a token that the client receives from the server if it doesn't have one. Long polling is used to update lobby state, a client sends a subscription request with a timestamp of the last known lobby state if lobby has been updated since that time, lobby state is sent immediately, otherwise, it is added in subscription pool and state will be sent as soon as the lobby is modified.  

## Basic application flow
![Usage diagram](/usage.png)
