package ru.yandex.practicum.filmorate.service.director;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.Collection;

@Service
public class DirectorService {
    private final DirectorStorage directorStorage;

    public DirectorService(@Qualifier("directorDbStorage") DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    public DirectorDto getDirector(int id) {
        return directorStorage.getDirector(id)
                .map(DirectorMapper::mapToDirectorDto)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден с ID: " + id));
    }

    public Collection<Director> getDirectors() {
        return directorStorage.getDirectors();
    }

    public Director addDirector(Director director) {
        return directorStorage.addDirector(director);
    }

    public Director updateDirector(Director director) {
        return directorStorage.updateDirector(director);
    }

    public void removeDirector(int id) {
        directorStorage.removeDirector(id);
    }
}
