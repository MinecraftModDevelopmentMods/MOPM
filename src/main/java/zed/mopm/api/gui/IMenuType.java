package zed.mopm.api.gui;

import net.minecraft.client.gui.GuiButton;
import zed.mopm.api.gui.lists.IListType;
import zed.mopm.gui.menus.base.SelectMenuBase;

import java.util.List;

public interface IMenuType {
    void listInit(IListType list);
    void invokeEntryCreation(SelectMenuBase menu);
    List<GuiButton> getButtonList();
}
