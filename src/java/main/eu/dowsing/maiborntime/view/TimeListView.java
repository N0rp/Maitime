package eu.dowsing.maiborntime.view;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;
import eu.dowsing.maiborntime.time.model.TimeList;

public class TimeListView extends ListView<TimeList> {

    // private final ObservableList<String> data;

    public TimeListView(ObservableList<TimeList> data) {
        setItems(data);
        init();
    }

    public void init() {

        setCellFactory(new Callback<ListView<TimeList>, ListCell<TimeList>>() {
            @Override
            public ListCell<TimeList> call(ListView<TimeList> list) {
                return new ColorRectCell();
            }
        });
    }

    static class ColorRectCell extends ListCell<TimeList> {
        @Override
        public void updateItem(TimeList item, boolean empty) {
            super.updateItem(item, empty);
            if (item != null) {
                Text txt = new Text(item.getName());
                setGraphic(txt);
            }
        }
    }
}
