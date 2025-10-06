package ru.yandex.practicum.filmorate.storage.genre;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.GenreResultExtractor;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;

@Repository
@Qualifier("genreDbStorage")
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String GET_GENRE_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO genres(name)" +
            "VALUES (?)";

    private final JdbcTemplate jdbc;

    public Genre addGenre(@Valid Genre genre) {
        List<String> genreNames = getAllGenres().stream().map(Genre::getName).toList();

        if (genreNames.contains(genre.getName())) {
            return genre;
        }

        int id = insert(
                INSERT_GENRE_QUERY,
                genre.getName()
        );
        genre.setId(id);
        return genre;
    }

    public Genre getGenre(int id) {
//        List<Integer> users = getUsers().stream().mapToInt(User::getId).boxed().toList();
//
//        if (!users.contains(user.getId())) {
//            throw new NotFoundException("Пользователь с id" + user.getId()  + "не найден");
//        }
        return jdbc.queryForObject(GET_GENRE_QUERY, Genre.class, id);
    }

    public Collection<Genre> getAllGenres() {
        return jdbc.query(GET_ALL_GENRES_QUERY, new GenreResultExtractor());
    }

    private int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        try {
            int id = keyHolder.getKeyAs(Integer.class);

            return id;
        } catch (NullPointerException e) {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }
}
