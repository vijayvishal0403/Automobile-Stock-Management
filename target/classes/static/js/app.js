document.addEventListener('DOMContentLoaded', function() {
    // Navigation functionality
    const navLinks = document.querySelectorAll('.navbar-nav .nav-link');
    const contentPages = document.querySelectorAll('.content-page');

    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Update active nav link
            navLinks.forEach(navLink => navLink.classList.remove('active'));
            this.classList.add('active');
            
            // Show the corresponding content page
            const targetPageId = this.getAttribute('data-page') + '-content';
            contentPages.forEach(page => {
                if (page.id === targetPageId) {
                    page.classList.add('active', 'fade-in');
                } else {
                    page.classList.remove('active', 'fade-in');
                }
            });

            // Load data for the active page
            loadDataForPage(this.getAttribute('data-page'));
        });
    });

    // Initialize the page
    initializeApp();
});

// Initialize the application
function initializeApp() {
    // Load vehicles data on initial page load
    loadVehicles();
    
    // Set up event listeners
    setupEventListeners();
    
    // Load makes for filter dropdown
    loadMakesForFilter();
}

// Set up all event listeners
function setupEventListeners() {
    // Vehicles page
    document.getElementById('search-btn').addEventListener('click', searchVehicles);
    document.getElementById('filter-make').addEventListener('change', filterVehicles);
    document.getElementById('filter-fuel-type').addEventListener('change', filterVehicles);
    document.getElementById('filter-transmission').addEventListener('change', filterVehicles);
    document.getElementById('filter-availability').addEventListener('change', filterVehicles);
    document.getElementById('add-vehicle-btn').addEventListener('click', showAddVehicleModal);
    
    // Orders page
    document.getElementById('filter-orders-btn').addEventListener('click', filterOrders);
    document.getElementById('create-order-btn').addEventListener('click', showCreateOrderModal);
    
    // Maintenance page
    document.getElementById('upcoming-maintenance-btn').addEventListener('click', loadUpcomingMaintenance);
    document.getElementById('add-maintenance-btn').addEventListener('click', showAddMaintenanceModal);
    
    // Users page
    document.getElementById('filter-users-btn').addEventListener('click', filterUsers);
    document.getElementById('add-user-btn').addEventListener('click', showAddUserModal);
}

// Load data based on active page
function loadDataForPage(page) {
    switch(page) {
        case 'vehicles':
            loadVehicles();
            break;
        case 'orders':
            loadOrders();
            break;
        case 'maintenance':
            loadMaintenance();
            break;
        case 'users':
            loadUsers();
            break;
    }
}

// --- VEHICLES FUNCTIONS ---

// Load all vehicles
function loadVehicles() {
    fetch('/api/vehicles')
        .then(response => response.json())
        .then(data => {
            displayVehicles(data);
        })
        .catch(error => {
            console.error('Error fetching vehicles:', error);
            alert('Failed to load vehicles. Please try again later.');
        });
}

// Display vehicles in the table
function displayVehicles(vehicles) {
    const tableBody = document.getElementById('vehicles-table-body');
    tableBody.innerHTML = '';
    
    if (vehicles.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="10" class="text-center">No vehicles found</td>';
        tableBody.appendChild(row);
        return;
    }
    
    vehicles.forEach(vehicle => {
        const row = document.createElement('tr');
        
        const statusBadgeClass = vehicle.available ? 'badge bg-success' : 'badge bg-danger';
        const statusText = vehicle.available ? 'Available' : 'Sold';
        
                row.innerHTML = `            <td>${vehicle.make} ${vehicle.model}</td>            <td>${vehicle.vehicleYear}</td>            <td>${vehicle.vin}</td>            <td>${vehicle.color}</td>            <td>$${vehicle.price.toLocaleString()}</td>            <td>${vehicle.mileage.toLocaleString()} km</td>            <td>${formatEnum(vehicle.fuelType)}</td>            <td>${formatEnum(vehicle.transmissionType)}</td>            <td><span class="${statusBadgeClass}">${statusText}</span></td>            <td>                <button class="btn btn-sm btn-primary action-btn" onclick="editVehicle(${vehicle.id})">Edit</button>                <button class="btn btn-sm btn-danger action-btn" onclick="deleteVehicle(${vehicle.id})">Delete</button>            </td>        `;
        
        tableBody.appendChild(row);
    });
}

// Search vehicles
function searchVehicles() {
    const searchTerm = document.getElementById('search-vehicle').value.trim();
    
    if (searchTerm === '') {
        loadVehicles();
        return;
    }
    
    fetch(`/api/vehicles/search?searchTerm=${encodeURIComponent(searchTerm)}`)
        .then(response => response.json())
        .then(data => {
            displayVehicles(data);
        })
        .catch(error => {
            console.error('Error searching vehicles:', error);
            alert('Failed to search vehicles. Please try again.');
        });
}

// Filter vehicles
function filterVehicles() {
    const make = document.getElementById('filter-make').value;
    const fuelType = document.getElementById('filter-fuel-type').value;
    const transmission = document.getElementById('filter-transmission').value;
    const availability = document.getElementById('filter-availability').value;
    
    let url = '/api/vehicles';
    
    if (make) {
        url = `/api/vehicles/make/${encodeURIComponent(make)}`;
    } else if (fuelType) {
        url = `/api/vehicles/fuel-type/${fuelType}`;
    } else if (transmission) {
        url = `/api/vehicles/transmission-type/${transmission}`;
    } else if (availability !== '') {
        url = `/api/vehicles/available`;
        if (availability === 'false') {
            // Custom handling for non-available vehicles
            fetch('/api/vehicles')
                .then(response => response.json())
                .then(data => {
                    const unavailableVehicles = data.filter(v => !v.available);
                    displayVehicles(unavailableVehicles);
                })
                .catch(error => {
                    console.error('Error filtering vehicles:', error);
                    alert('Failed to filter vehicles. Please try again.');
                });
            return;
        }
    }
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            displayVehicles(data);
        })
        .catch(error => {
            console.error('Error filtering vehicles:', error);
            alert('Failed to filter vehicles. Please try again.');
        });
}

