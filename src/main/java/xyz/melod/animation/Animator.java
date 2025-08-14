/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation;

import xyz.melod.animation.implementation.delay.DelayAnimation;
import xyz.melod.animation.implementation.keyframe.KeyframeAnimation;
import xyz.melod.animation.implementation.motion.MotionProfileAnimation;
import xyz.melod.animation.implementation.parallel.ParallelAnimation;
import xyz.melod.animation.implementation.spring.Spring;
import xyz.melod.animation.implementation.tween.Tween;
import xyz.melod.animation.util.Interpolators;
import xyz.melod.animation.util.TypeInterpolator;
import java.time.Duration;
import java.util.NavigableMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public final class Animator {
    private static final AnimationTracker tracker = new AnimationTracker();

    public static AnimationTracker getTracker() {
        return tracker;
    }

    public static void update() {
        tracker.update();
    }

    public static DelayAnimation delay(Duration duration) {
        return new DelayAnimation(duration);
    }

    public static <T> Tween<T> tween(Consumer<T> target, TypeInterpolator<T> interpolator, T from, T to, Duration duration) {
        return new Tween<>(target, interpolator, from, to, duration);
    }

    public static Tween<Float> tweenFloat(Consumer<Float> target, float from, float to, Duration duration) {
        return new Tween<>(target, Interpolators.FLOAT, from, to, duration);
    }

    public static Spring spring(Consumer<Float> target, float initialValue, float stiffness, float damping) {
        return new Spring(target, initialValue, stiffness, damping);
    }

    public static KeyframeAnimation keyframe(Consumer<Float> target, NavigableMap<Duration, Float> keyframes) {
        return new KeyframeAnimation(target, keyframes);
    }

    public static MotionProfileAnimation motionProfile(Consumer<Float> target, float startValue, float endValue, float maxVelocity, float maxAcceleration) {
        return new MotionProfileAnimation(target, startValue, endValue, maxVelocity, maxAcceleration);
    }

    public static ParallelAnimation stagger(Duration offset, Animatable... animations) {
        if (offset.isNegative() || offset.isZero()) {
            return new ParallelAnimation(animations);
        }

        for (int i = 0; i < animations.length; i++) {
            Duration staggerDelay = offset.multipliedBy(i);
            if (staggerDelay.isNegative() || staggerDelay.isZero()) continue;

            Animatable anim = animations[i];
            if (anim instanceof Animation) {
                ((Animation<?>) anim).delay(staggerDelay);
            } else {
                animations[i] = new DelayAnimation(staggerDelay).then(anim);
            }
        }

        return new ParallelAnimation(animations);
    }
}