CREATE TABLE Users (
   UserID INT GENERATED ALWAYS AS IDENTITY 
                (START WITH 1, INCREMENT BY 1),
   firstName varchar (40),
   lastName varchar (40),
   password varchar (30),
   wins INT,
   losses INT,
   draws INT,
   PRIMARY KEY (UserID)
);

CREATE TABLE Games (
   player1 INT,
   player2 INT,
   move varchar (20),
   PRIMARY KEY (player1,player2)
);