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
    
    public static DelayAnimation delay(float seconds) {
        return new DelayAnimation(seconds);
    }
    
    public static <T> Tween<T> tween(Consumer<T> target, TypeInterpolator<T> interpolator, T from, T to, float duration) {
        return new Tween<>(target, interpolator, from, to, duration);
    }
    
    public static Tween<Float> tweenFloat(Consumer<Float> target, float from, float to, float duration) {
        return new Tween<>(target, Interpolators.FLOAT, from, to, duration);
    }
    
    public static Spring spring(Consumer<Float> target, float initialValue, float stiffness, float damping) {
        return new Spring(target, initialValue, stiffness, damping);
    }

    public static KeyframeAnimation keyframe(Consumer<Float> target, NavigableMap<Float, Float> keyframes) {
        return new KeyframeAnimation(target, keyframes);
    }
    
    public static MotionProfileAnimation motionProfile(Consumer<Float> target, float startValue, float endValue, float maxVelocity, float maxAcceleration) {
        return new MotionProfileAnimation(target, startValue, endValue, maxVelocity, maxAcceleration);
    }

    public static ParallelAnimation stagger(float offsetSeconds, Animatable... animations) {
        if (offsetSeconds <= 0) {
            return new ParallelAnimation(animations);
        }

        Animatable[] staggeredAnimations = new Animatable[animations.length];

        for (int i = 0; i < animations.length; i++) {
            float staggerDelay = i * offsetSeconds;

            if (staggerDelay > 0) {
                staggeredAnimations[i] = new DelayAnimation(staggerDelay).then(animations[i]);
            } else {
                staggeredAnimations[i] = animations[i];
            }
        }

        return new ParallelAnimation(staggeredAnimations);
    }
}