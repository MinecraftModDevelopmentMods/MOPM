package zed.mopm.gui.elements.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

import java.util.List;

import static zed.mopm.gui.utils.constants.MenuConsts.*;
import static zed.mopm.gui.utils.constants.ButtonConsts.*;

public class ButtonFactory {
    private final int id;
    private final int x;
    private final int y;

    public ButtonFactory(final int btnId, final int btnX, final int btnY) {
        this.id = btnId;
        this.x = btnX;
        this.y = btnY;
    }

    public ToolTipButton createDirectoryButton() {
        return new ToolTipButton(
                id,
                x,
                y,
                SQUARE_BUTTON_DIM,
                SQUARE_BUTTON_DIM,
                "+",
                "New Folder"
        );
    }

    public ToolTipButton backButton() {
        return new ToolTipButton(
                id,
                x,
                y,
                BACK_BTN_WIDTH,
                SQUARE_BUTTON_DIM,
                "<<",
                "Back"
        );
    }

    public ToolTipButton saveButton() {
        return new ToolTipButton(
                id,
                x,
                y,
                SQUARE_BUTTON_DIM,
                SQUARE_BUTTON_DIM,
                "/",
                "Save"
        );
    }

    public ToolTipButton hidePathButton() {
        return new ToolTipButton(
                id,
                x,
                y,
                SPACE_WIDTH,
                SPACE_WIDTH,
                "",
                "Hide"
        );
    }

    public ToolTipButton defaultButton(final String label, final String toolTip) {
        return new ToolTipButton(
                id,
                x,
                y,
                BUTTON_WIDTH,
                BUTTON_HEIGHT,
                label,
                toolTip
        );
    }

    public static void drawButtonToolTips(
            final List<GuiButton> buttonList,
            final Minecraft mc,
            final int mouseX,
            final int mouseY,
            final float partialTicks
    ) {
        int mouseOver = -1;
        for (final GuiButton button : buttonList) {
            button.drawButton(mc, mouseX, mouseY, partialTicks);
            if (button.isMouseOver()) {
                mouseOver = buttonList.indexOf(button);
            }
        }

        if (
                mouseOver != -1
                        && buttonList.get(mouseOver)
                        instanceof ToolTipButton
        ) {
            ((ToolTipButton) buttonList.get(mouseOver))
                    .drawHoverState(mc, mouseX, mouseY);
        }
    }
}
