/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.delay;

import xyz.melod.animation.Animatable;
import java.time.Duration;

@SuppressWarnings("unused")
public class DelayAnimation implements Animatable {
    private long durationNanos;

    public DelayAnimation(Duration duration) {
        this.durationNanos = duration.toNanos();
    }

    @Override
    public void update(long deltaNanos) {
        if (this.durationNanos > 0) {
            this.durationNanos -= deltaNanos;
        }
    }

    @Override
    public boolean isFinished() {
        return this.durationNanos <= 0;
    }
}