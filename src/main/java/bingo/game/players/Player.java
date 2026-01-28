package bingo.game.players;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.Serial;
import java.io.Serializable;

public record Player(String name) implements Serializable {
    @Serial
    private static final long serialVersionUID = 6956199412122635202L;

    public StringProperty nameProperty() {
        StringProperty playerNameProperty = new SimpleStringProperty(this, "name");
        playerNameProperty.setValue(name);
        return playerNameProperty;
    }
}
