package com.cburch.logisim.gui.menu;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.WindowMenu;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

public class SmallMenuBar extends LogisimMenuBar {

    public SmallMenuBar(JFrame parent, Project proj) {
        super(parent, proj);
        removeAll();

        add(file);
        add(edit);
    }
}
