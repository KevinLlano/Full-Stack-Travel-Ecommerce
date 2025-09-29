-- Clean, ordered schema + seed

BEGIN;

-- Drop tables in reverse dependency order
DROP TABLE IF EXISTS excursion_cartitem CASCADE;
DROP TABLE IF EXISTS cart_items CASCADE;
DROP TABLE IF EXISTS carts CASCADE;
DROP TABLE IF EXISTS customers CASCADE;
DROP TABLE IF EXISTS excursions CASCADE;
DROP TABLE IF EXISTS vacations CASCADE;
DROP TABLE IF EXISTS divisions CASCADE;
DROP TABLE IF EXISTS countries CASCADE;

CREATE TABLE countries (
    country_id BIGSERIAL PRIMARY KEY,
    country VARCHAR(255),
    create_date TIMESTAMP(6),
    last_update TIMESTAMP(6)
);

CREATE TABLE vacations (
    vacation_id BIGSERIAL PRIMARY KEY,
    create_date TIMESTAMP(6),
    description VARCHAR(255),
    image_url VARCHAR(255),
    last_update TIMESTAMP(6),
    travel_fare_price NUMERIC(19,2),
    vacation_title VARCHAR(255)
);

CREATE TABLE divisions (
    division_id BIGSERIAL PRIMARY KEY,
    division VARCHAR(255),
    create_date TIMESTAMP(6),
    last_update TIMESTAMP(6),
    country_id BIGINT NOT NULL REFERENCES countries(country_id) ON DELETE CASCADE
);

CREATE TABLE excursions (
    excursion_id BIGSERIAL PRIMARY KEY,
    create_date TIMESTAMP(6),
    excursion_price NUMERIC(19,2),
    excursion_title VARCHAR(255),
    image_url VARCHAR(255),
    last_update TIMESTAMP(6),
    vacation_id BIGINT NOT NULL REFERENCES vacations(vacation_id) ON DELETE CASCADE
);

CREATE TABLE customers (
    customer_id BIGSERIAL PRIMARY KEY,
    address VARCHAR(255),
    create_date TIMESTAMP(6),
    customer_first_name VARCHAR(255),
    customer_last_name VARCHAR(255),
    last_update TIMESTAMP(6),
    phone VARCHAR(255),
    postal_code VARCHAR(255),
    division_id BIGINT REFERENCES divisions(division_id) ON DELETE CASCADE
);

CREATE TABLE carts (
    cart_id BIGSERIAL PRIMARY KEY,
    package_price NUMERIC(19,2),
    party_size INT,
    status VARCHAR(20) CHECK (status IN ('pending','ordered','cancelled')),
    order_tracking_number VARCHAR(255),
    create_date TIMESTAMP(6),
    last_update TIMESTAMP(6),
    customer_id BIGINT NOT NULL REFERENCES customers(customer_id) ON DELETE CASCADE
);

CREATE TABLE cart_items (
    cart_item_id BIGSERIAL PRIMARY KEY,
    create_date TIMESTAMP(6),
    last_update TIMESTAMP(6),
    cart_id BIGINT NOT NULL REFERENCES carts(cart_id) ON DELETE CASCADE,
    vacation_id BIGINT NOT NULL REFERENCES vacations(vacation_id) ON DELETE CASCADE
);

CREATE TABLE excursion_cartitem (
    cart_item_id BIGINT NOT NULL REFERENCES cart_items(cart_item_id) ON DELETE CASCADE,
    excursion_id BIGINT NOT NULL REFERENCES excursions(excursion_id) ON DELETE CASCADE,
    PRIMARY KEY (cart_item_id, excursion_id)
);

-- Seed base lookup tables
INSERT INTO countries(country_id, country, create_date, last_update) VALUES
 (1,'U.S', NOW(), NOW()),
 (2,'UK', NOW(), NOW()),
 (3,'Canada', NOW(), NOW());

