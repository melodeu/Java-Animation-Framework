/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.implementation.parallel;

import animation.Animatable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class ParallelAnimation implements Animatable {
    private final Set<Animatable> animations;

    public ParallelAnimation(Animatable... animations) {
        this.animations = new HashSet<>(Arrays.asList(animations));
    }

    public void add(Animatable animation) {
        this.animations.add(animation);
    }

    @Override
    public void update(float deltaTime) {
        for (var animation : this.animations) {
            if (!animation.isFinished()) {
                animation.update(deltaTime);
            }
        }
    }

    @Override
    public boolean isFinished() {
        return this.animations.stream().allMatch(Animatable::isFinished);
    }
}