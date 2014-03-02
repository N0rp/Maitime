package eu.dowsing.example;

import java.awt.AWTException;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;

import com.apple.eawt.Application;

public class MacOSXDock extends JFrame {

    PopupMenu menu;

    public MacOSXDock() {

        JButton butt = new JButton("Ende");
        butt.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        this.setLayout(new FlowLayout());
        this.add(butt);

        // Dockmenu erzeugen
        menu = new PopupMenu("Application");
        menu.add("Test");
        menu.add("Test 2");
        add(menu);

        this.setSize(200, 80);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    private Image loadImage(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);

        return new ImageIcon(fileName).getImage();
    }

    private PopupMenu getMenu() {
        return menu;
    }

    public static void main(String[] args) {
        MacOSXDock mac = new MacOSXDock();

        Application app = Application.getApplication();

        // Menu zum Dock hinzufuegen
        app.setDockMenu(mac.getMenu());

        String iPath = "res/img/awesome-smiley.png";
        File f = new File(iPath);
        if (f.exists()) {
            System.out.println("Exists!!!");
        }
        // Image laden
        Image icon = mac.loadImage(iPath);

        // Java 6
        // Tray-Icon setzen
        if (SystemTray.isSupported()) {
            SystemTray tray = SystemTray.getSystemTray();
            TrayIcon trayIcon = new TrayIcon(icon, "Icon");

            MenuItem start = new MenuItem("Attention", new MenuShortcut(KeyEvent.VK_S));
            MenuItem stop = new MenuItem("Foreground", new MenuShortcut(KeyEvent.VK_T));

            start.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Application.getApplication().requestUserAttention(true);
                }
            });

            stop.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    Application.getApplication().requestForeground(true);
                }
            });

            PopupMenu menu = new PopupMenu("Application");
            menu.add(start);
            menu.add(stop);
            trayIcon.setPopupMenu(menu);

            try {
                tray.add(trayIcon);
            } catch (AWTException e1) {
                System.out.println("Could not add tray icon");
            }

        } else {
            System.out.println("SystemTray not supported");
        }

        // Dock-Icon setzen
        app.setDockIconImage(icon);
        // Badge (Dock-Icon-Marke) anzeigen
        int i = 1;
        while (i < 10) {
            app.setDockIconBadge(new Integer(i).toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            i++;
        }
        // Badge ausblenden
        app.setDockIconBadge(null);
    }
}
