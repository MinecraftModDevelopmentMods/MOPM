package zed.mopm.api.gui.mutators;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import zed.mopm.gui.mutators.CreateEntryMenu;

import java.util.List;
import java.io.IOException;

public interface ICreatorMenu {

    GuiTextField getTextField();

    GuiButtonExt getSelectionButton(final int screenWidth);

    List<GuiButton> getButtons();

    void handleActionPerformed(final GuiButton btn, final CreateEntryMenu savePath) throws IOException;

    void doKeyTyped(final char typedChar, final int keyCode) throws IOException;

    void doMouseClicked(final int mouseX, final int mouseY, final int mouseButton) throws IOException;

    void doMouseReleased(final int mouseX, final int mouseY, final int state) throws IOException;

}
