DROP TABLE Game;
DROP TABLE GameRequest;
DROP TABLE UserProfile;


CREATE TABLE UserProfile(
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
   player1 varchar (40),
   player2 varchar (40),
   PRIMARY KEY (gameID),
   FOREIGN KEY (player1) REFERENCES UserProfile (username),
   FOREIGN KEY (player2) REFERENCES UserProfile (username)
);

CREATE TABLE GameRequest (
   requestingUser varchar (40),
   requestedUser varchar (40),
   gameID INT,
   PRIMARY KEY (requestingUser, requestedUser),
   FOREIGN KEY (requestedUser) REFERENCES UserProfile (username),
   FOREIGN KEY (requestingUser) REFERENCES UserProfile (username)
);