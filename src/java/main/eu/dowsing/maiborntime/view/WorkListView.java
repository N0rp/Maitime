package eu.dowsing.maiborntime.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import eu.dowsing.maiborntime.xml.model.Work;

public class WorkListView extends ListView<Work> {

    private final ObservableList<Work> data;

    public WorkListView(ObservableList<Work> data) {
        this.data = data;
        init();
    }

    public void init() {
        setItems(data);

        setCellFactory(new Callback<ListView<Work>, ListCell<Work>>() {
            @Override
            public ListCell<Work> call(ListView<Work> list) {
                return new ColorRectCell();
            }
        });

        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Work>() {
            public void changed(ObservableValue<? extends Work> ov, Work old_val, Work new_val) {

            }
        });
    }

    static class ColorRectCell extends ListCell<Work> {
        @Override
        public void updateItem(Work item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                DetailedWorkItemView workItemView = new DetailedWorkItemView();
                workItemView.update(item);
                setGraphic(workItemView);
            }
        }
    }
}
