package com.alvanklaveren.model;

import javax.persistence.*;
import java.sql.Blob;

@Entity
public class ProductImage {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private Blob image;

    private int sortorder;

    @Version
    private int version;

    @ManyToOne
    @JoinColumn(name="code_product")
    private Product product;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public int getSortorder() {
        return sortorder;
    }

    public void setSortorder(int sortorder) {
        this.sortorder = sortorder;
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
}
