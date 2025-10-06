package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RatingResultExtractor implements ResultSetExtractor<List<Rating>> {
    @Override
    public List<Rating> extractData(ResultSet rs) throws SQLException {
        List<Rating> ratings = new ArrayList<>();
        while(rs.next()) {
            Rating rating = new Rating();
            rating.setId(rs.getInt("id"));
            rating.setName(rs.getString("name"));
            ratings.add(rating);
        }
        return ratings;
    }
}
