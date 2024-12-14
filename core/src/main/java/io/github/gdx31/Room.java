package io.github.gdx31;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Room {
    private final HashMap<TiledMapTileLayer.Cell, Rectangle> collisions = new HashMap<>();
    private final List<GameObject> gameObjects = new ArrayList<>();
    private final Rectangle bounds;

    public Room(Rectangle bounds) {
        this.bounds = bounds;
    }

    public HashMap<TiledMapTileLayer.Cell, Rectangle> getCollisions() {
        return collisions;
    }

    public List<GameObject> getGameObjects() {
        return gameObjects;
    }

    public Rectangle getBounds() {
        return bounds;
    }
}
