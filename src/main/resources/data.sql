INSERT INTO genres(name)
VALUES
    ('Комедия'),
    ('Драма'),
    ('Мультфильм'),
    ('Триллер'),
    ('Документальный'),
    ('Боевик');

INSERT INTO mpas(name)
VALUES
    ('G'),
    ('PG'),
    ('PG-13'),
    ('R'),
    ('NC-17');
--
-- INSERT INTO films (name, description, release_date, duration, mpa_id)
--     VALUES
--         ('Джентельмены удачи', '', '1970-01-01', 1500, 1),
--         ('Кавказская пленница', '', '1970-01-01', 1500, 2, 2),
--         ('Ирония судьбы', '', '1970-01-01', 1500, 3, 3),
--         ('Белое солнце пустыни', '', '1970-01-01', 1500, 6, 4);
--
-- INSERT INTO users(login, email, name, birthday)
--     VALUES
--         ('ivan', 'ivan@yandex.ru', 'Иван', '1987-02-05'),
--         ('petr', 'petr@yandex.ru', 'Петр', '1981-12-20'),
--         ('sidor', 'sidor@yandex.ru', 'Сидор', '1990-01-07'),
--         ('ilya', 'ilya@yandex.ru', 'Илья', '1992-11-08'),
--         ('oleg', 'oleg@yandex.ru', 'Олег', '1992-11-08');
-- --
-- INSERT INTO likes(user_id, film_id)
--     VALUES
--         (1, 1),
--         (2, 1),
--         (3, 2),
--         (4, 1),
--         (5, 1),
--         (5, 3),
--         (2, 3),
--         (4, 2),
--         (3, 1),
--         (3, 3);
--
-- INSERT INTO friendship(following_user_id, followed_user_id, status)
--     VALUES
--         (1, 2, 'CONFIRMED'),
--         (1, 3, 'CONFIRMED'),
--         (1, 4, 'CONFIRMED'),
--         (2, 3, 'CONFIRMED'),
--         (3, 4, 'CONFIRMED'),
--         (4, 2, 'CONFIRMED'),
--         (5, 2, 'CONFIRMED');