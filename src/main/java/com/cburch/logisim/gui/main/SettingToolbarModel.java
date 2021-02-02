package com.cburch.logisim.gui.main;

import com.cburch.draw.toolbar.AbstractToolbarModel;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import org.apache.commons.collections15.list.UnmodifiableList;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class SettingToolbarModel extends AbstractToolbarModel {
    private final LogisimToolbarItem itemAnalysis;
    private final List<ToolbarItem> items;

    public SettingToolbarModel(MenuListener menu) {
        itemAnalysis = new LogisimToolbarItem(menu, "circuit_analy.svg", LogisimMenuBar.ANALYZE_CIRCUIT, "Analyze circuit");

        items = UnmodifiableList.decorate(Arrays.asList(itemAnalysis));
    }

    @Override
    public List<ToolbarItem> getItems() { return items; }

    @Override
    public boolean isSelected(ToolbarItem item) {
        return false;
    }

    @Override
    public void itemSelected(ToolbarItem item) {
        if(item instanceof LogisimToolbarItem) {
            ((LogisimToolbarItem) item).doAction();
        }
    }
}
