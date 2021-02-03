/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.toolbar;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.util.GraphicsUtil;

import java.awt.*;
import java.util.ArrayList;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Toolbar extends JPanel {
    public static final Object VERTICAL = new Object();
    public static final Object HORIZONTAL = new Object();

    private class MyListener implements ToolbarModelListener {
        @Override
        public void toolbarAppearanceChanged(ToolbarModelEvent event) {
            repaint();
        }

        @Override
        public void toolbarContentsChanged(ToolbarModelEvent event) {
            computeContents();
        }
    }

    private ToolbarModel model;
    private final Object orientation;
    public final MyListener myListener;
    private ToolbarButton curPressed;
    private boolean showLabel = false;
    private final ArrayList<ToolbarButton> buttonList;

    public Toolbar(ToolbarModel model, boolean showLabel) {
        this(model);
        this.showLabel = showLabel;

        computeContents();
    }
    public Toolbar(ToolbarModel model) {
        super(new GridBagLayout());
        this.model = model;
        this.orientation = HORIZONTAL;
        this.myListener = new MyListener();
        this.curPressed = null;

        buttonList = new ArrayList<>();

        computeContents();
        if (model != null) {
            model.addToolbarModelListener(myListener);
        }
    }

    public ToolbarModel getToolbarModel() {
        return model;
    }

    public void setToolbarModel(ToolbarModel value) {
        ToolbarModel oldValue = model;
        if (value != oldValue) {
            if (oldValue != null) {
                oldValue.removeToolbarModelListener(myListener);
            }

            if (value != null) {
                value.addToolbarModelListener(myListener);
            }

            model = value;
            computeContents();
        }
    }

    private void computeContents() {
        buttonList.clear();
        GridBagConstraints constraints = new GridBagConstraints();
        if(showLabel)
            constraints.insets = new Insets(0, 2, 10, 2);
        else
            constraints.insets = new Insets(4, 2, 4, 2);
        removeAll();
        ToolbarModel m = model;
        if (m != null) {
            for (ToolbarItem item : m.getItems()) {
                ToolbarButton button = new ToolbarButton(this, item, showLabel);
                buttonList.add(button);
                add(button, constraints);
            }
        }
        revalidate();
    }

    ToolbarButton getPressed() {
        return curPressed;
    }

    void setPressed(ToolbarButton value) {
        ToolbarButton oldValue = curPressed;
        if (oldValue != value) {
            curPressed = value;
            if (oldValue != null) {
                oldValue.repaint();
            }

            if (value != null) {
                value.repaint();
            }

        }
    }

    Object getOrientation() {
        return orientation;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(showLabel) {
            ToolbarModel m = model;
            if (m != null) {
                for (ToolbarButton button : buttonList) {
                    ToolbarItem item = button.getItem();
                    if(item.isSeperator()) continue;
                    String label = item.getNameShort();
                    if(label == null) continue;

                    FontMetrics metrics = g.getFontMetrics();
                    int x = button.getX() + button.getWidth() / 2 - metrics.stringWidth(label) / 2 - 1;
                    int y = button.getY() + button.getHeight() + 12;

                    g.setColor(ColorRegistry.GreyBright);
                    g.drawString(label, x, y);
                }
            }
        }
    }
}
