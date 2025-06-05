-- Sample vehicle data
INSERT INTO vehicles (make, model, vehicle_year, vin, color, price, mileage, fuel_type, transmission_type, engine_size, available, acquisition_date, description, image_url)
VALUES (
    'Toyota',
    'Camry',
    2023,
    'JTDKN3DU7D5012345',
    'Silver',
    25000.00,
    0,
    'PETROL',
    'AUTOMATIC',
    '2.5L',
    true,
    CURRENT_DATE,
    'Brand new Toyota Camry with advanced safety features',
    'https://example.com/camry.jpg'
);

INSERT INTO vehicles (make, model, vehicle_year, vin, color, price, mileage, fuel_type, transmission_type, engine_size, available, acquisition_date, description, image_url)
VALUES (
    'Honda',
    'CR-V',
    2022,
    '5J6RW2H89ML123456',
    'Black',
    32000.00,
    15000,
    'DIESEL',
    'AUTOMATIC',
    '1.6L',
    true,
    CURRENT_DATE,
    'Well-maintained Honda CR-V with low mileage',
    'https://example.com/crv.jpg'
);

INSERT INTO vehicles (make, model, vehicle_year, vin, color, price, mileage, fuel_type, transmission_type, engine_size, available, acquisition_date, description, image_url)
VALUES (
    'Tesla',
    'Model 3',
    2023,
    '5YJ3E1EA0PF123456',
    'White',
    45000.00,
    5000,
    'ELECTRIC',
    'AUTOMATIC',
    'Dual Motor',
    true,
    CURRENT_DATE,
    'Tesla Model 3 with autopilot capabilities',
    'https://example.com/tesla.jpg'
);

INSERT INTO vehicles (make, model, vehicle_year, vin, color, price, mileage, fuel_type, transmission_type, engine_size, available, acquisition_date, description, image_url)
VALUES (
    'Toyota',
    'Prius',
    2022,
    'JTDKN3DU7D5123456',
    'Blue',
    28000.00,
    10000,
    'HYBRID',
    'AUTOMATIC',
    '1.8L',
    true,
    CURRENT_DATE,
    'Fuel-efficient Toyota Prius hybrid',
    'https://example.com/prius.jpg'
);

INSERT INTO vehicles (make, model, vehicle_year, vin, color, price, mileage, fuel_type, transmission_type, engine_size, available, acquisition_date, description, image_url)
VALUES (
    'Volkswagen',
    'Golf',
    2021,
    'WVWZZZ1KZBW123456',
    'Red',
    22000.00,
    20000,
    'PETROL',
    'MANUAL',
    '2.0L',
    true,
    CURRENT_DATE,
    'Sporty Volkswagen Golf with manual transmission',
    'https://example.com/golf.jpg'
);

-- Sample user data
INSERT INTO users (username, first_name, last_name, email, phone, role)
VALUES ('admin', 'Admin', 'User', 'admin@example.com', '555-123-4567', 'ADMIN');

INSERT INTO users (username, first_name, last_name, email, phone, role)
VALUES ('manager1', 'John', 'Smith', 'john@example.com', '555-234-5678', 'MANAGER');

INSERT INTO users (username, first_name, last_name, email, phone, role)
VALUES ('sales1', 'Jane', 'Doe', 'jane@example.com', '555-345-6789', 'SALESPERSON');

INSERT INTO users (username, first_name, last_name, email, phone, role)
VALUES ('customer1', 'David', 'Johnson', 'david@example.com', '555-456-7890', 'CUSTOMER');

INSERT INTO users (username, first_name, last_name, email, phone, role)
VALUES ('customer2', 'Sarah', 'Brown', 'sarah@example.com', '555-567-8901', 'CUSTOMER');

-- Sample orders data
INSERT INTO orders (order_number, order_date, delivery_date, total_amount, status, payment_method, notes, customer_id, vehicle_id)
VALUES ('ORD-20230001', CURRENT_DATE - 10, CURRENT_DATE - 3, 25000.00, 'DELIVERED', 'CREDIT_CARD', 'First order - delivered on time', 4, 1);

INSERT INTO orders (order_number, order_date, delivery_date, total_amount, status, payment_method, notes, customer_id, vehicle_id)
VALUES ('ORD-20230002', CURRENT_DATE - 5, CURRENT_DATE + 5, 32000.00, 'CONFIRMED', 'BANK_TRANSFER', 'Customer requested delivery on weekend', 5, 2);

-- Sample order_items data
INSERT INTO order_items (order_id, vehicle_id, price, quantity, subtotal, additional_services, is_paid)
VALUES (1, 1, 25000.00, 1, 25000.00, NULL, true);

INSERT INTO order_items (order_id, vehicle_id, price, quantity, subtotal, additional_services, is_paid)
VALUES (2, 2, 32000.00, 1, 32000.00, NULL, false);

-- Example of an order with multiple items
INSERT INTO orders (order_number, order_date, delivery_date, total_amount, status, payment_method, notes, customer_id)
VALUES ('ORD-20230003', CURRENT_DATE - 2, CURRENT_DATE + 10, 67000.00, 'PROCESSING', 'BANK_TRANSFER', 'Multiple vehicle order with special handling', 4);

INSERT INTO order_items (order_id, vehicle_id, price, quantity, subtotal, additional_services, is_paid)
VALUES (3, 3, 45000.00, 1, 45000.00, 'Extended warranty', false);

INSERT INTO order_items (order_id, vehicle_id, price, quantity, subtotal, additional_services, is_paid)
VALUES (3, 4, 22000.00, 1, 22000.00, NULL, false);

-- Sample maintenance data
INSERT INTO maintenance (vehicle_id, maintenance_type, service_date, next_service_date, cost, service_provider, status, notes)
VALUES (3, 'ROUTINE', CURRENT_DATE - 15, CURRENT_DATE + 180, 150.00, 'Tesla Service Center', 'COMPLETED', 'Regular maintenance check and software update.');

INSERT INTO maintenance (vehicle_id, maintenance_type, service_date, next_service_date, cost, service_provider, status, notes)
VALUES (4, 'INSPECTION', CURRENT_DATE + 10, CURRENT_DATE + 100, 75.00, 'Toyota Certified Workshop', 'SCHEDULED', 'Scheduled 10,000 mile inspection.');

INSERT INTO maintenance (vehicle_id, maintenance_type, service_date, next_service_date, cost, service_provider, status, notes)
VALUES (5, 'REPAIR', CURRENT_DATE - 2, NULL, 350.00, 'AutoFix Shop', 'IN_PROGRESS', 'Replacing brake pads and rotors.'); 