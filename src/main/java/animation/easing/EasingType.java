/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */
package animation.easing;

import it.unimi.dsi.fastutil.doubles.DoubleUnaryOperator;

@SuppressWarnings("unused")
public enum EasingType implements EasingFunction {
    LINEAR(x -> x),
    QUADRATIC_IN(x -> x * x),
    QUADRATIC_OUT(x -> 1 - (1 - x) * (1 - x)),
    QUADRATIC_IN_OUT(x -> x < 0.5 ? 2 * x * x : 1 - Math.pow(-2 * x + 2, 2) / 2),
    CUBIC_IN(x -> x * x * x),
    CUBIC_OUT(x -> 1 - Math.pow(1 - x, 3)),
    CUBIC_IN_OUT(x -> x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2),
    QUARTIC_IN(x -> x * x * x * x),
    QUARTIC_OUT(x -> 1 - Math.pow(1 - x, 4)),
    QUARTIC_IN_OUT(x -> x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2),
    QUINTIC_IN(x -> x * x * x * x * x),
    QUINTIC_OUT(x -> 1 - Math.pow(1 - x, 5)),
    QUINTIC_IN_OUT(x -> x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2),
    SINE_IN(x -> 1 - Math.cos((x * Math.PI) / 2)),
    SINE_OUT(x -> Math.sin((x * Math.PI) / 2)),
    SINE_IN_OUT(x -> -(Math.cos(Math.PI * x) - 1) / 2),
    EXPONENTIAL_IN(x -> x == 0 ? 0 : Math.pow(2, 10 * x - 10)),
    EXPONENTIAL_OUT(x -> x == 1 ? 1 : 1 - Math.pow(2, -10 * x)),
    EXPONENTIAL_IN_OUT(x -> x == 0 ? 0 : x == 1 ? 1 : x < 0.5 ? Math.pow(2, 20 * x - 10) / 2 : (2 - Math.pow(2, -20 * x + 10)) / 2),
    CIRCULAR_IN(x -> 1 - Math.sqrt(1 - Math.pow(x, 2))),
    CIRCULAR_OUT(x -> Math.sqrt(1 - Math.pow(x - 1, 2))),
    CIRCULAR_IN_OUT(x -> x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2),
    BACK_IN(x -> 2.70158 * x * x * x - 1.70158 * x * x),
    BACK_OUT(x -> 1 + 2.70158 * Math.pow(x - 1, 3) + 1.70158 * Math.pow(x - 1, 2)),
    BACK_IN_OUT(x -> {
        double c2 = 1.70158 * 1.525;
        return x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;
    }),
    ELASTIC_IN(x -> {
        if (x == 0) return 0; if (x == 1) return 1;
        return -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * ((2 * Math.PI) / 3));
    }),
    ELASTIC_OUT(x -> {
        if (x == 0) return 0; if (x == 1) return 1;
        return Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * ((2 * Math.PI) / 3)) + 1;
    }),
    ELASTIC_IN_OUT(x -> {
        if (x == 0) return 0; if (x == 1) return 1;
        double sin = Math.sin((20 * x - 11.125) * ((2 * Math.PI) / 4.5));
        return x < 0.5 ? -(Math.pow(2, 20 * x - 10) * sin) / 2 : (Math.pow(2, -20 * x + 10) * sin) / 2 + 1;
    }),
    BOUNCE_OUT(x -> {
        if (x < 1 / 2.75) return 7.5625 * x * x;
        if (x < 2 / 2.75) return 7.5625 * (x -= 1.5 / 2.75) * x + 0.75;
        if (x < 2.5 / 2.75) return 7.5625 * (x -= 2.25 / 2.75) * x + 0.9375;
        return 7.5625 * (x -= 2.625 / 2.75) * x + 0.984375;
    }),
    BOUNCE_IN(x -> 1 - BOUNCE_OUT.apply(1 - x)),
    BOUNCE_IN_OUT(x -> x < 0.5 ? (1 - BOUNCE_OUT.apply(1 - 2 * x)) / 2 : (1 + BOUNCE_OUT.apply(2 * x - 1)) / 2);

    private final DoubleUnaryOperator function;

    EasingType(DoubleUnaryOperator function) {
        this.function = function;
    }

    @Override
    public double apply(double progress) {
        return this.function.apply(progress);
    }
}