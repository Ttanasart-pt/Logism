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
        Dimension dim = destination.getSize();
        g.setColor(ColorRegistry.GreyLight.brighter());
        int x = 0;
        int y = 0;
        int w = dim.width;
        int h = dim.height;
        // separator is a vertical line in horizontal toolbar
        if (h >= w) {
            h -= 0;
            y = 0;
            x = (w - 1) / 2;
            w = 1;
        // separator is a horizontal line in vertical toolbar
        } else {
            w -= 0;
            x = 0;
            y = (h - 1) / 2;
            h = 1;
        }
        g.fillRect(x, y, w, h);
    }

    @Override
    public String getToolTip() {
        return null;
    }

    @Override
    public Dimension getDimension(Object orientation) {
        return new Dimension(size, size);
    }
}
