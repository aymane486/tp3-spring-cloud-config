DROP TABLE IF EXISTS product;
DROP SEQUENCE IF EXISTS hibernate_sequence;

CREATE TABLE product (
  id INTEGER NOT NULL,
  titre VARCHAR(255),
  description VARCHAR(255),
  image VARCHAR(255),
  prix DOUBLE,
  PRIMARY KEY (id)
);

-- Sequence utilisee par @GeneratedValue (Hibernate 5) pour les POST
CREATE SEQUENCE hibernate_sequence START WITH 100 INCREMENT BY 1;
