/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.toolbar;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.std.io.Button;
import com.cburch.logisim.std.io.Keyboard;
import com.cburch.logisim.util.GraphicsUtil;
import org.apache.xpath.operations.Bool;

@SuppressWarnings("serial")
class ToolbarButton extends JButton implements MouseListener {
    public static final int BORDER = 8;

    private final Toolbar toolbar;
    private final ToolbarItem item;
    private boolean isShort = false;

    ToolbarButton(Toolbar toolbar, ToolbarItem item, boolean isShort) {
        this(toolbar, item);
        this.isShort = isShort;
    }
    ToolbarButton(Toolbar toolbar, ToolbarItem item) {
        this.toolbar = toolbar;
        this.item = item;

        addMouseListener(this);
        setFocusable(true);
        setToolTipText("");
    }

    public ToolbarItem getItem() {
        return item;
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension dim = item.getDimension(toolbar.getOrientation());
        dim.width += 2 * BORDER + (isShort? 4 : 0);
        dim.height += isShort? 4 : 2 * BORDER;
        return dim;
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public void paintComponent(Graphics g) {
        if(item.isSeperator()) {
            setBackground(ColorRegistry.Grey);
            setBorder(null);
        } else {
            super.paintComponent(g);

            if (toolbar.getToolbarModel().isSelected(item)) {
                setBackground(ColorRegistry.GreyBright.darker());
            } else {
                setBackground(ColorRegistry.Grey);
            }

            Graphics g2 = g.create();
            g2.translate(BORDER + (isShort? 2 : 0), isShort? 2 : BORDER);
            item.paintIcon(this, g2);
            g2.dispose();
        }
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        return item.getToolTip();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (item != null && item.isSelectable()) {
            toolbar.setPressed(this);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (toolbar.getPressed() == this) {
            toolbar.getToolbarModel().itemSelected(item);
            toolbar.setPressed(null);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    	
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    	
    }

    @Override
    public void mouseExited(MouseEvent e) {
        toolbar.setPressed(null);
    }
}
