/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.prefs;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.prefs.PrefMonitor;
import com.cburch.logisim.util.GraphicsUtil;
import org.apache.batik.ext.swing.JGridBagPanel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.cburch.logisim.util.LocaleString.*;

class WindowOptions extends OptionsPanel {
    private final PrefBoolean check;
    private final ArrayList<styleMenuTab> StylePages;
    private final JPanel stylePanel;
    private final GridBagConstraints c;

    public WindowOptions(PreferencesFrame window) {
        super(window);
        setLayout(new CardLayout(8, 8));

        BorderLayout border = new BorderLayout();
        border.setVgap(4);
        JPanel content = new JPanel(border);

        check = new PrefBoolean(AppPreferences.SHOW_TICK_RATE, getFromLocale("windowTickRate"));

        PrefOptionList lookAndFeel = new PrefOptionList(AppPreferences.LOOK_AND_FEEL, getFromLocale("lookAndFeel"),
            new PrefOption[]{
                new PrefOption("Dark", getFromLocale("Dark")),
                //new PrefOption("Light", getFromLocale("Light")),
                new PrefOption("Dracular", getFromLocale("Dracular")),
                //new PrefOption("Intellij", getFromLocale("Intellij")),
            });

        JPanel panel = new JPanel();
        BoxLayout box = new BoxLayout(panel, BoxLayout.LINE_AXIS);
        panel.setLayout(box);
        panel.add(lookAndFeel.getJLabel());
        panel.add(Box.createRigidArea(new Dimension(5, 0)));
        panel.add(lookAndFeel.getJComboBox());
        content.add(panel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel styleMenuPanel = new JPanel(new GridBagLayout());
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.PAGE_START;

        StylePages = new ArrayList<>();
        StylePages.add(new styleMenuTab("General", Arrays.asList(
                new doubleSliderPanel("Stroke scale", AppPreferences.STYLE_STROKE_THICK, 10),
                new fontPanel("Gate font", AppPreferences.STYLE_FONT),
                new colorPanel("Canvas color", AppPreferences.STYLE_CANVAS_BG),
                new colorPanel("Grid color", AppPreferences.STYLE_GRID_COLOR),
                new colorPanel("Text color", AppPreferences.STYLE_TEXT_COLOR),
                new colorPanel("Gate color", AppPreferences.STYLE_GATE_COLOR),
                new colorPanel("Handle color", AppPreferences.STYLE_HANDLE_COLOR)
        )));
        StylePages.add(new styleMenuTab("Wires", Arrays.asList(
                new colorPanel("Wire idle", AppPreferences.STYLE_WIRE_IDLE),
                new colorPanel("Wire nil", AppPreferences.STYLE_WIRE_NIL),
                new colorPanel("Wire false", AppPreferences.STYLE_WIRE_FALSE),
                new colorPanel("Wire true", AppPreferences.STYLE_WIRE_TRUE),
                new colorPanel("Wire unknown", AppPreferences.STYLE_WIRE_UNKNOW),
                new colorPanel("Wire error", AppPreferences.STYLE_WIRE_ERROR),
                new colorPanel("Wire width error", AppPreferences.STYLE_WIRE_WIDTH_ERROR),
                new colorPanel("Wire multi", AppPreferences.STYLE_WIRE_MULTI)
        )));

        for(int i = 0; i < StylePages.size(); i++) {
            c.gridy = i;
            styleMenuPanel.add(StylePages.get(i), c);
        }

        stylePanel = new JPanel(new GridBagLayout());
        stylePanel.setBackground(ColorRegistry.Grey.darker());
        setStylePage(0);
        JPanel styleScroll = new JPanel(new CardLayout(4, 4));
        styleScroll.add(new JScrollPane(stylePanel));

        centerPanel.add(styleMenuPanel, BorderLayout.WEST);
        centerPanel.add(styleScroll, BorderLayout.CENTER);

        content.add(centerPanel, BorderLayout.CENTER);
        content.add(new applyButton(), BorderLayout.SOUTH);

        add(content);
    }

    void setStylePage(int index) {
        setStylePage(StylePages.get(index).title);
    }
    void setStylePage(String page) {
        stylePanel.removeAll();
        for(styleMenuTab menu : StylePages) {
            menu.isSelecting = false;
            if(menu.title.equals(page)) {
                menu.isSelecting = true;

                for(int i = 0; i < menu.panels.size(); i++) {
                    c.gridy = i;
                    stylePanel.add(menu.panels.get(i), c);
                }
            }

            menu.repaint();
            stylePanel.repaint();
        }
    }

    @Override
    public String getTitle() { return getFromLocale("windowTitle"); }

    @Override
    public String getHelpText() { return getFromLocale("windowHelp"); }

    @Override
    public void localeChanged() { check.localeChanged(); }

    static class propertyPanel extends JPanel {

    }

    static class colorPanel extends propertyPanel {
        colorPanel(String name, PrefMonitor<Integer> pref) {
            setBackground(ColorRegistry.Grey.darker());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setPreferredSize(new Dimension(120, 32));
            add(nameLabel);
            add(new PrefColor(pref));
        }
    }

    static class fontPanel extends propertyPanel {
        fontPanel(String name, PrefMonitor<String> pref) {
            setBackground(ColorRegistry.Grey.darker());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setPreferredSize(new Dimension(120, 32));
            add(nameLabel);
            add(new PrefFont(pref));
        }
    }

    static class doubleSliderPanel extends propertyPanel implements ChangeListener {
        JLabel valueLabel;
        PrefDoubleSlider slider;

        doubleSliderPanel(String name, PrefMonitor<Double> pref, int divider) {
            setBackground(ColorRegistry.Grey.darker());
            JLabel nameLabel = new JLabel(name);
            nameLabel.setPreferredSize(new Dimension(120, 32));
            add(nameLabel);

            slider = new PrefDoubleSlider(pref, divider);
            slider.setMinimum(1);
            slider.setMaximum(10);
            add(slider);
            slider.addChangeListener(this);

            valueLabel = new JLabel("" + slider.getValueDouble());
            valueLabel.setPreferredSize(new Dimension(20, 32));
            add(valueLabel);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            valueLabel.setText(String.format("%.1f", slider.getValueDouble()));
        }
    }

    static class applyButton extends JButton implements ActionListener {
        applyButton() {
            super("Apply");
            addActionListener(this);
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            ColorRegistry.setStyle();
            GraphicsUtil.setStyle();
            Frame.This.repaint();
        }
    }


    class styleMenuTab extends JButton implements ActionListener, MouseListener {
        public String title;
        public List<propertyPanel> panels;
        boolean isSelecting;
        boolean isHovering;

        styleMenuTab(String name, List<propertyPanel> panels) {
            title = name;
            this.panels = panels;
            setPreferredSize(new Dimension(128, 48));
            addActionListener(this);
            addMouseListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            setStylePage(title);
        }

        @Override
        public void paint(Graphics g) {
            if(isHovering) {
                g.setColor(new Color(48, 50, 52));
                g.fillRect(0, 0, getWidth(), getHeight());
            } else if(isSelecting) {
                g.setColor(new Color(61, 75, 92));
                g.fillRect(0, 0, getWidth(), getHeight());
            }

            if(isSelecting) {
                g.setColor(new Color(74, 136, 199));
                g.fillRect(getWidth() - 3, 0, 3, getHeight());
            }

            g.setColor(ColorRegistry.White);
            GraphicsUtil.drawCenteredText(g, title, getWidth() / 2, getHeight() / 2 - 4);
        }

        @Override
        public void mouseClicked(MouseEvent e) {}

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {
            isHovering = true;
            repaint();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            isHovering = false;
            repaint();
        }
    }
}
