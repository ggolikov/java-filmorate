package ru.yandex.practicum.filmorate.storage.mappers;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaResultExtractor implements ResultSetExtractor<List<Mpa>> {
    @Override
    public List<Mpa> extractData(ResultSet rs) throws SQLException {
        List<Mpa> mpas = new ArrayList<>();
        while(rs.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getInt("id"));
            mpa.setName(rs.getString("name"));
            mpas.add(mpa);
        }
        return mpas;
    }
}
