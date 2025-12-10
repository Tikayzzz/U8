package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;

@Entity()
@Table(name = "UE08_GENRE")
public class Genre {
    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String genre;

    public Genre(String genre) {
        this.genre = genre;
    }
    public Genre() {}
    public String getGenre() {
        return genre;
    }
    public void setGenre(String genre) {};


}
