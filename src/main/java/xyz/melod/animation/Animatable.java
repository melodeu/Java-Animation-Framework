/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation;

import xyz.melod.animation.implementation.chained.ChainedAnimation;
import xyz.melod.animation.implementation.parallel.ParallelAnimation;

@FunctionalInterface
@SuppressWarnings("unused")
public interface Animatable {
    void update(long deltaNanos);

    default boolean isFinished() {
        return false;
    }

    default ChainedAnimation then(Animatable next) {
        if (this instanceof ChainedAnimation) {
            ((ChainedAnimation) this).add(next);
            return (ChainedAnimation) this;
        }
        return new ChainedAnimation(this, next);
    }

    default ParallelAnimation alongWith(Animatable other) {
        if (this instanceof ParallelAnimation) {
            ((ParallelAnimation) this).add(other);
            return (ParallelAnimation) this;
        }
        return new ParallelAnimation(this, other);
    }

    default void start() {
        Animator.getTracker().add(this);
    }
}