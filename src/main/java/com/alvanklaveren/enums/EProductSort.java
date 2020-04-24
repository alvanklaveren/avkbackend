package com.alvanklaveren.enums;

import org.springframework.data.domain.Sort;

import java.util.Arrays;

public enum EProductSort {

    Name_Ascending (0,"Name (ascending)", Sort.by("name").ascending()),
    Name_Descending (1, "Name (descending)", Sort.by("name").descending()),
    Rating          (2, "Rating", Sort.by("productRatings.rating").ascending());

    private int id;
    private String description;
    private Sort sort;

    public Sort getSort(){ return sort; }

    EProductSort(int id, String description, Sort sort){
        this.id = id;
        this.description = description;
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public String getDescription() { return description; }

    public static EProductSort getByDescription(String description){

        return Arrays.stream(values())
                .filter(v -> v.description.equalsIgnoreCase(description))
                .findFirst().orElse(Name_Ascending);
    }

    public static EProductSort getById(int id){

        return Arrays.stream(values())
                .filter(v -> v.id == id)
                .findFirst().orElse(Name_Ascending);
    }

}
