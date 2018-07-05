package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import zed.mopm.data.FolderEntry;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.gui.mutators.EditDirectory;
import zed.mopm.util.ColorUtils;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.References;

import java.io.File;
import java.util.*;

public class FolderList <K> extends ModifiableList {
    private GuiScreen container;

    private FolderEntry<K> base;
    private FolderEntry<K> currentDir;
    private Deque<String> currentPath = new LinkedList<>();
    private boolean clone = false;

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param parentIn
     * @param widthIn
     * @param heightIn
     * @param topIn
     * @param slotHeightIn
     * @param saveIn
     */
    public FolderList(GuiScreen parentIn, int widthIn, int heightIn, int topIn, int slotHeightIn, File saveIn) {
        this(widthIn, heightIn, topIn, slotHeightIn);
        if (parentIn instanceof SinglePlayerMenu) {
            base.load(new File(saveIn, "mopm_ssp.dat"));
        }
        else if (parentIn instanceof MultiplayerMenu) {
            base.load(new File(saveIn, "mopm_smp.dat"));
        }
        container = parentIn;
    }

    /**
     *
     * @param widthIn
     * @param heightIn
     * @param topIn
     * @param slotHeightIn
     */
    private FolderList(int widthIn, int heightIn, int topIn, int slotHeightIn) {
        super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, 0, slotHeightIn);
        base = new FolderEntry("base");
        this.currentDir = base;
        this.selectedElement = currentDir.getDepth();
    }

    /**
     *
     * @param copyFrom
     */
    public FolderList(FolderList copyFrom) {
        this(copyFrom.width, copyFrom.height, copyFrom.top, copyFrom.slotHeight);
        this.container = null;
        this.clone = true;
        this.headerPadding = 1;
        this.base = new FolderEntry(copyFrom.base);
        this.currentDir = new FolderEntry(copyFrom.currentDir);
        this.currentPath = new LinkedList<>();
        this.currentPath.addAll(copyFrom.currentPath);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://


    //:: GuiListExtended
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     *
     * @param slotIndex
     * @param isDoubleClick
     * @param mouseX
     * @param mouseY
     */
    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        if (isDoubleClick) {
            currentDir = currentDir.stepDown(slotIndex);
            currentPath.push(currentDir.getUniqueName());
        }
    }

    /**
     *
     * @param mouseX
     * @param mouseY
     * @param mouseEvent
     * @return
     */
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        boolean success = super.mouseClicked(mouseX, mouseY, mouseEvent);
        if (success) {
            this.delete(this.getSlotIndexFromScreenCoords(mouseX, mouseY));
        }
        else if (mouseEvent == 1 && this.getSlotIndexFromScreenCoords(mouseX, mouseY) != -1) {
            this.mc.displayGuiScreen(new EditDirectory(this.container, mouseX, mouseY, this));
        }
        return success;
    }

    /**
     *
     * @param mouseX
     * @param mouseY
     * @param partialTicks
     */
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int background = ColorUtils.getARGB(0, 0, 0, 100);
        GuiUtils.drawGradientRect(0, 32, this.width, this.bottom, background, background, 0);
    }

    /**
     *
     * @param index
     * @return
     */
    @Override
    public IGuiListEntry getListEntry(int index) {
        return getFolder().getDirectory(index);
    }

    /**
     *
     * @return
     */
    @Override
    public int getListWidth() {
        return this.width;
    }

    /**
     *
     * @return
     */
    @Override
    protected int getSize() {
        return currentDir.folders();
    }

    /**
     *
     * @return
     */
    @Override
    protected int getScrollBarX() {
        return this.right;
    }

    //:: Object
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    //:: ModifiableList
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public void rename(int entryIndex, String name) {
        getFolder().renameDir(entryIndex, name);
    }

    public void delete(int entryIndex) {
        getFolder().removeDir(entryIndex);
    }

    public void changeDir(int entryIndex) {

    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    /**
     * Some functionality is not applicable to clone instances of this class.
     * This can be used to determine if the instance in use is a clone of another instance.
     *
     * @return true if the object referenced is a deep copy.
     */
    public boolean isClone() {
        return this.clone;
    }

    /**
     * Sets the display height of the list container.
     *
     * @param height
     */
    public void setHeight(int height) {
        this.bottom = height - 64;
    }

    /**
     * Navigates through the base folder based on where the current directory is located.
     * Visually, this will move up a directory level from the directory branch that has been
     * moved into.
     */
    public void back() {
        if (!currentPath.isEmpty()) {
            currentPath.pop();
            currentDir = getFolder();
        }
    }

    /**
     * Adds a folder to the base directory.
     * This folder is inserted to the current directory.
     *
     * @param name
     */
    public void addFolder(String name) {
        getFolder().newFolder(name);
    }

    /**
     * returns the folder entry that is currently in focus.
     *
     * @return
     */
    public FolderEntry getFolder() {
        Queue<String> temp = new ArrayDeque<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.poll();
            while (!temp.isEmpty()) {
                path += "/" + temp.remove();
            }
            return base.folderPath(path);
        }
        catch (NullPointerException e) {
            return base;
        }
    }

    /**
     * returns the path of the currently displayed directory location.
     * use currentUniquePath() to get a functional path.
     *
     * @return returns a vanity path string.
     */
    public String currentPath() {
        Queue<String> temp = new ArrayDeque<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.poll();
            if (path == null) {
                throw new NullPointerException();
            }

            path = path.substring(0, path.lastIndexOf('#'));

            while (!temp.isEmpty()) {
                String append = temp.remove();
                path += "/" + append.substring(0, append.lastIndexOf('#'));
            }
            return "base/" + path;
        }
        catch (NullPointerException e) {
            return "base";
        }
    }

    /**
     * directories with the same name might conflict with using the path for navigation.
     * All directories have unique names that should be used for navigation.
     *
     * @return returns a usable, navigable path
     */
    public String uniquePath() {
        Queue<String> temp = new ArrayDeque<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.poll();
            if (path == null) {
                throw new NullPointerException();
            }

            while (!temp.isEmpty()) {
                path += "/" + temp.remove();
            }
            return "base#0/" + path;
        }
        catch (NullPointerException e) {
            return "base#0";
        }
    }

    /**
     *
     */
    public void print() {
        References.LOG.info(this.base);
    }
}
