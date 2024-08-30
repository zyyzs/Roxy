package lol.tgformat.module.impl.render;

import lol.tgformat.api.event.Listener;
import lol.tgformat.component.ESPComponent;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.module.Module;
import lol.tgformat.module.ModuleType;
import lol.tgformat.utils.render.ESPColor;

import java.awt.*;

public class ESPChams extends Module {
    public ESPChams() {
        super("ChamsESP", ModuleType.Render);
    }
        @Listener
        public void onUpdate(PreUpdateEvent event)
        {
            Color color = new Color(0,255,255);
            ESPComponent.add(new ChamsESP(new ESPColor(color, color, color)));
        };
}
