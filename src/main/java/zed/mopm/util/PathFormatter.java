package zed.mopm.util;

import static zed.mopm.util.MOPMLiterals.BASE_DIR;
import static zed.mopm.util.MOPMLiterals.MOPM_PATH_DELIM;

public final class PathFormatter {
    private PathFormatter() { }

    /**
     * Ensures that the path is formatted correctly.
     * @param pathIn The path to check the format of.
     * @return Returns a string with the proper path format.
     */
    public static String ensurePathFormat(final String pathIn) {
        String path = pathIn;
        if (path.startsWith(BASE_DIR + MOPM_PATH_DELIM)) {
            path = path.substring(
                    (BASE_DIR + MOPM_PATH_DELIM)
                            .length()
            );
        } else {
            path = "";
        }

        return path;
    }
}