INSERT INTO divisions(division_id, division, create_date, last_update, country_id) VALUES
 (54,'Alaska', NOW(), NOW(),1),(2,'Arizona', NOW(), NOW(),1),(3,'Arkansas', NOW(), NOW(),1),(4,'California', NOW(), NOW(),1),(5,'Colorado', NOW(), NOW(),1),
 (6,'Connecticut', NOW(), NOW(),1),(7,'Delaware', NOW(), NOW(),1),(8,'District of Columbia', NOW(), NOW(),1),(9,'Florida', NOW(), NOW(),1),(10,'Georgia', NOW(), NOW(),1),
 (52,'Hawaii', NOW(), NOW(),1),(11,'Idaho', NOW(), NOW(),1),(12,'Illinois', NOW(), NOW(),1),(13,'Indiana', NOW(), NOW(),1),(14,'Iowa', NOW(), NOW(),1),
 (15,'Kansas', NOW(), NOW(),1),(16,'Kentucky', NOW(), NOW(),1),(17,'Louisiana', NOW(), NOW(),1),(18,'Maine', NOW(), NOW(),1),(19,'Maryland', NOW(), NOW(),1),
 (20,'Massachusetts', NOW(), NOW(),1),(21,'Michigan', NOW(), NOW(),1),(22,'Minnesota', NOW(), NOW(),1),(23,'Mississippi', NOW(), NOW(),1),(24,'Missouri', NOW(), NOW(),1),
 (25,'Montana', NOW(), NOW(),1),(26,'Nebraska', NOW(), NOW(),1),(27,'Nevada', NOW(), NOW(),1),(28,'New Hampshire', NOW(), NOW(),1),(29,'New Jersey', NOW(), NOW(),1),
 (30,'New Mexico', NOW(), NOW(),1),(31,'New York', NOW(), NOW(),1),(32,'North Carolina', NOW(), NOW(),1),(33,'North Dakota', NOW(), NOW(),1),(34,'Ohio', NOW(), NOW(),1),
 (35,'Oklahoma', NOW(), NOW(),1),(36,'Oregon', NOW(), NOW(),1),(37,'Pennsylvania', NOW(), NOW(),1),(38,'Rhode Island', NOW(), NOW(),1),(39,'South Carolina', NOW(), NOW(),1),
 (40,'South Dakota', NOW(), NOW(),1),(41,'Tennessee', NOW(), NOW(),1),(42,'Texas', NOW(), NOW(),1),(43,'Utah', NOW(), NOW(),1),(44,'Vermont', NOW(), NOW(),1),
 (45,'Virginia', NOW(), NOW(),1),(46,'Washington', NOW(), NOW(),1),(47,'West Virginia', NOW(), NOW(),1),(48,'Wisconsin', NOW(), NOW(),1),(49,'Wyoming', NOW(), NOW(),1),
 (61,'Alberta', NOW(), NOW(),3),(62,'British Columbia', NOW(), NOW(),3),(63,'Manitoba', NOW(), NOW(),3),(64,'New Brunswick', NOW(), NOW(),3),(72,'Newfoundland and Labrador', NOW(), NOW(),3),
 (60,'Northwest Territories', NOW(), NOW(),3),(65,'Nova Scotia', NOW(), NOW(),3),(70,'Nunavut', NOW(), NOW(),3),(67,'Ontario', NOW(), NOW(),3),(66,'Prince Edward Island', NOW(), NOW(),3),
 (68,'Québec', NOW(), NOW(),3),(69,'Saskatchewan', NOW(), NOW(),3),(71,'Yukon', NOW(), NOW(),3),(101,'England', NOW(), NOW(),2),(102,'Wales', NOW(), NOW(),2),
 (103,'Scotland', NOW(), NOW(),2),(104,'Northern Ireland', NOW(), NOW(),2);

INSERT INTO customers(customer_id, address, create_date, customer_first_name, customer_last_name, last_update, phone, postal_code, division_id) VALUES
 (1,'123 Easy St', NOW(),'John','Doe', NOW(),'(555)555-5555','55555',31);

INSERT INTO carts(cart_id, package_price, party_size, status, order_tracking_number, create_date, last_update, customer_id) VALUES
 (1,0,1,'pending', NULL, NOW(), NOW(), 1);