// Load makes for filter dropdown
function loadMakesForFilter() {
    fetch('/api/vehicles')
        .then(response => response.json())
        .then(data => {
            const makeFilter = document.getElementById('filter-make');
            const uniqueMakes = [...new Set(data.map(vehicle => vehicle.make))];
            
            uniqueMakes.sort().forEach(make => {
                const option = document.createElement('option');
                option.value = make;
                option.textContent = make;
                makeFilter.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error loading makes:', error);
        });
}

// View vehicle details
function viewVehicleDetails(id) {
    // In a real application, show a modal with vehicle details
    alert(`View details of vehicle with ID: ${id}`);
}

// Edit vehicle
function editVehicle(id) {
    fetch(`/api/vehicles/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch vehicle details');
            }
            return response.json();
        })
        .then(vehicle => {
            // Populate the edit form with vehicle data
            document.getElementById('edit-vehicle-id').value = vehicle.id;
            document.getElementById('edit-make').value = vehicle.make;
            document.getElementById('edit-model').value = vehicle.model;
            document.getElementById('edit-year').value = vehicle.vehicleYear;
            document.getElementById('edit-vin').value = vehicle.vin;
            document.getElementById('edit-color').value = vehicle.color;
            document.getElementById('edit-price').value = vehicle.price;
            document.getElementById('edit-mileage').value = vehicle.mileage;
            document.getElementById('edit-fuelType').value = vehicle.fuelType;
            document.getElementById('edit-transmissionType').value = vehicle.transmissionType;
            document.getElementById('edit-available').value = vehicle.available.toString();
            document.getElementById('edit-engineSize').value = vehicle.engineSize;
            
            if (vehicle.acquisitionDate) {
                document.getElementById('edit-acquisitionDate').value = vehicle.acquisitionDate.split('T')[0];
            }
            
            document.getElementById('edit-description').value = vehicle.description || '';
            document.getElementById('edit-imageUrl').value = vehicle.imageUrl || '';
            
            // Show the edit modal
            const modal = new bootstrap.Modal(document.getElementById('editVehicleModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching vehicle details:', error);
            alert('Failed to load vehicle details. Please try again.');
        });
}

// Update Vehicle Event Listener
document.getElementById('updateVehicleBtn').addEventListener('click', function() {
    // Validate acquisition date against vehicle year
    const vehicleYear = parseInt(document.getElementById('edit-year').value);
    const acquisitionDate = new Date(document.getElementById('edit-acquisitionDate').value);
    const acquisitionYear = acquisitionDate.getFullYear();
    
    if (acquisitionYear < vehicleYear) {
        alert('Error: Acquisition date cannot be earlier than the vehicle manufacturing year.');
        return;
    }
    
    const vehicleId = document.getElementById('edit-vehicle-id').value;
    
    const vehicleData = {
        id: parseInt(vehicleId),
        make: document.getElementById('edit-make').value,
        model: document.getElementById('edit-model').value,
        vehicleYear: parseInt(document.getElementById('edit-year').value),
        vin: document.getElementById('edit-vin').value,
        color: document.getElementById('edit-color').value,
        price: document.getElementById('edit-price').value,
        mileage: parseInt(document.getElementById('edit-mileage').value),
        fuelType: document.getElementById('edit-fuelType').value,
        transmissionType: document.getElementById('edit-transmissionType').value,
        available: document.getElementById('edit-available').value === 'true',
        engineSize: document.getElementById('edit-engineSize').value,
        acquisitionDate: document.getElementById('edit-acquisitionDate').value,
        description: document.getElementById('edit-description').value,
        imageUrl: document.getElementById('edit-imageUrl').value
    };

    fetch(`/api/vehicles/${vehicleId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(vehicleData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to update vehicle');
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('editVehicleModal'));
        modal.hide();
        loadVehicles();
        alert('Vehicle updated successfully!');
    })
    .catch(error => {
        console.error('Error updating vehicle:', error);
        alert('Failed to update vehicle: ' + error.message);
    });
});

// Delete vehicle
function deleteVehicle(id) {
    if (confirm('Are you sure you want to delete this vehicle?')) {
        fetch(`/api/vehicles/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('Vehicle deleted successfully');
                loadVehicles();
            } else {
                throw new Error('Failed to delete vehicle');
            }
        })
        .catch(error => {
            console.error('Error deleting vehicle:', error);
            alert('Failed to delete vehicle. Please try again.');
        });
    }
}

// Show Add Vehicle Modal
function showAddVehicleModal() {
    const modal = new bootstrap.Modal(document.getElementById('addVehicleModal'));
    modal.show();
}

// --- ORDERS FUNCTIONS ---

// Load all orders
function loadOrders() {
    fetch('/api/orders')
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch orders');
            }
            return response.json();
        })
        .then(data => {
            // Ensure data is an array even if empty
            const orders = Array.isArray(data) ? data : [];
            displayOrders(orders);
        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            // Display empty table on error
            displayOrders([]);
        });
}

// Display orders in the table
function displayOrders(orders) {
    const tableBody = document.getElementById('orders-table-body');
    tableBody.innerHTML = '';
    
    if (!orders || orders.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="8" class="text-center">No orders found</td>';
        tableBody.appendChild(row);
        return;
    }
    
    orders.forEach(order => {
        const row = document.createElement('tr');
        
        let statusBadgeClass = 'badge bg-info';
        if (order.status === 'CONFIRMED') statusBadgeClass = 'badge bg-primary';
        if (order.status === 'DELIVERED') statusBadgeClass = 'badge bg-success';
        if (order.status === 'CANCELLED') statusBadgeClass = 'badge bg-danger';
        
        row.innerHTML = `
            <td>${order.orderNumber || '-'}</td>
            <td>${order.customerName || '-'}</td>
            <td>${formatDate(order.orderDate) || '-'}</td>
            <td><span class="${statusBadgeClass}">${formatEnum(order.status) || '-'}</span></td>
            <td>$${order.totalAmount ? order.totalAmount.toLocaleString() : '0.00'}</td>
            <td>${order.paymentMethod || '-'}</td>
            <td>${order.deliveryDate ? formatDate(order.deliveryDate) : '-'}</td>
            <td>
                <button class="btn btn-sm btn-info action-btn" onclick="viewOrderDetails(${order.id})">View</button>
                <button class="btn btn-sm btn-primary action-btn" onclick="editOrder(${order.id})">Edit</button>
                <button class="btn btn-sm btn-danger action-btn" onclick="deleteOrder(${order.id})">Delete</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
}

// Filter orders
function filterOrders() {
    const status = document.getElementById('filter-order-status').value;
    
    let url = '/api/orders';
    
    if (status) {
        url = `/api/orders/status/${status}`;
    }
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            displayOrders(data);
        })
        .catch(error => {
            console.error('Error filtering orders:', error);
            alert('Failed to filter orders. Please try again.');
        });
}

// Show Create Order Modal
function showCreateOrderModal() {
    // Load available vehicles and customers for the dropdown
    Promise.all([
        fetch('/api/vehicles').then(response => response.json()),
        fetch('/api/users').then(response => response.json())
    ])
    .then(([vehicles, users]) => {
        // Populate vehicle dropdown
        const vehicleSelect = document.getElementById('orderVehicle');
        vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';
        
        // Filter to only include available vehicles
        const availableVehicles = vehicles.filter(v => v.available === true);
        
        availableVehicles.forEach(vehicle => {
            const option = document.createElement('option');
            option.value = vehicle.id;
            option.textContent = `${vehicle.make} ${vehicle.model} (${vehicle.vehicleYear}) - ${vehicle.color}`;
            vehicleSelect.appendChild(option);
        });
        
        // Populate customer dropdown
        const customerSelect = document.getElementById('orderCustomer');
        customerSelect.innerHTML = '<option value="">Select Customer</option>';
        
        // Filter to only include users with CUSTOMER role
        const customers = users.filter(u => u.role === 'CUSTOMER');
        
        customers.forEach(customer => {
            const option = document.createElement('option');
            option.value = customer.id;
            option.textContent = customer.fullName || customer.username;
            customerSelect.appendChild(option);
        });
        
        // Set current date as default for order date
        const today = new Date().toISOString().split('T')[0];
        document.getElementById('orderDate').value = today;
        
        // Show the modal
        const modal = new bootstrap.Modal(document.getElementById('addOrderModal'));
        modal.show();
    })
    .catch(error => {
        console.error('Error loading data for order form:', error);
        alert('Failed to load vehicle and customer data. Please try again.');
    });
}

// View Order Details
function viewOrderDetails(id) {
    fetch(`/api/orders/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch order details');
            }
            return response.json();
        })
        .then(order => {
            console.log("Order details:", order);
            
            // Remove any existing modal
            const existingModal = document.getElementById('viewOrderModal');
            if (existingModal) {
                existingModal.remove();
            }
            
            // Create a modal dynamically to display order details
            let modalContent = `
                <div class="modal fade" id="viewOrderModal" tabindex="-1" aria-labelledby="viewOrderModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="viewOrderModalLabel">Order Details: ${order.orderNumber}</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <p><strong>Customer:</strong> ${order.customerName}</p>
                                        <p><strong>Order Date:</strong> ${formatDate(order.orderDate)}</p>
                                        <p><strong>Status:</strong> ${formatEnum(order.status)}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <p><strong>Payment Method:</strong> ${order.paymentMethod || 'N/A'}</p>
                                        <p><strong>Delivery Date:</strong> ${order.deliveryDate ? formatDate(order.deliveryDate) : 'N/A'}</p>
                                        <p><strong>Total Amount:</strong> $${order.totalAmount ? order.totalAmount.toLocaleString() : '0.00'}</p>
                                    </div>
                                </div>
                                
                                <h6 class="mt-4 mb-3">Vehicle Details</h6>
                                <div class="table-responsive">
                                    <table class="table table-bordered">
                                        <thead>
                                            <tr>
                                                <th>Vehicle</th>
                                                <th>Quantity</th>
                                                <th>Unit Price</th>
                                                <th>Subtotal</th>
                                                <th>Payment Status</th>
                                            </tr>
                                        </thead>
                                        <tbody>`;
            
            // Add order items to the table                    
            if (order.orderItems && order.orderItems.length > 0) {
                order.orderItems.forEach(item => {
                    modalContent += `
                        <tr>
                            <td>${item.vehicleDetails || 'N/A'}</td>
                            <td>${item.quantity}</td>
                            <td>$${item.unitPrice ? item.unitPrice.toLocaleString() : '0.00'}</td>
                            <td>$${item.subtotal ? item.subtotal.toLocaleString() : '0.00'}</td>
                            <td>${item.isPaid ? '<span class="badge bg-success">Paid</span>' : '<span class="badge bg-warning">Pending</span>'}</td>
                        </tr>`;
                });
            } else {
                modalContent += `<tr><td colspan="5" class="text-center">No items found</td></tr>`;
            }
            
            modalContent += `
                                        </tbody>
                                    </table>
                                </div>
                                
                                <div class="mt-3">
                                    <p><strong>Notes:</strong> ${order.notes || 'N/A'}</p>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Insert modal HTML into the DOM
            document.body.insertAdjacentHTML('beforeend', modalContent);
            
            // Wait for the DOM to be ready before initializing the Bootstrap modal
            setTimeout(() => {
                const modalElement = document.getElementById('viewOrderModal');
                const modal = new bootstrap.Modal(modalElement);
                modal.show();
                
                // Clean up modal when it's closed
                modalElement.addEventListener('hidden.bs.modal', function () {
                    modalElement.remove();
                });
            }, 100);
        })
        .catch(error => {
            console.error('Error fetching order details:', error);
            alert('Failed to load order details: ' + error.message);
        });
}

