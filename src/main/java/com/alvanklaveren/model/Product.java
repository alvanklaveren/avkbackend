package com.alvanklaveren.model;

import javax.persistence.*;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String name;
    private String description;
    private int year;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name="code_game_console")
    private GameConsole gameConsole;

    @ManyToOne
    @JoinColumn(name="code_product_type")
    private ProductType productType;

    @ManyToOne
    @JoinColumn(name="code_company")
    private Company company;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public GameConsole getGameConsole() {
        return gameConsole;
    }

    public void setGameConsole(GameConsole gameConsole) {
        this.gameConsole = gameConsole;
    }

    public ProductType getProductType() {
        return productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
