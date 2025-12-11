package de.hsh.inform.dbs2;

import de.hsh.inform.dbs2.gui.SearchMovieDialog;
import de.hsh.inform.dbs2.gui.SearchMovieDialogCallback;
import de.hsh.inform.dbs2.util.EMFSingleton;
import jakarta.persistence.EntityManagerFactory;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            EntityManagerFactory emf = EMFSingleton.getEntityManagerFactory();
            if (emf != null && emf.isOpen()) {
                emf.close();
            }
        }));
        
        SwingUtilities.invokeLater(Main::run);
    }

    public static void run() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SearchMovieDialogCallback callback = new SearchMovieDialogCallback();
        

        SearchMovieDialog searchDialog = new SearchMovieDialog(callback);
        

        callback.setOwner(searchDialog);
        

        searchDialog.setVisible(true);
    }
}