// Edit Order function
function editOrder(id) {
    // First fetch the order to know which vehicle is currently assigned
    fetch(`/api/orders/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch order details');
            }
            return response.json();
        })
        .then(order => {
            console.log("Fetched order for editing:", order); // Debug log
            
            // Then load all vehicles and customers
            return Promise.all([
                fetch('/api/vehicles').then(response => response.json()),
                fetch('/api/users').then(response => response.json().then(users => users.filter(u => u.role === 'CUSTOMER'))),
                order // Pass the order along
            ]);
        })
        .then(([vehicles, customers, order]) => {
            // Populate vehicle dropdown
            const vehicleSelect = document.getElementById('edit-orderVehicle');
            vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';
            
            // Include currently assigned vehicle and available vehicles
            vehicles.forEach(vehicle => {
                // Include if available OR if it's the vehicle currently assigned to this order
                if (vehicle.available === true || (order.vehicleId && vehicle.id === order.vehicleId)) {
                    const option = document.createElement('option');
                    option.value = vehicle.id;
                    option.textContent = `${vehicle.make} ${vehicle.model} (${vehicle.vehicleYear}) - ${vehicle.color}`;
                    if (order.vehicleId && vehicle.id === order.vehicleId) {
                        option.selected = true;
                    }
                    vehicleSelect.appendChild(option);
                }
            });
            
            // Populate customer dropdown
            const customerSelect = document.getElementById('edit-orderCustomer');
            customerSelect.innerHTML = '<option value="">Select Customer</option>';
            
            customers.forEach(user => {
                const option = document.createElement('option');
                option.value = user.id;
                option.textContent = `${user.firstName} ${user.lastName}` || user.username;
                if (user.id === order.userId) {
                    option.selected = true;
                }
                customerSelect.appendChild(option);
            });
            
            // Populate the form with order data
            document.getElementById('edit-order-id').value = order.id;
            
            if (order.orderDate) {
                document.getElementById('edit-orderDate').value = formatDateForInput(order.orderDate);
            }
            
            if (order.deliveryDate) {
                document.getElementById('edit-deliveryDate').value = formatDateForInput(order.deliveryDate);
            }
            
            if (order.paymentMethod) {
                document.getElementById('edit-paymentMethod').value = order.paymentMethod;
            }
            
            if (order.status) {
                document.getElementById('edit-orderStatus').value = order.status;
            }
            
            // Show edit modal
            const modal = new bootstrap.Modal(document.getElementById('editOrderModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching order details:', error);
            alert('Failed to load order details: ' + error.message);
        });
}

// Helper function to format date for input fields
function formatDateForInput(dateString) {
    if (!dateString) return '';
    // Handle both ISO strings and date objects
    const date = typeof dateString === 'string' ? new Date(dateString) : dateString;
    return date.toISOString().split('T')[0];
}

// Update Order button click handler
document.getElementById('updateOrderBtn').addEventListener('click', function() {
    const id = document.getElementById('edit-order-id').value;
    const vehicleId = document.getElementById('edit-orderVehicle').value;
    const customerId = document.getElementById('edit-orderCustomer').value;
    const orderDate = document.getElementById('edit-orderDate').value;
    const deliveryDate = document.getElementById('edit-deliveryDate').value;
    
    // Validate delivery date against order date
    if (deliveryDate && orderDate && deliveryDate < orderDate) {
        alert('Error: Delivery date cannot be earlier than the order date.');
        return;
    }
    
    // Validate required fields
    if (!id || !customerId || !orderDate) {
        alert('Please fill all required fields');
        return;
    }
    
    // Format dates properly for backend (ISO format with time)
    const formattedOrderDate = new Date(orderDate + 'T00:00:00').toISOString();
    let formattedDeliveryDate = null;
    if (deliveryDate) {
        formattedDeliveryDate = new Date(deliveryDate + 'T00:00:00').toISOString();
    }
    
    const orderData = {
        userId: parseInt(customerId), // Use userId instead of customerId
        vehicleId: vehicleId ? parseInt(vehicleId) : null,
        orderDate: formattedOrderDate,
        deliveryDate: formattedDeliveryDate,
        paymentMethod: document.getElementById('edit-paymentMethod').value,
        status: document.getElementById('edit-orderStatus').value
    };

    console.log('Updating order with data:', orderData); // Debug log

    fetch(`/api/orders/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(orderData)
    })
    .then(response => {
        console.log('Update response status:', response.status);
        
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Server error response:', text); // Log full error
                try {
                    // Try to parse as JSON to get detailed error
                    const errorJson = JSON.parse(text);
                    throw new Error(errorJson.message || errorJson.error || text);
                } catch (e) {
                    throw new Error(text || 'Failed to update order');
                }
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('editOrderModal'));
        modal.hide();
        loadOrders();
        alert('Order updated successfully!');
    })
    .catch(error => {
        console.error('Error updating order:', error);
        alert('Failed to update order: ' + error.message);
    });
});

