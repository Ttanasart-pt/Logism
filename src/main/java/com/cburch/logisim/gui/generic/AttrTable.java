/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

import com.bric.swing.ColorPicker;
import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.data.AttributeOptionButtonDrawer;
import com.cburch.logisim.util.JDialogOk;
import com.cburch.logisim.util.JInputComponent;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.LinkedList;
import static com.cburch.logisim.util.LocaleString.*;

public class AttrTable extends JPanel implements LocaleListener {
    private static final AttrTableModel NULL_ATTR_MODEL = new NullAttrModel();

    private static class NullAttrModel implements AttrTableModel {
        @Override
        public void addAttrTableModelListener(AttrTableModelListener listener) { }
        @Override
        public void removeAttrTableModelListener(AttrTableModelListener listener) { }

        @Override
        public String getTitle() { return null; }
        @Override
        public int getRowCount() { return 0; }
        @Override
        public AttrTableModelRow getRow(int rowIndex) { return null; }
    }

    private static class TitleLabel extends JLabel {
        @Override
        public Dimension getMinimumSize() {
            Dimension ret = super.getMinimumSize();
            return new Dimension(1, ret.height);
        }
    }

    private static class MyDialog extends JDialogOk {
        JInputComponent input;
        Object value;

        public MyDialog(Dialog parent, JInputComponent input) {
            super(parent, getFromLocale("attributeDialogTitle"), true);
            configure(input);
        }

        public MyDialog(Frame parent, JInputComponent input) {
            super(parent, getFromLocale("attributeDialogTitle"), true);
            configure(input);
        }

        private void configure(JInputComponent input) {
            this.input = input;
            this.value = input.getValue();

            // Thanks to Christophe Jacquet, who contributed a fix to this
            // so that when the dialog is resized, the component within it
            // is resized as well. (Tracker #2024479)
            JPanel p = new JPanel(new BorderLayout());
            p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            p.add((JComponent) input, BorderLayout.CENTER);
            getContentPane().add(p, BorderLayout.CENTER);

            pack();
        }

        @Override
        public void okClicked() {
            value = input.getValue();
        }

        public Object getValue() {
            return value;
        }
    }

    private class TableModelAdapter implements TableModel, AttrTableModelListener {
        Window parent;
        LinkedList<TableModelListener> listeners;
        AttrTableModel attrModel;

        TableModelAdapter(Window parent, AttrTableModel attrModel) {
            this.parent = parent;
            this.listeners = new LinkedList<>();
            this.attrModel = attrModel;
        }

