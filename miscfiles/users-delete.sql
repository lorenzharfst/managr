-- This clears all tables related to users in Spring Security, mainly used for testing/development.
ALTER TABLE users DISABLE TRIGGER ALL;
ALTER TABLE authorities DISABLE TRIGGER ALL;
DELETE FROM users;
DELETE FROM authorities;
ALTER TABLE users ENABLE TRIGGER ALL;
ALTER TABLE authorities ENABLE TRIGGER ALL;
