# IntBuffer Animation Framework Documentation

This document provides a guide to the animation framework, its features, and API.

---

**Note:** This library was made for **Java 21**.
**Note:** This library requires the following dependency:
```xml
<dependency>
    <groupId>it.unimi.dsi</groupId>
    <artifactId>fastutil</artifactId>
    <version>8.5.16</version>
</dependency>
```

---

## Table of Contents

- [Overview](#1-overview)  
- [Core Architecture](#2-core-architecture)  
  - [The Animator Hub](#21-the-animator-hub)  
  - [The Animatable Contract](#22-the-animatable-contract)  
  - [The Animationt-base](#23-the-animationt-base)  
- [Getting Started](#3-getting-started)  
  - [Integration](#31-integration)  
  - [Creating a Basic Animation](#32-creating-a-basic-animation)  
- [Animation Types](#4-animation-types)  
  - [Tween: Time-based Interpolation](#41-tween-time-based-interpolation)  
  - [Spring: Physics-based Motion](#42-spring-physics-based-motion)  
  - [KeyframeAnimation: Multi-step Sequences](#43-keyframeanimation-multi-step-sequences)  
  - [MotionProfileAnimation: Physically Plausible Movement](#44-motionprofileanimation-physically-plausible-movement)  
- [Fluent API & Lifecycle Control](#5-fluent-api--lifecycle-control)  
  - [Execution Control](#51-execution-control)  
  - [Lifecycle Callbacks](#52-lifecycle-callbacks)  
- [Composition](#6-composition)  
  - [Sequential Execution](#61-sequential-execution-then)  
  - [Parallel Execution](#62-parallel-execution-alongwith)  
  - [Staggered Execution](#63-staggered-execution)  
- [Advanced Usage](#7-advanced-usage)  
  - [Animating Custom Data Types](#71-animating-custom-data-types)  
  - [Easing Functions](#72-easing-functions)  
- [API Quick Reference](#8-api-quick-reference)  

---

## 1. Overview

This framework is an extensible system designed for creating complex UI and object animations.

### Core Principles
- **Fluent Interface** – Chainable API for declarative, readable animation sequences.  
- **Decoupled & Extensible** – Interfaces and base classes allow easy custom animation types.  
- **Generic by Design** – Works with any data type, from floats to vectors and colors.  

---

## 2. Core Architecture

### 2.1. The Animator Hub
`Animator` is a static class serving as the central hub.

Responsibilities:
- **State Management** – Owns the master `AnimationTracker`.  
- **Animation Factory** – Provides factory methods (`tweenFloat`, `spring`, etc.).  
- **Global Clock** – `Animator.update()` drives all animations each frame.  

---

### 2.2. The Animatable Contract
`Animatable` is the interface for any object managed by the tracker.  
Guarantees:
- Can be updated over time.  
- Reports completion status.  

---

### 2.3. The Animation<T> Base
Abstract base class implementing `Animatable`.  
Provides:
- Delay, repeat, and yoyo support.  
- Lifecycle callbacks.  
- Fluent configuration API.  

---

## 3. Getting Started

### 3.1. Integration

Call `Animator.update()` once per frame in your main update loop:

```java
public void onApplicationTick(float deltaTime) {
    // Other application logic...
    Animator.update();
    // Rendering logic...
}
```

### 3.2. Creating a Basic Animation

Animations are created using the factory methods on the `Animator` class.  
The first argument is always a `Consumer` or method reference that acts as the target, which the animation will update each frame.

**Example:** Animate an object's X-position from `0` to `500` pixels over `1.5` seconds.
```java
// Assumes 'myGameObject' has a 'setX(float x)' method.
Animator.tweenFloat(myGameObject::setX, 0f, 500f, Duration.ofMillis(1500))
    .ease(EasingType.CUBIC_OUT) // Apply an easing function for non-linear motion.
    .start();                  // Register the animation with the tracker to begin.
```

## 4. Animation Types

### 4.1. Tween: Time-based Interpolation
A **Tween** (from *"in-betweening"*) is the most fundamental animation type.  
It interpolates a value between a start and end point over a specified duration.

**Use Case:** Fading, scaling, moving objects over a predictable period.

**Signature:**
```java
Animator.tweenFloat(Consumer<Float> target, float from, float to, Duration duration)
```

### 4.2. Spring: Physics-based Motion
A **Spring** simulation produces fluid, physically-based motion.  
Its behavior is governed not by duration, but by physical properties:

- **Stiffness:** The strength of the spring. Higher values result in faster, more forceful movement.  
- **Damping:** The resistance or friction applied. Higher values cause the spring to settle more quickly, while lower values allow for more oscillation and "bounciness."

The `Spring` object is stateful. You typically create it once and then update its goal using the `.setTarget()` method.

**Use Case:** Creating responsive, interruptible UI animations that feel natural and dynamic.

**Signature:**
```java
Animator.spring(Consumer<Float> target, float initialValue, float stiffness, float damping)
```
**Example:**
```java
// In object initialization:
Spring xPositionSpring = Animator.spring(this::setX, 0f, 120f, 15f);
xPositionSpring.start();

// Later, to trigger motion:
public void onMouseEnter() {
    xPositionSpring.setTarget(100f); // The object will spring towards the new target.
}
```

### 4.3. KeyframeAnimation: Multi-step Sequences
Allows for the definition of complex animation paths through a series of waypoints.  
The animation proceeds by interpolating between each successive keyframe.

**Use Case:** Creating complex, non-linear animation paths, such as a blinking icon or a character's multi-part attack sequence.

**Signature:**
```java
Animator.keyframe(Consumer<Float> target, NavigableMap<Duration, Float> keyframes)
```

### 4.4. MotionProfileAnimation: Physically Plausible Movement
This animation type generates motion that adheres to specified **maximum velocity** and **acceleration** constraints.  
It produces a trapezoidal velocity profile: the object smoothly accelerates, maintains a constant velocity, and then smoothly decelerates.

**Use Case:** Moving UI panels, characters, or camera systems in a way that feels realistic and grounded, avoiding jarring starts or stops.

**Signature:**
```java
Animator.motionProfile(Consumer<Float> target, float startValue, float endValue, float maxVelocity, float maxAcceleration)
```

## 5. Fluent API & Lifecycle Control
The `Animation<T>` base class provides a powerful, chainable API for configuring animation behavior.

### 5.1. Execution Control
- **`.delay(float seconds)`**: Specifies a wait time before the animation begins.  
- **`.repeat(int count)`**: Sets the number of times the animation should execute. Use `-1` for infinite repetition.  
- **`.yoyo(boolean enabled)`**: If `true`, the animation will play in reverse on every other cycle during a repeat sequence.

### 5.2. Lifecycle Callbacks
These methods allow you to inject custom logic at specific points in an animation's lifecycle:

- **`.onStart(Runnable action)`**: Called once when the animation begins its execution (after any delay).  
- **`.onUpdate(Consumer<Animatable> action)`**: Called every frame the animation is active.  
- **`.onRepeat(Runnable action)`**: Called at the end of each cycle in a repeating animation.  
- **`.onComplete(Runnable action)`**: Called once after the animation and all its repetitions have fully concluded.

**Example:** Fade in a button, wait, and then pulse it forever.
```java
Animator.tweenFloat(button::setAlpha, 0f, 1f, Duration.ofMillis(500))
    .onComplete(() -> {
        Animator.tweenFloat(button::setScale, 1f, 1.1f, Duration.ofMillis(300))
            .yoyo(true)
            .repeatForever()
            .start();
    })
    .start();
```

## 6. Composition
Animations can be composed into larger, more complex sequences.

### 6.1. Sequential Execution (`.then()`)
The `.then()` method chains animations to run one after another.  
It implicitly creates a `ChainedAnimation` sequence.

**Example: Move right, then scale up.**
```java
Animator.tweenFloat(obj::setX, 0, 100, Duration.ofSeconds(1))
    .then(Animator.tweenFloat(obj::setScale, 1f, 1.5f, Duration.ofMillis(500)))
    .start();
```

### 6.2. Parallel Execution (`.alongWith()`)
The `.alongWith()` method runs animations concurrently.  
It implicitly creates a `ParallelAnimation` group.

**Example: Move right and fade out at the same time.**
```java
Animator.tweenFloat(obj::setX, 0, 100, Duration.ofSeconds(1))
    .alongWith(Animator.tweenFloat(obj::setAlpha, 1f, 0f, Duration.ofSeconds(1)))
    .start();
```

### 6.3. Staggered Execution
The `Animator.stagger()` utility runs a group of animations in parallel,  
but applies an incremental delay to each subsequent animation.

**Example: Animate a list of items into view with a cascading effect.**
```java
Animator.stagger(Duration.ofMillis(50), // 50ms delay between each animation
    Animator.tweenFloat(item1::setAlpha, 0, 1, Duration.ofMillis(400)),
    Animator.tweenFloat(item2::setAlpha, 0, 1, Duration.ofMillis(400)),
    Animator.tweenFloat(item3::setAlpha, 0, 1, Duration.ofMillis(400))
).start();
```

## 7. Advanced Usage

### 7.1. Animating Custom Data Types
The framework's generic design allows for the animation of any data type.  
To enable this, you must provide a `TypeInterpolator<T>`. This is a functional interface that teaches the system how to interpolate between two instances of your custom type.

**Step 1: Implement the TypeInterpolator**
```java
// For a custom 'Vector2D' class
TypeInterpolator<Vector2D> vectorInterpolator = (start, end, progress) -> {
    float newX = start.x() + (end.x() - start.x()) * progress;
    float newY = start.y() + (end.y() - start.y()) * progress;
    return new Vector2D(newX, newY);
};
```

**Step 2: Use the Generic `Animator.tween()` Factory**
```java
Vector2D startPosition = new Vector2D(0, 0);
Vector2D endPosition = new Vector2D(100, 200);

Animator.tween(myObject::setPosition, vectorInterpolator, startPosition, endPosition, Duration.ofSeconds(1))
    .start();
```

### 7.2. Easing Functions
Easing modifies the rate of an animation to produce more natural-looking motion.  
Apply an easing function via the `.ease()` method. The `EasingType` enum provides a large collection of industry-standard functions (e.g., `SINE_IN_OUT`, `ELASTIC_OUT`).

## 8. API Quick Reference

### Animator
- **`update()`**: Global clock tick.  
- **`tweenFloat(...)`, `spring(...)`, `keyframe(...)`, `motionProfile(...)`**: Core animation factories.  
- **`stagger(...)`**: Composition utility for creating cascade effects.  

### Animation<T> Methods
- **`delay(Duration)`**: Pre-animation wait time.  
- **`repeat(int)`**: Loop execution.  
- **`yoyo(boolean)`**: Reverse direction on alternate repeat cycles.  
- **`onStart(Runnable)`, `onComplete(Runnable)`, etc.**: Lifecycle event hooks.  

### Animatable Methods
- **`start()`**: Registers and begins the animation.  
- **`then(Animatable)`**: Appends an animation to run sequentially.  
- **`alongWith(Animatable)`**: Appends an animation to run concurrently.  

### Spring Specifics
- **`setTarget(float)`**: Updates the spring's goal, triggering motion.

## 9. Troubleshooting / FAQ

### "My animation isn't starting!"
1.  Did you call `.start()` on the animation?
2.  Is `Animator.update()` being called once per frame in your main application loop?

---

### "My spring animation is too bouncy/slow."
Your animation is controlled by physics. Try increasing the `damping` value to reduce bounciness or increasing the `stiffness` value to make it react faster.

---

### "My animation seems to stutter or jump."
The animator is designed to catch up if your main application loop lags. Ensure that other logic in your main loop isn't causing significant frame rate drops.

---

## 10. "Best Practices" or "Common Patterns"

### Stateful vs. Fire-and-Forget
It's helpful to understand the nature of different animations to use them effectively.

* **Stateful Animations (`Spring`)**: A `Spring` is best used for dynamic, interruptible motion. You should create it **once** and keep it alive, changing its goal by calling `.setTarget()`. It's perfect for UI elements that need to react continuously to user input, like a card that follows the mouse.

* **Fire-and-Forget Animations (`Tween`)**: A `Tween` is typically used for a single, uninterruptible sequence. You create it, run it, and then let it be garbage collected. This is ideal for one-off effects like fading an item in or out.

---

### Reusing Animations

Creating a new animation object for every event (like a button click) is inefficient. A better pattern is to create the animation once during your object's initialization and simply call `.start()` on the existing instance whenever you need to trigger it.

```java
// Create the animation once during initialization
var buttonPulse = Animator.tweenFloat(button::setScale, 1f, 1.1f, Duration.ofMillis(100))
    .yoyo(true);

// Trigger it from an event handler
public void onButtonClick() {
    // This simply restarts the existing animation, creating no new objects.
    buttonPulse.start();
}
```
