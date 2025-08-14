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
import java.time.Duration;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class KeyframeAnimation extends Animation<KeyframeAnimation> {
    private final Consumer<Float> target;
    private final NavigableMap<Long, Float> keyframesNanos;
    private EasingFunction easing = EasingType.LINEAR;
    private final long totalDurationNanos;
    private boolean isReversed = false;

    public KeyframeAnimation(Consumer<Float> target, NavigableMap<Duration, Float> keyframes) {
        this.target = target;
        this.keyframesNanos = keyframes.entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toNanos(),
                        Map.Entry::getValue,
                        (v1, v2) -> v2,
                        TreeMap::new
                ));

        this.totalDurationNanos = this.keyframesNanos.isEmpty() ? 0 : this.keyframesNanos.lastKey();
        this.target.accept(this.keyframesNanos.isEmpty() ? 0f : this.keyframesNanos.firstEntry().getValue());
    }

    @Override
    protected void doUpdate(long timeSinceDelayedNanos) {
        if (this.totalDurationNanos <= 0) return;

        long progressTime = Math.min(timeSinceDelayedNanos, this.totalDurationNanos);
        if (this.isReversed) {
            progressTime = this.totalDurationNanos - progressTime;
        }

        var floorEntry = this.keyframesNanos.floorEntry(progressTime);
        var ceilingEntry = this.keyframesNanos.ceilingEntry(progressTime);

        if (floorEntry == null) floorEntry = this.keyframesNanos.firstEntry();
        if (ceilingEntry == null) ceilingEntry = this.keyframesNanos.lastEntry();

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
        return elapsedNanos - delayNanos >= totalDurationNanos;
    }

    @Override
    protected void resetForRepeat() {
        super.resetForRepeat();
        if (this.yoyo) {
            this.isReversed = !this.isReversed;
        }
    }

    public KeyframeAnimation ease(EasingFunction easing) {
        this.easing = easing; return this;
    }
}