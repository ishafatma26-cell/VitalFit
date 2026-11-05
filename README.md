# VitalFit – Intelligent Online Fitness Tracking Platform

**Target Repository:** Online Fitness Tracking Application (VitalFit)
**Team Name:** Team NOVA
**Production URL:** `https://vitalfit-s9su.onrender.com`
**Demo User:** `user@vitalfit.demo` | **Password:** `User@123`
**Demo Admin:** `admin@vitalfit.com` | **Password:** `Admin@123`

---

## 👥 Team NOVA – Core Contributors & Academic Attribution

* **Isha Fatma (Admission No: 25SCSE1180892)**
  * *Role & Ownership:* System Architecture, Service Layer Engineering, Session Security (`UserPrincipal`), Role-Based Filters (`AuthFilter`), and Cryptographic CSRF Defense (`CsrfFilter`).
* **ADITYA KUMAR SINGH (Admission No: 25SCSE1180275)**
  * *Role & Ownership:* Database Architecture, HikariCP Connection Pool Integration, ACID `TransactionManager` Helper, and Generic Data Access Object Layer (`GenericDao<T, ID>`).
* **Mukesh kumar (Admission No: 25SCSE1330101)**
  * *Role & Ownership:* Servlet Controller Implementations, Strategy Pattern for Calorie Estimation (`WorkoutCalculator`), Chart.js Visual Analytics, BMR/TDEE & CSV Innovations, and Automated JUnit 5 / Mockito Test Suite.

---

## 🛠️ Architecture & Core Design Patterns

* **Model-View-Controller (MVC):** Decoupled Jakarta EE Servlets (6.0), JSTL 3.0 JSPs, and robust domain models.
* **Strategy Design Pattern:** Calorie burn estimation abstracted behind `WorkoutCalculator` with dynamic resolution via `WorkoutCalculatorFactory` across `CardioWorkoutCalculator`, `StrengthWorkoutCalculator`, `YogaWorkoutCalculator`, and `HIITWorkoutCalculator`.
* **Generic DAO Pattern:** `GenericDao<T, ID>` contract implemented across `UserDAO`, `WorkoutDAO`, and `ChallengeDAO` with `Optional<T>` return semantics.
* **ACID Transaction Management:** `TransactionManager` executing multi-step mutations with atomic `connection.setAutoCommit(false)`, `commit()`, and rollback on `SQLException`.
* **High-Performance Connection Pooling:** HikariCP 5.1.0 connection pool with leak detection and resource cleanup.
* **Enterprise Web Security:**
  * Role-Based Access Control via `AuthFilter` guarding `/dashboard`, `/workouts`, `/challenges`, `/leaderboard`, `/profile`, `/user/*`, and `/admin/*`.
  * Cross-Site Request Forgery (CSRF) protection filter enforcing per-session cryptographic random tokens on state-modifying requests (`POST`, `PUT`, `DELETE`).
  * Cross-Site Scripting (XSS) entity escaping via `XssSanitizer`.
  * Salted password hashing via BCrypt.

---

## 📊 Topic 8 Syllabus & Innovation Features

1. **User Dashboard (`/dashboard`, `/user/dashboard`):**
   * **Workout Log Management:** Tabular history with modal edit and delete operations.
   * **Visual Analytics (Chart.js):** Interactive 7-day calorie burn trend line chart and workout type distribution doughnut chart.
   * **Target Goals & Streaks:** Real-time progress bars and streak tracker computed via Java Stream API.
   * **Innovation 1 – Smart BMR & TDEE Calculator Modal:** Computes Basal Metabolic Rate (Mifflin-St Jeor), daily maintenance calories, and hydration targets.
   * **Innovation 2 – Workout CSV Data Export:** One-click download of all workout telemetry to a standard `.csv` file.
2. **Admin Dashboard (`/admin`, `/admin/dashboard`):**
   * **Athlete Role Toggle:** Instant role elevation and demotion between User and Admin.
   * **Challenge Moderation:** Management of community challenges.
   * **Multithreaded System Audit Log:** Live activity log executed asynchronously via a dedicated fixed worker thread pool (`Executors.newFixedThreadPool(4)`).
   * **Platform Settings Configuration:** System-wide baseline targets for steps and hydration.

---

## 🧪 Testing & Verification

The test suite leverages JUnit 5 (`junit-jupiter-api`, `junit-jupiter-params`, `junit-jupiter-engine`), Mockito (`mockito-core`, `mockito-junit-jupiter`), and an embedded H2 in-memory PostgreSQL mode database.

To run the complete automated test suite:
```bash
mvn clean test
```

To build the production deployment WAR artifact:
```bash
mvn clean package
```

### Test Suite Summary:
* `CalorieCalculatorStrategyTest`: Validates calculations for all 4 workout strategies, MET scaling, EPOC multipliers, boundaries (zero/negative durations), and factory dynamic dispatch.
* `TransactionManagerTest`: Asserts atomic commit behavior and rollback safety upon SQL failures.
* `UserAuthServiceTest`: Verifies BCrypt password hashing, authentication success/rejection, and `UserPrincipal` creation.
* `CsrfFilterTest`: Mocks Servlet requests to ensure GET passthrough, 403 Forbidden rejection for missing/tampered tokens, and proper token matching.
* `WorkoutDaoTest` & `ChallengeDaoTest`: Verifies full `GenericDao` CRUD contracts against the database.
* `VitalFitPlatformTest`: End-to-end integration tests without artificial thread sleep delays.
