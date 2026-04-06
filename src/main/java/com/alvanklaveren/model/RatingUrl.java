package com.alvanklaveren.model;

import jakarta.persistence.*;

@Entity
public class RatingUrl {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @SequenceGenerator(name = "rating_url_id_seq", sequenceName = "rating_url_seq")
    private Integer code;

    private String url;

    @Version
    private int version;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
