package lol.tgformat.firend;

import net.minecraft.entity.Entity;

/**
 * @author TG_format
 * @since 2024/7/23 上午12:09
 */
public class FriendsCollection {
    public static String[] friends = new String[] {};
    public static boolean isIRCFriend(Entity entity) {
        for (String name : friends) {
            if (entity.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
