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
    private static final long UPDATE_INTERVAL_NANOS = 1_000_000_000L / 60;
    private long accumulatorNanos = 0L;
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
        this.accumulatorNanos = 0L;
    }

    public void update() {
        var now = System.nanoTime();
        var realDeltaNanos = now - this.lastFrameTime;
        this.lastFrameTime = now;
        this.accumulatorNanos += realDeltaNanos;

        if (this.animations.isEmpty()) {
            this.accumulatorNanos = 0;
            return;
        }

        final int maxSteps = 5;
        int steps = 0;

        while (this.accumulatorNanos >= UPDATE_INTERVAL_NANOS && steps < maxSteps) {
            for (var animatable : new HashSet<>(this.animations)) {
                if (animatable.isFinished()) {
                    this.animations.remove(animatable);
                } else {
                    animatable.update(UPDATE_INTERVAL_NANOS);
                }
            }
            this.accumulatorNanos -= UPDATE_INTERVAL_NANOS;
            steps++;
        }
    }
}