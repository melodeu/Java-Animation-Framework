/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.implementation.tween;

import animation.Animation;
import animation.easing.EasingFunction;
import animation.easing.EasingType;
import animation.util.TypeInterpolator;
import java.util.function.Consumer;

@SuppressWarnings({"unused", "FieldMayBeFinal"})
public class Tween<T> extends Animation<Tween<T>> {
    private final Consumer<T> target;
    private final TypeInterpolator<T> interpolator;

    private T fromValue;
    private T toValue;
    private float duration;
    private EasingFunction easing = EasingType.LINEAR;
    private boolean isReversed = false;

    public Tween(Consumer<T> target, TypeInterpolator<T> interpolator, T from, T to, float duration) {
        this.target = target;
        this.interpolator = interpolator;
        this.fromValue = from;
        this.toValue = to;
        this.duration = duration;
        this.target.accept(from);
    }

    @Override
    protected void doUpdate(float timeSinceDelayed) {
        if (duration <= 0) {
            target.accept(toValue);
            return;
        }

        float progress = Math.min(1.0f, timeSinceDelayed / duration);
        double easedProgress = easing.apply(isReversed ? 1.0 - progress : progress);

        T currentValue = interpolator.interpolate(fromValue, toValue, (float) easedProgress);
        target.accept(currentValue);
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return elapsedTime - delay >= duration;
    }

    @Override
    protected void resetForRepeat() {
        this.elapsedTime = 0;
        if (this.yoyo) {
            this.isReversed = !this.isReversed;
        }
    }

    public Tween<T> ease(EasingFunction easingFunction) {
        this.easing = easingFunction; return this;
    }
}