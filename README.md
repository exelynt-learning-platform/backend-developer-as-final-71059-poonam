# 🏢 ReserveSpace - Enterprise Resource Booking Platform

A commercial-grade, real-time RESTful platform and interactive portal for **Corporate Resource & Meeting Space Booking**, engineered with **Java 17+**, **Spring Boot 3**, **Spring Security 6**, **Stateless JWT Authentication**, and **MySQL with JPA / Hibernate**.

ReserveSpace allows organizations and members to discover, manage, and reserve corporate resources such as meeting rooms, AV production equipment, and fleet transportation.

All resource rates and booking calculations are configured and displayed in **Indian Rupees (₹ INR)**.

---

## 🌟 Key Application Features

### 🇮🇳 Indian Rupee (₹ INR) Integration

Accurate decimal pricing and booking calculations are supported across meeting spaces, studio kits, and fleet transportation.

Example rates include:

- ₹2,500/hour
- ₹4,000/hour
- ₹1,200/hour
- ₹3,500/hour

### ⚡ Interactive Real-Time Price Estimator

The landing page provides an interactive price calculator where users can:

- Select a resource
- Select booking duration
- Calculate the estimated reservation cost
- View the amount in Indian Rupees (₹)

### 🎯 Dynamic Category Filtering

Users can instantly filter available resources by category:

- All
- Meeting Rooms
- AV Production
- Fleet Transit

### 🛡️ Double-Booking Collision Prevention

The application validates booking time ranges and prevents overlapping reservations for the same resource.

This ensures that a resource cannot be reserved by multiple users for overlapping time periods.

### 🔐 Enterprise Role-Based Access Control

The application provides role-based access using Spring Security and JWT authentication.

#### 👑 Administrator

Administrators can:

- Create resources
- View resources
- Update resources
- Delete resources
- View system-wide reservations
- Manage booking status
- Control the resource catalog

Supported booking statuses include:

- `CONFIRMED`
- `PENDING`
- `CANCELLED`

#### 👤 Member

Members can:

- Discover available resources
- View resource details
- Create bookings
- View their own reservations
- Manage their personal bookings
- Access protected APIs using JWT authentication

### 📖 OpenAPI / Swagger

The project provides Swagger/OpenAPI documentation for API exploration and testing.

Swagger UI:

```text
http://localhost:8080/swagger-ui.html