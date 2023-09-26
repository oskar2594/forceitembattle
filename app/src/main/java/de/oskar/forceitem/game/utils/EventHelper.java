package de.oskar.forceitem.game.utils;

import java.util.HashMap;
import java.util.LinkedList;

public class EventHelper {

    private static HashMap<String, LinkedList<EventCallback>> callbacks = new HashMap<>();

    protected void call(String event, Object ...args) {
        if (callbacks.containsKey(event)) {
            for (EventCallback callback : callbacks.get(event)) {
                callback.execute(args);
            }
        }
    }

    public static void on(String event, EventCallback callback) {
        if (!callbacks.containsKey(event)) {
            callbacks.put(event, new LinkedList<>());
        }
        callbacks.get(event).add(callback);
    }

}


