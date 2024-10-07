package lol.tgformat.module.impl.world;

import java.awt.*;
import java.util.*;
import java.util.List;

import lol.tgformat.Client;
import lol.tgformat.api.event.Listener;
import lol.tgformat.component.RotationComponent;
import lol.tgformat.events.PlaceEvent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.TickEvent;
import lol.tgformat.events.motion.PreMotionEvent;
import lol.tgformat.events.movement.StrafeEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.impl.render.HUD;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.ModeSetting;
import lol.tgformat.ui.font.FontUtil;
import lol.tgformat.ui.utils.Animation;
import lol.tgformat.ui.utils.DecelerateAnimation;
import lol.tgformat.ui.utils.Direction;
import lol.tgformat.utils.block.*;
import lol.tgformat.utils.enums.MovementFix;
import lol.tgformat.utils.math.MathUtil;
import lol.tgformat.utils.move.MoveUtil;
import lol.tgformat.utils.player.InventoryUtil;
import lol.tgformat.utils.render.DrawUtil;
import lol.tgformat.utils.render.RenderUtils;
import lol.tgformat.utils.rotation.RayCastUtil;
import lol.tgformat.utils.rotation.RotationUtil;
import lol.tgformat.utils.vector.Vector2f;
import lombok.Getter;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.*;
import net.netease.font.FontManager;
import net.netease.utils.AnimationUtil;
import net.netease.utils.RenderUtil;
import org.lwjgl.opengl.GL11;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

import static lol.tgformat.ui.clickgui.Utils.tahomaFont;
import static lol.tgformat.ui.clickgui.Utils.tenacityBoldFont18;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;
import static org.lwjgl.opengl.GL11.glDisable;

/**
 * @author TG_format
 * @since 2024/6/1 0:55
 */
@Renamer
@StringEncryption
public class Scaffold extends Module {
    public static final List<Block> invalidBlocks = Arrays.asList(Blocks.enchanting_table, Blocks.furnace, Blocks.carpet, Blocks.crafting_table, Blocks.trapped_chest, Blocks.chest, Blocks.dispenser, Blocks.air, Blocks.water, Blocks.lava, Blocks.flowing_water, Blocks.flowing_lava, Blocks.snow_layer, Blocks.torch, Blocks.anvil, Blocks.jukebox, Blocks.stone_button, Blocks.wooden_button, Blocks.lever, Blocks.noteblock, Blocks.stone_pressure_plate, Blocks.light_weighted_pressure_plate, Blocks.wooden_pressure_plate, Blocks.heavy_weighted_pressure_plate, Blocks.stone_slab, Blocks.wooden_slab, Blocks.stone_slab2, Blocks.red_mushroom, Blocks.brown_mushroom, Blocks.yellow_flower, Blocks.red_flower, Blocks.anvil, Blocks.glass_pane, Blocks.stained_glass_pane, Blocks.iron_bars, Blocks.cactus, Blocks.ladder, Blocks.web, Blocks.tnt);
    public static Scaffold INSTANCE;
    public final BooleanSetting swing = new BooleanSetting("Swing", true);
    public final BooleanSetting sprintValue = new BooleanSetting("Sprint", false);
    public final BooleanSetting eagle = new BooleanSetting("Eagle", false);
    public final BooleanSetting telly = new BooleanSetting("Telly", true);
    public final BooleanSetting autojump = new BooleanSetting("AutoJump", false);
    public final BooleanSetting fakeslot = new BooleanSetting("FakeSlot", false);
    public final BooleanSetting raycast = new BooleanSetting("RayCast", false);
    public final BooleanSetting tower = new BooleanSetting("Tower", false);
    public final BooleanSetting sameY = new BooleanSetting("SameY", false);
    public final BooleanSetting esp = new BooleanSetting("ESP", true);
    public final BooleanSetting count = new BooleanSetting("Count", true);
    public final ModeSetting countmode = new ModeSetting("Count Mode","Default","Default","Simple","Island");
    public boolean tip = false;
    protected Random rand = new Random();
    private final List<BlockPos> placedBlocks = new ArrayList<>();
    private double countscale = 0;
    private boolean towers = false;
    private int ticks = 0;
    private int holdticks = 0;
    @Getter
    private int slot;
    private int oldSlot;
    private BlockPos data;
    private boolean canTellyPlace;
    private int prevItem = 0;
    private EnumFacing facing;
    private final Animation anim = new DecelerateAnimation(250, 1);

