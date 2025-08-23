CREATE TABLE IF NOT EXISTS preferences
(
    player_id        TEXT NOT NULL,
    preference_key   TEXT NOT NULL,
    preference_value TEXT NULL
);