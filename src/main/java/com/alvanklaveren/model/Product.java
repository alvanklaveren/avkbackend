package com.alvanklaveren.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PUBLIC)
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String name;
    private String description;
    private int year;
    private int productStatus;
    private double price;

    @Version
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_game_console")
    private GameConsole gameConsole;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_product_type")
    private ProductType productType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="code_company")
    private Company company;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProductRating> productRatings;
}
