/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.gui.generic.LFrame;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.WindowMenuItemManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

public class PreferencesFrame extends LFrame {
    private static WindowMenuManager MENU_MANAGER = null;

    public static void initializeManager() {
        MENU_MANAGER = new WindowMenuManager();
    }

    private static class WindowMenuManager extends WindowMenuItemManager implements LocaleListener {
        private PreferencesFrame window = null;

        WindowMenuManager() {
            super(getFromLocale("preferencesFrameMenuItem"), true);
            LocaleManager.addLocaleListener(this);
        }

        @Override
        public JFrame getJFrame(boolean create) {
            if (create) {
                if (window == null) {
                    window = new PreferencesFrame();
                    frameOpened(window);
                }
            }
            return window;
        }

        @Override
        public void localeChanged() {
            setText(getFromLocale("preferencesFrameMenuItem"));
        }
    }

    private class MyListener implements ActionListener, LocaleListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
        }

        @Override
        public void localeChanged() {
            setTitle(getFromLocale("preferencesFrameTitle"));
            for (int i = 0; i < panels.length; i++) {
                tabbedPane.setTitleAt(i, panels[i].getTitle());
                tabbedPane.setToolTipTextAt(i, panels[i].getToolTipText());
                panels[i].localeChanged();
            }
        }
    }

    private final OptionsPanel[] panels;
    private final JTabbedPane tabbedPane;

    private PreferencesFrame() {
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        //setJMenuBar(new LogisimMenuBar(this, null));

        panels = new OptionsPanel[] {
                new TemplateOptions(this),
                new IntlOptions(this),
                new WindowOptions(this),
                new LayoutOptions(this),
                new ExperimentalOptions(this),
        };
        tabbedPane = new JTabbedPane();
        int intlIndex = -1;
        for (int index = 0; index < panels.length; index++) {
            OptionsPanel panel = panels[index];
            tabbedPane.addTab(panel.getTitle(), null, panel, panel.getToolTipText());
            if (panel instanceof IntlOptions) {
                intlIndex = index;
            }

        }
        MyListener myListener = new MyListener();

        Container contents = getContentPane();
        tabbedPane.setPreferredSize(new Dimension(600, 450));
        contents.add(tabbedPane, BorderLayout.CENTER);

        if (intlIndex >= 0) {
            tabbedPane.setSelectedIndex(intlIndex);
        }

        LocaleManager.addLocaleListener(myListener);
        myListener.localeChanged();
        pack();
    }

    public static void showPreferences() {
        JFrame frame = MENU_MANAGER.getJFrame(true);
        frame.setVisible(true);
    }
}
