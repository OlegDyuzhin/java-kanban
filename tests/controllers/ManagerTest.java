package controllers;

import org.junit.jupiter.api.Assertions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ManagerTest {
    @DisplayName("Создание таскМенеджера")
    @Test
    void getDefaultTest() {
        TaskManager tm = Managers.getDefault();
        assertNotNull(tm, "TaskManager не создан");
    }
}
