package zed.mopm.util;

public class PathFormatter {
    private PathFormatter() { }

    public static String ensurePathFormat(String path) {
        if (path.startsWith(MOPMLiterals.BASE_DIR + "/")) {
            path = path.substring((MOPMLiterals.BASE_DIR + "/").length());
        } else {
            path = "";
        }

        return path;
    }
}
