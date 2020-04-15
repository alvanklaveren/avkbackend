package com.alvanklaveren.model;

import javax.persistence.*;

@Entity
public class Classification {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String description;

    private boolean isAdmin;

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

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

}
