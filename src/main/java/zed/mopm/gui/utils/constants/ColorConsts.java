package zed.mopm.gui.utils.constants;

import zed.mopm.gui.utils.ColorUtils;
import zed.mopm.util.MOPMLiterals;

public class ColorConsts {
    private ColorConsts() { }

    /**
     * Max color int val.
     */
    public static final int COLOR_MAX = 255;
    /**
     * Default Background opacity.
     */
    private static final int BACKGROUND_OPACITY = 100;
    /**
     * Default Background color.
     */
    public static final int BACKGROUND_COLOR = ColorUtils.getARGB(
            0,
            0,
            0,
            BACKGROUND_OPACITY
    );
    /**
     * Solid Black
     */
    public static final int PURE_BLACK = ColorUtils.getARGB(
            0,
            0,
            0,
            COLOR_MAX
    );
    /**
     * The transparent color
     */
    public static final int TRANSPARENT = ColorUtils.getARGB(
            0,
            0,
            0,
            0
    );
    /**
     * Pure White
     */
    public static final int PURE_WHITE = ColorUtils.getARGB(
            COLOR_MAX,
            COLOR_MAX,
            COLOR_MAX,
            COLOR_MAX
    );
    /**
     * The int value of off white.
     */
    private static final int OFF_WHITE = 160;
    /**
     * The color of off white.
     */
    public static final int OFF_WHITE_COLOR = ColorUtils.getARGB(
            OFF_WHITE,
            OFF_WHITE,
            OFF_WHITE,
            COLOR_MAX
    );
    /**
     * The color of true white.
     */
    public static final int TRUE_WHITE = ColorUtils.getARGB(
            COLOR_MAX,
            COLOR_MAX,
            COLOR_MAX,
            COLOR_MAX
    );
    /**
     * The int value of off black.
     */
    private static final int OFF_BLACK = 81;
    /**
     * The color of off black.
     */
    public static final int OFF_BLACK_COLOR = ColorUtils.getARGB(
            OFF_BLACK,
            OFF_BLACK,
            OFF_BLACK,
            COLOR_MAX
    );
}
