package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity()
@Table(name = "UE08_MOVIE")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    private String title;

    private String type;

    private int year;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "UE08_MOVIE_GENRE",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovieCharacter> characters = new ArrayList<>();


    public Movie(String title, String type, int year) {
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Movie() {

    }

    //getter setter

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public int getYear() {
        return year;
    }
    public void setYear(int year) {
        this.year = year;
    }

    public Set<Genre> getGenres() { 
        return genres; 
    }

    public void setGenres(Set<Genre> genres) {
        this.genres = genres;
    }

    public List<MovieCharacter> getCharacters() {
        return characters;
    }

    public void setCharacters(List<MovieCharacter> characters) {
        this.characters = characters;
    }

    public void addGenre(Genre genre) {
        this.genres.add(genre);
    }
}
