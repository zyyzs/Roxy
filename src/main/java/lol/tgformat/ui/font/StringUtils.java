package lol.tgformat.ui.font;

import lol.tgformat.module.Module;

import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author TG_format
 * @since 2024/6/9 下午7:21
 */
public class StringUtils {

    public static String findLongestModuleName(List<Module> modules) {
        return Collections.max(modules, Comparator.comparing(module -> (module.getName() + (module.hasMode() ? " " + module.getSuffix() : "")).length())).getName();
    }

    public static String getLongestModeName(List<String> listOfWords) {
        String longestWord = null;
        for (String word : listOfWords) {
            if (longestWord == null || word.length() > longestWord.length()) {
                longestWord = word;
            }
        }
        return longestWord != null ? longestWord : "";
    }

    public static String b64(Object o) {
        return Base64.getEncoder().encodeToString(String.valueOf(o).getBytes());
    }

}
