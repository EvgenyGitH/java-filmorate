package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private Set<Genre> genres;
    private Mpa mpa;
    private Set<Long> likes;


    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", getName());
        values.put("description", getDescription());
        values.put("release_date", getReleaseDate());
        values.put("duration", getDuration());
        values.put("mpa_id", getMpa().getId());
        return values;
    }


}
