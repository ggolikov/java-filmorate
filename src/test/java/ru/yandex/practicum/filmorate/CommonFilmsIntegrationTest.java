package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureTestDatabase
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommonFilmsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmDbStorage filmStorage;

    @Autowired
    private UserDbStorage userStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user1;
    private User user2;
    private Film film1;
    private Film film2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@yandex.ru");
        user1.setLogin("user1");
        user1.setBirthday(LocalDate.of(2000, 1, 1));
        userStorage.addUser(user1);

        user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@yandex.ru");
        user2.setLogin("user2");
        user2.setBirthday(LocalDate.of(2000, 2, 2));
        userStorage.addUser(user2);

        Mpa mpa = new Mpa();
        mpa.setId(1);

        film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(2000, 10, 10));
        film1.setDuration(100);
        film1.setMpa(mpa);
        filmStorage.addFilm(film1);

        film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2001, 11, 11));
        film2.setDuration(120);
        film2.setMpa(mpa);
        filmStorage.addFilm(film2);

        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film1.getId(), user1.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film1.getId(), user2.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film2.getId(), user1.getId());
        jdbcTemplate.update("INSERT INTO likes (film_id, user_id) VALUES (?, ?)", film2.getId(), user2.getId());


    }

    @Test
    void shouldReturnCommonFilmsSortedByLikes() throws Exception {
        mockMvc.perform(get("/films/common")
                        .param("userId", String.valueOf(user1.getId()))
                        .param("friendId", String.valueOf(user2.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(film1.getId()))
                .andExpect(jsonPath("$[0].name").value("Film1"))
                .andExpect(jsonPath("$[1].id").value(film2.getId()))
                .andExpect(jsonPath("$[1].name").value("Film2"));
    }
}