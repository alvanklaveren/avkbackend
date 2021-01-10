package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Translation {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String original;
    private String us;
    private String nl;

    @Version
    private int version;
}
