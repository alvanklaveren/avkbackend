package com.alvanklaveren.model;

import jakarta.persistence.*;
import java.sql.Blob;

@Entity
public class Constants {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer code;

    private String id;

    private String stringValue;

    private Blob blobValue;

    @Version
    private int version;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Blob getBlobValue() {
        return blobValue;
    }

    public void setBlobValue(Blob blobValue) {
        this.blobValue = blobValue;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
