/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package xyz.melod.animation.util;

@SuppressWarnings("unused")
public final class Interpolators {
    public static final TypeInterpolator<Float> FLOAT = 
        (start, end, progress) -> start + (end - start) * progress;
}