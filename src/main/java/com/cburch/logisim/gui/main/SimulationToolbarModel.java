/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import java.util.Arrays;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.list.UnmodifiableList;

import com.cburch.draw.toolbar.AbstractToolbarModel;
import com.cburch.draw.toolbar.ToolbarItem;
import com.cburch.logisim.circuit.Simulator;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.proj.Project;
import static com.cburch.logisim.util.LocaleString.*;

public class SimulationToolbarModel extends AbstractToolbarModel implements ChangeListener {
    private final Project project;
    private final LogisimToolbarItem simEnable;
    private final LogisimToolbarItem simStep;
    private final LogisimToolbarItem tickEnable;
    private final LogisimToolbarItem tickStep;
    private final List<ToolbarItem> items;

    public SimulationToolbarModel(Project project, MenuListener menu, boolean isSmall) {
        this.project = project;

        simEnable = new LogisimToolbarItem(menu, "simplay.svg", LogisimMenuBar.SIMULATE_ENABLE, "Enable", getFromLocale("simulateEnableStepsTip"));
        simStep = new LogisimToolbarItem(menu, "simstep.svg", LogisimMenuBar.SIMULATE_STEP, "Step", getFromLocale("simulateStepTip"));
        tickEnable = new LogisimToolbarItem(menu, "simtplay.svg", LogisimMenuBar.TICK_ENABLE, "Start", getFromLocale("simulateEnableTicksTip"));
        tickStep = new LogisimToolbarItem(menu, "simtstep.svg", LogisimMenuBar.TICK_STEP, "Step", getFromLocale("simulateTickTip"));

        if(isSmall)
            items = UnmodifiableList.decorate(Arrays.asList(new ToolbarItem[] { tickEnable, tickStep }));
        else
            items = UnmodifiableList.decorate(Arrays.asList(new ToolbarItem[] { simEnable, simStep, tickEnable, tickStep }));

        menu.getMenuBar().addEnableListener(this);
        stateChanged(null);
    }

    @Override
    public List<ToolbarItem> getItems() {
        return items;
    }

    @Override
    public boolean isSelected(ToolbarItem item) {
        return false;
    }

    @Override
    public void itemSelected(ToolbarItem item) {
        if (item instanceof LogisimToolbarItem) {
            ((LogisimToolbarItem) item).doAction();
        }
    }

    //
    // ChangeListener methods
    //
    @Override
    public void stateChanged(ChangeEvent e) {
        Simulator sim = project.getSimulator();
        boolean running = sim != null && sim.isRunning();
        boolean ticking = sim != null && sim.isTicking();
        simEnable.setIcon(running ? "simstop.svg" : "simplay.svg");
        simEnable.setToolTip(running ? getFromLocale("simulateDisableStepsTip")
                : getFromLocale("simulateEnableStepsTip"));
        tickEnable.setIcon(ticking ? "simtstop.svg" : "simtplay.svg");
        tickEnable.setToolTip(ticking ? getFromLocale("simulateDisableTicksTip")
                : getFromLocale("simulateEnableTicksTip"));
        fireToolbarAppearanceChanged();
    }
}
