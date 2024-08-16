package lol.tgformat.utils.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegionalAbuseUtil {
    public static String country;
    public static void getAddressByIP() {
        try {
            final URL url = new URL("https://ip.appworlds.cn");
            final HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            final int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                final StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                final String jsonResponse = response.toString();
                final Pattern pattern = Pattern.compile("\"province\":\"(.*?)\"");
                final Matcher matcher = pattern.matcher(jsonResponse);
                if (matcher.find()) {
                    RegionalAbuseUtil.country = matcher.group(1).replace("уюЂ", "");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    static {
        RegionalAbuseUtil.country = "妈妈";
    }
}
