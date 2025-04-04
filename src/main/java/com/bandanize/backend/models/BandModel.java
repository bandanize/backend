package com.bandanize.backend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class BandModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String photo;
    private String description;
    private String genre;
    private String city;

    @ElementCollection
    @CollectionTable(name = "band_rrss", joinColumns = @JoinColumn(name = "band_id"))
    @MapKeyColumn(name = "platform")
    @Column(name = "url")
    private Map<String, String> rrss = new HashMap<>();

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(
        name = "band_user",
        joinColumns = @JoinColumn(name = "band_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<UserModel> users = new ArrayList<>();

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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Map<String, String> getRrss() {
        return rrss;
    }

    public void setRrss(Map<String, String> rrss) {
        this.rrss = rrss;
    }

    public List<UserModel> getUsers() {
        return users;
    }

    public void setUsers(List<UserModel> users) {
        this.users = users;
    }
}