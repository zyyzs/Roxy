package lol.tgformat.firend;

import lol.tgformat.irc.items.User;
import net.minecraft.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/7/23 上午12:09
 */
public class FriendsCollection {
    public static List<String> friendsNames = new ArrayList<>();
    public static List<User> IRC_friends = new ArrayList<>();
    public static boolean isIRCFriend(Entity entity) {
        for (User user : IRC_friends) {
            return user.getIGN().equals(entity.getDisplayName().getUnformattedText());
        }
        return false;
    }
}
