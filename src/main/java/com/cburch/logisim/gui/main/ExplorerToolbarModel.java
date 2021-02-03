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

public class ExplorerToolbarModel extends AbstractToolbarModel implements MenuListener.EnabledListener {
    private final Frame frame;
    private final LogisimToolbarItem itemLayout;
    private final LogisimToolbarItem itemAppearance;
    private final List<ToolbarItem> items;

    public ExplorerToolbarModel(Frame frame, MenuListener menu) {
        this.frame = frame;

        itemLayout = new LogisimToolbarItem(menu, "projlayo.svg", LogisimMenuBar.EDIT_LAYOUT, "Circuit", getFromLocale("projectEditLayoutTip"));
        itemAppearance = new LogisimToolbarItem(menu, "projapp.svg", LogisimMenuBar.EDIT_APPEARANCE, "Component", getFromLocale("projectEditAppearanceTip"));

        items = UnmodifiableList.decorate(Arrays.asList(itemLayout, itemAppearance));

        menu.addEnabledListener(this);
    }

    @Override
    public List<ToolbarItem> getItems() {
        return items;
    }

    @Override
    public boolean isSelected(ToolbarItem item) {
        if (item == itemLayout) {
            return frame.getEditorView().equals(Frame.EDIT_LAYOUT);
        } else if (item == itemAppearance) {
            return frame.getEditorView().equals(Frame.EDIT_APPEARANCE);
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
