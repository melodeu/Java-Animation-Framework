/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.util;

@FunctionalInterface
public interface TypeInterpolator<T> {
    T interpolate(T startValue, T endValue, float progress);
}