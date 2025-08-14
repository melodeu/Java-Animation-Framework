/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation;

import java.time.Duration;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class Animation<T extends Animation<T>> implements Animatable {
    protected long delayNanos;
    protected int repeatCount = 0;
    protected int executions = 0;
    protected boolean yoyo = false;
    protected boolean finished = false;
    protected long elapsedNanos;
    private boolean started = false;

    protected Runnable onStart;
    protected Runnable onComplete;
    protected Runnable onRepeat;
    protected Consumer<Animatable> onUpdate;

    @Override
    public final void update(long deltaNanos) {
        if (finished) return;

        elapsedNanos += deltaNanos;

        if (!started && elapsedNanos < delayNanos) {
            return;
        }

        if (!started) {
            fireOnStart();
            started = true;
        }

        doUpdate(elapsedNanos - delayNanos);
        fireOnUpdate();

        if (isFinished()) {
            executions++;
            if (repeatCount != -1 && executions > repeatCount) {
                finished = true;
                fireOnComplete();
            } else {
                fireOnRepeat();
                resetForRepeat();
            }
        }
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    protected abstract void doUpdate(long timeSinceDelayedNanos);

    protected void resetForRepeat() {
        this.elapsedNanos = 0;
    }

    private void fireOnStart() { if (onStart != null) onStart.run(); }
    private void fireOnComplete() { if (onComplete != null) onComplete.run(); }
    private void fireOnRepeat() { if (onRepeat != null) onRepeat.run(); }
    private void fireOnUpdate() { if (onUpdate != null) onUpdate.accept(this); }

    @SuppressWarnings("unchecked")
    private T self() { return (T) this; }

    @SuppressWarnings("UnusedReturnValue")
    public T delay(Duration duration) { this.delayNanos = duration.toNanos(); return self(); }
    public T yoyo(boolean enabled) { this.yoyo = enabled; return self(); }
    public T repeat(int count) { this.repeatCount = count; return self(); }
    public T repeatForever() { this.repeatCount = -1; return self(); }

    public T onStart(Runnable action) { this.onStart = action; return self(); }
    public T onComplete(Runnable action) { this.onComplete = action; return self(); }
    public T onRepeat(Runnable action) { this.onRepeat = action; return self(); }
    public T onUpdate(Consumer<Animatable> action) { this.onUpdate = action; return self(); }
}