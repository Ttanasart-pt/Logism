package com.cburch.draw.toolbar;

import com.cburch.logisim.gui.main.*;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.proj.Project;

import javax.swing.*;
import java.awt.*;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

public class FrameToolbar extends JPanel {
    public ExplorerToolbarModel toolExplorer;
    public Toolbar tools;
    public SimulationToolbarModel toolSimulation;
    public SettingToolbarModel toolSetting;

    public FrameToolbar(ToolbarModel model, Frame frame, MenuListener menu, Project project) {
        super(new CardLayout(8, 0));
        setPreferredSize(new Dimension(getWidth(), 64));

        JPanel content = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new GridBagLayout());
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel rightPanel = new JPanel(new BorderLayout());
        JPanel rightRightPanel = new JPanel(new GridBagLayout());

        toolExplorer = new ExplorerToolbarModel(frame, menu);
        leftPanel.add(new Toolbar(toolExplorer));

        tools = new Toolbar(model);
        leftPanel.add(new JPanel());
        leftPanel.add(tools);

        toolSimulation = new SimulationToolbarModel(project, menu, true);
        centerPanel.add(new Toolbar(toolSimulation));

        toolSetting = new SettingToolbarModel(menu);
        rightPanel.setPreferredSize(leftPanel.getPreferredSize());
        rightRightPanel.add(new Toolbar(toolSetting));
        rightPanel.add(rightRightPanel, BorderLayout.EAST);

        content.add(leftPanel, BorderLayout.WEST);
        content.add(centerPanel, BorderLayout.CENTER);
        content.add(rightPanel, BorderLayout.EAST);

        add(content);

        if (model != null) {
            model.addToolbarModelListener(tools.myListener);
        }
    }
}
