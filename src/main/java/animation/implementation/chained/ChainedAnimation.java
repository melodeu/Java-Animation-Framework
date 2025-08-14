/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.implementation.chained;

import animation.Animatable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class ChainedAnimation implements Animatable {
    private final Queue<Animatable> animations;

    public ChainedAnimation(Animatable... animations) {
        this.animations = new LinkedList<>(Arrays.asList(animations));
    }

    public void add(Animatable animation) {
        this.animations.add(animation);
    }

    @Override
    public void update(float deltaTime) {
        if (isFinished()) {
            return;
        }

        var currentAnimation = this.animations.peek();

        if (currentAnimation != null) {
            currentAnimation.update(deltaTime);
            if (currentAnimation.isFinished()) {
                this.animations.poll();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return this.animations.isEmpty();
    }
}