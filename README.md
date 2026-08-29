# 🏢 ReserveSpace - Enterprise Resource Booking Platform (Poonam Memane)

A commercial-grade, real-time RESTful platform and interactive portal for **Corporate Resource & Meeting Space Booking** engineered by **Poonam Memane** with **Java 17+**, **Spring Boot 3**, **Spring Security 6**, **Stateless JWT**, and **MySQL with JPA / Hibernate**.

All resource rates and booking calculations are fully configured and formatted in **Indian Rupees (₹ INR)**.

---

## 🌟 Key Application Features

- **🇮🇳 Full Indian Rupee (₹ INR) Integration**: Accurate decimal pricing and calculation across all meeting spaces, studio kits, and fleet transport (e.g. ₹2,500/hr, ₹4,000/hr, ₹1,200/hr, ₹3,500/hr).
- **⚡ Interactive Real-Time Price Estimator**: Live calculator on the landing page allowing clients to pick a space and duration to preview exact reservation budgets in ₹.
- **🎯 Dynamic Category Filtering**: Instant category pills (All, Meeting Rooms, AV Production, Fleet Transit) with real-time browser filtering.
- **🛡️ Zero Double-Booking Collision Prevention**: Intelligent overlap locking ensuring resources cannot be booked concurrently.
- **🔐 Enterprise Role-Based Access Control (RBAC)**:
  - **Administrator (Poonam Memane)**: Full CRUD on spaces, system-wide schedule control, and status approval (`CONFIRMED`, `PENDING`, `CANCELLED`).
  - **Member**: Self-service space discovery, automatic JWT identity binding, and isolated personal booking management.
- **📖 OpenAPI / Swagger 3 Explorer**: Complete API testing suite at `/swagger-ui.html`.

---

## 👥 Default Accounts (Poonam Memane)

| Role | Username | Password | Full Name | Permissions |
|---|---|---|---|---|
| **👑 Administrator** | **`poonam`** | **`poonam123`** | **Poonam Memane** | Full CRUD on catalog & all reservations |
| **👤 Member** | **`user`** | **`user123`** | **Poonam Memane** | Self-service booking & personal schedule |

---

## 🗄️ Database & Currency Configuration

```properties
spring.application.name=ResourceBookingSystem
server.port=8080

# MySQL Database (Asia/Kolkata Timezone)
spring.datasource.url=jdbc:mysql://localhost:3306/bookingsystem?useSSL=false&serverTimezone=Asia/Kolkata
spring.datasource.username=root
spring.datasource.password=poonam
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 🚀 Quick Launch

1. **Access Web Platform**:
   👉 [**http://localhost:8080**](http://localhost:8080)
2. **Access Sign In Portal**:
   👉 [**http://localhost:8080/pages/login.html**](http://localhost:8080/pages/login.html)
3. **Swagger API Explorer**:
   👉 [**http://localhost:8080/swagger-ui.html**](http://localhost:8080/swagger-ui.html)
4. **Run Test Suite**:
   ```bash
   .\mvnw.cmd test
   ```
