package zed.mopm.gui.mutators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import zed.mopm.api.data.Editor;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.api.data.IModifiableList;
import zed.mopm.gui.buttons.ContextButton;

import java.io.IOException;

public class EditDirectory<T extends GuiListExtended & IModifiableList> extends GuiScreen {
    //:: Possible entries
    /* Open
     * Rename
     * Delete
     */
    private GuiScreen parentIn;
    private int entryIndex;
    private boolean canClose;
    private T modifiableList;

    private ContextButton delete;
    private ContextButton rename;
    private ContextButton move;

    private GuiTextField changeName;

    public EditDirectory(GuiScreen parentIn, int mouseX, int mouseY, boolean doClose, T list) {
        this.parentIn = parentIn;
        this.modifiableList = list;
        this.entryIndex = list.getSlotIndexFromScreenCoords(mouseX, mouseY);
        this.canClose = doClose;

        delete = new ContextButton(Editor.DELETE, mouseX, mouseY, "Delete");
        rename = new ContextButton(Editor.RENAME, mouseX, mouseY + 10, "Rename");
        move = new ContextButton(Editor.CHANGE_DIRECTORY, mouseX, mouseY + 20, "Move");

        IDrawableListEntry temp = (IDrawableListEntry) list.getListEntry(entryIndex);
        changeName = new GuiTextField(0, Minecraft.getMinecraft().fontRenderer, temp.getX(), temp.getY(), list.getListWidth(), list.getSlotHeight());
        changeName.setMaxStringLength(Integer.MAX_VALUE);
        changeName.setText(temp.drawableText());
        changeName.setVisible(false);
    }

    public EditDirectory(GuiScreen parentIn, int mouseX, int mouseY, T list) {
        this(parentIn, mouseX, mouseY, true, list);
    }

    @Override
    public void initGui() {
        this.addButton(this.delete);
        this.addButton(this.rename);
        this.addButton(this.move);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.parentIn.drawScreen(mouseX, mouseY, partialTicks);

        if (this.changeName.getVisible()) {
            this.changeName.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Editor action = Editor.values()[button.id];
        switch (action) {
            case DELETE: {
                this.modifiableList.delete(this.entryIndex);
                if (canClose) {
                    closeGui();
                }
            }
            break;

            case RENAME: {
                this.changeName.setVisible(true);
                this.changeName.setFocused(true);
            }
            break;

            case CHANGE_DIRECTORY: {
                this.modifiableList.changeDir(this.entryIndex);
            }
            break;
        }

        this.delete.visible = false;
        this.rename.visible = false;
        this.move.visible = false;
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (keyCode == 28) {
            this.modifiableList.rename(this.entryIndex, this.changeName.getText());
            closeGui();
        }
        if (this.changeName.isFocused()) {
            this.changeName.textboxKeyTyped(typedChar, keyCode);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        if (!(this.delete.mousePressed(this.mc, mouseX, mouseY) || this.rename.mousePressed(this.mc, mouseX, mouseY) || this.move.mousePressed(this.mc, mouseX, mouseY))) {
            closeGui();
        }
        else {
            super.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawDefaultBackground() {
        // Do not draw any background
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h) {
        this.parentIn.onResize(mcIn, w, h);
    }

    @Override
    public void updateScreen() {
        this.changeName.updateCursorCounter();
    }

    /**
     * This operation should be used if there is an external operation that should override
     * when the context menu closes. Otherwise, gui closing will be handled by the context
     * menu.
     *
     * This method sets canClose to false.
     */
    public void toggleClose() {
        this.canClose = false;
    }

    private void closeGui() {
        this.mc.displayGuiScreen(parentIn);
    }

}
