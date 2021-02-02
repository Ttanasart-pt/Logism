/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.ContentHandler;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.file.FileStatistics;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.gui.generic.CardPanel;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.util.TableSorter;
import static com.cburch.logisim.util.LocaleString.*;

@SuppressWarnings("serial")
public class StatisticsDialog extends CardPanel implements ActionListener {
    private Frame parent;
    private LogisimFile file;
    private Circuit circuit;
    private FileStatistics stats;
    private StatisticsTableModel model;

    public static void show(Frame parent, LogisimFile file, Circuit circuit) {
        FileStatistics stats = FileStatistics.compute(file, circuit);
        StatisticsDialog dlog = new StatisticsDialog(parent);

        dlog.parent = parent;
        dlog.file = file;
        dlog.circuit = circuit;
        dlog.stats = stats;
        dlog.model = new StatisticsTableModel(stats);
        dlog.setVisible(true);
    }

    public void refreshStatistic() {
        file = parent.getProject().getLogisimFile();
        circuit = parent.getProject().getCurrentCircuit();

        stats = FileStatistics.compute(file, circuit);
        model = new StatisticsTableModel(stats);
        mySorter = new TableSorter(model, table.getTableHeader());
        table.setModel(mySorter);
    }

    public static class StatisticsTableModel extends AbstractTableModel {
        private FileStatistics stats;

        StatisticsTableModel(FileStatistics stats) {
            this.stats = stats;
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public int getRowCount() {
            return stats.getCounts().size() + 2;
        }

        @Override
        public Class<?> getColumnClass(int column) {
            return column < 2 ? String.class : Integer.class;
        }

        @Override
        public String getColumnName(int column) {
            switch (column) {
            case 0: return getFromLocale("statsComponentColumn");
            case 1: return getFromLocale("statsLibraryColumn");
            case 2: return getFromLocale("statsSimpleCountColumn");
            case 3: return getFromLocale("statsUniqueCountColumn");
            case 4: return getFromLocale("statsRecursiveCountColumn");
            // should never happen
            default: return "??";
            }
        }

        @Override
        public Object getValueAt(int row, int column) {
            List<FileStatistics.Count> counts = stats.getCounts();
            int countsLen = counts.size();
            if (row < 0 || row >= countsLen + 2) return "";
            FileStatistics.Count count;
            if (row < countsLen) count = counts.get(row);
            else if (row == countsLen) {
                count = stats.getTotalWithoutSubcircuits();
            }

            else {
                count = stats.getTotalWithSubcircuits();
            }

            switch (column) {
            case 0:
                if (row < countsLen) {
                    return count.getFactory().getDisplayName();
                } else if (row == countsLen) {
                    return getFromLocale("statsTotalWithout");
                } else {
                    return getFromLocale("statsTotalWith");
                }
            case 1:
                if (row < countsLen) {
                    Library lib = count.getLibrary();
                    return lib == null ? "-" : lib.getDisplayName();
                } else {
                    return "";
                }
            case 2: return count.getSimpleCount();
            case 3: return count.getUniqueCount();
            case 4: return count.getRecursiveCount();
            // should never happen
            default: return "";
            }
        }
    }

    private static class CompareString implements Comparator<String> {
        private String[] fixedAtBottom;

        public CompareString(String... fixedAtBottom) {
            this.fixedAtBottom = fixedAtBottom;
        }

        @Override
        public int compare(String a, String b) {
            for (int i = fixedAtBottom.length - 1; i >= 0; i--) {
                String s = fixedAtBottom[i];
                if (a.equals(s)) return b.equals(s) ? 0 : 1;
                if (b.equals(s)) return -1;
            }
            return a.compareToIgnoreCase(b);
        }
    }

    private static class StatisticsTable extends JTable {
        @Override
        public void setBounds(int x, int y, int width, int height) {
            super.setBounds(x, y, width, height);
            setPreferredColumnWidths(new double[] { 0.45, 0.25, 0.1, 0.1, 0.1 });
        }

        @Override
        public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component component = super.prepareRenderer(renderer, row, column);
            component.setBackground(new Color(255, 255, 255, row % 2 * 16));
            return component;
        }

        protected void setPreferredColumnWidths(double[] percentages) {
            Dimension tableDim = getPreferredSize();

            double total = 0;
            for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                total += percentages[i];
            }

            for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
                TableColumn column = getColumnModel().getColumn(i);
                double width = tableDim.width * (percentages[i] / total);
                column.setPreferredWidth((int) width);
            }
        }
    }

    JTable table;
    TableSorter mySorter;

    public StatisticsDialog(Frame parent) {
        this.parent = parent;

        table = new StatisticsTable();
        mySorter = new TableSorter(model, table.getTableHeader());
        Comparator<String> comp = new CompareString("", getFromLocale("statsTotalWithout"), getFromLocale("statsTotalWith"));
        mySorter.setColumnComparator(String.class, comp);
        table.setModel(mySorter);
        JScrollPane tablePane = new JScrollPane(table);

        this.setLayout(new BorderLayout());
        this.add(tablePane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //this.dispose();
    }
}
