# board-gam
Game Name: Mankala

Game Type : Multi player 

Bref info about game:
Each game is given a unique gameId and  intermidiate game state is stored using that key in in memory db in our case I used redis db

Technologies used:

Java 8, Rest api, Inmemory db - redis db, Spring core

How to play Mancala: 

https://www.youtube.com/watch?v=-A-djjimCcM



Screen 1: 

It is the welcome page where user can create new game or join a game which is already started with gameId
 
![](/src/main/resources/images/image1.png?raw=true)

Screen 2:
Game board presented to the player 1
 

Game started by player 1 and the player 1 need to share his game id to opponent player 

![](/src/main/resources/images/image2.png?raw=true)


Screen 3
Opponent player need to enter that game id

![](/src/main/resources/images/image3.png?raw=true)

 Screen 4::
Game board to the player 2

 
![](/src/main/resources/images/image5.png?raw=true)


