package bingo.ships;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public record Ship(String name) {

    public StringProperty nameProperty() {
        StringProperty shipNameProperty = new SimpleStringProperty(this, "name");
        shipNameProperty.setValue(name);
        return shipNameProperty;
    }
}
