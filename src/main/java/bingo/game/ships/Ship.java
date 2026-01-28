package bingo.game.ships;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;

public record Ship(String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = -6136440737552171976L;

    public StringProperty nameProperty() {
        StringProperty shipNameProperty = new SimpleStringProperty(this, "name");
        shipNameProperty.setValue(name);
        return shipNameProperty;
    }
}
