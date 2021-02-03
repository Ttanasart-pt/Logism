/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import com.bric.swing.ColorPicker;
import com.cburch.logisim.prefs.PrefMonitor;
import com.cburch.logisim.util.GraphicsUtil;
import com.connectina.swing.fontchooser.DefaultFontSelectionModel;
import com.connectina.swing.fontchooser.FontSelectionModel;
import com.connectina.swing.fontchooser.JFontChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

class PrefFont extends JButton implements ActionListener, PropertyChangeListener {
    private final PrefMonitor<String> pref;

    PrefFont(PrefMonitor<String> pref) {
        super(pref.get());
        this.pref = pref;

        setPreferredSize(new Dimension(140, 24));
        addActionListener(this);
        pref.addPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Font get = JFontChooser.showDialog(SwingUtilities.getWindowAncestor(this), GraphicsUtil.FONT);

        pref.set(get.getFontName());
        setText(get.getFontName());
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {}
}
