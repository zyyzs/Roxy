package lol.tgformat.component;

import lol.tgformat.accessable.IMinecraft;
import lol.tgformat.api.event.Listener;
import lol.tgformat.api.event.types.Priority;
import lol.tgformat.events.PreUpdateEvent;
import lol.tgformat.events.RespawnEvent;
import lol.tgformat.events.render.Render2DEvent;
import lol.tgformat.events.render.Render3DEvent;
import lol.tgformat.module.impl.render.ESP;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ESPComponent implements IMinecraft {
    public static ConcurrentLinkedQueue<ESP> esps = new ConcurrentLinkedQueue<>();
    static Executor threadPool = Executors.newFixedThreadPool(2);
    @Listener(Priority.HIGHEST)
    public void onRender(Render2DEvent event) {

        if (esps.isEmpty()) {
            return;
        }

        esps.forEach(ESP::render2D);
    }

    ;

    @Listener(Priority.HIGHEST)
    public void onRender(Render3DEvent event) {

        if (esps == null || esps.isEmpty()) {
            return;
        }

        esps.forEach(ESP::render3D);
    }

    ;

    @Listener(Priority.HIGHEST)
    public void onUpdate(PreUpdateEvent event) {
        threadPool.execute(() -> {
            for (ESP esp1 : esps) {
                esp1.updateTargets();

                if (esp1.tick + 2 < mc.thePlayer.ticksExisted) {
                    esps.remove(esp1);
                }
            }
        });
    }

    ;

    public static void add(ESP esp) {
        threadPool.execute(() -> {
            boolean modified = false;
            for (ESP esp1 : esps) {
                if (esp.getClass().getSimpleName().equals(esp1.getClass().getSimpleName())) {
                    esp1.espColor = esp.espColor;
                    esp1.tick = mc.thePlayer.ticksExisted;
                    ;
                    modified = true;
                }
            }

            if (!modified) {
                esps.add(esp);
            }
        });
    }

    @Listener
    public void WOrldLoad(RespawnEvent event) {
        esps.clear();
    }
}