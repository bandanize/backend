package com.bandanize.backend.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class BandModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String photo = "";
    private String description = "";
    private String genre = "";
    private String city = "";

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "band_id")
    private List<CommentModel> comments = new ArrayList<>();

    @ElementCollection
    private List<String> rrss = new ArrayList<>();

    @ElementCollection
    private List<String> songIds = new ArrayList<>();

    @ElementCollection
    private List<String> userIds = new ArrayList<>();

    // Getters and Setters
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

    public List<CommentModel> getComments() {
        return comments;
    }

    public void setComments(List<CommentModel> comments) {
        this.comments = comments;
    }

    public List<String> getRrss() {
        return rrss;
    }

    public void setRrss(List<String> rrss) {
        this.rrss = rrss;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds;
    }

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
    }
}