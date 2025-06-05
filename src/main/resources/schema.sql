-- Create vehicles table
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGSERIAL PRIMARY KEY,
    make VARCHAR(50) NOT NULL,
    model VARCHAR(50) NOT NULL,
    vehicle_year INTEGER NOT NULL,
    vin VARCHAR(17) NOT NULL UNIQUE,
    color VARCHAR(30) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    mileage INTEGER NOT NULL,
    fuel_type VARCHAR(20) NOT NULL,
    transmission_type VARCHAR(20) NOT NULL,
    engine_size VARCHAR(20) NOT NULL,
    available BOOLEAN NOT NULL DEFAULT true,
    acquisition_date DATE,
    description VARCHAR(1000),
    image_url VARCHAR(255)
);

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL DEFAULT 'defaultpassword',
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL
);

-- Create orders table
CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_number VARCHAR(20) NOT NULL UNIQUE,
    order_date TIMESTAMP NOT NULL,
    delivery_date TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    payment_method VARCHAR(20),
    notes TEXT,
    customer_id BIGINT NOT NULL,
    vehicle_id BIGINT,  -- Nullable since an order could contain multiple vehicles via order_items
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_customer 
        FOREIGN KEY (customer_id) 
        REFERENCES users(id) 
        ON DELETE RESTRICT,
    CONSTRAINT fk_vehicle 
        FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(id) 
        ON DELETE SET NULL
);

-- Create indexes for orders table foreign keys
CREATE INDEX IF NOT EXISTS idx_orders_customer_id ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_orders_vehicle_id ON orders(vehicle_id);

-- Create order_items table
CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    subtotal DECIMAL(10,2) NOT NULL,
    additional_services VARCHAR(500),
    is_paid BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT fk_order 
        FOREIGN KEY (order_id) 
        REFERENCES orders(id) 
        ON DELETE CASCADE,
    CONSTRAINT fk_vehicle 
        FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(id) 
        ON DELETE RESTRICT
);

-- Create indexes for order_items table foreign keys
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_vehicle_id ON order_items(vehicle_id);

-- Create maintenance table
CREATE TABLE IF NOT EXISTS maintenance (
    id BIGSERIAL PRIMARY KEY,
    vehicle_id BIGINT NOT NULL,
    maintenance_type VARCHAR(20) NOT NULL,
    service_date DATE NOT NULL,
    next_service_date DATE,
    cost DECIMAL(10,2) NOT NULL,
    service_provider VARCHAR(100),
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicle 
        FOREIGN KEY (vehicle_id) 
        REFERENCES vehicles(id) 
        ON DELETE CASCADE
);

-- Create index for maintenance table foreign key
CREATE INDEX IF NOT EXISTS idx_maintenance_vehicle_id ON maintenance(vehicle_id);  