package bingo.players;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlayerTest {
    private static final String DUMMY_PLAYER = "Dummy Player";

    @Test
    void getterMethodsShouldReturnName() {
        Player player = new Player(DUMMY_PLAYER);
        assertEquals(DUMMY_PLAYER, player.name());
        assertEquals(DUMMY_PLAYER, player.nameProperty().get());
        assertEquals("name", player.nameProperty().getName());
    }
}
