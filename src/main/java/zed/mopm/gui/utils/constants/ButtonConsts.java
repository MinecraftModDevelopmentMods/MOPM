package zed.mopm.gui.utils.constants;

public class ButtonConsts {
    /**
     * Cancel button label.
     */
    public static final String CANCEL = "Cancel";
    /**
     * Create button label.
     */
    public static final String CREATE = "Create";

    /**
     * Cancel/Exit button id.
     */
    public static final int EXIT_ID = 1;
    /**
     * Create/Select button id.
     */
    public static final int CREATE_ID = 2;

    /**
     * Standard button width.
     */
    public static final int BUTTON_WIDTH = 55;
    /**
     * Standard button height.
     */
    public static final int BUTTON_HEIGHT = 20;
    /**
     * Standard element spacing.
     */
    public static final int SPACE_WIDTH = 5;

    /**
     * Standard text field width.
     */
    public static final int TEXT_FIELD_WIDTH = 200;
    /**
     * Standard starting x position.
     */
    public static final int STARTING_X = 50;
    /**
     * Standard Select / Create button x position.
     */
    public static final int SELECT_X = TEXT_FIELD_WIDTH
            + STARTING_X
            + SPACE_WIDTH;
    /**
     * Standard Cancel / Exit button x position.
     */
    public static final int EXIT_X = SELECT_X
            + BUTTON_WIDTH
            + SPACE_WIDTH;

    /**
     * Standard Y offset location.
     */
    public static final int Y_OFFSET = 63;
    /**
     * Standard Y alignment from the offset.
     */
    public static final int Y_ALIGN = 20;
    /**
     * Standard text field offset.
     */
    public static final int TEXT_OFFSET = 10;
}
