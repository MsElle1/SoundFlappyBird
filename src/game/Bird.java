package game;

import java.awt.*;
import java.awt.geom.AffineTransform;

public class Bird {

    // 1. POSITION & SIZE
    public double x = 150, y = 200;
    public int width = 60, height = 45; // Proportional to 566 height

    // Power-up: No Gravity
    private boolean noGravity = false;
    private int frozenInputsRemaining = 0; // Counts down per user input (jump), not real time

    private double velocity = 0;

    // 2. PHYSICS (Re-tuned for 566px height)
    private final double gravity = 0.5;         // Lowered from 0.8
    private final double terminalVelocity = 12.0; // Lowered from 18.0

    public void update() {

        // If power is active
        if (noGravity) {

            // Stop all movement and lock position
            velocity = 0;
            y = frozenY; // Enforce frozen Y each frame

            return;  // 🚀 IMPORTANT → skip gravity completely
        }

        // Normal physics
        velocity += gravity;

        if (velocity > terminalVelocity) {
            velocity = terminalVelocity;
        }

        y += velocity;
    }
    // Stores Y position when power-up is activated so bird stays frozen in place
    private double frozenY = 0;

    public void activateNoGravity() {
        noGravity = true;
        frozenY = y;               // Lock current Y position
        frozenInputsRemaining = 3; // Freeze lasts for 5 user inputs (jumps)
    }

    public void jump() {
        if (noGravity) {
            // Each jump attempt counts as 1 input toward the 10-input limit
            frozenInputsRemaining--;
            if (frozenInputsRemaining <= 0) {
                noGravity = false; // Unfreeze after 10 inputs
            }
            return; // Still don't actually jump while frozen
        }
        velocity = -9.0; // Lowered from -14.0 for more control
    }

    public Rectangle getBounds() {
        // Hitbox padding adjusted for 60x45 size
        return new Rectangle((int)x + 6, (int)y + 6, width - 12, height - 12);
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 3. ROTATION (Using velocity for tilt)
        double rotation = Math.toRadians(velocity * 3);
        if (rotation > Math.toRadians(30)) rotation = Math.toRadians(30);
        if (rotation < Math.toRadians(-20)) rotation = Math.toRadians(-20);

        AffineTransform old = g2d.getTransform();
        g2d.rotate(rotation, x + width / 2.0, y + height / 2.0);

        // 4. DRAW BODY
        g2d.setColor(new Color(84, 56, 71));
        g2d.fillOval((int)x, (int)y, width, height);
        g2d.setColor(new Color(255, 235, 59));
        g2d.fillOval((int)x + 3, (int)y + 3, width - 6, height - 6);

        // 5. DRAW WING (Scaled for 60x45 bird)
        int wingY = (velocity < 0) ? (int)y + 18 : (int)y + 10;
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)x + 8, wingY, 25, 18);
        g2d.setColor(new Color(84, 56, 71));
        g2d.setStroke(new BasicStroke(3)); // Thinner than 1080p, thicker than original
        g2d.drawOval((int)x + 8, wingY, 25, 18);

        // 6. DRAW EYE
        g2d.setColor(Color.WHITE);
        g2d.fillOval((int)x + 35, (int)y + 6, 20, 20);
        g2d.setColor(Color.BLACK);
        g2d.fillOval((int)x + 44, (int)y + 12, 7, 7);

        // 7. DRAW BEAK
        g2d.setColor(new Color(255, 111, 0));
        int[] bx = {(int)x + width - 10, (int)x + width + 15, (int)x + width - 10};
        int[] by = {(int)y + 15, (int)y + 25, (int)y + 35};
        g2d.fillPolygon(bx, by, 3);
        g2d.setColor(new Color(84, 56, 71));
        g2d.drawPolygon(bx, by, 3);

        g2d.setTransform(old);
    }
}