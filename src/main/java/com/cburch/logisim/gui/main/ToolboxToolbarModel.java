/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections15.list.UnmodifiableList;

import com.cburch.draw.toolbar.AbstractToolbarModel;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import static com.cburch.logisim.util.LocaleString.*;

class ToolboxToolbarModel extends AbstractToolbarModel implements MenuListener.EnabledListener {
    private final LogisimToolbarItem itemAdd;
    private final LogisimToolbarItem itemUp;
    private final LogisimToolbarItem itemDown;
    private final LogisimToolbarItem itemDelete;
    private final List<ToolbarItem> items;

    public ToolboxToolbarModel(MenuListener menu) {
        itemAdd = new LogisimToolbarItem(menu, "projadd.svg", LogisimMenuBar.ADD_CIRCUIT, "Add", getFromLocale("projectAddCircuitTip"));
        itemUp = new LogisimToolbarItem(menu, "projup.svg", LogisimMenuBar.MOVE_CIRCUIT_UP, "Up", getFromLocale("projectMoveCircuitUpTip"));
        itemDown = new LogisimToolbarItem(menu, "projdown.svg", LogisimMenuBar.MOVE_CIRCUIT_DOWN, "Down", getFromLocale("projectMoveCircuitDownTip"));
        itemDelete = new LogisimToolbarItem(menu, "projdel.svg", LogisimMenuBar.REMOVE_CIRCUIT, "Delete", getFromLocale("projectRemoveCircuitTip"));

        items = UnmodifiableList.decorate(Arrays.asList(new ToolbarItem[] {
                itemAdd,
                itemUp,
                itemDown,
                itemDelete,
            }));

        menu.addEnabledListener(this);
    }

    @Override
    public List<ToolbarItem> getItems() {
        return items;
    }

    @Override
    public boolean isSelected(ToolbarItem item) {
        return false;
    }

    @Override
    public void itemSelected(ToolbarItem item) {
        if (item instanceof LogisimToolbarItem) {
            ((LogisimToolbarItem) item).doAction();
        }
    }

    //
    // EnabledListener methods
    //
    @Override
    public void menuEnableChanged(MenuListener source) {
        fireToolbarAppearanceChanged();
    }
}
