package zed.mopm.util;

public class PathFormatter {
    private PathFormatter() { }

    public static String ensurePathFormat(String path) {
        if (path.startsWith(MOPMLiterals.BASE_DIR + MOPMLiterals.MOPM_PATH_DELIM)) {
            path = path.substring((MOPMLiterals.BASE_DIR + MOPMLiterals.MOPM_PATH_DELIM).length());
        } else {
            path = "";
        }

        return path;
    }
}
