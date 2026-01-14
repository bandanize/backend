package com.bandanize.backend.dtos;

import java.util.List;
import java.util.Map;

public class BandDTO {
    private Long id;
    private String name;
    private String photo;
    private String description;
    private String genre;
    private String city;
    private Map<String, String> rrss;
    private List<UserSummaryDTO> members;

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

    public List<UserSummaryDTO> getMembers() {
        return members;
    }

    public void setMembers(List<UserSummaryDTO> members) {
        this.members = members;
    }
}