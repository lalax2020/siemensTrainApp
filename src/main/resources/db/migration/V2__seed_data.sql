-- Stations
INSERT INTO stations (name, city) VALUES
('Gara de Nord',        'Bucharest'),
('Sinaia',              'Sinaia'),
('Brasov',              'Brasov'),
('Cluj-Napoca',         'Cluj-Napoca'),
('Ploiesti Sud',        'Ploiesti'),
('Predeal',             'Predeal'),
('Campina',             'Campina'),
('Timisoara Nord',      'Timisoara');

-- Routes
INSERT INTO routes (name) VALUES
('Bucharest - Brasov'),
('Bucharest - Cluj-Napoca'),
('Brasov - Timisoara');

-- Route stops: Bucharest - Brasov (route 1)
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(1, 1, 1, '06:00:00'),
(1, 7, 2, '06:45:00'),
(1, 5, 3, '07:10:00'),
(1, 2, 4, '07:50:00'),
(1, 6, 5, '08:10:00'),
(1, 3, 6, '08:40:00');

-- Route stops: Bucharest - Cluj (route 2)
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(2, 1, 1, '07:00:00'),
(2, 5, 2, '07:55:00'),
(2, 3, 3, '09:20:00'),
(2, 4, 4, '12:30:00');

-- Route stops: Brasov - Timisoara (route 3)
INSERT INTO route_stops (route_id, station_id, stop_order, expected_time) VALUES
(3, 3, 1, '09:00:00'),
(3, 4, 2, '12:00:00'),
(3, 8, 3, '15:30:00');

-- Trains
INSERT INTO trains (name, capacity, route_id) VALUES
('IR 1581 Muntenia',  120, 1),
('IC 513 Transilvania', 200, 2),
('IR 1831 Apuseni',  150, 3);

-- Schedules
INSERT INTO schedules (train_id) VALUES (1), (2), (3);

-- Schedule days
INSERT INTO schedule_days (schedule_id, days_of_week) VALUES
(1, 'MONDAY'), (1, 'WEDNESDAY'), (1, 'FRIDAY'), (1, 'SUNDAY'),
(2, 'TUESDAY'), (2, 'THURSDAY'), (2, 'SATURDAY'),
(3, 'MONDAY'), (3, 'TUESDAY'), (3, 'WEDNESDAY'), (3, 'THURSDAY'), (3, 'FRIDAY'), (3, 'SATURDAY'), (3, 'SUNDAY');
