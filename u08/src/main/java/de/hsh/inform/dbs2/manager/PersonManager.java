package de.hsh.inform.dbs2.manager;

import de.hsh.inform.dbs2.entities.Person;
import de.hsh.inform.dbs2.persistence.DoesNotExistException;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class PersonManager {

    /**
     * Liefert eine Namens-Liste aller Personen, deren Name den Suchtext enthaelt.
     * Die Methode wird von der GUI (CharacterDialogCallback) benoetigt.
     * @param text Suchtext
     * @return Namens-Liste
     */
    public List<String> getPersonList(String text) {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<String> query = em.createQuery(
                "SELECT p.name FROM Person p WHERE LOWER(p.name) LIKE LOWER(:name) ORDER BY p.name", String.class);
            query.setParameter("name", "%" + (text == null ? "" : text) + "%");
            return query.getResultList();
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Liefert die ID einer Person anhand des exakten Namens.
     * Die Methode wird von der GUI (CharacterDialogCallback) benoetigt, die einen int erwartet.
     * @param text exakter Name
     * @return Person-ID als long
     * @throws DoesNotExistException falls keine Person mit dem Namen existiert
     */
    public Integer getPerson(String text) throws DoesNotExistException {
        EntityManager em = EMFSingleton.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p WHERE p.name = :name", Person.class);
            query.setParameter("name", text);
            Person person = query.getSingleResult();
            return person.getId();
        } catch (NoResultException e) {
            throw new DoesNotExistException("Person with name '" + text + "' does not exist.");
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