-- Vacations (full list)
INSERT INTO vacations(create_date, description, image_url, last_update, travel_fare_price, vacation_title) VALUES
 (NOW(),'Visit the beautiful country of Italy','https://images.unsplash.com/photo-1515859005217-8a1f08870f59?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1110&q=80', NOW(),1000,'Italy'),
 (NOW(),'Visit the beautiful country of Greece','https://images.unsplash.com/photo-1533105079780-92b9be482077?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80', NOW(),1500,'Greece'),
 (NOW(),'Visit the beautiful country of France','https://images.unsplash.com/photo-1502602898657-3e91760cbb34?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80', NOW(),1500,'France'),
 (NOW(),'Visit the beautiful country of Belgium','https://images.unsplash.com/photo-1491557345352-5929e343eb89?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80', NOW(),1500,'Belgium'),
 (NOW(),'Visit the beautiful country of Brazil','https://images.unsplash.com/photo-1483729558449-99ef09a8c325?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80', NOW(),1500,'Brazil'),
 (NOW(),'Visit the beautiful state of South Dakota','https://images.unsplash.com/photo-1605801495512-f47a64d01f4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1472&q=80', NOW(),1500,'South Dakota'),
 (NOW(),'Visit the beautiful city of Nashville','https://images.unsplash.com/photo-1545419913-775e3e82c7db?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1636&q=80', NOW(),1500,'Nashville'),
 (NOW(),'Visit the beautiful state of Wisconsin','https://images.unsplash.com/photo-1566419808810-658178380987?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1471&q=80', NOW(),1500,'Wisconsin');

-- Excursions using vacation title lookups
INSERT INTO excursions(create_date, excursion_price, excursion_title, image_url, last_update, vacation_id)
SELECT NOW(), seed.excursion_price, seed.excursion_title, seed.image_url, NOW(), v.vacation_id
FROM (
    VALUES
    -- Italy
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Italy'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Italy'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Italy'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Italy'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Italy'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Italy'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Italy'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Italy'),
    -- Greece
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Greece'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Greece'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Greece'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Greece'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Greece'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Greece'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Greece'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Greece'),
    -- France
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','France'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','France'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','France'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','France'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','France'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','France'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','France'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','France'),
    -- Belgium
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Belgium'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Belgium'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Belgium'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Belgium'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Belgium'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Belgium'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Belgium'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Belgium'),
    -- Brazil
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Brazil'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Brazil'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Brazil'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Brazil'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Brazil'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Brazil'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Brazil'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Brazil'),
    -- South Dakota
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','South Dakota'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','South Dakota'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','South Dakota'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','South Dakota'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','South Dakota'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','South Dakota'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','South Dakota'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','South Dakota'),
    -- Nashville
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Nashville'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Nashville'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Nashville'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Nashville'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Nashville'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Nashville'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Nashville'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Nashville'),
    -- Wisconsin
    (100,'Cheese Tour','https://images.unsplash.com/photo-1631379578550-7038263db699?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1474&q=80','Wisconsin'),
    (75,'Bicycle Tour','https://images.unsplash.com/uploads/14122621859313b34d52b/37e28531?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1473&q=80','Wisconsin'),
    (250,'Spa Treatment','https://images.unsplash.com/photo-1620733723572-11c53f73a416?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=687&q=80','Wisconsin'),
    (100,'Historic Tour','https://images.unsplash.com/photo-1479142506502-19b3a3b7ff33?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Wisconsin'),
    (25,'Boat Ride','https://images.unsplash.com/photo-1587252337395-d02401a8a814?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1026&q=80','Wisconsin'),
    (500,'Horseback Riding Lesson','https://images.unsplash.com/photo-1598810577851-34982c359155?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Wisconsin'),
    (120,'Zip Lining','https://images.unsplash.com/photo-1625076307714-a5cd1b2beb4f?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Wisconsin'),
    (150,'Dinner and a Show','https://plus.unsplash.com/premium_photo-1661774645265-ce387923cb5b?ixlib=rb-4.0.3&ixid=MnwxMJA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1470&q=80','Wisconsin')
) AS seed(excursion_price, excursion_title, image_url, vacation_title)
JOIN vacations v ON v.vacation_title = seed.vacation_title;

-- Sequence adjustments
SELECT setval(pg_get_serial_sequence('countries','country_id'), (SELECT MAX(country_id) FROM countries));
SELECT setval(pg_get_serial_sequence('divisions','division_id'), (SELECT MAX(division_id) FROM divisions));
SELECT setval(pg_get_serial_sequence('customers','customer_id'), (SELECT MAX(customer_id) FROM customers));
SELECT setval(pg_get_serial_sequence('carts','cart_id'), (SELECT MAX(cart_id) FROM carts));
SELECT setval(pg_get_serial_sequence('vacations','vacation_id'), (SELECT MAX(vacation_id) FROM vacations));
SELECT setval(pg_get_serial_sequence('excursions','excursion_id'), (SELECT MAX(excursion_id) FROM excursions));

COMMIT;