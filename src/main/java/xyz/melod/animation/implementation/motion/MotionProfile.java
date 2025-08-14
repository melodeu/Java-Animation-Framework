/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.motion;

@SuppressWarnings("unused")
public class MotionProfile {
    private final float distance;
    private final float maxVelocity;
    private final float maxAcceleration;
    private final float timeToAccelerate;
    private final float accelerationDistance;
    private final float constantVelocityTime;
    private final float totalDuration;

    public float getTotalDuration() {
        return totalDuration;
    }

    public MotionProfile(float distance, float maxVelocity, float maxAcceleration) {
        this.distance = Math.abs(distance);
        this.maxVelocity = Math.abs(maxVelocity);
        this.maxAcceleration = Math.abs(maxAcceleration);

        var timeToReachMaxV = this.maxVelocity / this.maxAcceleration;
        var distanceToAccelerateAndDecelerate = this.maxAcceleration * timeToReachMaxV * timeToReachMaxV;

        if (distanceToAccelerateAndDecelerate > this.distance) {
            this.timeToAccelerate = (float) Math.sqrt(this.distance / this.maxAcceleration);
            this.accelerationDistance = this.distance / 2.0f;
            this.constantVelocityTime = 0;
            this.totalDuration = 2 * this.timeToAccelerate;
        } else {
            this.timeToAccelerate = timeToReachMaxV;
            this.accelerationDistance = 0.5f * this.maxAcceleration * this.timeToAccelerate * this.timeToAccelerate;
            var constantVelocityDistance = this.distance - 2 * this.accelerationDistance;
            this.constantVelocityTime = constantVelocityDistance / this.maxVelocity;
            this.totalDuration = 2 * this.timeToAccelerate + this.constantVelocityTime;
        }
    }

    public float getPosition(float time) {
        if (time <= 0) return 0;
        if (time >= this.totalDuration) return this.distance;

        if (time < this.timeToAccelerate) {
            return 0.5f * this.maxAcceleration * time * time;
        }

        var timeAfterAcceleration = time - this.timeToAccelerate;
        if (timeAfterAcceleration < this.constantVelocityTime) {
            return this.accelerationDistance + this.maxVelocity * timeAfterAcceleration;
        }

        var timeInDeceleration = timeAfterAcceleration - this.constantVelocityTime;
        var vAtDecelerationStart = this.maxAcceleration * this.timeToAccelerate;

        return this.accelerationDistance + (this.maxVelocity * this.constantVelocityTime)
                + (vAtDecelerationStart * timeInDeceleration - 0.5f * this.maxAcceleration * timeInDeceleration * timeInDeceleration);
    }
}