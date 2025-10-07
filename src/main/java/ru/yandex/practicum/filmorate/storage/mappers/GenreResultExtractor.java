package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenreResultExtractor implements ResultSetExtractor<List<Genre>> {
    @Override
    public List<Genre> extractData(ResultSet rs) throws SQLException {
        List<Genre> genres = new ArrayList<>();
        while(rs.next()) {
            Genre genre = new Genre();
            genre.setId(rs.getInt("id"));
            genre.setName(rs.getString("name"));
            genres.add(genre);
        }
        return genres;
    }
}
