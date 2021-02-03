package com.cburch.logisim.gui.prefs;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.prefs.PrefMonitor;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class PrefDoubleSlider extends JSlider implements ChangeListener, PropertyChangeListener {
    private final PrefMonitor<Double> pref;
    private final int divider;

    PrefDoubleSlider(PrefMonitor<Double> pref) {
        this(pref, 1);
    }

    PrefDoubleSlider(PrefMonitor<Double> pref, int divider) {
        this.divider = divider;
        this.pref = pref;

        setPreferredSize(new Dimension(120, 24));
        setValue((int)(pref.get() * divider));

        addChangeListener(this);
        pref.addPropertyChangeListener(this);
    }

    public double getValueDouble() {
        return (double)getValue() / divider;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {}

    @Override
    public void stateChanged(ChangeEvent e) {
        pref.set(getValueDouble());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
