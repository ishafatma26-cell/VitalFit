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

-- Seed Data
INSERT INTO users (name, email, password_hash, role) VALUES
('Demo User', 'user@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER'),
('Admin User', 'admin@vitalfit.com', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'ADMIN'),
('Alex Rivera', 'alex@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER'),
('Elena Rostova', 'elena@vitalfit.demo', '$2a$10$8.UnVuG9HHgffUDAlk8qfOUVGkq8zg3E69k3Tq1s.v.pYy1z2uSsm', 'USER');

INSERT INTO workouts (user_id, activity_type, duration_minutes, calories_burned, distance_km, workout_date, notes) VALUES
(1, 'Running', 45, 480, 7.50, CURRENT_DATE - 1, 'Morning trail run'),
(1, 'Cycling', 60, 620, 22.00, CURRENT_DATE, 'Interval speed cycling'),
(3, 'HIIT Training', 30, 350, 0.00, CURRENT_DATE - 2, 'Full body circuit'),
(3, 'Running', 50, 550, 8.20, CURRENT_DATE, 'Pace run'),
(4, 'Swimming', 60, 700, 2.50, CURRENT_DATE - 1, 'Freestyle endurance');

INSERT INTO goals (user_id, title, target_value, current_value, unit, status) VALUES
(1, 'Burn 5000 Calories', 5000.00, 1100.00, 'kcal', 'IN_PROGRESS'),
(1, 'Run 50km This Month', 50.00, 7.50, 'km', 'IN_PROGRESS'),
(3, 'Weekly Workout Count', 5.00, 2.00, 'workouts', 'IN_PROGRESS');

INSERT INTO challenges (title, description, target_type, target_goal, start_date, end_date) VALUES
('100K Steps Challenge', 'Reach 100,000 total steps over 14 days to boost cardiovascular health.', 'Steps', 100000.00, CURRENT_DATE - 3, CURRENT_DATE + 11),
('Summer Calorie Burner', 'Burn 10,000 kcal through high intensity cardio and strength training.', 'Calories', 10000.00, CURRENT_DATE, CURRENT_DATE + 30),
('Marathon Endurance Drive', 'Complete a total cumulative distance of 42.2km.', 'Distance', 42.20, CURRENT_DATE - 5, CURRENT_DATE + 10);

INSERT INTO challenge_participants (challenge_id, user_id, status) VALUES
(1, 1, 'ACTIVE'),
(2, 3, 'ACTIVE');
