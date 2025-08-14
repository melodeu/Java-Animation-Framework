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

@SuppressWarnings("unused")
public class AnimationTracker {
    private final Set<Animatable> animatables = new HashSet<>();
    private static final float UPDATE_RATE = 1.0f / 60.0f;
    private float accumulator = 0.0f;
    private long lastFrameTime;

    public AnimationTracker() {
        this.lastFrameTime = System.nanoTime();
    }

    public void add(Animatable animatable) {
        this.animatables.add(animatable);
    }

    public void remove(Animatable animatable) {
        this.animatables.remove(animatable);
    }

    public void update() {
        var now = System.nanoTime();
        var realDeltaTime = (now - this.lastFrameTime) / 1_000_000_000.0f;
        this.lastFrameTime = now;
        this.accumulator += realDeltaTime;

        if (this.animatables.isEmpty()) {
            this.accumulator = 0;
            return;
        }

        while (this.accumulator >= UPDATE_RATE) {
            var animationsToUpdate = new HashSet<>(this.animatables);
            for (var animatable : animationsToUpdate) {
                if (!animatable.isFinished()) {
                    animatable.update(UPDATE_RATE);
                }
            }
            this.accumulator -= UPDATE_RATE;
        }
        this.animatables.removeIf(Animatable::isFinished);
    }
}