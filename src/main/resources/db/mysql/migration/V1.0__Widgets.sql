CREATE TABLE IF NOT EXISTS galoshesdb.widgets (
  ID INT NOT NULL AUTO_INCREMENT,
  FIRST_NAME VARCHAR(50) NOT NULL,
  LAST_NAME VARCHAR(50) NOT NULL,
  ADD_TS DATETIME(6) NOT NULL,
  WIDGETSTATUS VARCHAR(50) NOT NULL,
  PRIMARY KEY (ID))
ENGINE = InnoDB;