package com.bandanize.backend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class UserModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String username;
    private String fullName;
    private String city = "";
    private String hashedPassword = "";
    private boolean disabled = true;
    private String photo = "";

    @ElementCollection
    private List<String> rrss = new ArrayList<>();

    @ElementCollection
    private List<String> bandIds = new ArrayList<>();

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public List<String> getRrss() {
        return rrss;
    }

    public void setRrss(List<String> rrss) {
        this.rrss = rrss;
    }

    public List<String> getBandIds() {
        return bandIds;
    }

    public void setBandIds(List<String> bandIds) {
        this.bandIds = bandIds;
    }
}