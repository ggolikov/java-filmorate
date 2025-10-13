package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class})
public class FilmDbStorageTests {
    private final FilmDbStorage filmStorage;
    Mpa mpa;
    Film film;

    @BeforeEach
    public void beforeEach() {
        mpa = new Mpa();
        mpa.setId(1);
        film = new Film();
        film.setId(1);
        film.setName("user");
        film.setDuration(500);
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2000, 10, 10));
        film.setMpa(mpa);
    }

    @Test
    public void testGetFilm() {
        filmStorage.addFilm(film);
        Optional<Film> filmOptional = filmStorage.getFilm(1);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1)
                );

        filmStorage.removeFilm(film.getId());
    }
}
