/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */

import xyz.melod.animation.Animator;
import xyz.melod.animation.easing.EasingType;
import xyz.melod.animation.implementation.spring.Spring;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.Duration;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Start {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Animation Library Test");
        AnimationPanel panel = new AnimationPanel();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        
        var moveX = Animator.tweenFloat(panel::setRectX, 100f, 700f, Duration.ofSeconds(2))
            .ease(EasingType.SINE_IN_OUT)
            .yoyo(true)
            .repeatForever();

        var moveY = Animator.motionProfile(panel::setRectY, 100f, 500f, 400f, 250f)
            .yoyo(true)
            .repeatForever();

        NavigableMap<Duration, Float> rotationKeyframes = new TreeMap<>();

        rotationKeyframes.put(Duration.ofSeconds(0), 0f);
        rotationKeyframes.put(Duration.ofSeconds(1), -90f);
        rotationKeyframes.put(Duration.ofSeconds(2), 180f);
        rotationKeyframes.put(Duration.ofSeconds(3), 360f);

        var rotate = Animator.keyframe(panel::setRectRotation, rotationKeyframes)
                .ease(EasingType.CUBIC_IN_OUT)
                .yoyo(true)
                .repeatForever();

        Spring springSize = Animator.spring(panel::setRectSize, panel.getRectSize(), 20, 5);
        springSize.start();

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                springSize.setTarget(panel.getRectSize() < 75 ? 100f : 50f);
            }
        });

        var masterAnimation = Animator.delay(Duration.ofSeconds(1))
                .then(moveX.alongWith(moveY).alongWith(rotate));
        
        masterAnimation.start();
        
        frame.setVisible(true);

        Timer timer = new Timer(1000 / 60, e -> {
            Animator.update();
            panel.repaint();
        });

        timer.start();
    }
}