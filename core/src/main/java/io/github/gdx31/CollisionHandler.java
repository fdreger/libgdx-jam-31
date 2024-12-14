package io.github.gdx31;

import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Collection;

public class CollisionHandler {
    private final Rectangle intersection = new Rectangle();

    public boolean moveWithCollisionAndCheckIfOnTheFloor(float step, GameObject mover, Collection<Rectangle> whatCollides) {
        boolean onTheFloor = false;
        mover.position.x += mover.velocity.x * step;

        for (Rectangle collisionCandidate : whatCollides) {
            if (collisionCandidate.overlaps(mover.position)) {
                float horizontalDirection = -Math.signum(mover.velocity.x);
                Intersector.intersectRectangles(collisionCandidate, mover.position, intersection);
                mover.position.x += horizontalDirection * intersection.width;

                if (mover.bounces) {
                    mover.velocity.x *= -1;
                } else {
                    mover.velocity.x = 0;
                }
            }
        }

        mover.position.y += mover.velocity.y * step;
        for (Rectangle collisionCandidate : whatCollides) {
            if (collisionCandidate.overlaps(mover.position)) {
                float verticalDirection = -Math.signum(mover.velocity.y);
                Intersector.intersectRectangles(collisionCandidate, mover.position, intersection);
                mover.position.y += verticalDirection * intersection.height;

                if (verticalDirection == 1) {
                    onTheFloor = true;
                }

                if (mover.bounces) {
                    mover.velocity.y *= -1;
                } else {
                    mover.velocity.y = 0;
                }
            }
        }
        return onTheFloor;
    }
}
