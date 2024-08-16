package lol.tgformat.utils.block;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.utils.vector.Vector2f;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.RandomUtils;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author TG_format
 * @since 2024/6/1 1:10
 */
public class Rotation implements IMinecraft {
    public float yaw;
    public float pitch;
    public double distanceSq;

    public Rotation(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Vector2f toVec2f() {
        return new Vector2f(this.yaw, this.pitch);
    }

    public void toPlayer(EntityPlayer player) {
        if (!Float.isNaN(this.yaw) && !Float.isNaN(this.pitch)) {
            this.fixedSensitivity(mc.gameSettings.mouseSensitivity);
            player.rotationYaw = this.yaw;
            player.rotationPitch = this.pitch;
        }
    }

    public void fixedSensitivity(Float sensitivity) {
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;
        this.yaw -= this.yaw % gcd;
        this.pitch -= this.pitch % gcd;
    }

    public static float updateRotation(float current, float calc, float maxDelta) {
        float f = MathHelper.wrapAngleTo180_float(calc - current);
        if (f > maxDelta) {
            f = maxDelta;
        }

        if (f < -maxDelta) {
            f = -maxDelta;
        }

        return current + f;
    }

    public float rotateToYaw(float yawSpeed, float currentYaw, float calcYaw) {
        float yaw = updateRotation(currentYaw, calcYaw, yawSpeed + RandomUtils.nextFloat(0.0F, 15.0F));
        double diffYaw = (double)MathHelper.wrapAngleTo180_float(calcYaw - currentYaw);
        if ((double)(-yawSpeed) > diffYaw || diffYaw > (double)yawSpeed) {
            yaw += (float)((double)RandomUtils.nextFloat(1.0F, 2.0F) * Math.sin((double)mc.thePlayer.rotationPitch * 3.141592653589793D));
        }

        if (yaw == currentYaw) {
            return currentYaw;
        } else {
            if ((double)mc.gameSettings.mouseSensitivity == 0.5D) {
                mc.gameSettings.mouseSensitivity = 0.47887325F;
            }

            float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8.0F;
            int deltaX = (int)((6.667D * (double)yaw - 6.666666666666667D * (double)currentYaw) / (double)f2);
            float f3 = (float)deltaX * f2;
            yaw = (float)((double)currentYaw + (double)f3 * 0.15D);
            return yaw;
        }
    }

    public float rotateToYaw(float yawSpeed, float[] currentRots, float calcYaw) {
        float yaw = updateRotation(currentRots[0], calcYaw, yawSpeed + RandomUtils.nextFloat(0.0F, 15.0F));
        if (yaw != calcYaw) {
            yaw += (float)((double)RandomUtils.nextFloat(1.0F, 2.0F) * Math.sin((double)currentRots[1] * 3.141592653589793D));
        }

        if (yaw == currentRots[0]) {
            return currentRots[0];
        } else {
            yaw += (float)(ThreadLocalRandom.current().nextGaussian() * 0.2D);
            if ((double)mc.gameSettings.mouseSensitivity == 0.5D) {
                mc.gameSettings.mouseSensitivity = 0.47887325F;
            }

            float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f1 * f1 * f1 * 8.0F;
            int deltaX = (int)((6.667D * (double)yaw - 6.6666667D * (double)currentRots[0]) / (double)f2);
            float f3 = (float)deltaX * f2;
            yaw = (float)((double)currentRots[0] + (double)f3 * 0.15D);
            return yaw;
        }
    }

    public float rotateToPitch(float pitchSpeed, float currentPitch, float calcPitch) {
        float pitch = updateRotation(currentPitch, calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0F, 15.0F));
        if (pitch != calcPitch) {
            pitch += (float)((double)RandomUtils.nextFloat(1.0F, 2.0F) * Math.sin((double)mc.thePlayer.rotationYaw * 3.141592653589793D));
        }

        if ((double)mc.gameSettings.mouseSensitivity == 0.5D) {
            mc.gameSettings.mouseSensitivity = 0.47887325F;
        }

        float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f2 = f1 * f1 * f1 * 8.0F;
        int deltaY = (int)((6.667D * (double)pitch - 6.666667D * (double)currentPitch) / (double)f2) * -1;
        float f3 = (float)deltaY * f2;
        float f4 = (float)((double)currentPitch - (double)f3 * 0.15D);
        pitch = MathHelper.clamp_float(f4, -90.0F, 90.0F);
        return pitch;
    }

    public float rotateToPitch(float pitchSpeed, float[] currentRots, float calcPitch) {
        float pitch = updateRotation(currentRots[1], calcPitch, pitchSpeed + RandomUtils.nextFloat(0.0F, 15.0F));
        if (pitch != calcPitch) {
            pitch += (float)((double)RandomUtils.nextFloat(1.0F, 2.0F) * Math.sin((double)currentRots[0] * 3.141592653589793D));
        }

        if ((double)mc.gameSettings.mouseSensitivity == 0.5D) {
            mc.gameSettings.mouseSensitivity = 0.47887325F;
        }

        float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f2 = f1 * f1 * f1 * 8.0F;
        int deltaY = (int)((6.667D * (double)pitch - 6.666667D * (double)currentRots[1]) / (double)f2) * -1;
        float f3 = (float)deltaY * f2;
        float f4 = (float)((double)currentRots[1] - (double)f3 * 0.15D);
        pitch = MathHelper.clamp_float(f4, -90.0F, 90.0F);
        return pitch;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setDistanceSq(double distanceSq) {
        this.distanceSq = distanceSq;
    }

    public float getYaw() {
        return this.yaw;
    }

    public float getPitch() {
        return this.pitch;
    }

    public double getDistanceSq() {
        return this.distanceSq;
    }
}
