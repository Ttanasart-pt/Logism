/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.cburch.draw.toolbar.Toolbar;
import com.cburch.logisim.gui.generic.ProjectExplorer;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Tool;

@SuppressWarnings("serial")
class Toolbox extends JPanel {
    private final ProjectExplorer toolbox;
    private final JTextField searchField;

    Toolbox(Project proj, MenuListener menu) {
        super(new BorderLayout());
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

        ToolboxToolbarModel toolbarModel = new ToolboxToolbarModel(menu);
        Toolbar toolbar = new Toolbar(toolbarModel);
        topPanel.add(toolbar);

        searchField = new JTextField();
        //topPanel.add(searchField);

        add(topPanel, BorderLayout.NORTH);

        toolbox = new ProjectExplorer(proj);
        toolbox.setListener(new ToolboxManip(proj, toolbox));
        add(new JScrollPane(toolbox), BorderLayout.CENTER);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                toolbox.find(searchField.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                toolbox.find(searchField.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                toolbox.find(searchField.getText());
            }
        });
    }

    void setHaloedTool(Tool value) {
        toolbox.setHaloedTool(value);
    }
}
