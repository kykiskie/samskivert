//
// $Id: Chooser.java,v 1.10 2002/11/11 17:04:26 mdb Exp $

package robodj.chooser;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import com.samskivert.io.PersistenceException;
import com.samskivert.jdbc.StaticConnectionProvider;

import com.samskivert.swing.util.SwingUtil;
import com.samskivert.util.ConfigUtil;
import com.samskivert.util.PropertiesUtil;

import robodj.Log;
import robodj.repository.Model;
import robodj.repository.Repository;
import robodj.util.ServerControl;

/**
 * The chooser is the GUI-based application for browsing the music
 * collection and managing the playlist.
 */
public class Chooser
{
    public static Properties config;

    public static Repository repository;

    public static Model model;

    public static ServerControl scontrol;

    public static void main (String[] args)
    {
        // load our main configuration
        String cpath = "conf/chooser.properties";
        try {
            config = ConfigUtil.loadProperties(cpath);
        } catch (IOException ioe) {
            String err = "Unable to load configuration " +
                "[path=" + cpath + "]:\n" + ioe.getMessage();
            reportError(err);
            System.exit(-1);
        }

        // create an interface to the database repository
        try {
            StaticConnectionProvider scp =
                new StaticConnectionProvider("conf/repository.properties");
            repository = new Repository(scp);
            model = new Model(repository);

        } catch (IOException ioe) {
            Log.logStackTrace(ioe);
            reportError("Error loading repository config:\n" +
                        ioe.getMessage());
            System.exit(-1);

        } catch (PersistenceException pe) {
            Log.logStackTrace(pe);
            pe.printStackTrace();
            reportError("Unable to establish communication " +
                        "with database:\n" + pe.getMessage());
            System.exit(-1);
        }

        String mhost = System.getProperty("musicd_host", "localhost");
        String mportstr = System.getProperty("musicd_port", "2500");
        int mport = 2500;
        try {
            mport = Integer.parseInt(mportstr);
        } catch (NumberFormatException nfe) {
            Log.warning("Invalid musicd_port value. Using default " +
                        "(" + mport + ").");
        }

        try {
            // establish a connection with the music server
            scontrol = new ServerControl(mhost, 2500);
        } catch (IOException ioe) {
            Log.logStackTrace(ioe);
            reportError("Unable to establish communication with music " +
                        "server:\n" + ioe.getMessage());
            System.exit(-1);
        }

        // create our primary user interface frame, center the frame in
        // the screen and show it
	ChooserFrame frame = new ChooserFrame();
	frame.setSize(650, 665);
	SwingUtil.centerWindow(frame);
	frame.setVisible(true);
    }

    protected static void reportError (String error)
    {
        Object[] options = { "OK" };
        JOptionPane.showOptionDialog(null, error, "Error",
                                     JOptionPane.DEFAULT_OPTION,
                                     JOptionPane.ERROR_MESSAGE,
                                     null, options, options[0]);
    }
}
