package ru.yandex.practicum.filmorate.service.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.List;

@Service
public class MpaService {
    private final MpaStorage mpaStorage;
    @Autowired
    public MpaService(@Qualifier("mpaDbStorage") MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public MpaDto getRating(int ratingId) {
        List<Integer> mpas = getRatings().stream().map(Mpa::getId).toList();

        if (!mpas.contains(ratingId)) {
            throw new NotFoundException("Рейтинг с ID " + ratingId + " не найден");
        }

        return mpaStorage.getRating(ratingId).map(MpaMapper::mapToMpaDto).orElseThrow(() -> new NotFoundException("Рейтинг не найден с ID: " + ratingId));
    }

    public Collection<Mpa> getRatings() {
        return mpaStorage.getRatings();
    }
}
