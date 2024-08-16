package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.module.values.impl.NumberSetting;
import lol.tgformat.utils.render.RenderUtils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnderChest;
import org.lwjgl.input.Keyboard;

import java.awt.*;

/**
 * @Author KuChaZi
 * @Date 2024/6/10 10:48
 * @ClassName: ChestESP
 */
public class ChestESP extends Module {

    private final NumberSetting red = new NumberSetting("Red",0,255,0,1);
    private final NumberSetting green = new NumberSetting("Green",0,255,0,1);
    private final NumberSetting blue = new NumberSetting("Blue",0,255,0,1);

    private final BooleanSetting rainbow = new BooleanSetting("Rainbow", false);
    private final BooleanSetting outline = new BooleanSetting("Outline", false);
    private final BooleanSetting shade = new BooleanSetting("Shade", false);
    private final BooleanSetting disableIfOpened = new BooleanSetting("Disable if Opened", false);
    private int rgb = 0;

    public ChestESP() {
        super("ChestESP", ModuleType.Render);
    }

    private int color() {
        return this.rgb = (new Color((int) red.getValue().doubleValue(), (int) green.getValue().doubleValue(), (int) blue.getValue().doubleValue())).getRGB();
    }

    @Listener
    public void onRender3D(Render3DEvent ev) {
        if (isNull()) return;
        int rgb = rainbow.isEnabled() ? getChroma(2L, 0L) : color();
        for (TileEntity tileEntity : mc.theWorld.loadedTileEntityList) {
            if (tileEntity instanceof TileEntityChest) {
                if (disableIfOpened.isEnabled() && ((TileEntityChest) tileEntity).lidAngle > 0.0f) {
                    continue;
                }
                RenderUtils.renderBlock(tileEntity.getPos(), rgb, outline.isEnabled(), shade.isEnabled());
            } else {
                if (!(tileEntity instanceof TileEntityEnderChest)) {
                    continue;
                }
                if (disableIfOpened.isEnabled() && ((TileEntityEnderChest) tileEntity).lidAngle > 0.0f) {
                    continue;
                }
                RenderUtils.renderBlock(tileEntity.getPos(), rgb, outline.isEnabled(), shade.isEnabled());
            }
        }
    }

    public static int getChroma(long speed, long... delay) {
        long time = System.currentTimeMillis() + (delay.length > 0 ? delay[0] : 0L);
        return Color.getHSBColor((float) (time % (15000L / speed)) / (15000.0F / (float) speed), 1.0F, 1.0F).getRGB();
    }
}
