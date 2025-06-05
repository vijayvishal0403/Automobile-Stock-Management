-- Drop tables in the correct order to respect foreign key constraints
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS maintenance;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users; 