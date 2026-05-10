-- New stations for the Bucharest → Iași connection
INSERT INTO stations (name, city) VALUES
('Buzau',    'Buzau'),
('Focsani',  'Focsani'),
('Bacau',    'Bacau'),
('Roman',    'Roman'),
('Iasi',     'Iasi');

-- Route 4: Bucharest → Bacău (leg 1)
INSERT INTO routes (name) VALUES ('Bucharest - Bacau');

-- Route 5: Bacău → Iași (leg 2)
INSERT INTO routes (name) VALUES ('Bacau - Iasi');

-- Route stops: Bucharest - Bacău (route 4)
-- station ids: Bucharest=1, Ploiesti=5, Buzau=9, Focsani=10, Bacau=11
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(4, 1,  1, '08:00:00'),
(4, 5,  2, '08:55:00'),
(4, 9,  3, '10:05:00'),
(4, 10, 4, '11:10:00'),
(4, 11, 5, '12:30:00');

-- Route stops: Bacău - Iași (route 5)
-- station ids: Bacau=11, Roman=12, Iasi=13
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(5, 11, 1, '13:30:00'),
(5, 12, 2, '14:15:00'),
(5, 13, 3, '15:00:00');

-- Trains
INSERT INTO trains (name, capacity, route_id) VALUES
('IR 1651 Moldova',         180, 4),
('Accelerat 1923 Moldovita', 150, 5);

-- Schedules
INSERT INTO schedules (train_id) VALUES (4), (5);

-- Both trains run daily
INSERT INTO schedule_days (schedule_id, days_of_week) VALUES
(4, 'MONDAY'), (4, 'TUESDAY'), (4, 'WEDNESDAY'), (4, 'THURSDAY'), (4, 'FRIDAY'), (4, 'SATURDAY'), (4, 'SUNDAY'),
(5, 'MONDAY'), (5, 'TUESDAY'), (5, 'WEDNESDAY'), (5, 'THURSDAY'), (5, 'FRIDAY'), (5, 'SATURDAY'), (5, 'SUNDAY');
