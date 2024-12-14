package io.github.gdx31;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import java.util.*;

public class WorldState {
    private final TiledMap map;
    private final List<Room> rooms = new ArrayList<>();
    private GameObject hero;

    public WorldState(TiledMap map) {
        this.map = map;
        initializeRooms();
        initializeGameObjects();
    }

    private void initializeRooms() {
        for (MapLayer mapLayer : map.getLayers().getByType(TiledMapTileLayer.class)) {
            TiledMapTileLayer layer = (TiledMapTileLayer) mapLayer;
            if (layer.getName().startsWith("# ")) {
                Rectangle bounds = new Rectangle(layer.getOffsetX(), -layer.getOffsetY(), layer.getWidth() * layer.getTileWidth(), layer.getHeight() * layer.getTileHeight());
                Room room = new Room(bounds);
                retrieveCollideables(room.getCollisions(), map, layer.getName());
                rooms.add(room);
            }
        }
    }

    private void initializeGameObjects() {
        for (MapLayer mapLayer : map.getLayers()) {
            for (MapObject object : mapLayer.getObjects()) {
                if (object instanceof TiledMapTileMapObject) {
                    GameObject go = new GameObject();
                    go.tileObject = (TiledMapTileMapObject) object;
                    go.position.set(go.tileObject.getX(), go.tileObject.getY(), go.tileObject.getTile().getTextureRegion().getRegionWidth(), go.tileObject.getTile().getTextureRegion().getRegionHeight());
                    go.velocity.set(getFloatWithFallback("dx", go.tileObject), getFloatWithFallback("dy", go.tileObject));
                    go.type = (String) go.tileObject.getProperties().get("type");
                    go.bounces = getBooleanWithFallback("bounces", go.tileObject);
                    go.kills = getBooleanWithFallback("kills", go.tileObject);
                    go.savePoint = getBooleanWithFallback("savePoint", go.tileObject);
                    if (go.type == null) {
                        go.type = go.tileObject.getTile().getProperties().get("type", String.class);
                    }
                    if ("hero".equals(go.type)) {
                        hero = go;
                        hero.position.width = 6;
                        hero.position.height = 6;
                        savePoint.set(hero.position.x, hero.position.y);
                    }
                    addGameObjectToRoom(go);
                }
            }
        }
    }

    private void addGameObjectToRoom(GameObject go) {
        for (Room room : rooms) {
            if (room.getBounds().overlaps(go.position)) {
                room.getGameObjects().add(go);
                break;
            }
        }
    }

    private float getFloatWithFallback(String key, TiledMapTileMapObject tileObject) {
        Float result = tileObject.getProperties().get(key, Float.class);
        if (result == null) {
            result = tileObject.getTile().getProperties().get(key, Float.class);
        }
        return result == null ? 0 : result;
    }

    private boolean getBooleanWithFallback(String key, TiledMapTileMapObject tileObject) {
        Boolean result = tileObject.getProperties().get(key, Boolean.class);
        if (result == null) {
            result = tileObject.getTile().getProperties().get(key, Boolean.class);
        }
        return result != null && result;
    }

    private Map<TiledMapTileLayer.Cell, Rectangle> retrieveCollideables(Map<TiledMapTileLayer.Cell, Rectangle> collideables, TiledMap tiledMap, String layer) {
        if (collideables == null) {
            collideables = new HashMap<>();
        }

        TiledMapTileLayer collisionsLayer = (TiledMapTileLayer) tiledMap.getLayers().get(layer);
        int gridTileWidth = collisionsLayer.getTileWidth();
        int gridTileHeight = collisionsLayer.getTileHeight();
        for (int row = 0; row < collisionsLayer.getHeight(); row++) {
            for (int col = 0; col < collisionsLayer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = collisionsLayer.getCell(col, row);

                if (cell == null || Boolean.TRUE.equals(cell.getTile().getProperties().get("no-collision", Boolean.class))) continue;

                TextureRegion cellTextureRegion = cell.getTile().getTextureRegion();
                Rectangle cellRectangle = new Rectangle(
                    col * gridTileWidth + collisionsLayer.getOffsetX(),
                    row * gridTileHeight - collisionsLayer.getOffsetY(),
                    cellTextureRegion.getRegionWidth(),
                    cellTextureRegion.getRegionHeight());

                collideables.put(cell, cellRectangle);
            }
        }
        return collideables;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public GameObject getHero() {
        return hero;
    }

    public Room getCurrentRoom(GameObject hero) {
        for (Room room : rooms) {
            if (room.getBounds().overlaps(hero.position)) {
                return room;
            }
        }
        return null;
    }

    private Vector2 savePoint = new Vector2();

    public void setSavePoint(float x, float y) {
        savePoint.set(x, y);
    }

    public void playerKilled() {
        hero.position.set(savePoint.x, savePoint.y, hero.position.width, hero.position.height);
        hero.velocity.set(0, 0);
    }
}
