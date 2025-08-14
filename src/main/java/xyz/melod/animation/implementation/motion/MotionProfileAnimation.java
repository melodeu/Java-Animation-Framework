/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.motion;

import xyz.melod.animation.Animation;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MotionProfileAnimation extends Animation<MotionProfileAnimation> {
    private final Consumer<Float> target;
    private final float startValue;
    private final float endValue;
    private final MotionProfile profile;
    private boolean isReversed = false;
    private final long totalDurationNanos;

    public MotionProfileAnimation(Consumer<Float> target, float startValue, float endValue, float maxVelocity, float maxAcceleration) {
        this.target = target;
        this.startValue = startValue;
        this.endValue = endValue;
        this.profile = new MotionProfile(Math.abs(endValue - startValue), maxVelocity, maxAcceleration);
        this.totalDurationNanos = (long) (this.profile.getTotalDuration() * 1_000_000_000L);
        this.target.accept(startValue);
    }

    @Override
    protected void doUpdate(long timeSinceDelayedNanos) {
        float timeInSeconds = timeSinceDelayedNanos / 1_000_000_000.0f;
        float progressTime = Math.min(timeInSeconds, profile.getTotalDuration());

        if (this.isReversed) {
            progressTime = profile.getTotalDuration() - progressTime;
        }

        var positionDelta = this.profile.getPosition(progressTime);
        var direction = Math.signum(this.endValue - this.startValue);
        this.target.accept(this.startValue + (positionDelta * direction));
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return elapsedNanos - delayNanos >= totalDurationNanos;
    }

    @Override
    protected void resetForRepeat() {
        super.resetForRepeat();
        if (this.yoyo) {
            this.isReversed = !this.isReversed;
        }
    }
}