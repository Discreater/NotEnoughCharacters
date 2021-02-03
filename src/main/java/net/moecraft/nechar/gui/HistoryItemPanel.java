package net.moecraft.nechar.gui;

import codechicken.lib.gui.GuiDraw;
import codechicken.lib.vec.Rectangle4i;
import codechicken.nei.*;
import codechicken.nei.api.GuiInfo;
import codechicken.nei.api.INEIGuiHandler;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.api.IRecipeOverlayRenderer;
import codechicken.nei.guihook.GuiContainerManager;
import codechicken.nei.guihook.IContainerInputHandler;
import codechicken.nei.recipe.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.moecraft.nechar.NEINecharConfig;
import net.moecraft.nechar.NotEnoughCharacters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class HistoryItemPanel extends ItemPanel implements IUsageHandler, ICraftingHandler, IContainerInputHandler {
    public static final int MAX_HISTORY = 90;

    public static final int USE_ROWS = 3;

    public ArrayList<ItemStack> history = new ArrayList<>();

    public int h;

    int rows;

    int columns;

    int start;

    private boolean[] validSlotMap;

    @Override
    public void resize(GuiContainer gui) {
        super.resize(gui);
        if (NEINecharConfig.isEnableHistory()) {
            myResize();
            this.rows = this.getRows();
            this.columns = this.getColumns();
            this.start = this.rows * this.columns;
            this.updateValidSlots();
        }
    }

    private void updateValidSlots() {
        GuiContainer gui = NEIClientUtils.getGuiContainer();
        this.validSlotMap = new boolean[this.columns * USE_ROWS];
        for (int i = 0; i < this.validSlotMap.length; ++i) {
            if (this.slotValid(gui, i)) {
                this.validSlotMap[i] = true;
            }
        }
    }

    private boolean slotValid(GuiContainer gui, int i) {
        Rectangle4i rect = this.getSlotRect(this.start + i);

        for (INEIGuiHandler handler : GuiInfo.guiHandlers) {
            if (handler.hideItemPanelSlot(gui, rect.x, rect.y, rect.w, rect.h))
                return false;
        }
        return true;
    }

    private int getColumns() {
        int columns = this.h / 18;
        try {
            Field field = ItemPanel.class.getDeclaredField("columns");
            field.setAccessible(true);
            columns = field.getInt(this);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Get ItemPanel columns fail!", e);
        }

        return columns;
    }

    private int getRows() {
        int rows = this.h / 18;
        try {
            Field field = ItemPanel.class.getDeclaredField("rows");
            field.setAccessible(true);
            rows = field.getInt(this);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Get ItemPanel rows fail!", e);
        }

        return rows;
    }

    private void myResize() {
        this.h = super.h;
        super.h -= 54;
        this.setMarginLeft(this.getMarginLeft());
        this.setMarginTop(this.getMarginTop());
        this.setColumns(super.w / 18);
        this.setRows(super.h / 18);
        this.invokeSuperCalculatePage();
        this.invokeSuperUpdateValidSlots();
    }

    private void invokeSuperUpdateValidSlots() {
        try {
            Method method = ItemPanel.class.getDeclaredMethod("updateValidSlots");
            method.setAccessible(true);
            method.invoke(this);
            method.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Invoke ItemPanel updateValidSlots fail!", e);
        }
    }

    private void invokeSuperCalculatePage() {
        try {
            Method method = ItemPanel.class.getDeclaredMethod("calculatePage");
            method.setAccessible(true);
            method.invoke(this);
            method.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Invoke ItemPanel calculatePage fail!", e);
        }
    }

    private void setRows(int rows) {
        if (rows < 0) {
            rows = 0;
        }
        try {
            Field field = ItemPanel.class.getDeclaredField("rows");
            field.setAccessible(true);
            field.setInt(this, rows);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Set itemPanel rows fail!", e);
        }
    }

    private void setColumns(int columns) {
        if (columns < 0) {
            columns = 0;
        }
        try {
            Field field = ItemPanel.class.getDeclaredField("columns");
            field.setAccessible(true);
            field.setInt(this, columns);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Set itemPanel columns fail!", e);
        }
    }

    private void setMarginTop(int marginTop) {
        try {
            Field field = ItemPanel.class.getDeclaredField("marginTop");
            field.setAccessible(true);
            field.setInt(this, marginTop);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Set itemPanel marginTop fail!", e);
        }
    }

    private void setMarginLeft(int marginLeft) {
        try {
            Field field = ItemPanel.class.getDeclaredField("marginLeft");
            field.setAccessible(true);
            field.setInt(this, marginLeft);
            field.setAccessible(false);
        } catch (Exception e) {
            NotEnoughCharacters.logger.error("Set itemPanel marginLeft fail!", e);
        }
    }

    private void addHistory(ItemStack stack) {
        stack.stackSize = 1;
        for (int i = this.history.size() - 1; i >= 0; i--) {
            if (stack.isItemEqual(this.history.get(i)))
                this.history.remove(i);
        }
        this.history.add(0, stack);
        while (this.history.size() > MAX_HISTORY)
            this.history.remove(MAX_HISTORY - 1);
    }

    @Override
    public void draw(int mousex, int mousey) {
        super.draw(mousex, mousey);
        if (!NEINecharConfig.isEnableHistory())
            return;
        GuiContainerManager.enableMatrixStackLogging();
        Rectangle4i firstRect = getSlotRect(this.start);
        GuiDraw.drawRect(firstRect.x, firstRect.y, this.columns * firstRect.w, USE_ROWS * firstRect.h, 1157627903);
        GuiDraw.drawRect(firstRect.x, USE_ROWS * firstRect.h + firstRect.y, this.columns * firstRect.w, firstRect.h, 1728053247);
        int i;
        for (i = 0; i < this.columns * USE_ROWS && i < this.history.size(); i++) {
            if (this.validSlotMap[i]) {
                Rectangle4i rect = getSlotRect(this.start + i);
                if (rect.contains(mousex, mousey))
                    GuiDraw.drawRect(rect.x, rect.y, rect.w, rect.h, -296397483);
                GuiContainerManager.drawItem(rect.x + 1, rect.y + 1, this.history.get(i));
            }
        }
        GuiContainerManager.disableMatrixStackLogging();
    }

    @Override
    public void mouseUp(int mousex, int mousey, int button) {
        super.mouseUp(mousex, mousey, button);

        ItemPanel.ItemPanelSlot hoverSlot = this.getSlotMouseOver(mousex, mousey);
        if (hoverSlot != null && hoverSlot.slotIndex == this.mouseDownSlot && this.draggedStack == null) {
            ItemStack item = hoverSlot.item;
            if (NEIController.manager.window instanceof GuiRecipe || !NEIClientConfig.canCheatItem(item)) {
                if (button == 0) {
                    GuiCraftingRecipe.openRecipeGui("item", item);
                } else if (button == 1) {
                    GuiUsageRecipe.openRecipeGui("item", item);
                }

                this.draggedStack = null;
                this.mouseDownSlot = -1;
                return;
            }

            NEIClientUtils.cheatItem(item, button, -1);
        }

        this.mouseDownSlot = -1;
    }

    @Override
    public boolean handleClick(int mousex, int mousey, int button) {
        boolean returnValue = super.handleClick(mousex, mousey, button);
        if (!returnValue) {
            Rectangle4i firstRect = getSlotRect(this.start);
            firstRect.w = this.columns * firstRect.w;
            firstRect.h = USE_ROWS * firstRect.h;
            if (firstRect.contains(mousex, mousey))
                return true;
        }
        return returnValue;
    }

    @Override
    public ItemStack getStackMouseOver(int mousex, int mousey) {
        ItemStack stack = super.getStackMouseOver(mousex, mousey);
        if (stack != null)
            return stack;
        if (!NEINecharConfig.isEnableHistory())
            return null;
        for (int i = 0; i < this.columns * USE_ROWS && i < this.history.size(); i++) {
            if (this.validSlotMap[i] &&
                    getSlotRect(this.start + i).contains(mousex, mousey))
                return this.history.get(i);
        }
        return null;
    }

    @Override
    public boolean contains(int px, int py) {
        boolean returnValue = super.contains(px, py);
        if (!returnValue)
            try {
                Rectangle4i firstRect = getSlotRect(this.start);
                firstRect.w = this.columns * firstRect.w;
                firstRect.h = USE_ROWS * firstRect.h;
                if (firstRect.contains(px, py))
                    return true;
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        return returnValue;
    }

    @Override
    public boolean keyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public void onKeyTyped(GuiContainer guiContainer, char c, int i) {

    }

    @Override
    public boolean lastKeyTyped(GuiContainer guiContainer, char c, int i) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseClicked(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseUp(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public boolean mouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {
        return false;
    }

    @Override
    public void onMouseScrolled(GuiContainer guiContainer, int i, int i1, int i2) {

    }

    @Override
    public void onMouseDragged(GuiContainer guiContainer, int i, int i1, int i2, long l) {

    }

    @Override
    public ICraftingHandler getRecipeHandler(String s, Object... objects) {
        if (NEINecharConfig.isEnableHistory()) {
            if (objects.length > 0 && objects[0] instanceof ItemStack) {
                this.addHistory((ItemStack) objects[0]);
            }
        }
        return this;
    }

    @Override
    public IUsageHandler getUsageHandler(String s, Object... objects) {
        if (NEINecharConfig.isEnableHistory()) {
            if (objects.length > 0 && objects[0] instanceof ItemStack) {
                this.addHistory((ItemStack)objects[0]);
            }
        }
        return this;
    }

    @Override
    public String getRecipeName() {
        return "Not Enough Characters";
    }

    @Override
    public int numRecipes() {
        return 0;
    }

    @Override
    public void drawBackground(int i) {

    }

    @Override
    public void drawForeground(int i) {

    }

    @Override
    public List<PositionedStack> getIngredientStacks(int i) {
        return new ArrayList<>();
    }

    @Override
    public List<PositionedStack> getOtherStacks(int i) {
        return new ArrayList<>();
    }

    @Override
    public PositionedStack getResultStack(int i) {
        return null;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public boolean hasOverlay(GuiContainer guiContainer, Container container, int i) {
        return false;
    }

    @Override
    public IRecipeOverlayRenderer getOverlayRenderer(GuiContainer guiContainer, int i) {
        return null;
    }

    @Override
    public IOverlayHandler getOverlayHandler(GuiContainer guiContainer, int i) {
        return null;
    }

    @Override
    public int recipiesPerPage() {
        return 0;
    }

    @Override
    public List<String> handleTooltip(GuiRecipe guiRecipe, List<String> list, int i) {
        return list;
    }

    @Override
    public List<String> handleItemTooltip(GuiRecipe guiRecipe, ItemStack itemStack, List<String> list, int i) {
        return list;
    }

    @Override
    public boolean keyTyped(GuiRecipe guiRecipe, char c, int i, int i1) {
        return false;
    }

    @Override
    public boolean mouseClicked(GuiRecipe guiRecipe, int i, int i1) {
        return false;
    }
}
