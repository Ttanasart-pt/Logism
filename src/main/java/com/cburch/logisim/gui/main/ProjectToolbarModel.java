/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections15.list.UnmodifiableList;

import com.cburch.draw.toolbar.AbstractToolbarModel;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.draw.toolbar.ToolbarSeparator;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import static com.cburch.logisim.util.LocaleString.*;

class ProjectToolbarModel extends AbstractToolbarModel implements MenuListener.EnabledListener {
    private final Frame frame;
    private final LogisimToolbarItem itemAdd;
    private final LogisimToolbarItem itemUp;
    private final LogisimToolbarItem itemDown;
    private final LogisimToolbarItem itemDelete;
    private final LogisimToolbarItem itemLayout;
    private final LogisimToolbarItem itemAppearance;
    private final List<ToolbarItem> items;

    public ProjectToolbarModel(Frame frame, MenuListener menu) {
        this.frame = frame;

        itemAdd = new LogisimToolbarItem(menu, "projadd.svg", LogisimMenuBar.ADD_CIRCUIT, "Add", getFromLocale("projectAddCircuitTip"));
        itemUp = new LogisimToolbarItem(menu, "projup.svg", LogisimMenuBar.MOVE_CIRCUIT_UP, "Up", getFromLocale("projectMoveCircuitUpTip"));
        itemDown = new LogisimToolbarItem(menu, "projdown.svg", LogisimMenuBar.MOVE_CIRCUIT_DOWN, "Down", getFromLocale("projectMoveCircuitDownTip"));
        itemDelete = new LogisimToolbarItem(menu, "projdel.svg", LogisimMenuBar.REMOVE_CIRCUIT, "Remove", getFromLocale("projectRemoveCircuitTip"));
        itemLayout = new LogisimToolbarItem(menu, "projlayo.svg", LogisimMenuBar.EDIT_LAYOUT, "Circuit", getFromLocale("projectEditLayoutTip"));
        itemAppearance = new LogisimToolbarItem(menu, "projapp.svg", LogisimMenuBar.EDIT_APPEARANCE, "Component", getFromLocale("projectEditAppearanceTip"));

        items = UnmodifiableList.decorate(Arrays.asList(itemAdd, itemUp, itemDown, itemDelete, new ToolbarSeparator(2), itemLayout, itemAppearance));

        menu.addEnabledListener(this);
    }

    @Override
    public List<ToolbarItem> getItems() {
        return items;
    }

    @Override
    public boolean isSelected(ToolbarItem item) {
        String view = frame.getEditorView();
        if (item == itemLayout) {
            return view.equals(Frame.EDIT_LAYOUT);
        } else if (item == itemAppearance) {
            return view.equals(Frame.EDIT_APPEARANCE);
        } else {
            return false;
        }
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
