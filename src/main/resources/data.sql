INSERT INTO users (email, password_hash, role, name) VALUES 
('student@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5.7baOnv.LpKiOt0G201u4WvG.97U.', 'STUDENT', 'Test Student'),
('admin@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5.7baOnv.LpKiOt0G201u4WvG.97U.', 'ADMIN', 'Test Admin'),
('driver@test.com', '$2a$10$8.UnVuG9HHgffUDAlk8q7Ou5.7baOnv.LpKiOt0G201u4WvG.97U.', 'DRIVER', 'Test Driver');

INSERT INTO buses (bus_number, capacity) VALUES 
('MH04-AB1234', 50),
('MH04-AB1235', 60);

INSERT INTO stops (name, latitude, longitude) VALUES 
('BHEL Gate', 23.2599, 77.4126),
('College Gate', 23.2700, 77.4300);
