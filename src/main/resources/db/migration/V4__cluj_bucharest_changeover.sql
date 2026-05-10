-- Rename existing stations to correct real names
UPDATE stations SET name = 'Brasov Ghimbav'    WHERE id = 3;
UPDATE stations SET name = 'Universitatea Cluj' WHERE id = 4;

-- Leg 1: Universitatea Cluj → Brasov Ghimbav
INSERT INTO routes (name) VALUES ('Universitatea Cluj - Brasov Ghimbav');

-- Leg 2: Brasov Ghimbav → Gara de Nord
INSERT INTO routes (name) VALUES ('Brasov Ghimbav - Gara de Nord');

-- Route stops leg 1 (route 6): Cluj → Brasov
-- station 4 = Universitatea Cluj, station 3 = Brasov Ghimbav
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(6, 4, 1, '07:00:00'),
(6, 3, 2, '09:30:00');

-- Route stops leg 2 (route 7): Brasov → Bucharest
-- station 3 = Brasov Ghimbav, station 1 = Gara de Nord
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(7, 3, 1, '10:30:00'),
(7, 1, 2, '13:00:00');

-- Trains
INSERT INTO trains (name, capacity, route_id) VALUES
('IR 1841 Somesul',  180, 6),
('IR 1842 Prahova',  180, 7);

-- Schedules
INSERT INTO schedules (train_id) VALUES (6), (7);

-- Both trains run daily
INSERT INTO schedule_days (schedule_id, days_of_week) VALUES
(6, 'MONDAY'), (6, 'TUESDAY'), (6, 'WEDNESDAY'), (6, 'THURSDAY'), (6, 'FRIDAY'), (6, 'SATURDAY'), (6, 'SUNDAY'),
(7, 'MONDAY'), (7, 'TUESDAY'), (7, 'WEDNESDAY'), (7, 'THURSDAY'), (7, 'FRIDAY'), (7, 'SATURDAY'), (7, 'SUNDAY');
