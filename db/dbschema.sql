DROP TABLE IF EXISTS service_plan_info;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS country;

CREATE TABLE IF NOT EXISTS country 
  ( 
     id       INT(11) NOT NULL auto_increment, 
     name     VARCHAR(50) DEFAULT NULL, 
     code     VARCHAR(5) DEFAULT NULL, 
     currency VARCHAR(50) DEFAULT NULL, 
     PRIMARY KEY (id) 
  ) 
engine=innodb; 


CREATE TABLE IF NOT EXISTS customer 
  ( 
     id                     INT(11) NOT NULL auto_increment, 
     name                   VARCHAR(50) DEFAULT NULL, 
     email                  VARCHAR(50) DEFAULT NULL, 
     plan_type              VARCHAR(50) DEFAULT NULL, 
     signup_timestamp       BIGINT UNSIGNED DEFAULT NULL, 
     next_billing_timestamp BIGINT UNSIGNED DEFAULT NULL, 
     price                  FLOAT(5, 2) DEFAULT 0.0, 
     country_id             INT NOT NULL, 
     PRIMARY KEY (id), 
     FOREIGN KEY fk_country(country_id) REFERENCES country(id) ON UPDATE CASCADE 
     ON DELETE RESTRICT 
  ) 
engine=innodb; 


CREATE TABLE IF NOT EXISTS service_plan_info 
  ( 
     id                     INT(11) NOT NULL auto_increment, 
     plan_type              VARCHAR(50) DEFAULT NULL, 
     price                  FLOAT(5, 2) DEFAULT 0.0, 
     country_id             INT NOT NULL, 
     rules                  VARCHAR(50) DEFAULT NULL, 
     price_update_completed BOOLEAN, 
     PRIMARY KEY (id), 
     FOREIGN KEY fk_country(country_id) REFERENCES country(id) ON UPDATE CASCADE 
     ON DELETE RESTRICT 
  ) 
engine=innodb; 