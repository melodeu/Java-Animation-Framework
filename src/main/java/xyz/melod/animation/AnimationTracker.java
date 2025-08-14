/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class AnimationTracker {
    private final Set<Animatable> animations = ConcurrentHashMap.newKeySet();
    private static final float UPDATE_RATE = 1.0f / 60.0f;
    private float accumulator = 0.0f;
    private long lastFrameTime;

    public AnimationTracker() {
        this.lastFrameTime = System.nanoTime();
    }

    public void add(Animatable animatable) {
        this.animations.add(animatable);
    }

    public void remove(Animatable animatable) {
        this.animations.remove(animatable);
    }

    public void clear() {
        this.animations.clear();
        this.accumulator = 0f;
    }

    public void update() {
        var now = System.nanoTime();
        var realDeltaTime = (now - this.lastFrameTime) / 1_000_000_000.0f;
        this.lastFrameTime = now;
        this.accumulator += realDeltaTime;

        if (this.animations.isEmpty()) {
            this.accumulator = 0;
            return;
        }

        final int maxSteps = 5;
        int steps = 0;

        while (this.accumulator >= UPDATE_RATE && steps < maxSteps) {
            for (var animatable : new HashSet<>(this.animations)) {
                if (animatable.isFinished()) {
                    this.animations.remove(animatable);
                } else {
                    animatable.update(UPDATE_RATE);
                }
            }
            this.accumulator -= UPDATE_RATE;
            steps++;
        }
    }
}