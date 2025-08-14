/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.implementation.keyframe;

import xyz.melod.animation.Animation;
import xyz.melod.animation.easing.EasingFunction;
import xyz.melod.animation.easing.EasingType;

import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class KeyframeAnimation extends Animation<KeyframeAnimation> {
    private final Consumer<Float> target;
    private final NavigableMap<Float, Float> keyframes;
    private EasingFunction easing = EasingType.LINEAR;
    private final float totalDuration;
    private boolean isReversed = false;

    public KeyframeAnimation(Consumer<Float> target, NavigableMap<Float, Float> keyframes) {
        this.target = target;
        this.keyframes = new TreeMap<>(keyframes);
        this.totalDuration = this.keyframes.isEmpty() ? 0 : this.keyframes.lastKey();
        this.target.accept(this.keyframes.isEmpty() ? 0f : this.keyframes.firstEntry().getValue());
    }

    @Override
    protected void doUpdate(float timeSinceDelayed) {
        if (this.totalDuration <= 0) return;

        float progressTime = Math.min(timeSinceDelayed, this.totalDuration);
        if (this.isReversed) {
            progressTime = this.totalDuration - progressTime;
        }

        var floorEntry = this.keyframes.floorEntry(progressTime);
        var ceilingEntry = this.keyframes.ceilingEntry(progressTime);

        if (floorEntry == null) floorEntry = this.keyframes.firstEntry();
        if (ceilingEntry == null) ceilingEntry = this.keyframes.lastEntry();

        if (floorEntry.getKey().equals(ceilingEntry.getKey())) {
            this.target.accept(floorEntry.getValue());
            return;
        }

        var segmentStart = floorEntry.getKey();
        var startValue = floorEntry.getValue();
        var segmentEnd = ceilingEntry.getKey();
        var endValue = ceilingEntry.getValue();

        var segmentDuration = segmentEnd - segmentStart;
        var timeInSegment = progressTime - segmentStart;

        var progress = (double) timeInSegment / segmentDuration;
        var easedProgress = this.easing.apply(progress);
        this.target.accept((float) (startValue + (endValue - startValue) * easedProgress));
    }

    @Override
    public boolean isFinished() {
        if (finished) return true;
        return elapsedTime - delay >= totalDuration;
    }

    @Override
    protected void resetForRepeat() {
        this.elapsedTime = 0;
        if (this.yoyo) {
            this.isReversed = !this.isReversed;
        }
    }

    public KeyframeAnimation ease(EasingFunction easing) {
        this.easing = easing; return this;
    }
}