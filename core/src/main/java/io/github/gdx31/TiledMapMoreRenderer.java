package io.github.gdx31;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

import static com.badlogic.gdx.graphics.g2d.Batch.*;

public class TiledMapMoreRenderer extends OrthogonalTiledMapRenderer {
    public TiledMapMoreRenderer(TiledMap map) {
        super(map);
    }

    public TiledMapMoreRenderer(TiledMap map, Batch batch) {
        super(map, batch);
    }

    public TiledMapMoreRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
    }

    public TiledMapMoreRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
    }

    @Override
    public void renderObject(MapObject object) {
        if (object instanceof TiledMapTileMapObject && object.isVisible()) {
            TiledMapTileMapObject tileObject = (TiledMapTileMapObject) object;
            final TiledMapTile tile = tileObject.getTile();

            if (tile != null) {
                final boolean flipX = tileObject.isFlipHorizontally();
                final boolean flipY = tileObject.isFlipVertically();

                TextureRegion region = tile.getTextureRegion();

                float x1 = tileObject.getX() + tile.getOffsetX() * unitScale;
                float y1 = tileObject.getY() + tile.getOffsetY() * unitScale;
                float color = tileObject.getColor().toFloatBits();
                float x2 = x1 + region.getRegionWidth() * unitScale;
                float y2 = y1 + region.getRegionHeight() * unitScale;

                float u1 = region.getU();
                float v1 = region.getV2();
                float u2 = region.getU2();
                float v2 = region.getV();

                vertices[X1] = x1;
                vertices[Y1] = y1;
                vertices[C1] = color;
                vertices[U1] = u1;
                vertices[V1] = v1;

                vertices[X2] = x1;
                vertices[Y2] = y2;
                vertices[C2] = color;
                vertices[U2] = u1;
                vertices[V2] = v2;

                vertices[X3] = x2;
                vertices[Y3] = y2;
                vertices[C3] = color;
                vertices[U3] = u2;
                vertices[V3] = v2;

                vertices[X4] = x2;
                vertices[Y4] = y1;
                vertices[C4] = color;
                vertices[U4] = u2;
                vertices[V4] = v1;

                if (flipX) {
                    float temp = vertices[U1];
                    vertices[U1] = vertices[U3];
                    vertices[U3] = temp;
                    temp = vertices[U2];
                    vertices[U2] = vertices[U4];
                    vertices[U4] = temp;
                }
                if (flipY) {
                    float temp = vertices[V1];
                    vertices[V1] = vertices[V3];
                    vertices[V3] = temp;
                    temp = vertices[V2];
                    vertices[V2] = vertices[V4];
                    vertices[V4] = temp;
                }

                batch.draw(region.getTexture(), vertices, 0, NUM_VERTICES);
            }
        }
    }
}
