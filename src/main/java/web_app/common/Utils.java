package web_app.common;

import org.ini4j.Ini;
import org.ini4j.Profile;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Properties;

public class Utils {

    private Utils() {}

    public static boolean isInteger(@NotNull String string) {
        return string.matches("^\\d+$");
    }

    public static String numbersListToString(@NotNull List<Integer> numbers, String separator) {
        return numbers.stream()
                .map(String::valueOf)
                .reduce("", (str1, str2) -> str1.isEmpty() ? str2 : str1 + separator + str2);
    }

    public static void addConfigParams(@NotNull Ini config, String sectionName, @NotNull String[] params, @NotNull Properties properties) {
        Profile.Section section = config.get(sectionName);

        for (String param : params) {
            String val = section.get(param);

            if (val != null) {
                properties.setProperty(param, val);
            }
        }
    }

    public static void addAllConfigParamsFromSection(@NotNull Ini config,
                                                     @NotNull String sectionName,
                                                     @NotNull Properties properties) {

        Profile.Section section = config.get(sectionName);

        for (String key : section.keySet()) {
            properties.setProperty(key, section.get(key));
        }
    }
}
