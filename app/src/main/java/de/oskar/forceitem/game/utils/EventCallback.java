package de.oskar.forceitem.game.utils;

@FunctionalInterface
public interface EventCallback {
    void execute(Object... args);
}