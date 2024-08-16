package lol.tgformat.module.impl.render;

import lol.tgformat.module.ModuleType;
import lol.tgformat.ui.clickgui.ModernClickGui;
import lombok.Getter;
import lombok.Setter;
import tech.skidonion.obfuscator.annotations.NativeObfuscation;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:50
 */
@Renamer
@StringEncryption
public class ClickGui{
    public static final ModernClickGui modernClickGui = new ModernClickGui();

    @Getter
    @Setter
    private static ModuleType activeCategory = ModuleType.Combat;
}
