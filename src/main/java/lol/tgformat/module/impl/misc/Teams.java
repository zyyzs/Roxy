package lol.tgformat.module.impl.misc;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.ModuleType;
import lol.tgformat.module.values.impl.BooleanSetting;
import lol.tgformat.utils.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/6/15 19:25
 * @ClassName: Teams
 */

@Renamer

@StringEncryption
public class Teams extends Module {
    public Teams() {
        super("Teams", ModuleType.Misc);
    }

    private static final BooleanSetting armor = new BooleanSetting("ArmorColor", true);
    private static final BooleanSetting color = new BooleanSetting("Color", true);
    private static final BooleanSetting scoreboard = new BooleanSetting("ScoreboardTeam", true);


    
    public static boolean isSameTeam(final Entity entity) {
        if (entity instanceof EntityPlayer entityPlayer) {
            return ModuleManager.getModule(Teams.class).isState() && ((armor.isEnabled() && PlayerUtil.armorTeam(entityPlayer))
                    || (color.isEnabled() && PlayerUtil.colorTeam(entityPlayer))
                    || (scoreboard.isEnabled() && PlayerUtil.scoreTeam(entityPlayer)));
        }
        return false;
    }

}
