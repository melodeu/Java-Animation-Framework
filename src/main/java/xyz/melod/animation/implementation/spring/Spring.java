/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.spring;

import xyz.melod.animation.Animation;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class Spring extends Animation<Spring> {
    private final Consumer<Float> targetSetter;
    private float value;
    private float targetValue;
    private float velocity;
    private float stiffness;
    private float damping;
    private static final float TOLERANCE = 0.001f;
    private long lastTimeNanos = 0L;

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

    public void setStiffness(float stiffness) {
        this.stiffness = stiffness;
    }

    public void setDamping(float damping) {
        this.damping = damping;
    }

    @Override
    protected void doUpdate(long timeSinceDelayedNanos) {
        float dt = (timeSinceDelayedNanos - lastTimeNanos) / 1_000_000_000.0f;
        lastTimeNanos = timeSinceDelayedNanos;

        float force = this.stiffness * (this.targetValue - this.value);
        float dampingForce = this.damping * this.velocity;
        float acceleration = force - dampingForce;

        this.velocity += acceleration * dt;
        this.value += this.velocity * dt;

        this.targetSetter.accept(this.value);
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return Math.abs(this.targetValue - this.value) < TOLERANCE && Math.abs(this.velocity) < TOLERANCE;
    }

    @Override
    protected void resetForRepeat() {
        super.resetForRepeat();
        this.lastTimeNanos = 0L;
    }
}