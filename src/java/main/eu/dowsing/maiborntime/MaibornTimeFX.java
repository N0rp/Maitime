package eu.dowsing.maiborntime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import eu.dowsing.maiborntime.time.model.SingleTimeUnit.TimeUnit;
import eu.dowsing.maiborntime.time.model.TimeList;
import eu.dowsing.maiborntime.time.model.WorkStoreDivider;
import eu.dowsing.maiborntime.view.DetailedWorkItemView;
import eu.dowsing.maiborntime.view.TimeListView;
import eu.dowsing.maiborntime.view.WorkListView;
import eu.dowsing.maiborntime.xml.model.MasterDataStore;
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
    private static final String MASTERDATASTORE_XML = "res/jaxb/masterdatastore-jaxb.xml";

    private PieChart pieChart;
    private StackedAreaChart<Number, Number> stackedAreaChart;

    ObservableList<Work> workData = FXCollections.observableArrayList();

    ObservableList<TimeList> timeData = FXCollections.observableArrayList();

    ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(new PieChart.Data("Intern", 27),
            new PieChart.Data("Extern", 73));

    ObservableList<XYChart.Series<Number, Number>> stackedAreaChartData = FXCollections.observableArrayList();

    private TimeListView timeView;
    private WorkListView workView;

    private TimeUnit currentTimeUnit = TimeUnit.Year;

    private WorkStoreDivider divider = new WorkStoreDivider();

    public MaibornTimeFX() {
        timeView = new TimeListView(timeData);

        timeView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TimeList>() {
            public void changed(ObservableValue<? extends TimeList> ov, TimeList old_val, TimeList new_val) {
                if (new_val != null) {
                    workData.setAll(new_val.getWork());
                } else {
                    // set empty list
                    workData.setAll(new LinkedList<Work>());
                }
            }
        });
        workView = new WorkListView(workData);
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

        VBox allBox = new VBox();
        Region currentWorkView = createCurrentWorkView();
        allBox.getChildren().addAll(mainBox, currentWorkView);

        workBox.setMinWidth(500);
        Scene scene = new Scene(allBox, 1200, 400);
        stage.setScene(scene);

        stage.setScene(scene);
        stage.show();

        System.out.println("Showing");
        try {
            updateMasterData(MASTERDATASTORE_XML);
            updateWork(WORKSTORE_XML);
        } catch (FileNotFoundException | JAXBException e) {
            System.err.println("Could not read xml file");
            e.printStackTrace();
        }
        showTimeData(TimeUnit.Year);

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

    private void showTimeData(TimeUnit timeUnit) {
        this.currentTimeUnit = timeUnit;
        ObservableList<TimeList> timeList = divider.getTimeList(timeUnit);
        System.out.println("Show time data of type: " + timeUnit + " and " + timeList.size() + " elements");

        timeData.setAll(timeList);
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
        seriesApril.getData().add(new XYChart.Data<Number, Number>(1, 4));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(3, 10));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(6, 15));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(9, 8));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(12, 5));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(15, 18));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(18, 15));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(21, 13));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(24, 19));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(27, 21));
        seriesApril.getData().add(new XYChart.Data<Number, Number>(30, 21));
        XYChart.Series<Number, Number> seriesMay = new XYChart.Series<Number, Number>();
        seriesMay.setName("May");
        seriesMay.getData().add(new XYChart.Data<Number, Number>(1, 20));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(3, 15));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(6, 13));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(9, 12));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(12, 14));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(15, 18));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(18, 25));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(21, 25));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(24, 23));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(27, 26));
        seriesMay.getData().add(new XYChart.Data<Number, Number>(31, 26));

        List<XYChart.Series<Number, Number>> data = new LinkedList<>();
        data.add(seriesApril);
        data.add(seriesMay);

        return data;
    }

    private List<XYChart.Series<Number, Number>> getAreaPerProject() {
        XYChart.Series<Number, Number> projectA = new XYChart.Series<Number, Number>();
        projectA.setName("Projekt A");
        projectA.getData().add(new XYChart.Data<Number, Number>(1, 12));
        projectA.getData().add(new XYChart.Data<Number, Number>(3, 11));
        projectA.getData().add(new XYChart.Data<Number, Number>(6, 15));
        projectA.getData().add(new XYChart.Data<Number, Number>(9, 8));
        projectA.getData().add(new XYChart.Data<Number, Number>(12, 5));
        projectA.getData().add(new XYChart.Data<Number, Number>(15, 18));
        projectA.getData().add(new XYChart.Data<Number, Number>(18, 15));
        projectA.getData().add(new XYChart.Data<Number, Number>(21, 13));
        projectA.getData().add(new XYChart.Data<Number, Number>(24, 19));
        projectA.getData().add(new XYChart.Data<Number, Number>(27, 21));
        projectA.getData().add(new XYChart.Data<Number, Number>(30, 21));
        XYChart.Series<Number, Number> projectB = new XYChart.Series<Number, Number>();
        projectB.setName("Projekt B");
        projectB.getData().add(new XYChart.Data<Number, Number>(1, 20));
        projectB.getData().add(new XYChart.Data<Number, Number>(3, 15));
        projectB.getData().add(new XYChart.Data<Number, Number>(6, 13));
        projectB.getData().add(new XYChart.Data<Number, Number>(9, 12));
        projectB.getData().add(new XYChart.Data<Number, Number>(12, 14));
        projectB.getData().add(new XYChart.Data<Number, Number>(15, 18));
        projectB.getData().add(new XYChart.Data<Number, Number>(18, 25));
        projectB.getData().add(new XYChart.Data<Number, Number>(21, 25));
        projectB.getData().add(new XYChart.Data<Number, Number>(24, 23));
        projectB.getData().add(new XYChart.Data<Number, Number>(27, 26));
        projectB.getData().add(new XYChart.Data<Number, Number>(31, 26));

        List<XYChart.Series<Number, Number>> data = new LinkedList<>();
        data.add(projectA);
        data.add(projectB);

        return data;
    }

    private DetailedWorkItemView currentWorkView = new DetailedWorkItemView();

    private Region createCurrentWorkView() {
        VBox currentBox = new VBox();
        Button button = new Button("Create New");
        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
                System.out.println("Creating new");
                // workData.add("red");
                Work work = new Work();
                work.setAuthor("Temp");
                Calendar c = Calendar.getInstance();
                work.setTimeFrom(c.getTimeInMillis());
                c.set(Calendar.HOUR_OF_DAY, 1);
                work.setTimeTo(c.getTimeInMillis());
                divider.addData(work);
            }
        });
        currentBox.getChildren().addAll(currentWorkView, button);
        return currentBox;
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
                            if (!pieChartData.equals(toggleData.getData())) {
                                pieChartData.setAll((List<PieChart.Data>) toggleData.getData());
                            }

                        } else if (toggleData.getType() == ChartType.StackedArea) {
                            showChart(ChartType.StackedArea);
                            if (!stackedAreaChartData.equals(toggleData.getData())) {
                                stackedAreaChartData.setAll((List<XYChart.Series<Number, Number>>) toggleData.getData());
                            }
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

    private Region createWorkView() {
        VBox workBox = new VBox();

        final ToggleGroup group = new ToggleGroup();
        group.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
            public void changed(ObservableValue<? extends Toggle> ov, Toggle toggle, Toggle new_toggle) {
                if (group.getSelectedToggle().getUserData().equals("Details")) {

                } else if (group.getSelectedToggle().getUserData().equals("Info")) {

                }
            }
        });

        ToggleButton tb1 = new ToggleButton("Details");
        tb1.setToggleGroup(group);
        tb1.setUserData("Details");
        tb1.setTooltip(new Tooltip("Detailansicht der Arbeitszeiten"));

        ToggleButton tb2 = new ToggleButton("Info");
        tb2.setToggleGroup(group);
        tb2.setUserData("Info");
        tb2.setTooltip(new Tooltip("Zusammenfassung der Arbeitszeiten"));

        tb1.setSelected(true);
        HBox toggleBox = new HBox();
        toggleBox.getChildren().addAll(tb1, tb2);

        workBox.getChildren().addAll(toggleBox, workView);
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
                } else if (group.getSelectedToggle().getUserData() instanceof TimeUnit) {
                    System.out.println("Toggle with user data: " + group.getSelectedToggle().getUserData());
                    TimeUnit toggleData = (TimeUnit) group.getSelectedToggle().getUserData();
                    showTimeData(toggleData);
                }
            }
        });

        ToggleButton tb1 = new ToggleButton("J");
        tb1.setToggleGroup(group);
        tb1.setUserData(TimeUnit.Year);

        ToggleButton tb2 = new ToggleButton("M");
        tb2.setToggleGroup(group);
        tb2.setUserData(TimeUnit.Month);

        ToggleButton tb3 = new ToggleButton("W");
        tb3.setToggleGroup(group);
        tb3.setUserData(TimeUnit.Week);

        ToggleButton tb4 = new ToggleButton("T");
        tb4.setToggleGroup(group);
        tb4.setUserData(TimeUnit.Date);

        tb1.setSelected(true);
        showTimeData(TimeUnit.Year);
        HBox toggleBox = new HBox();
        toggleBox.getChildren().addAll(tb1, tb2, tb3, tb4);

        timeBox.getChildren().addAll(toggleBox, timeView);
        VBox.setVgrow(timeBox, Priority.ALWAYS);

        return timeBox;
    }

    private ArrayList<Unit> unitList = new ArrayList<>();
    private ArrayList<Work> workList = new ArrayList<Work>();

    private void updateMasterData(String fileName) throws JAXBException, FileNotFoundException {
        File jaxbFile = new File(fileName);
        if (!jaxbFile.exists()) {
            System.err.println("Jaxb does not exist");
            return;
        }

        MasterDataStore workstore = MasterDataStore.generateTemplate();
        MasterDataStore.write(fileName, workstore);

        // get variables from our xml file, created before
        System.out.println();
        System.out.println("Output from our XML File: ");
        MasterDataStore fileStore = MasterDataStore.read(fileName);

        for (Unit unit : fileStore.getUnitsList()) {
            System.out.println("Unit: " + unit.getName());
        }

        // clear data
        unitList.clear();
        // workData.setAll(masterStore.getWorksList());
    }

    private void updateWork(String fileName) throws JAXBException, FileNotFoundException {
        File jaxbFile = new File(fileName);
        if (!jaxbFile.exists()) {
            System.err.println("Jaxb does not exist");
            return;
        }

        // create JAXB context and instantiate marshaller
        WorkStore workstore = WorkStore.generateTemplate();
        WorkStore.write(fileName, workstore);

        // get variables from our xml file, created before
        System.out.println();
        System.out.println("Output from our XML File: ");

        WorkStore fileStore = WorkStore.read(fileName);
        ArrayList<Work> list = fileStore.getWorksList();
        for (Work work : list) {
            System.out.println("Work task: " + work.getTask() + " from " + work.getAuthor());
        }

        // clear data
        workList.clear();

        divider.updateListData(fileStore);
        System.out.println("Work xml update result: \n" + divider);
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
