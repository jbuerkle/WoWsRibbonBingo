package ships;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShipTest {
    private static final String DUMMY_SHIP = "Dummy Ship";

    @Test
    void getterMethodsShouldReturnName() {
        Ship ship = new Ship(DUMMY_SHIP);
        assertEquals(DUMMY_SHIP, ship.name());
        assertEquals(DUMMY_SHIP, ship.nameProperty().get());
        assertEquals("name", ship.nameProperty().getName());
    }
}
