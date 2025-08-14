/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation;

import animation.implementation.chained.ChainedAnimation;
import animation.implementation.parallel.ParallelAnimation;

@FunctionalInterface
@SuppressWarnings("unused")
public interface Animatable {
    void update(float deltaTime);

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