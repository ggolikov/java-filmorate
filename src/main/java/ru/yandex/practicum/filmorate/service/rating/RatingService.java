package ru.yandex.practicum.filmorate.service.rating;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.Collection;

@Service
public class RatingService {
    private final RatingStorage ratingStorage;
    @Autowired
    public RatingService(@Qualifier("ratingDbStorage") RatingStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Rating getRating(int ratingId) {
        return ratingStorage.getRating(ratingId);
    }

    public Collection<Rating> getRatings() {
        return ratingStorage.getAllRatings();
    }
}
