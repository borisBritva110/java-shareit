-- Test data for integration tests
-- Reset sequence to avoid primary key conflicts
ALTER TABLE users ALTER COLUMN id RESTART WITH 4;

INSERT INTO users (id, name, email) VALUES
(1, 'Test User 1', 'testuser1@example.com'),
(2, 'Test User 2', 'testuser2@example.com'),
(3, 'Test User 3', 'testuser3@example.com');

INSERT INTO items (id, name, description, available, owner_id, request_id) VALUES
(1, 'Test Item 1', 'Description 1', true, 1, NULL),
(2, 'Test Item 2', 'Description 2', true, 2, NULL),
(3, 'Test Item 3', 'Description 3', false, 1, NULL);

-- Reset items sequence
ALTER TABLE items ALTER COLUMN id RESTART WITH 4;

INSERT INTO bookings (id, start_date, end_date, item_id, booker_id, status) VALUES
(1, '2024-01-01 10:00:00', '2024-01-02 10:00:00', 1, 2, 'APPROVED'),
(2, '2024-01-03 10:00:00', '2024-01-04 10:00:00', 1, 3, 'WAITING');

-- Reset bookings sequence
ALTER TABLE bookings ALTER COLUMN id RESTART WITH 3;

INSERT INTO comments (id, text, item_id, author_id, created) VALUES
(1, 'Great item!', 1, 2, '2024-01-01 12:00:00');

-- Reset comments sequence
ALTER TABLE comments ALTER COLUMN id RESTART WITH 2;

INSERT INTO requests (id, description, requestor_id, created) VALUES
(1, 'Need a drill', 2, '2024-01-01 09:00:00');

-- Reset requests sequence
ALTER TABLE requests ALTER COLUMN id RESTART WITH 2;
