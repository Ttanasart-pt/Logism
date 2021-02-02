/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.toolbar;

import com.cburch.draw.util.ColorRegistry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

public class ToolbarSeparator implements ToolbarItem {
    private int size;

    public ToolbarSeparator(int size) {
        this.size = size;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void paintIcon(Component destination, Graphics g) {

    }

    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public Dimension getDimension(Object orientation) {
        return new Dimension(size, size);
    }

    @Override
    public boolean isSeperator() { return true; }
}
