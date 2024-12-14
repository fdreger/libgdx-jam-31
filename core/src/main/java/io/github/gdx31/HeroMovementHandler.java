package io.github.gdx31;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;

public class HeroMovementHandler {

    public static final float HERO_HORIZONTAL_ACCELERATION = 2000;
    public static final float MAX_HORIZONTAL_SPEED = 100;
    public static final float MAX_VERTICAL_SPEED = 200;
    public static final float GRAVITY_ACCELERATION = 100f;
    public static final float HERO_JUMP_VELOCITY = GRAVITY_ACCELERATION;
    public static final float HORIZONTAL_DUMPING = 900f;

    public static void handleHeroMovement(GameObject hero, boolean onTheFloor, float deltaTime) {

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
        hero.velocity.y = MathUtils.clamp(hero.velocity.y, -MAX_VERTICAL_SPEED, MAX_VERTICAL_SPEED);

        float dampingDirection = Math.signum(hero.velocity.x) * -1;

        if (Math.abs(hero.velocity.x) < HORIZONTAL_DUMPING * deltaTime) {
            hero.velocity.x = 0;
        } else {
            hero.velocity.x += dampingDirection * HORIZONTAL_DUMPING * deltaTime;
        }

        hero.velocity.y -= GRAVITY_ACCELERATION * deltaTime;
    }
}
