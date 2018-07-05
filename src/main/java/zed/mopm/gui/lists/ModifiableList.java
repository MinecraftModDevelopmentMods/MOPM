package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;

public abstract class ModifiableList extends GuiListExtended {
    public ModifiableList(Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    public abstract void rename(int entryIndex, String name);
    public abstract void delete(int entryIndex);
    public abstract void changeDir(int entryIndex);
}
