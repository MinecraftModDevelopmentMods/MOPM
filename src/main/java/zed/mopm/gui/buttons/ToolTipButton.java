package zed.mopm.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.util.GuiUtils;

public class ToolTipButton extends GuiButtonExt {
    private String hoverText;

    public ToolTipButton(int id, int xPos, int yPos, String displayString, String hoverText) {
        super(id, xPos, yPos, displayString);
        this.hoverText = hoverText;
    }

    public ToolTipButton(int id, int xPos, int yPos, int width, int height, String displayString, String hoverText) {
        super(id, xPos, yPos, width, height, displayString);
        this.hoverText = hoverText;
    }

    public void drawHoverState(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            GuiUtils.drawToolTip(mc.fontRenderer, hoverText, mouseX + 3, mouseY - 3, mc.fontRenderer.getStringWidth(hoverText), mc.fontRenderer.FONT_HEIGHT + 5);
        }
    }

    public void setToolTip(String toolTip) {
        this.hoverText = toolTip;
    }
}
