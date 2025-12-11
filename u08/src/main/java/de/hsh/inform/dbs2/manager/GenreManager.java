package de.hsh.inform.dbs2.manager;

import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class GenreManager {

    /**
     * Liefert eine Liste aller Genre-Namen.
     * Die Methode wird von der GUI (MovieDialogCallback) benoetigt, um eine JList zu fuellen.
     * @return Liste aller Genre-Namen als Strings
     */
    public List<String> getGenres() {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery("SELECT g.genre FROM Genre g ORDER BY g.genre", String.class);
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
