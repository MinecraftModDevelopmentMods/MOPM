package zed.mopm.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.data.Editor;
import zed.mopm.util.ColorUtils;

public class ListButton extends GuiButtonExt {
    Editor function;

    public ListButton(Editor e, int xPos, int yPos, String displayString) {
        super(e.ordinal(), xPos, yPos, 55, 10, displayString);
        function = e;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partial) {
        if (this.visible) {
            int white = ColorUtils.getARGB(255, 255, 255, 255);
            int black = ColorUtils.getARGB(81, 81, 81, 255);

            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, white, white); // - Body

            this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + 1, black, black);
            this.drawGradientRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 1, black, black);
            this.drawGradientRect(this.x + this.width, this.y, this.x + this.width + 1, this.y + this.height + 1, black, black);
            this.drawGradientRect(this.x, this.y, this.x + 1, this.y + this.height, black, black);

            Minecraft.getMinecraft().fontRenderer.drawString(this.displayString, this.x + 3F, this.y + (this.height / 2F) - 3F, black, false);
        }
    }
}