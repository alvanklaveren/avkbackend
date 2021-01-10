package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ForumUser {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String username;
    private String password;
    private String emailAddress;
    private String displayName;
    private Blob avatar;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_classification")
    private Classification classification;
}
