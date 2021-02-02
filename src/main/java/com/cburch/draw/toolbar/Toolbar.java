/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.draw.toolbar;

import java.awt.*;

import javax.swing.Box;
import javax.swing.BoxLayout;
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
    private Object orientation;
    public final MyListener myListener;
    private ToolbarButton curPressed;

    public Toolbar(ToolbarModel model) {
        super(new FlowLayout());
        this.model = model;
        this.orientation = HORIZONTAL;
        this.myListener = new MyListener();
        this.curPressed = null;

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
        removeAll();
        ToolbarModel m = model;
        if (m != null) {
            for (ToolbarItem item : m.getItems()) {
                add(new ToolbarButton(this, item));
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
}
