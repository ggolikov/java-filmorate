package ru.yandex.practicum.filmorate.storage.mappers;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
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
        Array genreIdsSqlArray = rs.getArray("genre_ids");
        Array genreNamessSqlArray = rs.getArray("genre_names");
        if (genreIdsSqlArray != null && genreNamessSqlArray != null) {
            Object[] idsArray = (Object[]) genreIdsSqlArray.getArray();
            Object[] namesArray = (Object[]) genreNamessSqlArray.getArray();

            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < idsArray.length; i++) {
                if (idsArray[i] instanceof Integer && namesArray[i] instanceof String) {
                    int id = (Integer) idsArray[i];
                    String name = namesArray[i].toString();

                    if (!(genres.stream().map(Genre::getId).toList().contains(id))) {
                        Genre genre = new Genre();

                        genre.setId(id);
                        genre.setName(name);
                        genres.add(genre);
                    }
                }
            }
            film.setGenres(genres);
        }

        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa_id"));
        mpa.setName(rs.getString("mpa_name"));
        film.setMpa(mpa);

        return film;
    }
}