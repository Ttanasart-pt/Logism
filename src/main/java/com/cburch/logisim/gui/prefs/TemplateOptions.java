/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.cburch.logisim.file.Loader;
import com.cburch.logisim.file.LoaderException;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.prefs.Template;
import com.cburch.logisim.util.JFileChoosers;
import static com.cburch.logisim.util.LocaleString.*;

class TemplateOptions extends OptionsPanel {
    private class MyListener implements ActionListener, PropertyChangeListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            Object src = event.getSource();
            if (src == templateButton) {
                JFileChooser chooser = JFileChoosers.create();
                chooser.setDialogTitle(getFromLocale("selectDialogTitle"));
                chooser.setApproveButtonText(getFromLocale("selectDialogButton"));
                int action = chooser.showOpenDialog(getPreferencesFrame());
                if (action == JFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    FileInputStream reader = null;
                    InputStream reader2 = null;
                    try {
                        Loader loader = new Loader(getPreferencesFrame());
                        reader = new FileInputStream(file);
                        Template template = Template.create(reader);
                        reader2 = template.createStream();
                        // to see if OK
                        LogisimFile.load(reader2, loader);
                        AppPreferences.setTemplateFile(file, template);
                        AppPreferences.setTemplateType(AppPreferences.TEMPLATE_CUSTOM);
                    } catch (LoaderException ignored) {
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(getPreferencesFrame(),
                                String.format(getFromLocale("templateErrorMessage"), ex.toString()),
                                getFromLocale("templateErrorTitle"),
                                JOptionPane.ERROR_MESSAGE);
                    } finally {
                        try {
                            if (reader != null) {
                                reader.close();
                            }
                        } catch (IOException ignored) { }
                        try {
                            if (reader != null) {
                                assert reader2 != null;
                                reader2.close();
                            }

                        } catch (IOException ignored) { }
                    }
                }
            } else {
                int value = AppPreferences.TEMPLATE_UNKNOWN;
                if (plain.isSelected()) {
                    value = AppPreferences.TEMPLATE_PLAIN;
                }

                else if (empty.isSelected()) {
                    value = AppPreferences.TEMPLATE_EMPTY;
                }

                else if (custom.isSelected()) {
                    value = AppPreferences.TEMPLATE_CUSTOM;
                }

                AppPreferences.setTemplateType(value);
            }
            computeEnabled();
        }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            if (prop.equals(AppPreferences.TEMPLATE_TYPE)) {
                int value = AppPreferences.getTemplateType();
                plain.setSelected(value == AppPreferences.TEMPLATE_PLAIN);
                empty.setSelected(value == AppPreferences.TEMPLATE_EMPTY);
                custom.setSelected(value == AppPreferences.TEMPLATE_CUSTOM);
            } else if (prop.equals(AppPreferences.TEMPLATE_FILE)) {
                setTemplateField((File) event.getNewValue());
            }
        }

        private void setTemplateField(File f) {
            try {
                templateField.setText(f == null ? "" : f.getCanonicalPath());
            } catch (IOException e) {
                templateField.setText(f.getName());
            }
            computeEnabled();
        }

        private void computeEnabled() {
            custom.setEnabled(!templateField.getText().equals(""));
            templateField.setEnabled(custom.isSelected());
        }
    }

    private final JRadioButton plain = new JRadioButton();
    private final JRadioButton empty = new JRadioButton();
    private final JRadioButton custom = new JRadioButton();
    private final JTextField templateField = new JTextField(40);
    private final JButton templateButton = new JButton();

    public TemplateOptions(PreferencesFrame window) {
        super(window);

        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(plain);
        bgroup.add(empty);
        bgroup.add(custom);

        MyListener myListener = new MyListener();
        plain.addActionListener(myListener);
        empty.addActionListener(myListener);
        custom.addActionListener(myListener);
        templateField.setEditable(false);
        templateButton.addActionListener(myListener);
        myListener.computeEnabled();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel content = new JPanel(gridbag);
        setLayout(new CardLayout(24, 24));

        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.LINE_START;

        gridbag.setConstraints(plain, gbc);
        content.add(plain);
        gridbag.setConstraints(empty, gbc);
        content.add(empty);
        gridbag.setConstraints(custom, gbc);
        content.add(custom);

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.gridy = 3;
        gbc.gridx = GridBagConstraints.RELATIVE;
        JPanel strut = new JPanel();
        strut.setMinimumSize(new Dimension(50, 1));
        strut.setPreferredSize(new Dimension(50, 1));

        gbc.weightx = 0.0; gridbag.setConstraints(strut, gbc);
        content.add(strut);

        gbc.weightx = 1.0; gridbag.setConstraints(templateField, gbc);
        content.add(templateField);

        gbc.weightx = 0.0; gridbag.setConstraints(templateButton, gbc);
        content.add(templateButton);

        add(content);

        AppPreferences.addPropertyChangeListener(AppPreferences.TEMPLATE_TYPE, myListener);
        AppPreferences.addPropertyChangeListener(AppPreferences.TEMPLATE_FILE, myListener);
        switch (AppPreferences.getTemplateType()) {
            case AppPreferences.TEMPLATE_PLAIN -> plain.setSelected(true);
            case AppPreferences.TEMPLATE_EMPTY -> empty.setSelected(true);
            case AppPreferences.TEMPLATE_CUSTOM -> custom.setSelected(true);
        }
        myListener.setTemplateField(AppPreferences.getTemplateFile());
    }

    @Override
    public String getTitle() {
        return getFromLocale("templateTitle");
    }

    @Override
    public String getHelpText() {
        return getFromLocale("templateHelp");
    }

    @Override
    public void localeChanged() {
        plain.setText(getFromLocale("templatePlainOption"));
        empty.setText(getFromLocale("templateEmptyOption"));
        custom.setText(getFromLocale("templateCustomOption"));
        templateButton.setText(getFromLocale("templateSelectButton"));
    }
}
