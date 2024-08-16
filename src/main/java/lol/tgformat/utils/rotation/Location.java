package lol.tgformat.utils.rotation;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;

/**
 * @Author KuChaZi
 * @Date 2024/8/2 17:20
 * @ClassName: Location
 */
public class Location {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    public Location(double x2, double y2, double z, float yaw, float pitch) {
        this.x = x2;
        this.y = y2;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Location(double x2, double y2, double z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Location(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Location(int x2, int y2, int z) {
        this.x = x2;
        this.y = y2;
        this.z = z;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Location(EntityLivingBase entity) {
        this.x = entity.posX;
        this.y = entity.posY;
        this.z = entity.posZ;
        this.yaw = 0.0f;
        this.pitch = 0.0f;
    }

    public Location add(int x2, int y2, int z) {
        this.x += (double)x2;
        this.y += (double)y2;
        this.z += (double)z;
        return this;
    }

    public Location add(double x2, double y2, double z) {
        this.x += x2;
        this.y += y2;
        this.z += z;
        return this;
    }

    public Location subtract(int x2, int y2, int z) {
        this.x -= (double)x2;
        this.y -= (double)y2;
        this.z -= (double)z;
        return this;
    }

    public Location subtract(double x2, double y2, double z) {
        this.x -= x2;
        this.y -= y2;
        this.z -= z;
        return this;
    }

    public Block getBlock() {
        return Minecraft.getMinecraft().theWorld.getBlockState(this.toBlockPos()).getBlock();
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x2) {
        this.x = x2;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y2) {
        this.y = y2;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public float getYaw() {
        return this.yaw;
    }

    public Location setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public float getPitch() {
        return this.pitch;
    }

    public Location setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public BlockPos toBlockPos() {
        return new BlockPos(this.x, this.y, this.z);
    }

    public double distanceTo(Location loc) {
        double dx = loc.x - this.x;
        double dz = loc.z - this.z;
        double dy = loc.y - this.y;
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}