// Delete Order
function deleteOrder(id) {
    if (confirm('Are you sure you want to delete this order?')) {
        fetch(`/api/orders/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('Order deleted successfully');
                loadOrders();
            } else {
                throw new Error('Failed to delete order');
            }
        })
        .catch(error => {
            console.error('Error deleting order:', error);
            alert('Failed to delete order. Please try again.');
        });
    }
}

// --- MAINTENANCE FUNCTIONS ---

// Load all maintenance records
function loadMaintenance() {
    fetch('/api/maintenance')
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch maintenance records: ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            // Ensure data is always an array, even if backend returns an error object
            const maintenanceRecords = Array.isArray(data) ? data : [];
            displayMaintenance(maintenanceRecords);
        })
        .catch(error => {
            console.error('Error fetching maintenance records:', error);
            // Display empty table on error
            displayMaintenance([]);
            alert('Failed to load maintenance records. Please try again later.');
        });
}

// Display maintenance records in the table
function displayMaintenance(records) {
    const tableBody = document.getElementById('maintenance-table-body');
    tableBody.innerHTML = '';
    
    // Ensure records is an array
    if (!Array.isArray(records) || records.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="8" class="text-center">No maintenance records found</td>';
        tableBody.appendChild(row);
        return;
    }
    
    records.forEach(record => {
        const row = document.createElement('tr');
        
        let statusBadgeClass = 'badge bg-info';
        if (record.status === 'SCHEDULED') statusBadgeClass = 'badge bg-warning';
        if (record.status === 'IN_PROGRESS') statusBadgeClass = 'badge bg-primary';
        if (record.status === 'COMPLETED') statusBadgeClass = 'badge bg-success';
        if (record.status === 'CANCELLED') statusBadgeClass = 'badge bg-danger';
        
        row.innerHTML = `
            <td>${record.vehicleDetails || 'Unknown Vehicle'}</td>
            <td>${record.maintenanceType || '-'}</td>
            <td>${formatDate(record.serviceDate) || '-'}</td>
            <td>${record.nextServiceDate ? formatDate(record.nextServiceDate) : '-'}</td>
            <td>$${record.cost ? record.cost.toLocaleString() : '0.00'}</td>
            <td>${record.serviceProvider || '-'}</td>
            <td><span class="${statusBadgeClass}">${formatEnum(record.status) || '-'}</span></td>
            <td>
                <button class="btn btn-sm btn-info action-btn" onclick="viewMaintenanceDetails(${record.id})">View</button>
                <button class="btn btn-sm btn-primary action-btn" onclick="editMaintenance(${record.id})">Edit</button>
                <button class="btn btn-sm btn-danger action-btn" onclick="deleteMaintenance(${record.id})">Delete</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
}

// Load upcoming maintenance
function loadUpcomingMaintenance() {
    const today = new Date().toISOString().split('T')[0]; // Format: YYYY-MM-DD
    
    fetch(`/api/maintenance/upcoming?date=${today}`)
        .then(response => response.json())
        .then(data => {
            displayMaintenance(data);
        })
        .catch(error => {
            console.error('Error fetching upcoming maintenance:', error);
            alert('Failed to load upcoming maintenance. Please try again.');
        });
}

// Show Add Maintenance Modal
function showAddMaintenanceModal() {
    // First fetch orders with DELIVERED status
    fetch('/api/orders/status/DELIVERED')
        .then(response => response.json())
        .then(deliveredOrders => {
            if (!deliveredOrders || deliveredOrders.length === 0) {
                alert('No delivered orders found. Please deliver orders before scheduling maintenance.');
                return;
            }
            
            // Extract unique vehicle IDs from delivered orders
            const vehicleIds = [...new Set(deliveredOrders
                .filter(order => order.vehicleId)
                .map(order => order.vehicleId))];
            
            if (vehicleIds.length === 0) {
                alert('No vehicles found in delivered orders.');
                return;
            }
            
            // Now fetch the full vehicle details for these IDs
            fetch('/api/vehicles')
                .then(response => response.json())
                .then(allVehicles => {
                    // Filter to only include vehicles from delivered orders
                    const deliveredVehicles = allVehicles.filter(vehicle => 
                        vehicleIds.includes(vehicle.id)
                    );
                    
                    // Populate the select dropdown
                    const vehicleSelect = document.getElementById('maintenanceVehicle');
                    vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';
                    
                    deliveredVehicles.forEach(vehicle => {
                        const option = document.createElement('option');
                        option.value = vehicle.id;
                        option.textContent = `${vehicle.make} ${vehicle.model} (${vehicle.vehicleYear}) - ${vehicle.color}`;
                        vehicleSelect.appendChild(option);
                    });
                    
                    // Set current date as default for service date
                    const today = new Date().toISOString().split('T')[0];
                    document.getElementById('serviceDate').value = today;
                    
                    // Show the modal
                    const modal = new bootstrap.Modal(document.getElementById('addMaintenanceModal'));
                    modal.show();
                })
                .catch(error => {
                    console.error('Error fetching vehicles:', error);
                    alert('Failed to load vehicles. Please try again.');
                });
        })
        .catch(error => {
            console.error('Error fetching delivered orders:', error);
            alert('Failed to load delivered orders. Please try again.');
        });
}

// View Maintenance Details
function viewMaintenanceDetails(id) {
    fetch(`/api/maintenance/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch maintenance details');
            }
            return response.json();
        })
        .then(maintenance => {
            console.log("Maintenance details:", maintenance);
            
            // Remove any existing modal
            const existingModal = document.getElementById('viewMaintenanceModal');
            if (existingModal) {
                existingModal.remove();
            }
            
            // Create a modal dynamically to display maintenance details
            let modalContent = `
                <div class="modal fade" id="viewMaintenanceModal" tabindex="-1" aria-labelledby="viewMaintenanceModalLabel" aria-hidden="true">
                    <div class="modal-dialog modal-lg">
                        <div class="modal-content">
                            <div class="modal-header">
                                <h5 class="modal-title" id="viewMaintenanceModalLabel">Maintenance Details</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <div class="row mb-3">
                                    <div class="col-md-6">
                                        <p><strong>Vehicle:</strong> ${maintenance.vehicleDetails || 'Unknown Vehicle'}</p>
                                        <p><strong>Maintenance Type:</strong> ${maintenance.maintenanceType || '-'}</p>
                                        <p><strong>Service Date:</strong> ${formatDate(maintenance.serviceDate) || '-'}</p>
                                        <p><strong>Next Service Date:</strong> ${maintenance.nextServiceDate ? formatDate(maintenance.nextServiceDate) : 'Not scheduled'}</p>
                                    </div>
                                    <div class="col-md-6">
                                        <p><strong>Cost:</strong> $${maintenance.cost ? maintenance.cost.toLocaleString() : '0.00'}</p>
                                        <p><strong>Service Provider:</strong> ${maintenance.serviceProvider || 'Not specified'}</p>
                                        <p><strong>Status:</strong> ${formatEnum(maintenance.status) || '-'}</p>
                                        ${maintenance.mileageAtService ? `<p><strong>Mileage at Service:</strong> ${maintenance.mileageAtService.toLocaleString()} km</p>` : ''}
                                    </div>
                                </div>
                                
                                <div class="mt-4">
                                    <h6 class="mb-3">Description/Notes</h6>
                                    <div class="p-3 bg-light rounded">
                                        <p class="mb-0">${maintenance.description || 'No description provided.'}</p>
                                    </div>
                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            `;
            
            // Insert modal HTML into the DOM
            document.body.insertAdjacentHTML('beforeend', modalContent);
            
            // Wait for the DOM to be ready before initializing the Bootstrap modal
            setTimeout(() => {
                const modalElement = document.getElementById('viewMaintenanceModal');
                const modal = new bootstrap.Modal(modalElement);
                modal.show();
                
                // Clean up modal when it's closed
                modalElement.addEventListener('hidden.bs.modal', function () {
                    modalElement.remove();
                });
            }, 100);
        })
        .catch(error => {
            console.error('Error fetching maintenance details:', error);
            alert('Failed to load maintenance details: ' + error.message);
        });
}

// Edit Maintenance function
function editMaintenance(id) {
    // First get the maintenance record to preserve the current vehicle selection
    fetch(`/api/maintenance/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch maintenance details');
            }
            return response.json();
        })
        .then(maintenance => {
            // Now fetch orders with DELIVERED status to get eligible vehicles
            return Promise.all([
                fetch('/api/orders/status/DELIVERED').then(response => response.json()),
                fetch('/api/vehicles').then(response => response.json()),
                maintenance // Pass maintenance along
            ]);
        })
        .then(([deliveredOrders, allVehicles, maintenance]) => {
            // Extract unique vehicle IDs from delivered orders
            const vehicleIds = [...new Set(deliveredOrders
                .filter(order => order.vehicleId)
                .map(order => order.vehicleId))];
                
            // Also include the vehicle currently assigned to this maintenance record
            if (maintenance.vehicleId && !vehicleIds.includes(maintenance.vehicleId)) {
                vehicleIds.push(maintenance.vehicleId);
            }
            
            // Filter vehicles to only include those from delivered orders + current maintenance vehicle
            const eligibleVehicles = allVehicles.filter(vehicle => 
                vehicleIds.includes(vehicle.id)
            );
            
            // Populate the select dropdown
            const vehicleSelect = document.getElementById('edit-maintenanceVehicle');
            vehicleSelect.innerHTML = '<option value="">Select Vehicle</option>';
            
            eligibleVehicles.forEach(vehicle => {
                const option = document.createElement('option');
                option.value = vehicle.id;
                option.textContent = `${vehicle.make} ${vehicle.model} (${vehicle.vehicleYear}) - ${vehicle.color}`;
                if (vehicle.id === maintenance.vehicleId) {
                    option.selected = true;
                }
                vehicleSelect.appendChild(option);
            });
            
            // Populate the form with maintenance data
            document.getElementById('edit-maintenance-id').value = maintenance.id;
            document.getElementById('edit-maintenanceType').value = maintenance.maintenanceType;
            
            if (maintenance.serviceDate) {
                document.getElementById('edit-serviceDate').value = maintenance.serviceDate.split('T')[0];
            }
            
            if (maintenance.nextServiceDate) {
                document.getElementById('edit-nextServiceDate').value = maintenance.nextServiceDate.split('T')[0];
            }
            
            document.getElementById('edit-serviceCost').value = maintenance.cost;
            document.getElementById('edit-serviceProvider').value = maintenance.serviceProvider || '';
            document.getElementById('edit-maintenanceDescription').value = maintenance.description || '';
            document.getElementById('edit-maintenanceStatus').value = maintenance.status;
            
            // Show the edit modal
            const modal = new bootstrap.Modal(document.getElementById('editMaintenanceModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching maintenance details:', error);
            alert('Failed to load maintenance details. Please try again.');
        });
}

// Update Maintenance Event Listener
document.getElementById('updateMaintenanceBtn').addEventListener('click', function() {
    const maintenanceId = document.getElementById('edit-maintenance-id').value;
    const serviceDate = document.getElementById('edit-serviceDate').value;
    const nextServiceDate = document.getElementById('edit-nextServiceDate').value;
    
    // Validate next service date against service date
    if (nextServiceDate && serviceDate && nextServiceDate < serviceDate) {
        alert('Error: Next service date cannot be earlier than the service date.');
        return;
    }
    
    const maintenanceData = {
        id: parseInt(maintenanceId),
        vehicleId: document.getElementById('edit-maintenanceVehicle').value,
        maintenanceType: document.getElementById('edit-maintenanceType').value,
        serviceDate: serviceDate,
        nextServiceDate: nextServiceDate,
        cost: parseFloat(document.getElementById('edit-serviceCost').value),
        serviceProvider: document.getElementById('edit-serviceProvider').value,
        description: document.getElementById('edit-maintenanceDescription').value,
        status: document.getElementById('edit-maintenanceStatus').value
    };

    fetch(`/api/maintenance/${maintenanceId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(maintenanceData)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Failed to update maintenance record');
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('editMaintenanceModal'));
        modal.hide();
        loadMaintenance();
        alert('Maintenance record updated successfully!');
    })
    .catch(error => {
        console.error('Error updating maintenance record:', error);
        alert('Failed to update maintenance record: ' + error.message);
    });
});

// Delete Maintenance
function deleteMaintenance(id) {
    if (confirm('Are you sure you want to delete this maintenance record?')) {
        fetch(`/api/maintenance/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('Maintenance record deleted successfully');
                loadMaintenance();
            } else {
                throw new Error('Failed to delete maintenance record');
            }
        })
        .catch(error => {
            console.error('Error deleting maintenance record:', error);
            alert('Failed to delete maintenance record. Please try again.');
        });
    }
}

