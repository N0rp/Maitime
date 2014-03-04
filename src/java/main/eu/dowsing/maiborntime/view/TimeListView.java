package eu.dowsing.maiborntime.view;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class TimeListView extends ListView<String> {

    private final ObservableList<String> data;

    public TimeListView(ObservableList<String> data) {
        this.data = data;
        init();
    }

    public void init() {
        setItems(data);

        setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> list) {
                return new ColorRectCell();
            }
        });

        getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {

            }
        });
    }

    static class ColorRectCell extends ListCell<String> {
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);
            Text txt = new Text(item);
            if (item != null) {
                setGraphic(txt);
            }
        }
    }
}
