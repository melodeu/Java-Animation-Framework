/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.implementation.delay;

import animation.Animatable;

@SuppressWarnings("unused")
public class DelayAnimation implements Animatable {
    private float duration;
    
    public DelayAnimation(float seconds) {
        this.duration = seconds;
    }

    @Override
    public void update(float deltaTime) {
        if (this.duration > 0) {
            this.duration -= deltaTime;
        }
    }

    @Override
    public boolean isFinished() {
        return this.duration <= 0;
    }
}