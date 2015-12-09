package org.satelliteglasses.comorbidityrank;

import java.util.List;

public final class StringUtils {

    public static String join(final List list, final String conjunction) {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < list.size() - 1; i++) {
            builder.append(list.get(i)).append(conjunction);
        }

        builder.append(list.size() - 1);
        return builder.toString();
    }
}
