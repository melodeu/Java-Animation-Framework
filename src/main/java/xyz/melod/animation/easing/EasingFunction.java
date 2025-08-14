/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.easing;

@FunctionalInterface
public interface EasingFunction {
    double apply(double progress);
}