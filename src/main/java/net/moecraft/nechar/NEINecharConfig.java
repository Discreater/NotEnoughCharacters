package net.moecraft.nechar;

import codechicken.nei.ItemPanel;
import codechicken.nei.ItemPanels;
import codechicken.nei.LayoutManager;
import codechicken.nei.NEIClientConfig;
import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import net.moecraft.nechar.gui.HistoryItemPanel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class NEINecharConfig implements IConfigureNEI {
    public static boolean NEI_INSTALLED = false;

    public static final String ENABLE_HISTORY_TAG = "nechar.enableHistory";
    private static boolean enableHistory = true;

    @Override
    public void loadConfig() {
        initConfig();
        NEI_INSTALLED = true;

        HistoryItemPanel historyItemPanel = new HistoryItemPanel();
        historyItemPanel.init();
        setItemPanelsItemPanel(historyItemPanel);
        LayoutManager.itemPanel = historyItemPanel;
        API.registerUsageHandler(historyItemPanel);
        API.registerRecipeHandler(historyItemPanel);
        GuiContainerManager.addInputHandler((historyItemPanel));

        API.addSearchProvider(new NecharSearchProvider());
        NotEnoughCharacters.logger.info("search provider added!");
    }

    private void setItemPanelsItemPanel(ItemPanel itemPanel) {
        //noinspection InstantiationOfUtilityClass
        ItemPanels itemPanels = new ItemPanels();
        try {
            Field field = itemPanels.getClass().getDeclaredField("itemPanel");
            Field modifiers = field.getClass().getDeclaredField("modifiers");
            modifiers.setAccessible(true);
            modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(itemPanels, itemPanel);
            modifiers.setInt(field, field.getModifiers() | Modifier.FINAL);
            modifiers.setAccessible(false);
        } catch (Exception e) {
            NEIClientConfig.logger.error("Set ItemPanel in ItemPanels fail!", e);
        }
    }

    /**
     * TODO 配置无效，目前禁用
     */
    private void initConfig() {
//        ConfigTagParent tag = NEIClientConfig.global.config;
//        tag.getTag(ENABLE_HISTORY_TAG).getBooleanValue(enableHistory);
//        API.addOption(new OptionToggleButton(ENABLE_HISTORY_TAG) {
//            @Override
//            public boolean onClick(int button) {
//                enableHistory = super.onClick(button);
//                return enableHistory;
//            }
//        });
    }

    @Override
    public String getName() {
        return NotEnoughCharacters.ID;
    }

    @Override
    public String getVersion() {
        return NotEnoughCharacters.VERSION;
    }

    public static boolean isEnableHistory() {
        return NEI_INSTALLED && enableHistory;
    }
}
