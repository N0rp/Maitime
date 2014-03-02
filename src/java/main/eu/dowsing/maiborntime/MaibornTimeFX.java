package eu.dowsing.maiborntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;
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

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import eu.dowsing.maiborntime.view.TimeView;
import eu.dowsing.maiborntime.view.WorkView;
import eu.dowsing.maiborntime.xml.model.Unit;
import eu.dowsing.maiborntime.xml.model.Work;
import eu.dowsing.maiborntime.xml.model.WorkStore;

public class MaibornTimeFX extends Application {

    private enum ChartType {
        Pie, StackedArea
    }

    private static final String CSV_IN = "res/csv/in.csv";
    private static final String CSV_OUT = "res/csv/out.csv";

    private static final String WORKSTORE_XML = "res/jaxb/workstore-jaxb.xml";

    private PieChart pieChart;
    private StackedAreaChart<Number, Number> stackedAreaChart;

    ObservableList<String> timeData = FXCollections.observableArrayList("chocolate", "salmon", "gold", "coral",
            "darkorchid", "darkgoldenrod", "lightsalmon", "black", "rosybrown", "blue", "blueviolet", "brown");
    ObservableList<Work> workData = FXCollections.observableArrayList();

    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Intern", 27),
            new PieChart.Data("Extern", 73));

    ObservableList<XYChart.Series<Number, Number>> stackedAreaChartData = FXCollections.observableArrayList();

    private TimeView timeView;
    private WorkView workView;

    public MaibornTimeFX() {
        timeView = new TimeView(timeData);
        workView = new WorkView(workData);
    }

    @Override
    public void start(final Stage stage) {
        // add version number to xml
        // add local / remote ids to xml for entries
        // add separate, global xml for units, projects etc
        // use ids to refer to global units, projects etc.
        // add csv export and display
        // add different types besides intern/extern to the global pie chart and model

        stage.setTitle("MaiTime");

        HBox mainBox = new HBox();

        Region workBox = createWorkView();
        Region timeBox = createTimeView();
        Region chartBox = createSummaryView();
        mainBox.getChildren().addAll(timeBox, workBox, chartBox);

        Scene scene = new Scene(mainBox, 800, 400);
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
        try {
            launchCsv();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private Region createSummaryView() {
        VBox chartBox = new VBox();

        // create toggle group
        final ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
                if (new_toggle == null || group.getSelectedToggle().getUserData() == null) {
                    System.out.println("New toggle");
                } else {
                    Object userData = group.getSelectedToggle().getUserData();
                    System.out.println("Toggle with user data: " + userData);

                    if (userData instanceof ToggleData) {
                        ToggleData toggleData = (ToggleData) userData;
                        if (toggleData.getType() == ChartType.Pie) {
                            showChart(ChartType.Pie);
                            pieChartData.setAll((List<PieChart.Data>) toggleData.getData());

                        } else if (toggleData.getType() == ChartType.StackedArea) {
                            showChart(ChartType.StackedArea);
                            stackedAreaChartData.setAll((List<XYChart.Series<Number, Number>>) toggleData.getData());
                        }

                    }
                }
            }
        });

        ToggleButton tb1 = new ToggleButton("Total");
        tb1.setToggleGroup(group);
        tb1.setUserData(new ToggleData(ChartType.Pie, Arrays.asList(new PieChart.Data("Intern", 23), new PieChart.Data(
                "Extern", 77))));

        ToggleButton tb2 = new ToggleButton("Projekt");
        tb2.setToggleGroup(group);
        tb2.setUserData(new ToggleData(ChartType.Pie, Arrays.asList(new PieChart.Data("Cooles Project", 13),
                new PieChart.Data("Internes Projekt", 13), new PieChart.Data("Internes Projekt", 74))));

        ToggleButton tb3 = new ToggleButton("Tage");
        tb3.setToggleGroup(group);
        tb3.setUserData(new ToggleData(ChartType.StackedArea, getAreaPerDay()));

        ToggleButton tb4 = new ToggleButton("Projekte");
        tb4.setToggleGroup(group);
        tb4.setUserData(new ToggleData(ChartType.StackedArea, getAreaPerProject()));

        HBox toggleBox = new HBox();
        toggleBox.getChildren().addAll(tb1, tb2, tb3, tb4);

        pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Auslastung");
        stackedAreaChart = createAreaChart();

        tb1.setSelected(true);
        showChart(ChartType.Pie);

        HBox charts = new HBox();
        charts.getChildren().addAll(pieChart, stackedAreaChart);

        chartBox.getChildren().addAll(toggleBox, charts);
        return chartBox;
    }

    private void showChart(ChartType type) {
        if (type == ChartType.Pie) {
            pieChart.setVisible(true);
            pieChart.setManaged(true);
            stackedAreaChart.setVisible(false);
            stackedAreaChart.setManaged(false);
        } else if (type == ChartType.StackedArea) {
            pieChart.setVisible(false);
            pieChart.setManaged(false);
            stackedAreaChart.setVisible(true);
            stackedAreaChart.setManaged(true);
        }
    }

    private StackedAreaChart<Number, Number> createAreaChart() {
        final NumberAxis xAxis = new NumberAxis(1, 31, 1);
        final NumberAxis yAxis = new NumberAxis();
        final StackedAreaChart<Number, Number> sac = new StackedAreaChart<Number, Number>(xAxis, yAxis);
        sac.setTitle("Auslastung pro Tag");

        sac.setData(stackedAreaChartData);
        stackedAreaChartData.setAll(getAreaPerDay());

        return sac;
    }

    private List<XYChart.Series<Number, Number>> getAreaPerDay() {
        XYChart.Series<Number, Number> seriesApril = new XYChart.Series<Number, Number>();
        seriesApril.setName("April");
        seriesApril.getData().add(new XYChart.Data(1, 4));
        seriesApril.getData().add(new XYChart.Data(3, 10));
        seriesApril.getData().add(new XYChart.Data(6, 15));
        seriesApril.getData().add(new XYChart.Data(9, 8));
        seriesApril.getData().add(new XYChart.Data(12, 5));
        seriesApril.getData().add(new XYChart.Data(15, 18));
        seriesApril.getData().add(new XYChart.Data(18, 15));
        seriesApril.getData().add(new XYChart.Data(21, 13));
        seriesApril.getData().add(new XYChart.Data(24, 19));
        seriesApril.getData().add(new XYChart.Data(27, 21));
        seriesApril.getData().add(new XYChart.Data(30, 21));
        XYChart.Series<Number, Number> seriesMay = new XYChart.Series<Number, Number>();
        seriesMay.setName("May");
        seriesMay.getData().add(new XYChart.Data(1, 20));
        seriesMay.getData().add(new XYChart.Data(3, 15));
        seriesMay.getData().add(new XYChart.Data(6, 13));
        seriesMay.getData().add(new XYChart.Data(9, 12));
        seriesMay.getData().add(new XYChart.Data(12, 14));
        seriesMay.getData().add(new XYChart.Data(15, 18));
        seriesMay.getData().add(new XYChart.Data(18, 25));
        seriesMay.getData().add(new XYChart.Data(21, 25));
        seriesMay.getData().add(new XYChart.Data(24, 23));
        seriesMay.getData().add(new XYChart.Data(27, 26));
        seriesMay.getData().add(new XYChart.Data(31, 26));

        List<XYChart.Series<Number, Number>> data = new LinkedList<>();
        data.add(seriesApril);
        data.add(seriesMay);

        return data;
    }

    private List<XYChart.Series<Number, Number>> getAreaPerProject() {
        XYChart.Series<Number, Number> projectA = new XYChart.Series<Number, Number>();
        projectA.setName("Projekt A");
        projectA.getData().add(new XYChart.Data(1, 12));
        projectA.getData().add(new XYChart.Data(3, 11));
        projectA.getData().add(new XYChart.Data(6, 15));
        projectA.getData().add(new XYChart.Data(9, 8));
        projectA.getData().add(new XYChart.Data(12, 5));
        projectA.getData().add(new XYChart.Data(15, 18));
        projectA.getData().add(new XYChart.Data(18, 15));
        projectA.getData().add(new XYChart.Data(21, 13));
        projectA.getData().add(new XYChart.Data(24, 19));
        projectA.getData().add(new XYChart.Data(27, 21));
        projectA.getData().add(new XYChart.Data(30, 21));
        XYChart.Series<Number, Number> projectB = new XYChart.Series<Number, Number>();
        projectB.setName("Projekt B");
        projectB.getData().add(new XYChart.Data(1, 20));
        projectB.getData().add(new XYChart.Data(3, 15));
        projectB.getData().add(new XYChart.Data(6, 13));
        projectB.getData().add(new XYChart.Data(9, 12));
        projectB.getData().add(new XYChart.Data(12, 14));
        projectB.getData().add(new XYChart.Data(15, 18));
        projectB.getData().add(new XYChart.Data(18, 25));
        projectB.getData().add(new XYChart.Data(21, 25));
        projectB.getData().add(new XYChart.Data(24, 23));
        projectB.getData().add(new XYChart.Data(27, 26));
        projectB.getData().add(new XYChart.Data(31, 26));

        List<XYChart.Series<Number, Number>> data = new LinkedList<>();
        data.add(projectA);
        data.add(projectB);

        return data;
    }

    private Region createWorkView() {
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

    private Region createTimeView() {
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

        ToggleButton tb1 = new ToggleButton("J");
        tb1.setToggleGroup(group);
        tb1.setSelected(true);
        tb1.setUserData(Arrays.asList("2013", "2014"));

        ToggleButton tb2 = new ToggleButton("M");
        tb2.setToggleGroup(group);
        tb2.setUserData(Arrays.asList("Januar", "Februar", "März", "April", "Mai"));

        ToggleButton tb3 = new ToggleButton("W");
        tb3.setToggleGroup(group);
        tb3.setUserData(Arrays.asList("Januar KW 04", "Februar KW 05", "Februar KW 06", "Februar KW 07",
                "Februar KW 08"));

        ToggleButton tb4 = new ToggleButton("T");
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

    /**
     * Launch the csv reader/writer
     * 
     * @throws IOException
     */
    private void launchCsv() throws IOException {
        CSVReader reader = new CSVReader(new FileReader(CSV_IN));
        List<String[]> myEntries = reader.readAll();
        System.out.println("CSV contains: " + myEntries);

        // bind to beans
        // ColumnPositionMappingStrategy strat = new ColumnPositionMappingStrategy();
        // strat.setType(YourOrderBean.class);
        // String[] columns = new String[] {"name", "orderNumber", "id"}; // the fields to bind do in your JavaBean
        // strat.setColumnMapping(columns);
        //
        // CsvToBean csv = new CsvToBean();
        // List list = csv.parse(strat, yourReader);

        // write beans
        CSVWriter writer = new CSVWriter(new FileWriter(CSV_OUT), '\t');
        // feed in your array (or convert your data to an array)
        String[] entries = "first#second#third".split("#");
        writer.writeNext(entries);
        writer.close();
    }

    private final class ToggleData {
    
        private final Object data;
        private final ChartType type;
    
        public ToggleData(ChartType type, Object data) {
            this.type = type;
            this.data = data;
        }
    
        public ChartType getType() {
            return this.type;
        }
    
        public Object getData() {
            return this.data;
        }
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