// --- USERS FUNCTIONS ---

// Load all users
function loadUsers() {
    fetch('/api/users')
        .then(response => response.json())
        .then(data => {
            displayUsers(data);
        })
        .catch(error => {
            console.error('Error fetching users:', error);
            alert('Failed to load users. Please try again later.');
        });
}

// Display users in the table
function displayUsers(users) {
    const tableBody = document.getElementById('users-table-body');
    tableBody.innerHTML = '';
    
    if (users.length === 0) {
        const row = document.createElement('tr');
        row.innerHTML = '<td colspan="6" class="text-center">No users found</td>';
        tableBody.appendChild(row);
        return;
    }
    
    users.forEach(user => {
        const row = document.createElement('tr');
        
        let roleBadgeClass = 'badge bg-secondary';
        if (user.role === 'ADMIN') roleBadgeClass = 'badge bg-danger';
        if (user.role === 'MANAGER') roleBadgeClass = 'badge bg-warning';
        if (user.role === 'SALESPERSON') roleBadgeClass = 'badge bg-info';
        if (user.role === 'CUSTOMER') roleBadgeClass = 'badge bg-primary';
        
        row.innerHTML = `
            <td>${user.username}</td>
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>${user.phone || '-'}</td>
            <td><span class="${roleBadgeClass}">${formatEnum(user.role)}</span></td>
            <td>
                <button class="btn btn-sm btn-primary action-btn" onclick="editUser(${user.id})">Edit</button>
                <button class="btn btn-sm btn-danger action-btn" onclick="deleteUser(${user.id})">Delete</button>
            </td>
        `;
        
        tableBody.appendChild(row);
    });
}

