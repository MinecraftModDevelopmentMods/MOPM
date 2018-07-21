package zed.mopm.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.util.GuiUtils;

public class ToolTipButton extends GuiButtonExt {
    private String hoverText;

    public ToolTipButton(final int id, final int xPos, final int yPos, final String displayString, final String hoverTextIn) {
        super(id, xPos, yPos, displayString);
        this.hoverText = hoverTextIn;
    }

    public ToolTipButton(final int id, final int xPos, final int yPos, final int width, final int height, final String displayString, final String hoverTextIn) {
        super(id, xPos, yPos, width, height, displayString);
        this.hoverText = hoverTextIn;
    }

    public void drawHoverState(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            GuiUtils.drawToolTip(mc.fontRenderer, hoverText, mouseX + 3, mouseY - 3, mc.fontRenderer.getStringWidth(hoverText), mc.fontRenderer.FONT_HEIGHT + 5);
        }
    }

    public void setToolTip(final String toolTip) {
        this.hoverText = toolTip;
    }
}
