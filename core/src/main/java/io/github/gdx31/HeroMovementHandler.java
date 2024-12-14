package io.github.gdx31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.Collection;

public class HeroMovementHandler {

    public static final float HERO_HORIZONTAL_ACCELERATION = 2000;
    public static final float MAX_HORIZONTAL_SPEED = 100;
    public static final float MAX_VERTICAL_SPEED = 200;
    public static final float GRAVITY_ACCELERATION = 100f;
    public static final float HERO_JUMP_VELOCITY = GRAVITY_ACCELERATION;
    public static final float HORIZONTAL_DUMPING = 900f;

    public enum Mode {
        NORMAL, GRID, NORMAL_NO_GRAVITY
    }

    public static Mode mode = Mode.GRID;

    public static void handleHeroMovement(GameObject hero, boolean onTheFloor, float deltaTime, Collection<Rectangle> collisions) {
        switch (mode) {
            case NORMAL_NO_GRAVITY:
            case NORMAL:
                normalMovement(hero, onTheFloor, deltaTime);
                break;
            case GRID:
                gridMovement(hero, collisions);
                break;
        }
        hero.tileObject.setX(hero.position.x);
        hero.tileObject.setY(hero.position.y);
    }

    private static Rectangle tmp = new Rectangle();
    private static void gridMovement(GameObject hero, Collection<Rectangle> collisions) {

        tmp.set(hero.position);
        if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            tmp.x += 8;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            tmp.x -= 8;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            tmp.y -= 8;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            tmp.y += 8;
        }
        for (Rectangle collision : collisions) {
            if (collision.overlaps(tmp)) {
                return;
            }
        }
        hero.position.set(tmp);
    }


    private static void normalMovement(GameObject hero, boolean onTheFloor, float deltaTime) {
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && onTheFloor && mode != Mode.NORMAL_NO_GRAVITY) {
            hero.velocity.y = HERO_JUMP_VELOCITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && mode == Mode.NORMAL_NO_GRAVITY) {
            hero.velocity.y += HERO_HORIZONTAL_ACCELERATION * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) && mode == Mode.NORMAL_NO_GRAVITY) {
            hero.velocity.y -= HERO_HORIZONTAL_ACCELERATION * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP) && onTheFloor) {
            hero.velocity.y = HERO_JUMP_VELOCITY;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            hero.velocity.x += HERO_HORIZONTAL_ACCELERATION * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            hero.velocity.x -= HERO_HORIZONTAL_ACCELERATION * deltaTime;
        }

        // clamp velocity:
        hero.velocity.x = MathUtils.clamp(hero.velocity.x, -MAX_HORIZONTAL_SPEED, MAX_HORIZONTAL_SPEED);
        if (mode == Mode.NORMAL_NO_GRAVITY) {
            hero.velocity.y = MathUtils.clamp(hero.velocity.y, -MAX_HORIZONTAL_SPEED, MAX_HORIZONTAL_SPEED);
        } else {
            hero.velocity.y = MathUtils.clamp(hero.velocity.y, -MAX_VERTICAL_SPEED, MAX_VERTICAL_SPEED);
        }

        float dampingDirection = Math.signum(hero.velocity.x) * -1;

        if (Math.abs(hero.velocity.x) < HORIZONTAL_DUMPING * deltaTime) {
            hero.velocity.x = 0;
        } else {
            hero.velocity.x += dampingDirection * HORIZONTAL_DUMPING * deltaTime;
        }

        if (mode == Mode.NORMAL_NO_GRAVITY) {
            // dump vertical
            dampingDirection = Math.signum(hero.velocity.y) * -1;
            if (Math.abs(hero.velocity.y) < HORIZONTAL_DUMPING * deltaTime) {
                hero.velocity.y = 0;
            } else {
                hero.velocity.y += dampingDirection * HORIZONTAL_DUMPING * deltaTime;
            }
        }

        if (mode != Mode.NORMAL_NO_GRAVITY) {
            hero.velocity.y -= GRAVITY_ACCELERATION * deltaTime;
        }
    }
}
