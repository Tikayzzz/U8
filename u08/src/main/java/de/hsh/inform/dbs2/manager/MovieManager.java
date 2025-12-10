package de.hsh.inform.dbs2.manager;

import de.hsh.inform.dbs2.dto.CharacterDTO;
import de.hsh.inform.dbs2.dto.MovieDTO;
import de.hsh.inform.dbs2.entities.Genre;
import de.hsh.inform.dbs2.entities.Movie;
import de.hsh.inform.dbs2.entities.MovieCharacter;
import de.hsh.inform.dbs2.entities.Person;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MovieManager {

    /**
     * Erzeugt eine Liste von MovieDTOs fuer die Anzeige in der Haupttabelle.
     * Filtert nach Titel, wenn ein Suchtext uebergeben wird.
     * Kompatibel mit SearchMovieDialogCallback.
     */
    public List<MovieDTO> getMovieList(String title) {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        try {
            String queryString = "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE LOWER(:title) ORDER BY m.title";
            TypedQuery<Movie> query = em.createQuery(queryString, Movie.class);
            query.setParameter("title", "%" + (title == null ? "" : title) + "%");
            // Konvertiere die Ergebnisliste von Movie-Entitäten in eine Liste von MovieDTOs
            return query.getResultList().stream().map(this::toSmallDTO).collect(Collectors.toList());
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Holt ein vollstaendiges MovieDTO fuer einen Film anhand seiner ID, inkl. Genres und Charaktere.
     * Kompatibel mit MovieDialogCallback.
     */
    public MovieDTO getMovie(long id) {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        try {
            // Cast von long auf int, da die ID in der Entity int ist
            Movie movie = em.find(Movie.class, (int) id);
            if (movie == null) {
                return null;
            }
            // Konvertiere die Entität in ein vollstaendiges DTO
            return toFullDTO(movie);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Speichert (fuegt ein oder aktualisiert) ein MovieDTO.
     * Dieser Name (`insertUpdateMovie`) wird von der GUI erwartet.
     */
    public void insertUpdateMovie(MovieDTO dto) {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Movie movie;
            if (dto.getId() != null) {
                movie = em.find(Movie.class, dto.getId());
                // Alte Charaktere loeschen, da wir sie basierend auf dem DTO neu erstellen.
                // Dies ist notwendig, da kein `orphanRemoval` konfiguriert ist.
                em.createQuery("DELETE FROM MovieCharacter mc WHERE mc.movie = :movie")
                  .setParameter("movie", movie)
                  .executeUpdate();
            } else {
                movie = new Movie();
            }

            // Basisattribute setzen
            movie.setTitle(dto.getTitle());
            movie.setYear(dto.getYear());
            movie.setType(dto.getType());

            // Genres setzen
            if (dto.getGenres() != null) {
                TypedQuery<Genre> genreQuery = em.createQuery("SELECT g FROM Genre g WHERE g.name IN :names", Genre.class);
                genreQuery.setParameter("names", dto.getGenres());
                List<Genre> genres = genreQuery.getResultList();
                movie.setGenres(new HashSet<>(genres));
            } else {
                movie.setGenres(new HashSet<>());
            }
            
            // Wichtig: Die alte Collection leeren, bevor neue Elemente hinzugefuegt werden
            movie.getCharacters().clear();

            // Charaktere und Personen verwalten
            if (dto.getCharacters() != null) {
                for (CharacterDTO charDTO : dto.getCharacters()) {
                    MovieCharacter newChar = new MovieCharacter();
                    newChar.setCharacter(charDTO.getCharacter());
                    newChar.setAlias(charDTO.getAlias());
                    newChar.setMovie(movie); // Wichtige Rueckverbindung setzen

                    // Person (Player) suchen oder neu anlegen
                    if (charDTO.getPlayer() != null && !charDTO.getPlayer().isEmpty()) {
                        Person player;
                        try {
                            TypedQuery<Person> personQuery = em.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class);
                            personQuery.setParameter("name", charDTO.getPlayer());
                            player = personQuery.getSingleResult();
                        } catch (NoResultException e) {
                            // Person existiert nicht, also neu anlegen
                            player = new Person();
                            player.setName(charDTO.getPlayer());
                            // em.persist(player) ist nicht unbedingt noetig, wenn Cascade-Persist auf MovieCharacter->Person gesetzt ist. 
                            // Zur Sicherheit machen wir es explizit, falls die Entitaeten sich aendern.
                            // Da keine Cascade-Beziehung definiert ist, machen wir es mit merge(player).
                        }
                        newChar.setPerson(em.merge(player));
                    }
                    movie.getCharacters().add(newChar);
                }
            }
            
            // Die Movie-Entitaet speichern/aktualisieren (merge ist sicher fuer neue und vorhandene)
            em.merge(movie);

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            // Fehler weiterwerfen, damit die GUI ihn bemerken kann
            throw new RuntimeException("Fehler beim Speichern des Films: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Loescht einen Film anhand seiner ID.
     * Kompatibel mit SearchMovieDialog.
     */
    public void deleteMovie(long id) {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Movie movieToDelete = em.find(Movie.class, (int) id);
            if (movieToDelete != null) {
                // Zuerst alle abhaengigen MovieCharacter-Eintraege loeschen
                em.createQuery("DELETE FROM MovieCharacter mc WHERE mc.movie = :movie")
                  .setParameter("movie", movieToDelete)
                  .executeUpdate();
                
                // Dann den Film selbst loeschen
                em.remove(movieToDelete);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Fehler beim Loeschen des Films: " + e.getMessage(), e);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // --- Private Helper-Methoden zur Konvertierung ---

    /**
     * Konvertiert ein Movie-Entity in ein "kleines" DTO (nur Basisinfos).
     * Wird fuer die Uebersichtsliste verwendet.
     */
    private MovieDTO toSmallDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setYear(movie.getYear());
        dto.setType(movie.getType());
        return dto;
    }

    /**
     * Konvertiert ein Movie-Entity in ein "volles" DTO (inkl. Relationen).
     * Wird fuer die Detailansicht/Bearbeitung verwendet.
     */
    private MovieDTO toFullDTO(Movie movie) {
        MovieDTO dto = toSmallDTO(movie);

        // Genres konvertieren (List<Genre> -> Set<String>)
        if (movie.getGenres() != null) {
            dto.setGenres(movie.getGenres().stream()
                .map(Genre::getGenre)
                .collect(Collectors.toSet()));
        }

        // Charaktere konvertieren (List<MovieCharacter> -> List<CharacterDTO>)
        if (movie.getCharacters() != null) {
            List<CharacterDTO> characterDTOs = movie.getCharacters().stream()
                .map(this::toCharacterDTO)
                .collect(Collectors.toList());
            dto.getCharacters().addAll(characterDTOs);
        }
        return dto;
    }

    /**
     * Konvertiert ein MovieCharacter-Entity in ein CharacterDTO.
     */
    private CharacterDTO toCharacterDTO(MovieCharacter mc) {
        CharacterDTO dto = new CharacterDTO();
        dto.setCharacter(mc.getCharacter());
        dto.setAlias(mc.getAlias());
        if (mc.getPerson() != null) {
            dto.setPlayer(mc.getPerson().getName());
            // Wichtig: ID vom Player setzen! (Long -> Integer)
            dto.setPersonId(mc.getPerson().getId());
        }
        return dto;
    }
}
