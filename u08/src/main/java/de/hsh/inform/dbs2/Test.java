package de.hsh.inform.dbs2;


import de.hsh.inform.dbs2.entities.*;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.*;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Test {

    private static EntityManagerFactory emf;
    private EntityManager em;

    @BeforeAll
    static void init() {
        emf = EMFSingleton.getEntityManagerFactory();
    }

    @BeforeEach
    void openEm() {
        em = emf.createEntityManager();
    }

    @AfterEach
    void closeEm() {
        em.close();
    }


    // 1. Insert a Movie
    @Order(1)
     void testInsertMovie() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Movie m = new Movie("Star Wars", "Movie", 1977);
        em.persist(m);

        tx.commit();

        Assertions.assertNotNull(m.getId());
        System.out.println("Inserted Movie: " + m.getTitle() + " (ID: " + m.getId() + ")");
    }

    // 2. Insert Movie with Genres
    @Order(2)
    void testInsertMovieWithGenres() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Movie m = new Movie("Interstellar", "Movie", 2014);

        Genre g1 = new Genre("Sci-Fi");
        Genre g2 = new Genre("Drama");

        m.addGenre(g1);
        m.addGenre(g2);

        em.persist(m);

        tx.commit();

        Assertions.assertFalse(m.getGenres().isEmpty());
        System.out.println("Inserted Movie with genres: " + m.getTitle());
    }

    // 3. Insert a Person + Character in Movie
    @Order(3)
    void testInsertMovieCharacter() {
        EntityTransaction tx = em.getTransaction();
        tx.begin();

        Person p = new Person("Mark Hamill");
        Movie m = new Movie("Star Wars", "Movie", 1977);

        MovieCharacter ch = new MovieCharacter();
        ch.setAlias("Luke");
        ch.setCharacter("Luke Skywalker");
        ch.setPosition(1);
        ch.setMovie(m);
        ch.setPerson(p);

        em.persist(p);
        em.persist(m);
        em.persist(ch);

        tx.commit();

        Assertions.assertNotNull(ch.getId());
        System.out.println("Inserted Character: " + ch.getAlias());
    }

    // 4. Query Movies from specific year
    @Order(4)
    void testQueryMoviesByYear() {
        List<Movie> movies = em.createQuery(
                        "SELECT m FROM Movie m WHERE m.year = :y", Movie.class)
                .setParameter("y", 1977)
                .getResultList();

        movies.forEach(m -> System.out.println("Found movie: " + m.getTitle()));

        Assertions.assertFalse(movies.isEmpty());
    }

    // 5. Query Characters for a movie
    @Order(5)
    void testQueryMovieCharacters() {
        List<MovieCharacter> chars = em.createQuery(
                        "SELECT c FROM MovieCharacter c WHERE c.movie.title = :title",
                        MovieCharacter.class
                )
                .setParameter("title", "Star Wars")
                .getResultList();

        chars.forEach(c ->
                System.out.println("Character: " + c.getAlias() +
                        " -> " + c.getCharacter())
        );

        Assertions.assertFalse(chars.isEmpty());
    }
}

