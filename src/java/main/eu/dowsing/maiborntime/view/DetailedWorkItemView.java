package eu.dowsing.maiborntime.view;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import eu.dowsing.maiborntime.xml.model.Work;

/**
 * Represents a detailed work item, not a summary.
 * 
 * @author richardg
 * 
 */
public class DetailedWorkItemView extends Pane {

    private Text txtUnit = new Text();
    private Text txtPartner = new Text();
    private Text txtProject = new Text();
    private Text txtSubProject = new Text();

    private Text txtAuthor = new Text();
    private Text txtTask = new Text();
    private Text txtSubTask = new Text();
    private Text txtTaskTime = new Text();

    public DetailedWorkItemView() {
        initView();
    }

    private void initView() {
        HBox headerBox = new HBox();
        headerBox.getChildren().addAll(txtUnit, txtPartner, txtProject, txtSubProject);

        HBox detailBox = new HBox();
        detailBox.getChildren().addAll(txtAuthor, txtTask, txtSubTask, txtTaskTime);

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(headerBox, detailBox);
        getChildren().add(mainBox);
    }

    public void update(Work work) {
        txtUnit.setText(work.getUnit());
        txtPartner.setText(work.getPartner());
        txtProject.setText(work.getProject());
        txtSubProject.setText(work.getSubproject());

        txtAuthor.setText(work.getAuthor());
        txtTask.setText(work.getTask());
        txtSubTask.setText(work.getSubtask());
        txtTaskTime.setText(work.getTaskTime() + "");
    }
}
