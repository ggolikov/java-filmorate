package ru.yandex.practicum.filmorate;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.controller.DirectorController;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DirectorController.class)
@AutoConfigureMockMvc
class DirectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DirectorService directorService;

    private Director director;
    private DirectorDto directorDto;
    private List<Director> directors;

    @BeforeEach
    void setUp() {
        director = new Director();
        director.setId(1);
        director.setName("Test Director");

        directorDto = new DirectorDto();
        directorDto.setId(1);
        directorDto.setName("Test Director");

        Director director2 = new Director();
        director2.setId(2);
        director2.setName("Another Director");

        directors = Arrays.asList(director, director2);
    }

    @Test
    void testGetDirectors() throws Exception {
        when(directorService.getDirectors()).thenReturn(directors);

        mockMvc.perform(get("/directors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Test Director")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Another Director")));

        verify(directorService, times(1)).getDirectors();
    }

    @Test
    void testGetDirectorWithValidId() throws Exception {
        when(directorService.getDirector(1)).thenReturn(directorDto);

        mockMvc.perform(get("/directors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Director")));

        verify(directorService, times(1)).getDirector(1);
    }

    @Test
    void testGetDirectorWithInvalidId() throws Exception {
        when(directorService.getDirector(99999))
                .thenThrow(new ru.yandex.practicum.filmorate.exception.NotFoundException("Режиссёр не найден с ID: 99999"));

        mockMvc.perform(get("/directors/99999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Режиссёр не найден с ID: 99999")));

        verify(directorService, times(1)).getDirector(99999);
    }

    @Test
    void testAddDirectorWithValidData() throws Exception {
        when(directorService.addDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Director")));

        verify(directorService, times(1)).addDirector(any(Director.class));
    }

    @Test
    void testAddDirectorWithoutBody() throws Exception {
        mockMvc.perform(post("/directors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Required request body is missing")));

        verify(directorService, never()).addDirector(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithValidData() throws Exception {
        when(directorService.updateDirector(any(Director.class))).thenReturn(director);

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(director)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Test Director")));

        verify(directorService, times(1)).updateDirector(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithInvalidId() throws Exception {
        Director nonExistentDirector = new Director();
        nonExistentDirector.setId(999);
        nonExistentDirector.setName("Non-existent Director");

        when(directorService.updateDirector(any(Director.class)))
                .thenThrow(new ru.yandex.practicum.filmorate.exception.NotFoundException("Режиссёр не найден с ID: 999"));

        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentDirector)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Режиссёр не найден с ID: 999")));

        verify(directorService, times(1)).updateDirector(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithoutBody() throws Exception {
        mockMvc.perform(put("/directors")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Required request body is missing")));

        verify(directorService, never()).updateDirector(any(Director.class));
    }

    @Test
    void testDeleteDirectorWithValidId() throws Exception {
        doNothing().when(directorService).removeDirector(1);

        mockMvc.perform(delete("/directors/1"))
                .andExpect(status().isOk());

        verify(directorService, times(1)).removeDirector(1);
    }

    @Test
    void testDeleteDirectorWithInvalidId() throws Exception {
        doThrow(new ru.yandex.practicum.filmorate.exception.NotFoundException("Режиссёр не найден с ID: 999"))
                .when(directorService).removeDirector(999);

        mockMvc.perform(delete("/directors/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Режиссёр не найден с ID: 999")));

        verify(directorService, times(1)).removeDirector(999);
    }
}