// Filter users
function filterUsers() {
    const role = document.getElementById('filter-user-role').value;
    
    let url = '/api/users';
    
    if (role) {
        url = `/api/users/role/${role}`;
    }
    
    fetch(url)
        .then(response => response.json())
        .then(data => {
            displayUsers(data);
        })
        .catch(error => {
            console.error('Error filtering users:', error);
            alert('Failed to filter users. Please try again.');
        });
}

// Show Add User Modal
function showAddUserModal() {
    const modal = new bootstrap.Modal(document.getElementById('addUserModal'));
    modal.show();
}

// Save Vehicle
document.getElementById('saveVehicleBtn').addEventListener('click', function() {
    // Validate acquisition date against vehicle year
    const vehicleYear = parseInt(document.getElementById('year').value);
    const acquisitionDate = new Date(document.getElementById('acquisitionDate').value);
    const acquisitionYear = acquisitionDate.getFullYear();
    
    if (acquisitionYear < vehicleYear) {
        alert('Error: Acquisition date cannot be earlier than the vehicle manufacturing year.');
        return;
    }
    
    const vehicleData = {
        make: document.getElementById('make').value,
        model: document.getElementById('model').value,
        vehicleYear: vehicleYear,
        vin: document.getElementById('vin').value,
        color: document.getElementById('color').value,
        price: document.getElementById('price').value,
        mileage: parseInt(document.getElementById('mileage').value),
        fuelType: document.getElementById('fuelType').value,
        transmissionType: document.getElementById('transmissionType').value,
        available: document.getElementById('available').value === 'true',
        engineSize: document.getElementById('engineSize').value,
        acquisitionDate: document.getElementById('acquisitionDate').value,
        description: document.getElementById('description').value,
        imageUrl: document.getElementById('imageUrl').value
    };

    console.log('Sending vehicle data:', vehicleData); // Debug log

    fetch('/api/vehicles', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(vehicleData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to add vehicle');
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('addVehicleModal'));
        modal.hide();
        loadVehicles();
        alert('Vehicle added successfully!');
    })
    .catch(error => {
        console.error('Error adding vehicle:', error);
        alert('Failed to add vehicle: ' + error.message);
    });
});

