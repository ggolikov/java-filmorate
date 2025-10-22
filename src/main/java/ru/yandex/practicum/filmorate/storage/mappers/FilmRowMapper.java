package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setLikes(rs.getInt("likes_count"));

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        Array genreIdsArray = rs.getArray("genre_ids");
        Array genreNamesArray = rs.getArray("genre_names");
        List<Genre> genres = new ArrayList<>();

        if (genreIdsArray != null && genreNamesArray != null) {
            Object[] genreIds = (Object[]) genreIdsArray.getArray();
            Object[] genreNames = (Object[]) genreNamesArray.getArray();

            for (int i = 0; i < genreIds.length; i++) {
                if (genreIds[i] != null && genreNames[i] != null) {
                    Genre genre = new Genre();
                    genre.setId(((Number) genreIds[i]).intValue());
                    genre.setName(genreNames[i].toString());
                    genres.add(genre);
                }
            }
        }
        film.setGenres(genres);

        Array directorIdsArray = rs.getArray("director_ids");
        Array directorNamesArray = rs.getArray("director_names");
        List<Director> directors = new ArrayList<>();

        if (directorIdsArray != null && directorNamesArray != null) {
            Object[] directorIds = (Object[]) directorIdsArray.getArray();
            Object[] directorNames = (Object[]) directorNamesArray.getArray();

            for (int i = 0; i < directorIds.length; i++) {
                if (directorIds[i] != null && directorNames[i] != null) {
                    Director director = new Director();
                    director.setId(((Number) directorIds[i]).intValue());
                    director.setName(directorNames[i].toString());
                    directors.add(director);
                }
            }
        }
        film.setDirectors(directors);

        return film;
    }
}