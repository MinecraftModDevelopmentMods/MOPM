package zed.mopm.api.gui;

import net.minecraft.client.gui.GuiButton;
import zed.mopm.gui.ModifiableMenu;
import java.util.List;

public interface IMenuType {
    void invokeEntryCreation(ModifiableMenu menu);
    List<GuiButton> getButtonList();
}
