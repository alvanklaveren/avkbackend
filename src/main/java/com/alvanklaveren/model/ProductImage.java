package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class ProductImage {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private Blob image;

    private int sortorder;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_product")
    private Product product;
}
