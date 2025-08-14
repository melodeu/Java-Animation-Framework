/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.implementation.spring;

import animation.Animation;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Spring extends Animation<Spring> {
    private final Consumer<Float> targetSetter;
    private float value;
    private float targetValue;
    private float velocity;
    private final float stiffness;
    private final float damping;
    private static final float TOLERANCE = 0.001f;

    public Spring(Consumer<Float> targetSetter, float initialValue, float stiffness, float damping) {
        this.targetSetter = targetSetter;
        this.value = initialValue;
        this.targetValue = initialValue;
        this.stiffness = stiffness;
        this.damping = damping;
    }

    public void setTarget(float target) {
        this.targetValue = target;
        this.finished = false;
    }

    @Override
    protected void doUpdate(float timeSinceDelayed) {
        var force = this.stiffness * (this.targetValue - this.value);
        var dampingForce = this.damping * this.velocity;
        var acceleration = force - dampingForce;

        this.velocity += acceleration * (1.0f / 60.0f);
        this.value += this.velocity * (1.0f / 60.0f);

        this.targetSetter.accept(this.value);
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return Math.abs(this.targetValue - this.value) < TOLERANCE && Math.abs(this.velocity) < TOLERANCE;
    }

    @Override
    protected void resetForRepeat() {
        this.elapsedTime = 0;
    }
}