        void setAttrTableModel(AttrTableModel value) {
            if (attrModel != value) {
                TableCellEditor editor = table.getCellEditor();
                if (editor != null) {
                    editor.cancelCellEditing();
                }
                attrModel.removeAttrTableModelListener(this);
                attrModel = value;
                attrModel.addAttrTableModelListener(this);
                fireTableChanged();
            }
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        void fireTableChanged() {
            TableModelEvent e = new TableModelEvent(this);
            for (TableModelListener l : new ArrayList<>(listeners)) {
                l.tableChanged(e);
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int columnIndex) {
            if (columnIndex == 0) {
                return "Attribute";
            }
            else {
                return "Value";
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public int getRowCount() {
            return attrModel.getRowCount();
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (columnIndex == 0) {
                return attrModel.getRow(rowIndex).getLabel();
            } else {
                return attrModel.getRow(rowIndex).getValue();
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex > 0 && attrModel.getRow(rowIndex).isValueEditable();
        }

        @Override
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            if (columnIndex > 0) {
                try {
                    attrModel.getRow(rowIndex).setValue(value);
                } catch (AttrTableSetException e) {
                    JOptionPane.showMessageDialog(parent, e.getMessage(), getFromLocale("attributeChangeInvalidTitle"), JOptionPane.WARNING_MESSAGE);
                }
            }
        }

        //
        // AttrTableModelListener methods
        //
        @Override
        public void attrTitleChanged(AttrTableModelEvent e) {
            if (e.getSource() != attrModel) {
                attrModel.removeAttrTableModelListener(this);
                return;
            }
            updateTitle();
        }

        @Override
        public void attrStructureChanged(AttrTableModelEvent e) {
            if (e.getSource() != attrModel) {
                attrModel.removeAttrTableModelListener(this);
                return;
            }
            TableCellEditor ed = table.getCellEditor();
            if (ed != null) {
                ed.cancelCellEditing();
            }
            fireTableChanged();
        }

        @Override
        public void attrValueChanged(AttrTableModelEvent e) {
            if (e.getSource() != attrModel) {
                attrModel.removeAttrTableModelListener(this);
                return;
            }
            int row = e.getRowIndex();
            TableCellEditor ed = table.getCellEditor();
            if (row >= 0 && ed instanceof CellEditor
                    && attrModel.getRow(row) == ((CellEditor) ed).currentRow) {
                ed.cancelCellEditing();
            }
            fireTableChanged();
        }

    }

    private class CellEditor implements TableCellEditor, FocusListener, ActionListener {
        LinkedList<CellEditorListener> listeners = new LinkedList<>();
        AttrTableModelRow currentRow;
        Component currentEditor;

        //
        // TableCellListener management
        //
        @Override
        public void addCellEditorListener(CellEditorListener l) {
            // Adds a listener to the list that's notified when the
            // editor stops, or cancels editing.
            listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            // Removes a listener from the list that's notified
            listeners.remove(l);
        }

        public void fireEditingCanceled() {
            ChangeEvent e = new ChangeEvent(AttrTable.this);
            for (CellEditorListener l : new ArrayList<>(listeners)) {
                l.editingCanceled(e);
            }
        }

        public void fireEditingStopped() {
            ChangeEvent e = new ChangeEvent(AttrTable.this);
            for (CellEditorListener l : new ArrayList<>(listeners)) {
                l.editingStopped(e);
            }
        }

        //
        // other TableCellEditor methods
        //
        @Override
        public void cancelCellEditing() {
            // Tells the editor to cancel editing and not accept any
            // partially edited value.
            fireEditingCanceled();
        }

        @Override
        public boolean stopCellEditing() {
            // Tells the editor to stop editing and accept any partially
            // edited value as the value of the editor.
            fireEditingStopped();
            return true;
        }

        @Override
        public Object getCellEditorValue() {
            // Returns the value contained in the editor.
            Component comp = currentEditor;
            if (comp instanceof JTextField) {
                return ((JTextField) comp).getText();
            } else if (comp instanceof JComboBox) {
                return ((JComboBox) comp).getSelectedItem();
            } else if (comp instanceof JCheckBox) {
                return ((JCheckBox) comp).isSelected();
            } else if (comp instanceof AttributeOptionButtonDrawer) {
                return ((AttributeOptionButtonDrawer) comp).getValue();
            } else {
                return null;
            }
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            // Asks the editor if it can start editing using anEvent.
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            // Returns true if the editing cell should be selected,
            // false otherwise.
            return true;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int rowIndex, int columnIndex) {
            AttrTableModel attrModel = tableModel.attrModel;
            AttrTableModelRow row = attrModel.getRow(rowIndex);

            if (columnIndex == 0) {
                return new JLabel(row.getLabel());
            } else {
                if (currentEditor != null) {
                    currentEditor.transferFocus();
                }

                JPanel panel = new JPanel();
                panel.setLayout(new CardLayout(0, 0));

                Component editor = row.getEditor(parent);
                if (editor instanceof JComboBox) {
                    ((JComboBox) editor).addActionListener(this);
                    editor.addFocusListener(this);
                } else if (editor instanceof JInputComponent) {
                    JInputComponent input = (JInputComponent) editor;
                    MyDialog dlog;
                    Window parent = AttrTable.this.parent;
                    if (parent instanceof Frame) {
                        dlog = new MyDialog((Frame) parent, input);
                    } else {
                        dlog = new MyDialog((Dialog) parent, input);
                    }
                    dlog.setVisible(true);
                    Object retval = dlog.getValue();
                    try {
                        row.setValue(retval);
                    } catch (AttrTableSetException e) {
                        JOptionPane.showMessageDialog(parent, e.getMessage(), getFromLocale("attributeChangeInvalidTitle"), JOptionPane.WARNING_MESSAGE);
                    }
                    editor = new JLabel(row.getValue());
                } else if(editor instanceof JCheckBox) {
                    ((JCheckBox)editor).addActionListener(this);
                } else if(editor instanceof AttributeOptionButtonDrawer) {
                    ((AttributeOptionButtonDrawer)editor).addActionListener(this);
                } else {
                    editor.addFocusListener(this);
                }
                currentRow = row;
                currentEditor = editor;

                panel.add(editor);
                return panel;
            }
        }

        //
        // FocusListener methods
        //
        @Override
        public void focusLost(FocusEvent e) {
            Component dst = e.getOppositeComponent();
            if (dst != null) {
                Component p = dst;
                while (p != null && !(p instanceof Window)) {
                    if (p == AttrTable.this) {
                        // switch to another place in this table,
                        // no problem
                        return;
                    }
                    p = p.getParent();
                }
                // focus transferred outside table; stop editing
                editor.stopCellEditing();
            }
        }

        @Override
        public void focusGained(FocusEvent e) {}

        //
        // ActionListener methods
        //
        @Override
        public void actionPerformed(ActionEvent e) {
            stopCellEditing();
        }
    }

    private class attributeCellRenderer implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            AttrTableModel attrModel = tableModel.attrModel;
            AttrTableModelRow rowModel = attrModel.getRow(row);

            JPanel panel = new JPanel();
            panel.setBackground(ColorRegistry.GreyLight);

            if(column == 0) {
                panel.setLayout(new CardLayout(8, 2));
                panel.add(new JLabel("" + value));
            } else {
                panel.setLayout(new CardLayout(0, 0));
                Component editorUI = rowModel.getEditor(parent);
                if (editorUI instanceof JComboBox) {
                    panel.setLayout(new CardLayout(4, 4));
                    panel.add(new comboDrawer("" + value));
                } else if(editorUI instanceof JInputComponent) {
                    if(editorUI instanceof ColorPicker) {
                        panel.setLayout(new CardLayout(4, 4));
                        panel.add(new colorDrawer("" + value, Color.decode("" + value)));
                    } else {
                        panel.setLayout(new CardLayout(4, 4));
                        panel.add(new JButton("" + value));
                    }
                } else if(editorUI instanceof JCheckBox) {
                    panel.add(editorUI);
                } else if(editorUI instanceof AttributeOptionButtonDrawer) {
                    panel.add(editorUI);
                } else {
                    panel.setLayout(new CardLayout(4, 4));
                    panel.add(new JButton("" + value));
                }
            }

            return panel;
        }

        private class comboDrawer extends JButton {
            comboDrawer(String label) {
                super(label);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x1 = getWidth() - 20;
                int x2 = getWidth() - 10;
                int y1 = 11;
                int y2 = getHeight() - 11;

                int[] x = {x1, x2, (x1 + x2) / 2};
                int[] y = {y1, y1, y2};
                g.setColor(ColorRegistry.GreyBright);
                g.fillPolygon(x, y, 3);
            }
        }

        private class colorDrawer extends JButton {
            final Color color;

            colorDrawer(String label, Color value) {
                super(label);
                color = value;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(color);
                g.fillRect(4, 4, 24, getHeight() - 8);
            }
        }
    }

    private final Window parent;
    private boolean titleEnabled;
    private final JLabel title;
    private final JTable table;
    private final TableModelAdapter tableModel;
    private final CellEditor editor = new CellEditor();

    public AttrTable(Window parent) {
        super(new BorderLayout());
        this.parent = parent;

        titleEnabled = true;
        title = new TitleLabel();
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setVerticalAlignment(SwingConstants.CENTER);

        tableModel = new TableModelAdapter(parent, NULL_ATTR_MODEL);
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new attributeCellRenderer());
        table.setDefaultEditor(Object.class, editor);
        table.setTableHeader(null);
        table.setRowHeight(36);
        table.getColumnModel().getColumn(0).setPreferredWidth(128);
        table.getColumnModel().getColumn(0).setMaxWidth(160);

        Font baseFont = title.getFont();
        int titleSize = Math.round(baseFont.getSize() * 1.2f);
        Font titleFont = baseFont.deriveFont((float) titleSize).deriveFont(Font.BOLD);
        title.setFont(titleFont);
        setBackground(ColorRegistry.Grey);

        JScrollPane tableScroll = new JScrollPane(table);

        this.add(title, BorderLayout.PAGE_START);
        this.add(tableScroll, BorderLayout.CENTER);

        LocaleManager.addLocaleListener(this);
        localeChanged();
    }

    public void setTitleEnabled(boolean value) {
        titleEnabled = value;
        updateTitle();
    }

    public boolean getTitleEnabled() {
        return titleEnabled;
    }

    public void setAttrTableModel(AttrTableModel value) {
        tableModel.setAttrTableModel(value == null ? NULL_ATTR_MODEL : value);
        updateTitle();
    }

    public AttrTableModel getAttrTableModel() {
        return tableModel.attrModel;
    }

    @Override
    public void localeChanged() {
        updateTitle();
        tableModel.fireTableChanged();
    }

    private void updateTitle() {
        if (titleEnabled) {
            String text = tableModel.attrModel.getTitle();
            if (text == null) {
                title.setVisible(false);
            } else {
                title.setText(text);
                title.setVisible(true);
            }
        } else {
            title.setVisible(false);
        }
    }
}