// Save Order
document.getElementById('saveOrderBtn').addEventListener('click', function() {
    const vehicleId = document.getElementById('orderVehicle').value;
    const customerId = document.getElementById('orderCustomer').value;
    const orderDate = document.getElementById('orderDate').value;
    const deliveryDate = document.getElementById('deliveryDate').value;
    
    // Validate delivery date against order date
    if (deliveryDate && orderDate && deliveryDate < orderDate) {
        alert('Error: Delivery date cannot be earlier than the order date.');
        return;
    }
    
    // Validate required fields
    if (!vehicleId || !customerId || !orderDate) {
        alert('Please fill all required fields');
        return;
    }
    
    // Format dates properly for backend (ISO format with time)
    const formattedOrderDate = new Date(orderDate + 'T00:00:00').toISOString();
    let formattedDeliveryDate = null;
    if (deliveryDate) {
        formattedDeliveryDate = new Date(deliveryDate + 'T00:00:00').toISOString();
    }
    
    // Create order data with proper structure
    const orderData = {
        userId: parseInt(customerId), // Convert to number
        vehicleId: parseInt(vehicleId), // Convert to number
        orderDate: formattedOrderDate,
        deliveryDate: formattedDeliveryDate,
        paymentMethod: document.getElementById('paymentMethod').value,
        status: document.getElementById('orderStatus').value,
        // Add order items expected by the backend
        orderItems: [
            {
                vehicleId: parseInt(vehicleId), // Convert to number
                quantity: 1,
                isPaid: false
            }
        ]
    };

    console.log('Sending order data:', orderData); // Debug log

    fetch('/api/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(orderData)
    })
    .then(response => {
        console.log('Order response status:', response.status);
        
        if (!response.ok) {
            return response.text().then(text => {
                console.error('Server error response:', text); // Log full error
                try {
                    // Try to parse as JSON to get detailed error
                    const errorJson = JSON.parse(text);
                    throw new Error(errorJson.message || errorJson.error || text);
                } catch (e) {
                    throw new Error(text || 'Failed to create order');
                }
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('addOrderModal'));
        modal.hide();
        loadOrders();
        alert('Order created successfully!');
    })
    .catch(error => {
        console.error('Error creating order:', error);
        alert('Failed to create order: ' + error.message);
    });
});

// Save Maintenance Record
document.getElementById('saveMaintenanceBtn').addEventListener('click', function() {
    const serviceDate = document.getElementById('serviceDate').value;
    const nextServiceDate = document.getElementById('nextServiceDate').value;
    
    // Validate next service date against service date
    if (nextServiceDate && serviceDate && nextServiceDate < serviceDate) {
        alert('Error: Next service date cannot be earlier than the service date.');
        return;
    }
    
    const maintenanceData = {
        vehicleId: document.getElementById('maintenanceVehicle').value,
        maintenanceType: document.getElementById('maintenanceType').value,
        serviceDate: serviceDate,
        nextServiceDate: nextServiceDate,
        cost: parseFloat(document.getElementById('serviceCost').value),
        serviceProvider: document.getElementById('serviceProvider').value,
        description: document.getElementById('maintenanceDescription').value,
        status: document.getElementById('maintenanceStatus').value
    };

    fetch('/api/maintenance', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(maintenanceData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                try {
                    // Try to parse as JSON to get detailed error
                    const errorJson = JSON.parse(text);
                    throw new Error(errorJson.message || errorJson.error || text);
                } catch (e) {
                    throw new Error(text || 'Failed to add maintenance record');
                }
            });
        }
        const modal = bootstrap.Modal.getInstance(document.getElementById('addMaintenanceModal'));
        modal.hide();
        loadMaintenance();
        alert('Maintenance record added successfully!');
        return response.json();
    })
    .catch(error => {
        console.error('Error adding maintenance record:', error);
        alert('Failed to add maintenance record: ' + error.message);
    });
});

