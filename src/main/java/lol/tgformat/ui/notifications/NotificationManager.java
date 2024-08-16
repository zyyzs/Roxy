package lol.tgformat.ui.notifications;

import lol.tgformat.module.ModuleManager;
import lol.tgformat.module.impl.render.NotificationsMod;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.CopyOnWriteArrayList;

public class NotificationManager {
    @Getter
    @Setter
    private static float toggleTime = 2;

    @Getter
    private static final CopyOnWriteArrayList<Notification> notifications = new CopyOnWriteArrayList<>();

    public static void post(NotificationType type, String title, String description) {
        post(new Notification(type, title, description));
    }

    public static void post(NotificationType type, String title, String description, float time) {
        post(new Notification(type, title, description, time));
    }

    private static void post(Notification notification) {
        if (ModuleManager.getModule(NotificationsMod.class).isState()){
            notifications.add(notification);
        }
    }

}
