package com.alvanklaveren.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

@Getter(AccessLevel.PUBLIC)
@RequiredArgsConstructor
public enum EProductSort {

    Name_Ascending(0,"Name (A-Z)", Sort.by("name").ascending()),
    Name_Descending(1, "Name (Z-A)", Sort.by("name").descending()),
    Rating(2, "Rating", Sort.by("name").ascending()),
    Newest(3, "Newest-Oldest", Sort.by("code").descending()),
    Oldest(4, "Oldest-Newest", Sort.by("code").ascending());

    private final int id;
    private final String description;
    private final Sort sort;

    public static EProductSort getByDescription(String description){
        return Arrays.stream(values()).filter(v -> v.description.equalsIgnoreCase(description))
                .findFirst().orElse(Name_Ascending);
    }

    public static EProductSort getById(int id){
        return Arrays.stream(values()).filter(v -> v.id == id).findFirst().orElse(Name_Ascending);
    }

    public static class EProductSortDTO {

        public Integer id;
        public String description;
    }
}
