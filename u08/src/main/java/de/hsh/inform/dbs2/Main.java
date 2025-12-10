package de.hsh.inform.dbs2;

import de.hsh.inform.dbs2.entities.Genre;
import de.hsh.inform.dbs2.entities.Movie;
import de.hsh.inform.dbs2.entities.MovieCharacter;
import de.hsh.inform.dbs2.entities.Person;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

public class Main {

    public static void main(String [] args) {
        createMovie();
        printMovies();
        createGenre();
        printGenres();
        createMovChar();
        printMovChar();
        createPerson();
        printPersons();
    }

    public static void createMovie() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                Movie movie = new Movie("Star Wars", "C", 1977);
                em.persist(movie);
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void printMovies() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                List<Movie> results = em.createQuery("SELECT m FROM Movie m WHERE m.year = :year", Movie.class).setParameter("year", 1977).getResultList();
                for (Movie movie : results) {
                    System.out.println(movie.getId() + ":" + movie.getTitle());
                }
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void createGenre() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                Genre genre = new Genre("D");
                em.persist(genre);
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void printGenres() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                List<Genre> results = em.createQuery("SELECT g FROM Genre g", Genre.class).getResultList();
                for (Genre genre : results) {
                    System.out.println(genre.getGenre());
                }
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void createMovChar() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                MovieCharacter movChar = new MovieCharacter("Tom", "", 1 );
                em.persist(movChar);
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }
    public static void printMovChar() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                List<MovieCharacter> results = em.createQuery("SELECT mc FROM MovieCharacter mc", MovieCharacter.class).getResultList();
                for (MovieCharacter movChar : results) {
                    System.out.println(movChar.getId() + ":" + movChar.getCharacter() + " " + movChar.getAlias() + " " + movChar.getPosition());
                }
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void createPerson() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                Person person = new Person("Tommy");
                em.persist(person);
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }

    public static void printPersons() {
        try (EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager()) {
            EntityTransaction trx = em.getTransaction();
            try {
                trx.begin();
                List<Person> results = em.createQuery("SELECT p FROM Person p", Person.class).getResultList();
                for (Person person : results) {
                    System.out.println(person.getId() + ":" + person.getName());
                }
                trx.commit();
            } catch (Exception ex) {
                if (trx.isActive()) {
                    trx.rollback();
                }
                throw ex;
            }
        }
    }


}
