package com.alvanklaveren.model;

import javax.persistence.*;

@Entity
public class ProductRating {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private int rating;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name="code_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name="code_rating_url")
    private RatingUrl ratingUrl;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public RatingUrl getRatingUrl() {
        return ratingUrl;
    }

    public void setRatingUrl(RatingUrl ratingUrl) {
        this.ratingUrl = ratingUrl;
    }
}
