DROP TABLE GameRequest;
DROP TABLE User;
DROP TABLE Game;

CREATE TABLE User(
   UserID INT AUTO_INCREMENT,
   username varchar (40) UNIQUE,
   password varchar (30),
   wins INT,
   losses INT,
   draws INT,
   available BOOLEAN,
   PRIMARY KEY (UserID)
);

CREATE TABLE Game (
   gameID INT AUTO_INCREMENT,
   move varchar (20),
   PRIMARY KEY (gameID)
);

CREATE TABLE GameRequest (
   requestingUser varchar (40),
   requestedUser varchar (40),
   gameID INT,
   PRIMARY KEY (requestingUser, requestedUser),
   FOREIGN KEY (requestedUser) REFERENCES User (username),
   FOREIGN KEY (requestingUser) REFERENCES USER (username)
);