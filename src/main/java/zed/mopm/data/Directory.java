package zed.mopm.data;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import zed.mopm.api.data.IDrawableListEntry;
import zed.mopm.gui.utils.ColorUtils;
import zed.mopm.gui.utils.GuiUtils;
import zed.mopm.gui.utils.constants.MenuConsts;
import zed.mopm.util.References;

import java.util.ArrayList;
import java.util.List;

import static zed.mopm.gui.utils.constants.ColorConsts.COLOR_MAX;
import static zed.mopm.gui.utils.constants.ColorConsts.PURE_WHITE;

public class Directory
        implements GuiListExtended.IGuiListEntry,
        IDrawableListEntry {

    //-----Consts:--------------------------------------//

    /**
     * File location of the trash icon.
     */
    private static final String TRASH_ICO = "textures/gui/trash_5.png";
    /**
     * Used prevent text overflow if text is longer than
     * the list width.
     */
    private static final String ELLIPSES = ". . .";
    /**
     * Padding text is roughly how long the list width is.
     */
    private static final String STR_PADDING = "   ";

    /**
     * Hover line color R.
     */
    private static final int HOVER_R = 31;
    /**
     * Hover line color G.
     */
    private static final int HOVER_G = 58;
    /**
     * Hover line color B.
     */
    private static final int HOVER_B = 112;

    /**
     * Text xLoc yLoc relative position.
     */
    private static final int TEXT_X_Y = 5;

    /**
     * Trash icon xLoc relative position.
     */
    private static final int TRASH_X = 20;
    /**
     * Trash icon width and height.
     */
    private static final int TRASH_DIM = 16;
    /**
     * Trash icon texture width and height.
     */
    private static final float TRASH_TEX_DIM = 16f;
    /**
     * Trash icon U mapping.
     */
    private static final float TRASH_U = 32f;

    /**
     * Hover line xLoc relative position.
     */
    private static final int HOVER_LINE_X = 5;
    /**
     * Hover line yLoc relative position.
     */
    private static final int HOVER_LINE_Y = 15;
    /**
     * Hover line padding from the right end of the
     * list.
     */
    private static final int HOVER_LINE_OFFSET = 7;
    /**
     * Hover line start from the bottom of the slot.
     */
    private static final int HOVER_LINE_BOTTOM = 16;

    //-----Fields:--------------------------------------//

    /**
     * The name of the directory.
     */
    private String dirName;
    /**
     * The unique iteration of the directory.
     */
    private String uniqueDirName;

    /**
     * Directory list width.
     */
    private int width;
    /**
     * Directory list focused slot xLoc position.
     */
    private int xLoc;
    /**
     * Directory list focused slot yLoc position.
     */
    private int yLoc;
    /**
     * The hover line color.
     */
    private static int hoverColor = ColorUtils.getARGB(
            HOVER_R,
            HOVER_G,
            HOVER_B,
            COLOR_MAX
    );
    /**
     * Trash icon resource location.
     */
    private static final ResourceLocation ICON_TRASH
            = new ResourceLocation(References.MODID, TRASH_ICO);

    //-----Constructors:--------------------------------//

    /**
     * Creates a new directory with the given parameters.
     * @param name The name of the directory.
     * @param uuid The unique iteration of the directory.
     */
    public Directory(final String name, final String uuid) {
        this.dirName = name;
        this.uniqueDirName = uuid;
    }

    //-----Interfaces:----------------------------------//

    //:: GuiListExtended.IGuiListEntry
    //:::::::::::::::::::::::::::::://

    /**
     * This method does nothing. There is no need for it,
     * but it must be overridden regardless.
     * @param slotIndex The focused slot index.
     * @param x The focused slot xLoc position.
     * @param y The focused slot yLoc position.
     * @param partialTicks Partial game ticks.
     */
    @Override
    public void updatePosition(
            final int slotIndex,
            final int x,
            final int y,
            final float partialTicks
    ) {
        //:: Does not have any functional use.
        // - This might be used to re organize folders in the future.
    }

    /**
     * This draws every directory slot. <br>
     * Drawn elements include: <br>
     * <ul>
     *     <li>The directory name</li>
     *     <li>Hovered underline decoration</li>
     *     <li>Hovered trash icon</li>
     *     <li>Hovered tool tip if the directory name is long</li>
     * </ul>
     * @param slotIndex Slot index of the directory.
     * @param x The relative xLoc position of the slot.
     * @param y The relative yLoc position of the slot.
     * @param listWidth The list width.
     * @param slotHeight The slot height.
     * @param mouseX The xLoc position of the mouse.
     * @param mouseY The yLoc position of the mouse.
     * @param isSelected Whether or not the directory is hovered.
     * @param partialTicks Partial game ticks.
     */
    @Override
    public final void drawEntry(
            final int slotIndex,
            final int x,
            final int y,
            final int listWidth,
            final int slotHeight,
            final int mouseX,
            final int mouseY,
            final boolean isSelected,
            final float partialTicks
    ) {
        FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
        String display = dirName;

        this.width = listWidth;
        this.xLoc = x;
        this.yLoc = y;
        int stringWidth = renderer.getStringWidth(display);
        boolean isLong = stringWidth > listWidth;

        //:: Draws the directory string
        if (isLong) {
            int trimWidth =
                    listWidth
                    - renderer.getStringWidth(ELLIPSES)
                    - renderer.getStringWidth(STR_PADDING);
            display = renderer.trimStringToWidth(display, trimWidth) + ELLIPSES;
        }
        renderer.drawString(display, x + TEXT_X_Y, y + TEXT_X_Y, PURE_WHITE);
        int j = mouseX - x;
        int i = j < TRASH_DIM ? TRASH_DIM : 0;

        if (isSelected) {
            //:: Draws the line under hovered text.
            GuiUtils.drawGradientRect(
                    x + HOVER_LINE_X,
                    y + HOVER_LINE_Y,
                    listWidth - HOVER_LINE_OFFSET,
                    y + HOVER_LINE_BOTTOM,
                    hoverColor,
                    hoverColor,
                    1
            );

            //:: Draws the trash icon.
            if (listWidth == MenuConsts.DIR_LIST_WIDTH) {
                Minecraft.getMinecraft()
                        .getTextureManager()
                        .bindTexture(ICON_TRASH);
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                Gui.drawModalRectWithCustomSizedTexture(
                        listWidth - TRASH_X,
                        y,
                        TRASH_U,
                        (float) i,
                        TRASH_DIM,
                        TRASH_DIM,
                        TRASH_TEX_DIM,
                        TRASH_TEX_DIM
                );
            }

            //:: Draw tool tip for long strings
            if (isLong) {
                List<String> toolTip = new ArrayList<>();
                toolTip.add(dirName);
                GuiUtils.drawToolTip(
                        renderer,
                        toolTip,
                        x + listWidth,
                        y,
                        stringWidth,
                        slotHeight
                );
            }
        }
    }

    /**
     * Returns true if a mouse click was within the
     * trash icon's dimensions. <br>
     * Returns false otherwise.
     * @param slotIndex The slot index clicked on.
     * @param mouseX The xLoc position of the mouse.
     * @param mouseY The yLoc position of the mouse.
     * @param mouseEvent The mouse event.
     * @param relativeX The relative xLoc position.
     * @param relativeY The relative yLoc position.
     * @return Returns true if a mouse click was within the
     * trash icon's boundaries.
     */
    @Override
    public final boolean mousePressed(
            final int slotIndex,
            final int mouseX,
            final int mouseY,
            final int mouseEvent,
            final int relativeX,
            final int relativeY
    ) {
        return mouseX >= this.width - TRASH_X && mouseX <= this.width;
    }

    /**
     * This method does nothing. There is no need for it,
     * but it must be overridden regardless.
     * @param slotIndex The focused slot index.
     * @param x The focused slot xLoc position.
     * @param y The focused slot yLoc position.
     * @param mouseEvent The mouse event.
     * @param relativeX The relative xLoc position.
     * @param relativeY The relative yLoc position.
     */
    @Override
    public void mouseReleased(
            final int slotIndex,
            final int x,
            final int y,
            final int mouseEvent,
            final int relativeX,
            final int relativeY
    ) {
        //:: Not in use
    }

    //:: IDrawableListEntry
    //:::::::::::::::::::::::::::::://

    /**
     * @return Returns the directory name.
     */
    @Override
    public final String getEntryText() {
        return this.dirName;
    }

    /**
     * @return Returns the xLoc position of the directory.
     */
    @Override
    public final int getX() {
        return this.xLoc;
    }

    /**
     * @return Returns the yLoc position of the directory.
     */
    @Override
    public final int getY() {
        return this.yLoc;
    }

    //-----This:----------------------------------------//

    /**
     * The directory's unique name consists of the directory
     * name and its position in the list.
     * @return Returns the unique name of the directory.
     */
    public final String dirUUID() {
        return this.uniqueDirName;
    }

    /**
     * Sets the name of the directory.
     * @param name The new name of the directory.
     */
    protected final void setDirName(final String name) {
        this.dirName = name;
    }

    /**
     * Sets the unique name of the directory.
     * @param uuid The new unique name of the directory.
     */
    protected final void setDirUUID(final String uuid) {
        this.uniqueDirName = uuid;
    }
}
