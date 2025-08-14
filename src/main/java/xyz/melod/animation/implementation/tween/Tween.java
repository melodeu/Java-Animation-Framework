/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.tween;

import xyz.melod.animation.Animation;
import xyz.melod.animation.easing.EasingFunction;
import xyz.melod.animation.easing.EasingType;
import xyz.melod.animation.util.TypeInterpolator;
import java.time.Duration;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class Tween<T> extends Animation<Tween<T>> {
    private final Consumer<T> target;
    private final TypeInterpolator<T> interpolator;

    private T fromValue;
    private T toValue;
    private long durationNanos;
    private EasingFunction easing = EasingType.LINEAR;
    private boolean isReversed = false;

    public Tween(Consumer<T> target, TypeInterpolator<T> interpolator, T from, T to, Duration duration) {
        this.target = target;
        this.interpolator = interpolator;
        this.fromValue = from;
        this.toValue = to;
        this.durationNanos = duration.toNanos();
        this.target.accept(from);
    }

    @Override
    protected void doUpdate(long timeSinceDelayedNanos) {
        if (durationNanos <= 0) {
            target.accept(toValue);
            return;
        }

        float progress = Math.min(1.0f, (float) timeSinceDelayedNanos / durationNanos);
        double easedProgress = easing.apply(isReversed ? 1.0 - progress : progress);

        T currentValue = interpolator.interpolate(fromValue, toValue, (float) easedProgress);
        target.accept(currentValue);
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return elapsedNanos - delayNanos >= durationNanos;
    }

    @Override
    protected void resetForRepeat() {
        super.resetForRepeat();
        if (this.yoyo) {
            this.isReversed = !this.isReversed;
        }
    }

    public Tween<T> ease(EasingFunction easingFunction) {
        this.easing = easingFunction; return this;
    }
}