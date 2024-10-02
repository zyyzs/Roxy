package lol.tgformat.firend;

import lol.tgformat.module.impl.misc.IRC;
import net.minecraft.entity.Entity;

/**
 * @author TG_format
 * @since 2024/7/23 上午12:09
 */
public class FriendsCollection {
    public static String[] friends = new String[] {};
    public static boolean isFriend(Entity entity) {
        for (String name : friends) {
            if (entity.getName().equals(name)) {
                return true;
            }
        }
        return IRC.transport != null && IRC.transport.isUser(entity.getName());
    }
    public static boolean isFriend(String name) {
        for (String friend : friends) {
            if (name.equals(friend)) {
                return true;
            }
        }
        return false;
    }
    public static void addFriend(String name) {
        if (isFriend(name)) return;
        StringBuilder friendsName = new StringBuilder();
        for (String firend : friends) {
            friendsName.append(firend).append(" ");
        }
        friendsName.append(name).append(" ");
        friends = friendsName.toString().split(" ");
    }
    public static void removeFriend(String name) {
        StringBuilder friendsName = new StringBuilder();
        for (String firend : friends) {
            friendsName.append(firend).append(" ");
        }
        friends = friendsName.toString().replace(name + " ", "").split(" ");
    }
}
