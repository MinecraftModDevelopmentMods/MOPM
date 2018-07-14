package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import zed.mopm.api.data.IFolderMenu;
import zed.mopm.api.data.IFolderPath;
import zed.mopm.data.FolderEntry;
import zed.mopm.gui.MultiplayerMenu;
import zed.mopm.gui.SinglePlayerMenu;
import zed.mopm.gui.mutators.EditDirectory;
import zed.mopm.util.ColorUtils;
import zed.mopm.util.GuiUtils;
import zed.mopm.util.MOPMLiterals;
import zed.mopm.util.References;

import java.io.File;
import java.util.*;

public class FolderList <K extends IFolderPath> extends ModifiableList {
    private GuiScreen container;

    private File saveFile;
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
            saveFile = new File(saveIn, MOPMLiterals.MOPM_SSP);
        }
        else if (parentIn instanceof MultiplayerMenu) {
            saveFile = new File(saveIn, MOPMLiterals.MOPM_SMP);
        }

        base.load(saveFile);
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
        base = new FolderEntry(MOPMLiterals.BASE_DIR_NAME);
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
     * If a directory entry is entered into, update the current directory being looked at,
     * and add the current directory location to the path.
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
            ((IFolderMenu)container).refreshDirectoryEntryList();
        }
    }

    /**
     * Handles mouse clicks.
     * If x area was successfully clicked, delete the directory located at the cursor's coordinates.
     * If the mouse was right clicked, open up the context menu to choose more actions.
     *
     * @param mouseX
     * @param mouseY
     * @param mouseEvent
     * @return Returns true if a directory entry was successfully clicked.
     *         Returns false otherwise.
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
     * Draws the list.
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
     * @param index Directory location index
     * @return Returns the list entry representation of the directory located at the index of the currently displayed directory list.
     */
    @Override
    public IGuiListEntry getListEntry(int index) {
        return getFolder().getDirectory(index);
    }

    /**
     * @return Returns the width of the directory list.
     */
    @Override
    public int getListWidth() {
        return this.width;
    }

    /**
     * @return Gets the number of displayed directories
     */
    @Override
    protected int getSize() {
        return currentDir.folders();
    }

    /**
     * @return Returns the x position of the scroll bar
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
        //:: Todo: add feature
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
            ((IFolderMenu)container).refreshDirectoryEntryList();
        }
    }

    /**
     * Adds a folder to the base directory.
     * This folder is inserted to the current directory.
     *
     * @param name The name of the new directory.
     */
    public void addFolder(String name) {
        getFolder().newFolder(name);
    }

    public void populateDirectoryList(List<K> entries) {
        for (K entry : entries) {
            try {
                String populateTo = entry.getPathToDir();
                if (populateTo.equals(MOPMLiterals.BASE_DIR)) {
                    base.newEntry(entry);
                }
                else {
                    base.folderPath(populateTo.substring((MOPMLiterals.BASE_DIR + "/").length())).newEntry(entry);

                }
            }
            catch (NoSuchElementException e) {
                FolderEntry.writeWorldToBase(entry.getMopmSaveData());
            }
        }
    }

    /**
     * returns the folder entry that is currently in focus.
     *
     * @return Returns the current directory being browsed.
     */
    public FolderEntry<K> getFolder() {
        String path = this.uniquePath();

        if (path.equals(MOPMLiterals.BASE_DIR)) {
            return base;
        }
        return base.folderPath(this.uniquePath().substring((MOPMLiterals.BASE_DIR + "/").length()));
    }

    /**
     * returns the path of the currently displayed directory location.
     * use currentUniquePath() to get a functional path.
     *
     * @return returns a vanity path string.
     */
    public String currentPath() {
        Deque<String> temp = new ArrayDeque<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.removeLast();
            path = path.substring(0, path.lastIndexOf('#'));
            while (!temp.isEmpty()) {
                String append = temp.removeLast();
                path += "/" + append.substring(0, append.lastIndexOf('#'));
            }
            return MOPMLiterals.BASE_DIR_NAME + "/" + path;
        }
        catch (NoSuchElementException e) {
            return MOPMLiterals.BASE_DIR_NAME;
        }
    }

    /**
     * directories with the same name might conflict with using the path for navigation.
     * All directories have unique names that should be used for navigation.
     *
     * @return returns a usable, navigable path
     */
    public String uniquePath() {
        Deque<String> temp = new LinkedList<>();
        temp.addAll(this.currentPath);
        try {
            String path = temp.removeLast();
            while (!temp.isEmpty()) {
                path += "/" + temp.removeLast();
            }
            return MOPMLiterals.BASE_DIR + "/" + path;
        }
        catch (NoSuchElementException e) {
            return MOPMLiterals.BASE_DIR;
        }
    }

    /**
     * Writes the directory tree to a file to persist through game sessions.
     *
     * @return Returns true if the directory tree was successfully saved.
     *         Returns false if an error occurred during the saving process.
     */
    public boolean save() {
        return this.base.save(this.saveFile);
    }

    /**
     *
     */
    public void print() {
        References.LOG.info(this.base);
    }
}
