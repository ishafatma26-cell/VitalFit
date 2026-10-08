-- VitalFit Database Schema (PostgreSQL / Neon compatible)

DROP TABLE IF EXISTS activity_log CASCADE;
DROP TABLE IF EXISTS challenge_participants CASCADE;
DROP TABLE IF EXISTS challenges CASCADE;
DROP TABLE IF EXISTS goals CASCADE;
DROP TABLE IF EXISTS workouts CASCADE;
DROP TABLE IF EXISTS users CASCADE;

-- 1. Users Table
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 2. Workouts Table
CREATE TABLE workouts (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    activity_type VARCHAR(50) NOT NULL,
    intensity VARCHAR(20) NOT NULL DEFAULT 'Medium',
    duration_minutes INT NOT NULL CHECK (duration_minutes > 0),
    calories_burned INT NOT NULL CHECK (calories_burned >= 0),
    distance_km NUMERIC(5,2) DEFAULT 0.0,
    workout_date DATE NOT NULL DEFAULT CURRENT_DATE,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 3. Goals Table
CREATE TABLE goals (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(100) NOT NULL,
    target_value NUMERIC(10,2) NOT NULL,
    current_value NUMERIC(10,2) DEFAULT 0.0,
    unit VARCHAR(20) NOT NULL,
    status VARCHAR(20) DEFAULT 'IN_PROGRESS',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Challenges Table
CREATE TABLE challenges (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    target_type VARCHAR(50) NOT NULL,
    target_goal NUMERIC(10,2) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Challenge Participants Table
CREATE TABLE challenge_participants (
    id SERIAL PRIMARY KEY,
    challenge_id INT NOT NULL REFERENCES challenges(id) ON DELETE CASCADE,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    joined_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_challenge UNIQUE (challenge_id, user_id)
);

-- 6. Activity Log Table (Asynchronous Multithreaded Logging)
CREATE TABLE activity_log (
    id SERIAL PRIMARY KEY,
    user_email VARCHAR(150),
    action VARCHAR(100) NOT NULL,
    details TEXT,
    thread_name VARCHAR(100),
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance optimization
CREATE INDEX idx_workouts_user_id ON workouts(user_id);
CREATE INDEX idx_workouts_date ON workouts(workout_date);
CREATE INDEX idx_goals_user_id ON goals(user_id);
CREATE INDEX idx_activity_log_timestamp ON activity_log(timestamp);

-- Seed Data (Valid 2026 dates)
INSERT INTO users (name, email, password_hash, role) VALUES
('Demo Athlete', 'user@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER'),
('Admin User', 'admin@vitalfit.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'ADMIN'),
('Aarav Sharma', 'aarav@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER'),
('Neha Patel', 'neha@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER');

INSERT INTO workouts (user_id, activity_type, intensity, duration_minutes, calories_burned, distance_km, workout_date, notes) VALUES
(1, 'Running', 'High', 45, 520, 8.50, DATE '2026-03-01', 'Morning outdoor trail run'),
(1, 'Cycling', 'Medium', 60, 640, 22.50, DATE '2026-03-02', 'Speed intervals'),
(2, 'HIIT', 'High', 45, 480, 0.00, DATE '2026-03-01', 'Full body circuit'),
(3, 'Cycling', 'High', 90, 950, 35.00, DATE '2026-03-01', 'Long distance endurance ride'),
(3, 'Running', 'High', 60, 720, 11.20, DATE '2026-03-02', 'Tempo tempo pace run'),
(4, 'Swimming', 'Medium', 50, 580, 2.80, DATE '2026-03-01', 'Freestyle endurance laps'),
(4, 'Yoga', 'Low', 40, 210, 0.00, DATE '2026-03-02', 'Recovery mobility stretch');

INSERT INTO goals (user_id, title, target_value, current_value, unit, status) VALUES
(1, 'Burn 5000 Calories', 5000.00, 1160.00, 'kcal', 'IN_PROGRESS'),
(1, 'Run 50km This Month', 50.00, 8.50, 'km', 'IN_PROGRESS'),
(3, 'Weekly Cycling Distance', 100.00, 35.00, 'km', 'IN_PROGRESS');

INSERT INTO challenges (title, description, target_type, target_goal, start_date, end_date) VALUES
('30-Day Cardio Shred', 'Complete 10,000 active calories through high intensity cardio and running.', 'Calories', 10000.00, DATE '2026-03-01', DATE '2026-03-31'),
('Century Ride 100km', 'Accumulate 100km of outdoor or indoor cycling distance over 14 days.', 'Distance', 100.00, DATE '2026-03-01', DATE '2026-03-15'),
('500-Set Strength Matrix', 'Complete 500 total sets across strength and resistance training sessions.', 'Sets', 500.00, DATE '2026-03-05', DATE '2026-04-05'),
('Core Calorie Burn', 'Burn 5,000 kcal through core, HIIT, and functional fitness workouts.', 'Calories', 5000.00, DATE '2026-03-01', DATE '2026-03-20');

INSERT INTO challenge_participants (challenge_id, user_id, status) VALUES
(1, 1, 'ACTIVE'),
(2, 3, 'ACTIVE'),
(4, 1, 'ACTIVE');
