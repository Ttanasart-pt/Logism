/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import com.bric.swing.ColorPicker;
import com.cburch.logisim.prefs.PrefMonitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class PrefColor extends JButton implements ActionListener, PropertyChangeListener {
    private final PrefMonitor<Integer> pref;

    PrefColor(PrefMonitor<Integer> pref) {
        super();
        this.pref = pref;

        setPreferredSize(new Dimension(100, 24));
        addActionListener(this);
        pref.addPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Color c = ColorPicker.showDialog(null, new Color(pref.get()));
        pref.set(c.getRGB());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) { }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int x = 4;
        int y = 4;
        g.setColor(new Color(pref.get()));
        g.fillRect(x, y, getWidth() - x * 2, getHeight() - y * 2);
    }
}
