package lol.tgformat.utils.rotation;

import lol.tgformat.utils.block.Rotation;
import net.minecraft.client.Minecraft;

import javax.vecmath.Vector2f;

public class RotationManager {
    private Minecraft mc = Minecraft.getMinecraft();
    public Vector2f rotation, lastRotation, targetRotation, lastServerRotation;
    private float rotationSpeed;
    private boolean modify, smoothed;
    private boolean movementFix, strict;

    public RotationManager() {
        this.rotation = new Vector2f(0, 0);
    }

    public Vector2f getRotation() {
        return rotation;
    }
    public double getRotationDifference(final Rotation rotation) {
        return lastServerRotation == null ? 0D : getRotationDifference(rotation, lastServerRotation);
    }

    public float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }


    public double getRotationDifference(final Rotation a, final Vector2f b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getX()), a.getPitch() - b.getY());
    }
}
