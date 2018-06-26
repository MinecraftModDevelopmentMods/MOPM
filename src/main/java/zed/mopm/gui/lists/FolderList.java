package zed.mopm.gui.lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import zed.mopm.data.FolderEntry;
import zed.mopm.util.ColorUtils;
import zed.mopm.util.GuiUtils;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.Stack;

public class FolderList <K> extends GuiListExtended implements Cloneable {

    private FolderEntry<K> base;
    private FolderEntry<K> currentDir;
    private Stack<String> currentPath = new Stack<>();

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Constructors:------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public FolderList(int widthIn, int heightIn, int topIn, int slotHeightIn, File saveIn) {
        this(widthIn, heightIn, topIn, slotHeightIn);
        base.load(new File(saveIn, "mopm_ssp.dat"));
    }

    private FolderList(int widthIn, int heightIn, int topIn, int slotHeightIn) {
        super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, 0, slotHeightIn);
        base = new FolderEntry("base");
        this.currentDir = base;
        this.selectedElement = currentDir.getDepth();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----Overridden Methods:------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    @Override
    protected void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
        if (isDoubleClick) {
            currentDir = currentDir.stepDown(slotIndex);
            currentPath.push(currentDir.getUniqueName());
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        int background = ColorUtils.getARGB(0, 0, 0, 100);
        GuiUtils.drawGradientRect(0, 32, this.width, this.bottom, background, background, 0);
    }

    @Override
    public IGuiListEntry getListEntry(int index) {
        return getFolder().getDirectory(index);
    }

    @Override
    public int getListWidth() {
        return this.width;
    }

    @Override
    protected int getSize() {
        return currentDir.folders();
    }

    @Override
    protected int getScrollBarX() {
        return this.right;
    }

    @Override
    public FolderList clone() {
        FolderList clone = new FolderList(this.width, this.height, this.top, this.slotHeight);
        clone.headerPadding = 1;
        clone.base = this.base.clone();
        clone.currentDir = this.currentDir.clone();
        clone.currentPath = new Stack();
        clone.currentPath.addAll(this.currentPath);
       return clone;
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://
    //-----This:--------------------------------------------------------------------------------------//
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::://

    public void setHeight(int height) {
        this.bottom = height - 64;
    }

    public void back() {
        if (!currentPath.empty()) {
            currentPath.pop();
            currentDir = getFolder();
        }
    }

    public void addFolder(String name) {
        getFolder().newFolder(name);
    }

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

    public void print() {
        System.out.println(this.base);
    }
}
