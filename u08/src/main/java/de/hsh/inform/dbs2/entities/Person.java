package de.hsh.inform.dbs2.entities;

import jakarta.persistence.*;
import java.util.List;


@Entity()
@Table(name = "UE08_Person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(mappedBy = "person")
    private List<MovieCharacter> characters;


    private String name;
    public Person(String name) {
        this.name = name;
    }

    public Person() {}

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
