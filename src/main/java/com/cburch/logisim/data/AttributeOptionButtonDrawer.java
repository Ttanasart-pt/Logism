package com.cburch.logisim.data;

import com.cburch.draw.util.ColorRegistry;
import com.formdev.flatlaf.FlatDarkLaf;
import org.w3c.dom.Attr;

import javax.swing.*;
import javax.swing.event.EventListenerList;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

public class AttributeOptionButtonDrawer<V> extends JPanel implements ActionListener {
    public ButtonGroup buttonGroup;
    public EventListenerList listenerList;
    V[] vals;
    JToggleButtonStatic[] buttons;
    V selecting;

    AttributeOptionButtonDrawer(V[] vals) {
        super();
        this.vals = vals;
        listenerList = new EventListenerList();

        setLayout(new CardLayout(4, 4));
        JPanel buttonPanel = new JPanel(new GridLayout(1, vals.length, 4, 8));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        setBackground(ColorRegistry.GreyLight);
        buttonGroup = new ButtonGroup();
        buttons = new JToggleButtonStatic[vals.length];
        int i = 0;

        for(V val : vals) {
            JToggleButtonStatic b;
            if(val instanceof AttributeOption) {
                AttributeOption a = (AttributeOption)val;
                if(a.isImage)
                    b = new JToggleButtonImageStatic(a.iconPath, a.toDisplayString());
                else
                    b = new JToggleButtonStatic(((AttributeOption) val).toDisplayString());
            } else if(val instanceof Direction)
                b = new JToggleButtonImageStatic(((Direction)val).iconPath(), ((Direction)val).toDisplayString());
            else
                b = new JToggleButtonStatic("");

            b.addActionListener(this);

            buttons[i++] = b;
            buttonGroup.add(b);
            buttonPanel.add(b);
        }
        add(buttonPanel);
    }

    public void addActionListener(ActionListener listener) {
        listenerList.add(ActionListener.class, listener);
    }

    public void setSelectedItem(V value) {
        for(int i = 0; i < buttons.length; i++) {
            if(value == vals[i]) {
                buttons[i].setSelected(true);
                selecting = vals[i];
            } else {
                buttons[i].setSelected(false);
            }
        }
    }

    public V getValue() {
        return selecting;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for(int i = 0; i < buttons.length; i++) {
            if(buttons[i].isSelected()) {
                selecting = vals[i];
            }
        }

        Object[] listeners = listenerList.getListenerList();
        for(int i = 0; i <= listeners.length - 2; i += 2) {
            if(listeners[i] == ActionListener.class) {
                ((ActionListener) listeners[i + 1]).actionPerformed(e);
            }
        }
    }
}
