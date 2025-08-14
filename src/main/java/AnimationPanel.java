/*
 * Java Animation Library
 * Author: Melod
 * Part of the IntBuffer project
 *
 * License: MIT
 * This code is free to use, modify, and distribute.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class AnimationPanel extends JPanel {
    private float rectX = 100;
    private float rectY = 100;
    private float rectSize = 50;
    private float rectRotation = 0;

    public void setRectX(float rectX) {
        this.rectX = rectX;
    }

    public void setRectY(float rectY) {
        this.rectY = rectY;
    }

    public void setRectSize(float rectSize) {
        this.rectSize = rectSize;
    }

    public void setRectRotation(float rectRotation) {
        this.rectRotation = rectRotation;
    }

    public float getRectSize() {
        return this.rectSize;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.translate(this.rectX, this.rectY);
        g2d.rotate(Math.toRadians(this.rectRotation));
        
        g2d.setColor(new Color(50, 150, 250));
        g2d.fill(new Rectangle2D.Float(-this.rectSize / 2, -this.rectSize / 2, this.rectSize, this.rectSize));
        
        g2d.dispose();
    }
}