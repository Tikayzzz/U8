package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity()
@Table(name = "UE08_MOVIE")
public class Movie {

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String type;

    private int year;

    @ManyToMany
    @JoinTable(
            name = "UE08_MOVIE_GENRE",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    @OneToMany(mappedBy = "movie")
    private List<MovieCharacter> characters;


    public Movie(String title, String type, int year) {
        this.title = title;
        this.type = type;
        this.year = year;
    }

    public Movie() {

    }

    public String getTitle() {
        return title;
    }

    public Long getId() {
        return id;
    }

    public void addGenre(Genre genre) {}
    public  List<Genre> getGenres() { return genres; }
}
