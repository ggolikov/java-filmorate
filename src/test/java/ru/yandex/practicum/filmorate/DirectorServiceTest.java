package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.director.DirectorService;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DirectorServiceTest {

    @Mock
    private DirectorStorage directorStorage;

    @InjectMocks
    private DirectorService directorService;

    private Director director;

    @BeforeEach
    void setUp() {
        director = new Director();
        director.setId(1);
        director.setName("Test Director");

        DirectorDto directorDto = new DirectorDto();
        directorDto.setId(1);
        directorDto.setName("Test Director");
    }

    @Test
    void testGetDirectorWithValidId() {
        when(directorStorage.getDirector(1)).thenReturn(Optional.of(director));

        DirectorDto result = directorService.getDirector(1);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Director", result.getName());
        verify(directorStorage, times(1)).getDirector(1);
    }

    @Test
    void testGetDirectorWithInvalidId() {
        when(directorStorage.getDirector(999)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> directorService.getDirector(999));
        verify(directorStorage, times(1)).getDirector(999);
    }

    @Test
    void testGetDirectors() {
        Director director2 = new Director();
        director2.setId(2);
        director2.setName("Another Director");

        List<Director> directors = Arrays.asList(director, director2);
        when(directorStorage.getDirectors()).thenReturn(directors);

        List<Director> result = (List<Director>) directorService.getDirectors();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Director", result.get(0).getName());
        assertEquals("Another Director", result.get(1).getName());
        verify(directorStorage, times(1)).getDirectors();
    }

    @Test
    void testAddDirector() {
        when(directorStorage.addDirector(any(Director.class))).thenReturn(director);

        Director result = directorService.addDirector(director);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Director", result.getName());
        verify(directorStorage, times(1)).addDirector(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithValidData() {
        when(directorStorage.updateDirector(any(Director.class))).thenReturn(director);

        Director result = directorService.updateDirector(director);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Test Director", result.getName());
        verify(directorStorage, times(1)).updateDirector(any(Director.class));
    }

    @Test
    void testUpdateDirectorWithInvalidId() {
        Director nonExistentDirector = new Director();
        nonExistentDirector.setId(999);
        nonExistentDirector.setName("Non-existent Director");

        when(directorStorage.updateDirector(any(Director.class)))
                .thenThrow(new NotFoundException("Режиссёр не найден с ID: 999"));

        assertThrows(NotFoundException.class, () -> directorService.updateDirector(nonExistentDirector));
        verify(directorStorage, times(1)).updateDirector(any(Director.class));
    }

    @Test
    void testRemoveDirectorWithValidId() {
        doNothing().when(directorStorage).removeDirector(1);

        directorService.removeDirector(1);

        verify(directorStorage, times(1)).removeDirector(1);
    }

    @Test
    void testRemoveDirectorWithInvalidId() {
        doThrow(new NotFoundException("Режиссёр не найден с ID: 999"))
                .when(directorStorage).removeDirector(999);

        assertThrows(NotFoundException.class, () -> directorService.removeDirector(999));
        verify(directorStorage, times(1)).removeDirector(999);
    }
}