package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Constants {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String id;

    private String stringValue;

    private Blob blobValue;

    @Version
    private int version;
}
