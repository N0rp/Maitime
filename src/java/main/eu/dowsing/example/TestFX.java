package eu.dowsing.example;

import java.awt.AWTException;
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
import java.io.IOException;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextBuilder;
import javafx.stage.Stage;

import javax.swing.ImageIcon;

public class TestFX extends Application {

    private Pane root;

    public TestFX() {

    }

    @Override
    public void start(final Stage primaryStage) {

        this.root = new StackPane();

        Text debugText = TextBuilder.create().text("Overlay").translateY(100)
                .font(Font.font("Helvetica", FontWeight.BOLD, 18)).fill(Color.BLUE).build();
        this.root.getChildren().add(debugText);

        Scene scene = new Scene(root, 600, 400);

        primaryStage.setScene(scene);
        primaryStage.show();

        System.out.println("Showing");
    }

    /**
     * Initialize mac os tray icon
     */
    public static void initMacOS() {
        com.apple.eawt.Application app = com.apple.eawt.Application.getApplication();

        // Menu zum Dock hinzufuegen
        // app.setDockMenu(mac.getMenu());

        String iPath = "res/img/awesome-smiley.png";
        File f = new File(iPath);
        if (f.exists()) {
            System.out.println("Exists!!!");
        }
        // Image laden
        Image icon = new ImageIcon(iPath).getImage();

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
                    com.apple.eawt.Application.getApplication().requestUserAttention(true);
                }
            });

            stop.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    com.apple.eawt.Application.getApplication().requestForeground(true);
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
        app.setDockIconBadge(1 + "");
        // Badge (Dock-Icon-Marke) anzeigen
        // int i = 1;
        // while (i < 10) {
        // app.setDockIconBadge(new Integer(i).toString());
        // try {
        // Thread.sleep(1000);
        // } catch (InterruptedException e) {
        // e.printStackTrace();
        // }
        // i++;
        // }
        // Badge ausblenden
        // app.setDockIconBadge(null);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Load Test");

        System.out.println("Starting TestFX");
        Application.launch(TestFX.class);
        // System.out.println("Starting MacOSX Tray Stuff");
        // MacOS awt stuff does not work together with javafx...
        // initMacOS();
    }
}
