/*
DROP TABLE IF EXISTS Nessus.UserModel;
DROP TABLE IF EXISTS Nessus.UserState;
*/

CREATE TABLE IF NOT EXISTS Nessus.UserState (
  userId VARCHAR(48) NOT NULL,
  username VARCHAR(48) NOT NULL,
  email VARCHAR(48) NOT NULL,
  logins INTEGER NOT NULL DEFAULT 0,
  lastLogin DATETIME NOT NULL, 
  status VARCHAR(12) NOT NULL,
  PRIMARY KEY (userId)
);

CREATE TABLE IF NOT EXISTS Nessus.UserModel ( 
  userId VARCHAR(48) NOT NULL,
  modelId VARCHAR(48) NOT NULL,
  content CLOB, 
  PRIMARY KEY (modelId),
  FOREIGN KEY (userId) REFERENCES Nessus.UserState (userId) ON DELETE CASCADE
);

/**************  DO NOT DROP TABLES BELOW *************/
