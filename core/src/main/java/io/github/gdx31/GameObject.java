package io.github.gdx31;

import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class GameObject {
    public final Rectangle position = new Rectangle();
    public final Vector2 velocity = new Vector2();

    public boolean moves;
    public boolean bounces;
    public TiledMapTileMapObject tileObject;
    public String type;

    public GameObject() {

    }
}
