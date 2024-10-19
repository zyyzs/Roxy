package lol.tgformat.utils.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.*;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.*;

/**
 * @author TG_format
 * @since 2024/6/1 1:08
 */
public final class BlockUtil {
    private static final Minecraft mc;
    public static final List<Block> invalidBlocks;
    private static final List<Integer> nonValidItems;

    public static boolean isValid(Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock)((ItemBlock)item)).getBlock());
    }

    public static boolean isInteractBlock(Block block) {
        return block instanceof BlockFence ||
                block instanceof BlockFenceGate ||
                block instanceof BlockDoor ||
                block instanceof BlockChest ||
                block instanceof BlockEnderChest ||
                block instanceof BlockEnchantmentTable ||
                block instanceof BlockFurnace ||
                block instanceof BlockAnvil ||
                block instanceof BlockBed ||
                block instanceof BlockWorkbench ||
                block instanceof BlockNote ||
                block instanceof BlockTrapDoor ||
                block instanceof BlockHopper ||
                block instanceof BlockDispenser ||
                block instanceof BlockDaylightDetector ||
                block instanceof BlockRedstoneRepeater ||
                block instanceof BlockRedstoneComparator ||
                block instanceof BlockButton ||
                block instanceof BlockBeacon ||
                block instanceof BlockBrewingStand ||
                block instanceof BlockSign;
    }

    public static Block getBlock(BlockPos blockPos) {
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static boolean isValidBock(BlockPos blockPos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return !(block instanceof BlockLiquid) && !(block instanceof BlockAir) && !(block instanceof BlockChest) && !(block instanceof BlockFurnace);
    }

    public static boolean isAirBlock(BlockPos blockPos) {
        Block block = Minecraft.getMinecraft().theWorld.getBlockState(blockPos).getBlock();
        return block instanceof BlockAir;
    }

    public static boolean isValidStack(ItemStack itemStack) {
        Item item = itemStack.getItem();
        if (!(item instanceof ItemSlab) && !(item instanceof ItemLeaves) && !(item instanceof ItemSnow) && !(item instanceof ItemBanner) && !(item instanceof ItemFlintAndSteel)) {
            Iterator var2 = nonValidItems.iterator();

            int item2;
            do {
                if (!var2.hasNext()) {
                    return true;
                }

                item2 = (Integer)var2.next();
            } while(!item.equals(Item.getItemById(item2)));

            return false;
        } else {
            return false;
        }
    }

    public static Vec3 floorVec3(Vec3 vec3) {
        return new Vec3(Math.floor(vec3.xCoord), Math.floor(vec3.yCoord), Math.floor(vec3.zCoord));
    }

    public static Material getMaterial(BlockPos blockPos) {
        return getBlock(blockPos).getMaterial();
    }

    public static boolean isReplaceable(BlockPos blockPos) {
        return getMaterial(blockPos).isReplaceable();
    }

    public static IBlockState getState(BlockPos pos) {
        return mc.theWorld.getBlockState(pos);
    }

    public static boolean canBeClicked(BlockPos pos) {
        return getBlock(pos).canCollideCheck(getState(pos), false);
    }

    public static String getBlockName(int id) {
        return Block.getBlockById(id).getLocalizedName();
    }

    public static boolean isFullBlock(BlockPos blockPos) {
        AxisAlignedBB axisAlignedBB = getBlock(blockPos).getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos));
        return axisAlignedBB != null && axisAlignedBB.maxX - axisAlignedBB.minX == 1.0D && axisAlignedBB.maxY - axisAlignedBB.minY == 1.0D && axisAlignedBB.maxZ - axisAlignedBB.minZ == 1.0D;
    }

    public static double getCenterDistance(BlockPos blockPos) {
        return mc.thePlayer.getDistance((double)blockPos.getX() + 0.5D, (double)blockPos.getY() + 0.5D, (double)blockPos.getZ() + 0.5D);
    }

    public static List<BlockPos> searchBlock(int range) {
        return BlockUtil.searchBlocks(mc.thePlayer, range);
    }
    public static List<BlockPos> searchBlocks(EntityLivingBase entity, int range) {
        return BlockUtil.searchBlocks(entity, range, range, range);
    }
    public static List<BlockPos> searchBlocks(EntityLivingBase entity, int xRange, int yRange, int zRange) {
        ArrayList<BlockPos> foundBlocks = new ArrayList<BlockPos>();
        WorldClient world = BlockUtil.mc.theWorld;
        BlockPos entityPos = entity.getPosition();
        if (world == null) {
            return foundBlocks;
        }
        int startX = entityPos.getX() - xRange;
        int endX = entityPos.getX() + xRange;
        int startY = entityPos.getY() - yRange;
        int endY = entityPos.getY() + yRange;
        int startZ = entityPos.getZ() - zRange;
        int endZ = entityPos.getZ() + zRange;
        for (int x = startX; x <= endX; ++x) {
            for (int y = startY; y <= endY; ++y) {
                for (int z = startZ; z <= endZ; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    Block block = world.getBlockState(pos).getBlock();
                    if (block instanceof BlockAir) continue;
                    foundBlocks.add(pos);
                }
            }
        }
        return foundBlocks;
    }
    public static Map<BlockPos, Block> searchBlocks(int radius) {
        Map<BlockPos, Block> blocks = new HashMap();

        for(int x = radius; x > -radius; --x) {
            for(int y = radius; y > -radius; --y) {
                for(int z = radius; z > -radius; --z) {
                    BlockPos blockPos = new BlockPos(mc.thePlayer.lastTickPosX + (double)x, mc.thePlayer.lastTickPosY + (double)y, mc.thePlayer.lastTickPosZ + (double)z);
                    Block block = getBlock(blockPos);
                    blocks.put(blockPos, block);
                }
            }
        }

        return blocks;
    }

    public static boolean collideBlock(AxisAlignedBB axisAlignedBB, BlockUtil.ICollide collide) {
        for(int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for(int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                Block block = getBlock(new BlockPos((double)x, axisAlignedBB.minY, (double)z));
                if (block != null && !collide.collideBlock(block)) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean collideBlockIntersects(AxisAlignedBB axisAlignedBB, BlockUtil.ICollide collide) {
        for(int x = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minX); x < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxX) + 1; ++x) {
            for(int z = MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minZ); z < MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().maxZ) + 1; ++z) {
                BlockPos blockPos = new BlockPos((double)x, axisAlignedBB.minY, (double)z);
                Block block = getBlock(blockPos);
                if (block != null && collide.collideBlock(block)) {
                    AxisAlignedBB boundingBox = block.getCollisionBoundingBox(mc.theWorld, blockPos, getState(blockPos));
                    if (boundingBox != null && mc.thePlayer.getEntityBoundingBox().intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    static {
        invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.sand, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web);
        nonValidItems = Arrays.asList(30, 58, 116, 158, 23, 6, 54, 146, 130, 26, 50, 76, 46, 37, 38);
        mc = Minecraft.getMinecraft();
    }

    public static boolean insideBlock(final AxisAlignedBB bb) {
        final WorldClient world = mc.theWorld;
        for (int x = MathHelper.floor_double(bb.minX); x < MathHelper.floor_double(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor_double(bb.minY); y < MathHelper.floor_double(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor_double(bb.minZ); z < MathHelper.floor_double(bb.maxZ) + 1; ++z) {
                    final Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    final AxisAlignedBB boundingBox;
                    if (block != null && !(block instanceof BlockAir) && (boundingBox = block.getCollisionBoundingBox(world, new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)))) != null && bb.intersectsWith(boundingBox)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public interface ICollide {
        boolean collideBlock(Block var1);
    }
}
