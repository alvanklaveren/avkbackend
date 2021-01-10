package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Classification {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String description;

    private boolean isAdmin;

    @Version
    private int version;
}
