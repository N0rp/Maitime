package eu.dowsing.maiborntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import eu.dowsing.maiborntime.view.TimeView;
import eu.dowsing.maiborntime.view.WorkView;
import eu.dowsing.maiborntime.xml.model.Unit;
import eu.dowsing.maiborntime.xml.model.Work;
import eu.dowsing.maiborntime.xml.model.WorkStore;

public class MaibornTimeFX extends Application {

    private static final String WORKSTORE_XML = "res/jaxb/workstore-jaxb.xml";

    ObservableList<String> timeData = FXCollections.observableArrayList("chocolate", "salmon", "gold", "coral",
            "darkorchid", "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue", "blueviolet", "brown");
    ObservableList<Work> workData = FXCollections.observableArrayList();

    private TimeView timeView;
    private WorkView workView;

    public MaibornTimeFX() {
        timeView = new TimeView(timeData);
        workView = new WorkView(workData);
    }

    @Override
    public void start(final Stage stage) {

        stage.setTitle("MaiTime");

        HBox mainBox = new HBox();

        Region workBox = initWorkView();
        Region timeBox = initTimeView();
        mainBox.getChildren().addAll(timeBox, workBox);

        Scene scene = new Scene(mainBox, 600, 400);
        stage.setScene(scene);

        stage.setScene(scene);
        stage.show();

        System.out.println("Showing");
        try {
            launchJaxb();
        } catch (FileNotFoundException | JAXBException e) {
            System.err.println("Could not read xml file");
            e.printStackTrace();
        }

        // workData.setAll("chocolate", "salmon", "gold", "coral", "darkorchid", "darkgoldenrod", "lightsalmon",
        // "black",
        // "rosybrown", "blue", "blueviolet", "brown");
    }

    private Region initWorkView() {
        VBox workBox = new VBox();

        Button button = new Button("Create New");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Creating new");
                // workData.add("red");
                Work work = new Work();
                work.setAuthor("Temp");
                workData.add(work);
            }
        });

        workBox.getChildren().addAll(button, workView);
        VBox.setVgrow(workView, Priority.ALWAYS);

        return workBox;
    }

    private Region initTimeView() {
        VBox timeBox = new VBox();

        // create toggle group
        final ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
                if (new_toggle == null || group.getSelectedToggle().getUserData() == null) {
                    System.out.println("New toggle");
                } else {
                    System.out.println("Toggle with user data: " + group.getSelectedToggle().getUserData());
                    List<String> toggleData = (List<String>) group.getSelectedToggle().getUserData();
                    timeData.setAll(toggleData);
                }
            }
        });

        ToggleButton tb1 = new ToggleButton("Jahr");
        tb1.setToggleGroup(group);
        tb1.setSelected(true);
        tb1.setUserData(Arrays.asList("2013", "2014"));

        ToggleButton tb2 = new ToggleButton("Monat");
        tb2.setToggleGroup(group);
        tb2.setUserData(Arrays.asList("Januar", "Februar", "März", "April", "Mai"));

        ToggleButton tb3 = new ToggleButton("Woche");
        tb3.setToggleGroup(group);
        tb3.setUserData(Arrays.asList("Januar KW 04", "Februar KW 05", "Februar KW 06", "Februar KW 07",
                "Februar KW 08"));

        ToggleButton tb4 = new ToggleButton("Tag");
        tb4.setToggleGroup(group);
        tb4.setUserData(Arrays.asList("Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag", "Sonntag"));

        HBox toggleBox = new HBox();
        toggleBox.getChildren().addAll(tb1, tb2, tb3, tb4);

        timeBox.getChildren().addAll(toggleBox, timeView);
        VBox.setVgrow(timeBox, Priority.ALWAYS);

        return timeBox;
    }

    private void launchJaxb() throws JAXBException, FileNotFoundException {
        File jaxbFile = new File(WORKSTORE_XML);
        if (jaxbFile.exists()) {
            System.out.println("Jaxb exists");
        } else {
            System.out.println("Jaxb does not exist");
        }

        // create work
        ArrayList<Work> workList = new ArrayList<Work>();

        Work work1 = new Work();
        work1.setAuthor("Neil Strauss");
        workList.add(work1);

        Work work2 = new Work();
        work2.setAuthor("Wilhelm Tell");
        workList.add(work2);

        // create unit
        ArrayList<Unit> unitList = new ArrayList<>();

        Unit unit1 = new Unit();
        unit1.setName("PE");
        unitList.add(unit1);

        Unit unit2 = new Unit();
        unit2.setName("SE");
        unitList.add(unit2);

        // create bookstore, assigning book
        WorkStore workstore = new WorkStore();
        workstore.setName("Fraport Bookstore");
        workstore.setLocation("Frankfurt Airport");
        workstore.setWorkList(workList);
        workstore.setUnitList(unitList);

        // create JAXB context and instantiate marshaller
        JAXBContext context = JAXBContext.newInstance(WorkStore.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        // Write to System.out
        m.marshal(workstore, System.out);

        // Write to File
        m.marshal(workstore, new File(WORKSTORE_XML));

        // get variables from our xml file, created before
        System.out.println();
        System.out.println("Output from our XML File: ");
        Unmarshaller um = context.createUnmarshaller();
        WorkStore fileStore = (WorkStore) um.unmarshal(new FileReader(WORKSTORE_XML));
        ArrayList<Work> list = fileStore.getWorksList();
        for (Work work : list) {
            System.out.println("Book: " + work.getTask() + " from " + work.getAuthor());
        }

        workData.setAll(workstore.getWorksList());
    }

    public static void main(String[] args) throws IOException {
        System.out.println("Load Test");

        System.out.println("Starting TestFX");
        Application.launch(MaibornTimeFX.class);
        // System.out.println("Starting MacOSX Tray Stuff");
        // MacOS awt stuff does not work together with javafx...
        // initMacOS();
    }
}
