# VitalFit – Intelligent Health & Fitness Tracking Web Application

VitalFit is a modern, responsive, full-stack Java web application built with Jakarta EE (Servlets + JSP + JSTL), plain JDBC, and PostgreSQL. Users can log workouts, monitor vital health metrics, track calorie balance, join community challenges, and view dynamic leaderboards. Administrators have dedicated tools to manage users, evaluate fitness metrics, moderate platform content, and review real-time multithreaded system activity logs.

---

## 🚀 Live Application & Demo

* **Live URL:** `https://vitalfit.onrender.com`
* **Demo User:** `user@vitalfit.demo` | **Password:** `User@123`
* **Demo Admin:** `admin@vitalfit.com` | **Password:** `Admin@123`

---

## 🛠️ Tech Stack & Architecture

* **Architecture:** Model-View-Controller (MVC) Pattern
* **Language & Runtime:** Java 17, Apache Tomcat 10.1 (Docker multi-stage build)
* **Web Technologies:** Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0 (`jakarta.tags.core`)
* **Database:** PostgreSQL (Neon Cloud Database) with Connection Lifecycle Optimization
* **Data Access:** Plain JDBC with `PreparedStatement` (DAO Pattern)
* **Security:** BCrypt salted password hashing, Role-Based Session Filters
* **UI/UX:** Dark Glassmorphism, Responsive Mobile-First CSS Grid/Flexbox
* **Build Tool:** Apache Maven
* **Deployment & Containerization:** Docker Multi-Stage Build

---

## 📋 Academic Rubric Alignment (Review 1)

### 1. Core Java Concepts & OOP (10 Marks)
* **Encapsulation:** JavaBean domain models (`User`, `VitalMetric`, `Workout`, `Challenge`, `FitnessContent`) with strictly private fields and validated getters/setters.
* **Abstraction & Interfaces:** Clean Data Access Object (DAO) architecture (`VitalMetricDao`, `WorkoutDao`, `ChallengeDao`, `ActivityLogDao`, `UserDao`) decoupled from SQL implementations.
* **Polymorphism:** Dynamic method dispatch via interface bindings across Servlet controller layers.
* **Inheritance:** Hierarchical service and domain structure ensuring code reusability and modularity.

### 2. Advanced Collections & Multithreading (10 Marks)
* **Collections Framework:** In-memory leaderboard sorting and ranking using `ArrayList`, `HashMap`, and `TreeSet`.
* **Multithreading:** Asynchronous background worker threads for non-blocking user activity logging and metric computation.

### 3. Database Connectivity & Reliability (10 Marks)
* **Relational Schema:** Normalized 3NF PostgreSQL schema with foreign keys, cascade rules, and optimized index constraints.
* **SQL Injection Prevention:** 100% parameter-binding utilizing `PreparedStatement`.
* **Cloud Database:** High-availability serverless PostgreSQL database hosted on Neon Cloud.

### 4. Web Tier, MVC Design & Enterprise Security (10 Marks)
* **Controller Layer:** Specialized Jakarta Servlets handling application routing and request lifecycles.
* **View Layer:** Modular JSP components utilizing standard JSTL core tags without legacy scriptlets.
* **Session Management:** Secure HTTP session validation with role-based authentication filters.

---

## 📁 Repository Structure

```text
VitalFit/
├── src/main/java/com/vitalfit/
│   ├── controller/      # Jakarta Servlets (Auth, Vitals, Workout, Admin)
│   ├── dao/             # Data Access Object Interfaces & Implementations
│   ├── model/           # JavaBean Entities (User, Workout, Vitals)
│   └── util/            # DB Connection, BCrypt Encryption, Session Filters
├── src/main/webapp/
│   ├── WEB-INF/         # web.xml & secure JSP view components
│   ├── css/             # Glassmorphic responsive UI styles
│   └── js/              # Dynamic client-side validations
├── Dockerfile           # Multi-stage production container build
├── pom.xml              # Maven dependencies & build configuration
└── schema.sql           # Database schema & demo seed data
