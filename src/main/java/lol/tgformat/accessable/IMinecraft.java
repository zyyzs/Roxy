package lol.tgformat.accessable;

import net.minecraft.client.Minecraft;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/5/31 20:05
 */
@Renamer
@StringEncryption
public interface IMinecraft {
    Minecraft mc = Minecraft.getMinecraft();
}
