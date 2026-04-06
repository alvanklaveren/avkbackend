package com.alvanklaveren.model;

import jakarta.persistence.*;

@Entity
public class Company {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @SequenceGenerator(name = "company_id_seq", sequenceName = "company_seq")
    private Integer code;

    private String description;

    @Version
    private int version;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