    public Scaffold() {
        super("Scaffold", ModuleType.World);
    }

    @Override
    public void onEnable() {
        countscale = 0;
        if (mc.thePlayer == null) {
            return;
        }
        oldSlot = mc.thePlayer.inventory.currentItem;
        this.prevItem = mc.thePlayer.inventory.currentItem;
        mc.thePlayer.setSprinting(this.sprintValue.isEnabled() || !this.canTellyPlace);
        mc.gameSettings.keyBindSprint.pressed = this.sprintValue.isEnabled() || !this.canTellyPlace;
        this.canTellyPlace = false;
        this.tip = false;
        this.data = null;
        this.slot = -1;
        this.facing = null;
    }

    @Override
    public void onDisable() {
        if (mc.thePlayer == null) {
            return;
        }
        if (fakeslot.isEnabled()) {
            Client.instance.getSlotSpoofComponent().stopSpoofing();
        }
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
        mc.thePlayer.inventory.currentItem = this.prevItem;
        placedBlocks.clear();
    }

    @Listener
    public void onRender3D(Render3DEvent event) {
        if (this.data == null) {
            return;
        }

        BlockPos currentBlockPos = this.data;
        PlaceInfo placeInfo = PlaceInfo.get(currentBlockPos);

        if (BlockUtil.isValidBock(currentBlockPos) && placeInfo != null && this.esp.isEnabled()) {
            if (!placedBlocks.contains(currentBlockPos)) {
                placedBlocks.add(currentBlockPos);
            }
        }

        Iterator<BlockPos> iterator = placedBlocks.iterator();
        while (iterator.hasNext()) {
            BlockPos blockPos = iterator.next();

            double distance = mc.thePlayer.getDistance(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

            int maxDistance = 8; // Max Range
            int alpha = (int) Math.max(0, 255 - (distance / maxDistance) * 255);

            if (alpha <= 0) {
                iterator.remove();
            } else {
                RenderUtils.renderBlock(blockPos, new Color(50, 160, 200, Math.min(255, alpha)).getRGB(),true, false);
            }
        }
    }
    @Listener
    private void onRender2D(Render2DEvent event) {
        ScaledResolution sr = event.getScaledResolution();

        float width = 50 + mc.fontRendererObj.getStringWidth(String.valueOf(mc.thePlayer.getHeldItem().stackSize));
        int x = (int) (sr.getScaledWidth() / 2 - width / 2);
        int y = sr.getScaledHeight() / 2 + 12;
        float height = 18;
        if (count.isEnabled()) {
            switch (countmode.getMode()){
                case "Default":{

                    if (mc.thePlayer.getHeldItem() == null || mc.thePlayer.getHeldItem().stackSize == 0) {
                        mc.fontRendererObj.drawString("?", x + 4, y, new Color(40, 44, 52).getRGB());
                    } else {
                        this.drawItemStack(mc.thePlayer.getHeldItem(), x + 2.5f, y + 1);
                    }

                    tenacityBoldFont18.drawString("Blocks " + mc.thePlayer.getHeldItem().stackSize, x + 21, (y + 6), new Color(255, 255, 255).getRGB());
                    GL11.glPopMatrix();
                    break;
                }
                case "Simple":{
                    int X = sr.getScaledWidth() / 2 - 68;
                    mc.fontRendererObj.drawString("Blocks " + mc.thePlayer.getHeldItem().stackSize, (int) (X + 10 + 60 - tahomaFont.boldSize(18).getStringWidth("Blocks " + mc.thePlayer.getHeldItem().stackSize)/2), (y + 6), new Color(255, 255, 255).getRGB());
                    break;
                }
                case "Island":{
                    anim.setDirection(this.isState() ? Direction.FORWARDS : Direction.BACKWARDS);
                    if (!this.isState() && anim.isDone()) return;
                    double output = anim.getOutput();
                    int slot = getBlockSlot();
                    ItemStack heldItem = slot == -1 ? null : mc.thePlayer.inventory.mainInventory[slot];
                    int count = slot == -1 ? 0 : getBlockCount();
                    String countStr = String.valueOf(count);
                    float blockWH = heldItem != null ? 15 : -2;
                    int spacing = 3;
                    float x1, y1;
                    String text = "§l" + countStr + "§r Block" + (count != 1 ? "s" : "");
                    float textWidth = FontUtil.tenacityFont18.getStringWidth(text);
                    float totalWidth = (float) (((textWidth + blockWH + spacing) + 6 + 2) * output);
                    x1 = sr.getScaledWidth() / 2f - (totalWidth / 2f);
                    y1= sr.getScaledHeight() - (sr.getScaledHeight() / 2f + 30);
                    float height1 = 20;
                    RenderUtil.scissorStart(x1 - 1.5, y1 - 1.5, totalWidth + 3, height + 3);

                    RenderUtil.drawRectWH(x, y, totalWidth, height, RenderUtil.tripleColor(20, .55f).getRGB());
                    FontUtil.tenacityFont18.drawString(text, x1 + 2 + blockWH + spacing + 2, y1 + FontUtil.tenacityFont18.getMiddleOfBox(height1) + 3f, -1);

                    if (heldItem != null) {
                        RenderHelper.enableGUIStandardItemLighting();
                        mc.getRenderItem().renderItemAndEffectIntoGUI(heldItem, (int) x1 + 3 + 1, (int) (y1 + 10 - (blockWH / 2)));
                        RenderHelper.disableStandardItemLighting();
                    }
                    glDisable(GL_SCISSOR_TEST);
                    break;
                }

            }

        }

    }


    @Listener
    public void onUpdate(PreMotionEvent event) {
        if (tower.isEnabled()) {
            onTower();
        }

        if (this.eagle.isEnabled()) {
            if (Eagle.getBlockUnderPlayer(mc.thePlayer) instanceof BlockAir) {
                if (mc.thePlayer.onGround) {
                    KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), true);
                }
            } else if (mc.thePlayer.onGround) {
                KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), false);
            }
        }
        if (this.slot < 0) {
            return;
        }
        if (this.getBlockCount() <= 1) {
            int spoofSlot = this.getBestSpoofSlot();
            this.getBlock(spoofSlot);
        }
        if (this.slot < 0) {
            return;
        }
        mc.thePlayer.inventoryContainer.getSlot(this.slot + 36).getStack();
    }

    public void onTower() {
        boolean isKeyDown = mc.gameSettings.keyBindJump.isKeyDown() && mc.gameSettings.keyBindSprint.isKeyDown();
        if (isKeyDown) {
            holdticks = holdticks + 1;
            towers = true;
            ticks = ticks + 1;
            if (mc.thePlayer.onGround) {
                ticks = 0;
            }
            if (holdticks < 19) {
                mc.thePlayer.motionY = 0.41965;
                float speed = 0.241f;
                EntityPlayerSP thePlayer = mc.thePlayer;
                float rotationYaw = thePlayer.rotationYaw;
                if (MoveUtil.isMoveKeybind()) {
                    float yaw = (float) Math.toRadians(rotationYaw);
                    thePlayer.motionX = -Math.sin(yaw) * speed;
                    thePlayer.motionZ = Math.cos(yaw) * speed;
                }
//                mc.thePlayer.speedInAir = 0.241F;
                if (ticks == 1) {
                    mc.thePlayer.motionY = 0.33;
                }
                if (ticks == 2) {
                    mc.thePlayer.motionY = 1 - mc.thePlayer.posY % 1;
                }
                if (ticks == 3) {
                    ticks = 0;
                }
            }
        }

        if (ticks >= 3) {
            ticks = 0;
        }

        if (towers && (!mc.gameSettings.keyBindJump.isKeyDown() || !mc.gameSettings.keyBindSprint.isKeyDown())) {
            mc.thePlayer.motionX = 0;
            ticks = 0;
            towers = false;
        }

        if (holdticks > 23 && mc.thePlayer.onGround) {
            holdticks = 0;
        }
    }

    @Listener
    public void onStrafe(StrafeEvent event) {
        if ((autojump.isEnabled()) && mc.thePlayer.onGround && MoveUtil.isMoving() && !mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.thePlayer.jump();
        }
    }

    @Listener
    private void onTick(TickEvent event) {
        if (mc.thePlayer == null) {
            return;
        }
        if (this.slot < 0) {
            return;
        }
        if (!this.telly.isEnabled()) {
            this.canTellyPlace = true;
        }
        Scaffold scaffold = ModuleManager.getModule(Scaffold.class);
        if (scaffold.isState()) {
            countscale = AnimationUtil.moveUD((float) countscale, (float) 1, (float)( (30 * RenderUtil.deltaTime())+2), (float)((20 * RenderUtil.deltaTime())+2));
        } else {
            countscale = AnimationUtil.moveUD((float) countscale, (float) 0, (float) (30 * RenderUtil.deltaTime()), (float) (20 * RenderUtil.deltaTime()));
        }
        search();
    }

    @Listener
    private void onPlace(PlaceEvent event) {
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (!(this.telly.isEnabled())) {
            mc.thePlayer.setSprinting(this.sprintValue.isEnabled());
            mc.gameSettings.keyBindSprint.pressed = false;
        }
        event.setCancelled(true);
        if (mc.thePlayer == null) {
            return;
        }
        if (raycast.isEnabled()) {

        }
        this.place();
        mc.sendClickBlockToController(mc.currentScreen == null && mc.gameSettings.keyBindAttack.isKeyDown() && mc.inGameHasFocus);

    }

    @Listener
    private void onUpdate(PreUpdateEvent event) {
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (fakeslot.isEnabled()) {
            Client.instance.getSlotSpoofComponent().startSpoofing(oldSlot);
        }
        mc.thePlayer.inventory.currentItem = this.slot;
        this.search();
        if (this.telly.isEnabled()) {
            if (this.canTellyPlace && !mc.thePlayer.onGround && MoveUtil.isMoving()) {
                mc.thePlayer.setSprinting(false);
            }
            this.canTellyPlace = mc.thePlayer.offGroundTicks >= 1;
        }
        if (!this.canTellyPlace) {
            return;
        }
        if (this.data != null) {
            float yaw = RotationUtil.getRotationBlock(this.data)[0];
            float pitch = RotationUtil.getRotationBlock(this.data)[1];
            RotationComponent.setRotations(new Vector2f(yaw, pitch), 180.0f, MovementFix.NORMAL);
        }
    }

    private void place() {
        if (!this.canTellyPlace) {
            return;
        }
        this.slot = this.getBlockSlot();
        if (this.slot < 0) {
            return;
        }
        if (this.data != null) {
            if (facing == null) {
                return;
            }
            Vec3 hitvec = getVec3(data, facing);
            if (RayCastUtil.overBlock(RotationComponent.lastServerRotations, facing, data, false)) {
                if (validateBlockRange(hitvec)) {
                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem(), this.data, facing, hitvec)) {
                        if (this.swing.isEnabled()) {
                            mc.thePlayer.swingItem();
                        } else {
                            mc.thePlayer.sendQueue.addToSendQueue(new C0APacketAnimation());
                        }
                    }
                }
            }
        }
    }

    private void search() {
        EntityPlayerSP player = Scaffold.mc.thePlayer;
        WorldClient world = Scaffold.mc.theWorld;
        double posX = player.posX;
        double posZ = player.posZ;
        double minY = player.getEntityBoundingBox().minY;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            sameY.setState(false);
        }else {
            sameY.setState(true);
        }
        Vec3 vec3 = getPlacePossibility(0.0, 0.0, 0.0, !sameY.isEnabled());
        if (vec3 == null) {
            return;
        }
        BlockPos pos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        if (!Scaffold.mc.theWorld.getBlockState(pos).getBlock().getMaterial().isReplaceable()) {
            return;
        }
        for (EnumFacing facingType : EnumFacing.values()) {
            BlockPos neighbor = pos.offset(facingType);
            if (!canBeClick(neighbor)) continue;
            Vec3 dirVec = new Vec3(facingType.getDirectionVec());
            for (double xSearch = 0.5; xSearch <= 0.5; xSearch += 0.01) {
                for (double ySearch = 0.5; ySearch <= 0.5; ySearch += 0.01) {
                    double zSearch = 0.5;
                    while (zSearch <= 0.5) {
                        Vec3 eyesPos = new Vec3(posX, minY + (double)mc.thePlayer.getEyeHeight(), posZ);
                        Vec3 posVec = new Vec3(pos).addVector(xSearch, ySearch, zSearch);
                        Vec3 hitVec = posVec.add(new Vec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5));
                        double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);
                        if (eyesPos.distanceTo(hitVec) > 5.0 || distanceSqPosVec > eyesPos.squareDistanceTo(posVec.add(dirVec)) || world.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null) {
                            zSearch += 0.01;
                            continue;
                        }
                        double diffX = hitVec.xCoord - eyesPos.xCoord;
                        double diffY = hitVec.yCoord - eyesPos.yCoord;
                        double diffZ = hitVec.zCoord - eyesPos.zCoord;
                        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
                        if (facingType != EnumFacing.UP && facingType != EnumFacing.DOWN && (facingType == EnumFacing.NORTH || facingType == EnumFacing.SOUTH ? Math.abs(diffZ) : Math.abs(diffX)) < 0.0) {
                            zSearch += 0.01;
                            continue;
                        }
                        Vector2f rotation = new Vector2f(MathHelper.wrapAngleTo180_float((float)(Math.toDegrees(MathHelper.atan2(diffZ, diffX)) - 90.0)), MathHelper.wrapAngleTo180_float((float)(-Math.toDegrees(MathHelper.atan2(diffY, diffXZ)))));
                        Vec3 rotVec = getVectorForRotation(rotation);
                        Vec3 vector = eyesPos.addVector(rotVec.xCoord * 5.0, rotVec.yCoord * 5.0, rotVec.zCoord * 5.0);
                        MovingObjectPosition obj = mc.theWorld.rayTraceBlocks(eyesPos, vector, false, false, true);
                        if (obj == null) continue;
                        if (obj.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK || obj.getBlockPos().getX() != neighbor.getX() || obj.getBlockPos().getZ() != neighbor.getZ() || obj.getBlockPos().getY() != neighbor.getY() || obj.sideHit != facingType.getOpposite()) {
                            zSearch += 0.01;
                            continue;
                        }
                        this.data = neighbor;
                        this.facing = facingType.getOpposite();
                        return;
                    }
                }
            }
        }
    }
    public static boolean canBeClick(BlockPos pos) {
        return mc.theWorld.getBlockState(pos).getBlock().canCollideCheck(mc.theWorld.getBlockState(pos), false) && mc.theWorld.getWorldBorder().contains(pos);
    }
    public static Vec3 getPlacePossibility(double offsetX, double offsetY, double offsetZ, boolean searchUP) {
        ArrayList<Vec3> possibilities = new ArrayList<>();
        int range = (int)(6.0 + (Math.abs(offsetX) + Math.abs(offsetZ)));
        Vec3 playerPos = new Vec3(mc.thePlayer.posX + offsetX, mc.thePlayer.posY - 1.0 + offsetY, mc.thePlayer.posZ + offsetZ);
        if (!(mc.theWorld.getBlockState(new BlockPos(playerPos)).getBlock() instanceof BlockAir)) {
            return playerPos;
        }
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= 0; ++y) {
                for (int z = -range; z <= range; ++z) {
                    Block block = mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(x, y, z)).getBlock();
                    if (block instanceof BlockAir) continue;
                    for (int x2 = -1; x2 <= 1; x2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x + (double)x2, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z));
                    }
                    for (int y2 = -1; y2 <= 1; y2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y + (double)y2, mc.thePlayer.posZ + (double)z));
                    }
                    for (int z2 = -1; z2 <= 1; z2 += 2) {
                        possibilities.add(new Vec3(mc.thePlayer.posX + (double)x, mc.thePlayer.posY + (double)y, mc.thePlayer.posZ + (double)z + (double)z2));
                    }
                }
            }
        }
        possibilities.removeIf(vec3 -> {
            BlockPos blockPos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            if (mc.thePlayer.getPosition().getX() == blockPos.getX() && mc.thePlayer.getPosition().getY() == blockPos.getY() && mc.thePlayer.getPosition().getZ() == blockPos.getZ()) {
                return true;
            }
            BlockPos position = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
            return mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > 6.0 || !(mc.theWorld.getBlockState(new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord)).getBlock() instanceof BlockAir);
        });
        possibilities.removeIf(e -> {
            boolean hasBlock = false;
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos position;
                if (facing == EnumFacing.UP || facing == EnumFacing.DOWN && !searchUP || mc.theWorld.getBlockState((position = new BlockPos(e.xCoord, e.yCoord, e.zCoord)).offset(facing)) == null || mc.theWorld.getBlockState(position.offset(facing)).getBlock() instanceof BlockAir) continue;
                BlockPos facePos = position.offset(facing);
                if (mc.thePlayer.getDistance((double)position.getX() + 0.5, (double)position.getY() + 0.5, (double)position.getZ() + 0.5) > mc.thePlayer.getDistance((double)facePos.getX() + 0.5, (double)facePos.getY() + 0.5, (double)facePos.getZ() + 0.5)) {
                    return true;
                }
                hasBlock = true;
            }
            if (e.yCoord > mc.thePlayer.getEntityBoundingBox().minY && !searchUP) {
                return true;
            }
            return !hasBlock;
        });
        if (possibilities.isEmpty()) {
            return null;
        }
        possibilities.sort(Comparator.comparingDouble(vec3 -> {
            double d0 = mc.thePlayer.posX + offsetX - vec3.xCoord;
            double d1 = mc.thePlayer.posY - 1.0 + offsetY - vec3.yCoord;
            double d2 = mc.thePlayer.posZ + offsetZ - vec3.zCoord;
            return MathHelper.sqrt_double(d0 * d0 + d1 * d1 + d2 * d2);
        }));
        return possibilities.getFirst();
    }
    public static Vec3 getVectorForRotation(Vector2f rotation) {
        float yawCos = (float)Math.cos(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float yawSin = (float)Math.sin(-rotation.x * ((float)Math.PI / 180) - (float)Math.PI);
        float pitchCos = (float)(-Math.cos(-rotation.y * ((float)Math.PI / 180)));
        float pitchSin = (float)Math.sin(-rotation.y * ((float)Math.PI / 180));
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }
    public int getBlockSlot() {
        for (int i = 0; i < 9; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i + 36).getHasStack() || !(mc.thePlayer.inventoryContainer.getSlot(i + 36).getStack().getItem() instanceof ItemBlock)) continue;
            return i;
        }
        return -1;
    }

    public int getBlockCount() {
        int n = 0;
        for (int i = 36; i < 45; ++i) {
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            ItemStack stack = mc.thePlayer.inventoryContainer.getSlot(i).getStack();
            Item item = stack.getItem();
            if (!(stack.getItem() instanceof ItemBlock) || !this.isValid(item)) continue;
            n += stack.stackSize;
        }
        return n;
    }

    public boolean isValid(Item item) {
        return item instanceof ItemBlock && !invalidBlocks.contains(((ItemBlock)item).getBlock());
    }

    private void getBlock(int switchSlot) {
        for (int i = 9; i < 45; ++i) {
            ItemStack is;
            if (!mc.thePlayer.inventoryContainer.getSlot(i).getHasStack() || mc.currentScreen != null && !(mc.currentScreen instanceof GuiInventory) || !((is = mc.thePlayer.inventoryContainer.getSlot(i).getStack()).getItem() instanceof ItemBlock) || !this.isValid(is.getItem())) continue;
            if (36 + switchSlot == i) break;
            InventoryUtil.swap(i, switchSlot);
            break;
        }
    }

    int getBestSpoofSlot() {
        int spoofSlot = 5;
        for (int i = 36; i < 45; ++i) {
            if (mc.thePlayer.inventoryContainer.getSlot(i).getHasStack()) continue;
            spoofSlot = i - 36;
            break;
        }
        return spoofSlot;
    }
    private static boolean validateBlockRange(final Vec3 pos) {
        if (pos == null)
            return false;
        final EntityPlayerSP player = mc.thePlayer;
        final double x = (pos.xCoord - player.posX);
        final double y = (pos.yCoord - (player.posY + player.getEyeHeight()));
        final double z = (pos.zCoord - player.posZ);
        return StrictMath.sqrt(x * x + y * y + z * z) <= 5.0D;
    }
    public static Vec3 getVec3(BlockPos pos, EnumFacing face) {
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.5;
        double z = (double)pos.getZ() + 0.5;
        if (face == EnumFacing.UP || face == EnumFacing.DOWN) {
            x += MathUtil.getRandomInRange(0.3, -0.3);
            z += MathUtil.getRandomInRange(0.3, -0.3);
        } else {
            y += 0.08;
        }
        if (face == EnumFacing.WEST || face == EnumFacing.EAST) {
            z += MathUtil.getRandomInRange(0.3, -0.3);
        }
        if (face == EnumFacing.SOUTH || face == EnumFacing.NORTH) {
            x += MathUtil.getRandomInRange(0.3, -0.3);
        }
        return new Vec3(x, y, z);
    }
    private void drawItemStack(ItemStack itemStack, float x, float y) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        RenderHelper.enableGUIStandardItemLighting();
        mc.getRenderItem().renderItemAndEffectIntoGUI(itemStack, (int) x, (int) y);
        GlStateManager.disableRescaleNormal();
        GlStateManager.disableBlend();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popMatrix();
    }

}