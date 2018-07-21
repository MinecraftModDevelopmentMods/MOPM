package zed.mopm.gui.buttons;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.api.data.Editor;
import zed.mopm.util.ColorUtils;

public class ContextButton extends GuiButtonExt {
    Editor function;

    public ContextButton(final Editor e, final int xPos, final int yPos, final String displayString) {
        super(e.ordinal(), xPos, yPos, 55, 10, displayString);
        function = e;
    }

    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY, final float partial) {
        if (this.visible) {
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int white = (this.hovered) ? ColorUtils.getARGB(160, 160, 160, 255) : ColorUtils.getARGB(255, 255, 255, 255);
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
