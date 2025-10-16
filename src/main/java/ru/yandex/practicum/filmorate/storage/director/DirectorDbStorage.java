package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.BaseStorage;
import ru.yandex.practicum.filmorate.storage.mappers.DirectorRowMapper;

import java.util.Collection;
import java.util.Optional;

@Repository
@Qualifier("directorDbStorage")
public class DirectorDbStorage extends BaseStorage<Director> implements DirectorStorage {
    private static final String GET_DIRECTOR_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String GET_ALL_DIRECTORS_QUERY = "SELECT * FROM directors";
    private static final String INSERT_DIRECTOR_QUERY = "INSERT INTO directors(name) VALUES(?)";
    private static final String UPDATE_DIRECTOR_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_DIRECTOR_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorDbStorage(JdbcTemplate jdbc) {
        super(jdbc, new DirectorRowMapper());
    }

    public Optional<Director> getDirector(int id) {
        return findOne(GET_DIRECTOR_QUERY, id);
    }

    public Collection<Director> getDirectors() {
        return findMany(GET_ALL_DIRECTORS_QUERY);
    }

    public Director addDirector(Director director) {
        int id = insert(INSERT_DIRECTOR_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        if (getDirector(director.getId()).isEmpty()) {
            throw new NotFoundException("Режиссёр не найден с ID: " + director.getId());
        }

        update(UPDATE_DIRECTOR_QUERY, director.getName(), director.getId());
        return director;
    }

    public void removeDirector(int id) {
        delete(DELETE_DIRECTOR_QUERY, id);
    }
}
