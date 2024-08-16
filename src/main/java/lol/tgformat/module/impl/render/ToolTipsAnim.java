package lol.tgformat.module.impl.render;

import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;

/**
 * @author TG_format
 * @since 2024/7/13 下午1:29
 */
public class ToolTipsAnim extends Module {
    public static ToolTipsAnim getInstance;

    public ToolTipsAnim() {
        super("ToolTipsAnim", ModuleType.Render);
        getInstance = this;
    }
}
