package lol.tgformat.module.impl.misc;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import tech.skidonion.obfuscator.annotations.Renamer;
import tech.skidonion.obfuscator.annotations.StringEncryption;

/**
 * @Author KuChaZi
 * @Date 2024/7/13 13:39
 * @ClassName: ForgeSpoof
 */
@Renamer
@StringEncryption
public class ForgeSpoof extends Module {
    public ForgeSpoof() {
        super("ForgeSpoof", ModuleType.Misc);
    }
}
