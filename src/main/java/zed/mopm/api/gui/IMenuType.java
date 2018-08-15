package zed.mopm.api.gui;

import net.minecraft.client.gui.GuiButton;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.api.gui.lists.IModifiableList;
import zed.mopm.gui.ModifiableMenu;
import java.util.List;

public interface IMenuType {
    void listInit(IListType list);

    void invokeEntryCreation(ModifiableMenu menu);

    List<GuiButton> getButtonList();
}