// Save User
document.getElementById('saveUserBtn').addEventListener('click', function() {
    // Parse full name into first and last name
    const fullNameInput = document.getElementById('fullName').value.trim();
    let firstName = fullNameInput;
    let lastName = "";
    
    // Split on the first space to get first and last name
    if (fullNameInput.includes(' ')) {
        const spaceIndex = fullNameInput.indexOf(' ');
        firstName = fullNameInput.substring(0, spaceIndex);
        lastName = fullNameInput.substring(spaceIndex + 1);
    }
    
    const userData = {
        username: document.getElementById('username').value,
        firstName: firstName,
        lastName: lastName,
        email: document.getElementById('email').value,
        phone: document.getElementById('phone').value,
        role: document.getElementById('userRole').value
    };

    console.log('Sending user data:', userData); // Debug log

    fetch('/api/users', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to add user');
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('addUserModal'));
        modal.hide();
        loadUsers();
        alert('User added successfully!');
    })
    .catch(error => {
        console.error('Error adding user:', error);
        alert('Failed to add user: ' + error.message);
    });
});

// Edit User function
function editUser(id) {
    fetch(`/api/users/${id}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Failed to fetch user details');
            }
            return response.json();
        })
        .then(user => {
            // Populate the edit form with user data
            document.getElementById('edit-user-id').value = user.id;
            document.getElementById('edit-username').value = user.username;
            
            // Combine first and last name for fullName field
            const fullName = (user.firstName || '') + (user.lastName ? ' ' + user.lastName : '');
            document.getElementById('edit-fullName').value = fullName;
            
            document.getElementById('edit-email').value = user.email;
            document.getElementById('edit-phone').value = user.phone || '';
            document.getElementById('edit-userRole').value = user.role;
            
            // Show the edit modal
            const modal = new bootstrap.Modal(document.getElementById('editUserModal'));
            modal.show();
        })
        .catch(error => {
            console.error('Error fetching user details:', error);
            alert('Failed to load user details. Please try again.');
        });
}

// Update User Event Listener
document.getElementById('updateUserBtn').addEventListener('click', function() {
    const userId = document.getElementById('edit-user-id').value;
    
    // Parse full name into first and last name
    const fullNameInput = document.getElementById('edit-fullName').value.trim();
    let firstName = fullNameInput;
    let lastName = "";
    
    // Split on the first space to get first and last name
    if (fullNameInput.includes(' ')) {
        const spaceIndex = fullNameInput.indexOf(' ');
        firstName = fullNameInput.substring(0, spaceIndex);
        lastName = fullNameInput.substring(spaceIndex + 1);
    }
    
    const userData = {
        id: parseInt(userId),
        username: document.getElementById('edit-username').value,
        firstName: firstName,
        lastName: lastName,
        email: document.getElementById('edit-email').value,
        phone: document.getElementById('edit-phone').value,
        role: document.getElementById('edit-userRole').value
    };

    console.log('Updating user data:', userData); // Debug log

    fetch(`/api/users/${userId}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(text => {
                throw new Error(text || 'Failed to update user');
            });
        }
        return response.json();
    })
    .then(data => {
        const modal = bootstrap.Modal.getInstance(document.getElementById('editUserModal'));
        modal.hide();
        loadUsers();
        alert('User updated successfully!');
    })
    .catch(error => {
        console.error('Error updating user:', error);
        alert('Failed to update user: ' + error.message);
    });
});

// Delete user
function deleteUser(id) {
    if (confirm('Are you sure you want to delete this user?')) {
        fetch(`/api/users/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (response.ok) {
                alert('User deleted successfully');
                loadUsers();
            } else {
                throw new Error('Failed to delete user');
            }
        })
        .catch(error => {
            console.error('Error deleting user:', error);
            alert('Failed to delete user. Please try again.');
        });
    }
}

// --- UTILITY FUNCTIONS ---

// Format date
function formatDate(dateString) {
    if (!dateString) return '-';
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
}

// Format enum values for display
function formatEnum(enumValue) {
    if (!enumValue) return '-';
    
    // Convert SNAKE_CASE to Title Case
    return enumValue
        .toLowerCase()
        .split('_')
        .map(word => word.charAt(0).toUpperCase() + word.slice(1))
        .join(' ');
} 