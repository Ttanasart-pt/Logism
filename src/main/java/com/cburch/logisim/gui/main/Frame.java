/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.main;

import com.cburch.draw.toolbar.FrameToolbar;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.gui.appear.AppearanceView;
import com.cburch.logisim.gui.generic.*;
import com.cburch.logisim.gui.menu.LogisimMenuBar;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.proj.*;
import com.cburch.logisim.tools.Tool;
import com.cburch.logisim.util.JFileChoosers;
import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;

import static com.cburch.logisim.util.LocaleString.getFromLocale;

@SuppressWarnings("serial")
public class Frame extends LFrame implements LocaleListener {
    public static Frame This = null;
    public static final String EDITOR_VIEW = "editorView";
    public static final String EXPLORER_VIEW = "explorerView";
    public static final String EDIT_LAYOUT = "layout";
    public static final String EDIT_APPEARANCE = "appearance";
    public static final String VIEW_TOOLBOX = "Circuit";
    public static final String VIEW_SIMULATION = "Simulation";

    private static final double[] ZOOM_OPTIONS = { 20, 30, 50, 75, 100, 125, 150, 200, 250, 300, 400 };

    class MyProjectListener implements ProjectListener, LibraryListener, CircuitListener, PropertyChangeListener, ChangeListener {
        @Override
        public void projectChanged(ProjectEvent event) {
            int action = event.getAction();

            if (action == ProjectEvent.ACTION_SET_FILE) {
                computeTitle();
                proj.setTool(proj.getOptions().getToolbarData().getFirstTool());
                placeToolbar();
            } else if (action == ProjectEvent.ACTION_SET_CURRENT) {
                setEditorView(EDIT_LAYOUT);
                if (appearance != null) {
                    appearance.setCircuit(proj, proj.getCircuitState());
                }
                viewAttributes(proj.getTool());
                computeTitle();
            } else if (action == ProjectEvent.ACTION_SET_TOOL) {
                // for startup
                if (attrTable == null) {
                    return;
                }

                Tool oldTool = (Tool) event.getOldData();
                Tool newTool = (Tool) event.getData();
                if (getEditorView().equals(EDIT_LAYOUT)) {
                    viewAttributes(oldTool, newTool, false);
                }
            }

            statistic.refreshStatistic();
        }

        @Override
        public void libraryChanged(LibraryEvent e) {
            if (e.getAction() == LibraryEvent.SET_NAME) {
                computeTitle();
            } else if (e.getAction() == LibraryEvent.DIRTY_STATE) {
                enableSave();
            }
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            if (event.getAction() == CircuitEvent.ACTION_SET_NAME) {
                computeTitle();
            }
        }

        private void enableSave() {
            Project proj = getProject();
            boolean ok = proj.isFileDirty();
            getRootPane().putClientProperty("windowModified", ok);
        }

        public void attributeListChanged(AttributeEvent e) { }

        @Override
        public void propertyChange(PropertyChangeEvent event) {
            if (AppPreferences.TOOLBAR_PLACEMENT.isSource(event)) {
                placeToolbar();
            }
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            Object source = event.getSource();
            if (source == mainPanel) {
                firePropertyChange(EDITOR_VIEW, "???", getEditorView());
            }
        }
    }

    class MyWindowListener extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            if (confirmClose(getFromLocale("confirmCloseTitle"))) {
                layoutCanvas.closeCanvas();
                Frame.this.dispose();
            }
        }

        @Override
        public void windowOpened(WindowEvent e) {
            layoutCanvas.computeSize(true);
        }
    }

    private final Project           proj;
    private final MyProjectListener myProjectListener = new MyProjectListener();

    private final MenuListener        menuListener;
    private final FrameToolbar        toolbar;
    private final CardPanel           mainPanel;

    private final JTabbedPane         explorerPane;
    private final Toolbox             toolbox;
    private final AttrTable           attrTable;
    private final ZoomControl         zoom;

    private final StatisticsDialog    statistic;

    private final JSplitPane mainPanelSuper;
    private final JSplitPane leftCenter;
    private final JSplitPane allContent;

    // for the Layout view
    private final LayoutToolbarModel  layoutToolbarModel;
    private final Canvas              layoutCanvas;
    private final ZoomModel           layoutZoomModel;
    private final LayoutEditHandler   layoutEditHandler;
    private final AttrTableSelectionModel attrTableSelectionModel;
    
    public static final Logger logger = LoggerFactory.getLogger( Frame.class );

    // for the Appearance view
    private AppearanceView appearance;

    public Frame(Project proj) {
        this.proj = proj;
        This = this;

        setBackground(Color.white);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        try {
        	ArrayList<Image> icons = new ArrayList<Image>();
        	icons.add(ImageIO.read(Frame.class.getResourceAsStream("/logisim/logisim 32.png")));
        	icons.add(ImageIO.read(Frame.class.getResourceAsStream("/logisim/logisim 16.png")));
			setIconImages(icons);
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
        addWindowListener(new MyWindowListener());

        proj.addProjectListener(myProjectListener);
        proj.addLibraryListener(myProjectListener);
        proj.addCircuitListener(myProjectListener);
        computeTitle();

        // set up elements for the Layout view
        layoutToolbarModel = new LayoutToolbarModel(this, proj);
        layoutCanvas = new Canvas(proj);
        layoutZoomModel = new BasicZoomModel(AppPreferences.LAYOUT_SHOW_GRID, AppPreferences.LAYOUT_ZOOM, ZOOM_OPTIONS);

        layoutCanvas.getGridPainter().setZoomModel(layoutZoomModel);
        layoutEditHandler = new LayoutEditHandler(this);
        attrTableSelectionModel = new AttrTableSelectionModel(proj, this);

        // set up menu bar and toolbar
        // GUI elements shared between views
        LogisimMenuBar menubar = new LogisimMenuBar(this, proj);
        menuListener = new MenuListener(this, menubar);
        menuListener.setEditHandler(layoutEditHandler);
        setJMenuBar(menubar);
        toolbar = new FrameToolbar(layoutToolbarModel, this, menuListener, proj);

        toolbox = new Toolbox(proj, menuListener);
        SimulationExplorer simExplorer = new SimulationExplorer(proj, menuListener);
        explorerPane = new JTabbedPane();
        explorerPane.addTab(VIEW_TOOLBOX, toolbox);
        explorerPane.addTab(VIEW_SIMULATION, simExplorer);

        attrTable = new AttrTable(this);
        zoom = new ZoomControl(layoutZoomModel);

        // set up the central area
        CanvasPane canvasPane = new CanvasPane(layoutCanvas);

        canvasPane.setZoomModel(layoutZoomModel);
        mainPanel = new CardPanel();
        mainPanel.addView(EDIT_LAYOUT, canvasPane);
        mainPanel.setView(EDIT_LAYOUT);

        statistic = new StatisticsDialog(this);
        statistic.addChangeListener(myProjectListener);
        mainPanelSuper = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mainPanel, statistic);
        mainPanelSuper.setDividerSize(12);
        mainPanelSuper.setDividerLocation(AppPreferences.WINDOW_STAT_SPLIT.get());
        mainPanelSuper.setOneTouchExpandable(true);

        // set up the contents, split down the middle, with the canvas
        // on the right and a split pane on the left containing the
        // explorer and attribute values.
        JPanel explPanel = new JPanel(new BorderLayout());
        explPanel.add(explorerPane, BorderLayout.CENTER);
        JPanel attrPanel = new JPanel(new BorderLayout());
        attrPanel.add(attrTable, BorderLayout.CENTER);
        attrPanel.add(zoom, BorderLayout.SOUTH);

        JPanel leftPane = explPanel;
        JSplitPane centerPane = mainPanelSuper;
        JPanel rightPane = attrPanel;

        leftCenter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPane, centerPane);
        leftCenter.setDividerSize(12);
        leftCenter.setDividerLocation(AppPreferences.WINDOW_LEFT_SPLIT.get());
        leftCenter.setOneTouchExpandable(true);

        allContent = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftCenter, rightPane);
        allContent.setDividerSize(12);
        allContent.setDividerLocation(AppPreferences.WINDOW_MAIN_SPLIT.get());
        allContent.setOneTouchExpandable(true);

        getContentPane().add(allContent, BorderLayout.CENTER);

        computeTitle();

        this.setSize(AppPreferences.WINDOW_WIDTH.get(), AppPreferences.WINDOW_HEIGHT.get());
        Point prefPoint = getInitialLocation();
        if (prefPoint != null) {
            this.setLocation(prefPoint);
        }
        this.setExtendedState(AppPreferences.WINDOW_STATE.get());

        menuListener.register(mainPanel);
        KeyboardToolSelection.register(toolbar.tools);

        proj.setFrame(this);
        if (proj.getTool() == null) {
            proj.setTool(proj.getOptions().getToolbarData().getFirstTool());
        }
        mainPanel.addChangeListener(myProjectListener);
        explorerPane.addChangeListener(myProjectListener);
        AppPreferences.TOOLBAR_PLACEMENT.addPropertyChangeListener(myProjectListener);
        placeToolbar();
        ((MenuListener.EnabledListener) toolbar.toolExplorer).menuEnableChanged(menuListener);

        LocaleManager.addLocaleListener(this);
    }

    private void placeToolbar() {
        Container contents = getContentPane();
        contents.add(toolbar, BorderLayout.NORTH);
        contents.validate();
    }

    public Project getProject() {
        return proj;
    }

    public void viewComponentAttributes(Circuit circ, Component comp) {
        if (comp == null) {
            setAttrTableModel(null);
        } else {
            setAttrTableModel(new AttrTableComponentModel(proj, circ, comp));
        }
    }

    void setAttrTableModel(AttrTableModel value) {
        attrTable.setAttrTableModel(value);
        if (value instanceof AttrTableToolModel) {
            Tool tool = ((AttrTableToolModel) value).getTool();
            toolbox.setHaloedTool(tool);
            layoutToolbarModel.setHaloedTool(tool);
        } else {
            toolbox.setHaloedTool(null);
            layoutToolbarModel.setHaloedTool(null);
        }
        if (value instanceof AttrTableComponentModel) {
            Circuit circ = ((AttrTableComponentModel) value).getCircuit();
            Component comp = ((AttrTableComponentModel) value).getComponent();
            layoutCanvas.setHaloedComponent(circ, comp);
        } else {
            layoutCanvas.setHaloedComponent(null, null);
        }
    }

    public void setEditorView(String view) {
        String curView = mainPanel.getView();
        if (curView.equals(view)) {
            return;
        }

        // appearance view
        if (view.equals(EDIT_APPEARANCE)) {
            AppearanceView app = appearance;
            if (app == null) {
                app = new AppearanceView();
                app.setCircuit(proj, proj.getCircuitState());
                mainPanel.addView(EDIT_APPEARANCE, app.getCanvasPane());
                appearance = app;
            }
            toolbar.tools.setToolbarModel(app.getToolbarModel());
            app.getAttrTableDrawManager(attrTable).attributesSelected();
            zoom.setZoomModel(app.getZoomModel());
            menuListener.setEditHandler(app.getEditHandler());
            mainPanel.setView(view);
            app.getCanvas().requestFocus();
        // layout view
        } else {
            toolbar.tools.setToolbarModel(layoutToolbarModel);
            zoom.setZoomModel(layoutZoomModel);
            menuListener.setEditHandler(layoutEditHandler);
            viewAttributes(proj.getTool(), true);
            mainPanel.setView(view);
            layoutCanvas.requestFocus();
        }
    }

    public String getEditorView() {
        return mainPanel.getView();
    }

    public Canvas getCanvas() {
        return layoutCanvas;
    }

    private void computeTitle() {
        String s;
        Circuit circuit = proj.getCurrentCircuit();
        String name = proj.getLogisimFile().getName();
        if (circuit != null) {
            s = getFromLocale("titleCircFileKnown", circuit.getName(), name);
        } else {
            s = getFromLocale("titleFileKnown", name);
        }
        this.setTitle(s);
        myProjectListener.enableSave();
    }

    void viewAttributes(Tool newTool) {
        viewAttributes(null, newTool, false);
    }

    private void viewAttributes(Tool newTool, boolean force) {
        viewAttributes(null, newTool, force);
    }

    private void viewAttributes(Tool oldTool, Tool newTool, boolean force) {
        AttributeSet newAttrs;
        if (newTool == null) {
            newAttrs = null;
            if (!force) {
                return;
            }

        } else {
            newAttrs = newTool.getAttributeSet(layoutCanvas);
        }
        if (newAttrs == null) {
            AttrTableModel oldModel = attrTable.getAttrTableModel();
            boolean same = oldModel instanceof AttrTableToolModel
                && ((AttrTableToolModel) oldModel).getTool() == oldTool;
            if (!force && !same && !(oldModel instanceof AttrTableCircuitModel)) {
                return;
            }
        }
        if (newAttrs == null) {
            Circuit circ = proj.getCurrentCircuit();
            if (circ != null) {
                setAttrTableModel(new AttrTableCircuitModel(proj, circ));
            } else if (force) {
                setAttrTableModel(null);
            }
        } else if (newAttrs instanceof SelectionAttributes) {
            setAttrTableModel(attrTableSelectionModel);
        } else {
            setAttrTableModel(new AttrTableToolModel(proj, newTool));
        }
    }

    @Override
    public void localeChanged() {
        computeTitle();
    }

    public void savePreferences() {
        AppPreferences.TICK_FREQUENCY.set(proj.getSimulator().getTickFrequency());
        AppPreferences.LAYOUT_SHOW_GRID.setBoolean(layoutZoomModel.getShowGrid());
        AppPreferences.LAYOUT_ZOOM.set(layoutZoomModel.getZoomFactor());
        if (appearance != null) {
            ZoomModel aZoom = appearance.getZoomModel();
            AppPreferences.APPEARANCE_SHOW_GRID.setBoolean(aZoom.getShowGrid());
            AppPreferences.APPEARANCE_ZOOM.set(aZoom.getZoomFactor());
        }
        int state = getExtendedState() & ~java.awt.Frame.ICONIFIED;
        AppPreferences.WINDOW_STATE.set(state);
        Dimension dim = getSize();
        AppPreferences.WINDOW_WIDTH.set(dim.width);
        AppPreferences.WINDOW_HEIGHT.set(dim.height);
        Point loc;
        try {
            loc = getLocationOnScreen();
        } catch (IllegalComponentStateException e) {
            loc = Projects.getLocation(this);
        }
        if (loc != null) {
            AppPreferences.WINDOW_LOCATION.set(loc.x + "," + loc.y);
        }
        AppPreferences.WINDOW_LEFT_SPLIT.set(leftCenter.getDividerLocation());
        AppPreferences.WINDOW_MAIN_SPLIT.set(allContent.getDividerLocation());
        AppPreferences.WINDOW_STAT_SPLIT.set(mainPanelSuper.getDividerLocation());
        AppPreferences.DIALOG_DIRECTORY.set(JFileChoosers.getCurrentDirectory());
    }

    public boolean confirmClose() {
        return confirmClose(getFromLocale("confirmCloseTitle"));
    }

    // returns true if user is OK with proceeding
    public boolean confirmClose(String title) {
        String message = getFromLocale("confirmDiscardMessage", proj.getLogisimFile().getName());

        if (!proj.isFileDirty()) {
            return true;
        }

        toFront();
        String[] options = { getFromLocale("saveOption"), getFromLocale("discardOption"), getFromLocale("cancelOption") };
        int result = JOptionPane.showOptionDialog(this,
                message, title, 0, JOptionPane.QUESTION_MESSAGE, null,
                options, options[0]);
        boolean ret;
        if (result == 0) {
            ret = ProjectActions.doSave(proj);
        } else ret = result == 1;
        if (ret) {
            dispose();
        }
        return ret;
    }

    private static Point getInitialLocation() {
        String s = AppPreferences.WINDOW_LOCATION.get();
        if (s == null) {
            return null;
        }

        int comma = s.indexOf(',');
        if (comma < 0) {
            return null;
        }

        try {
            int x = Integer.parseInt(s.substring(0, comma));
            int y = Integer.parseInt(s.substring(comma + 1));
            while (isProjectFrameAt(x, y)) {
                x += 20;
                y += 20;
            }
            Rectangle desired = new Rectangle(x, y, 50, 50);

            int gcBestSize = 0;
            Point gcBestPoint = null;
            GraphicsEnvironment ge;
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            for (GraphicsDevice gd : ge.getScreenDevices()) {
                for (GraphicsConfiguration gc : gd.getConfigurations()) {
                    Rectangle gcBounds = gc.getBounds();
                    if (gcBounds.intersects(desired)) {
                        Rectangle inter = gcBounds.intersection(desired);
                        int size = inter.width * inter.height;
                        if (size > gcBestSize) {
                            gcBestSize = size;
                            int x2 = Math.max(gcBounds.x, Math.min(inter.x,
                                    inter.x + inter.width - 50));
                            int y2 = Math.max(gcBounds.y, Math.min(inter.y,
                                    inter.y + inter.height - 50));
                            gcBestPoint = new Point(x2, y2);
                        }
                    }
                }
            }
            if (gcBestPoint != null) {
                if (isProjectFrameAt(gcBestPoint.x, gcBestPoint.y)) {
                    gcBestPoint = null;
                }
            }
            return gcBestPoint;
        } catch (Exception t) {
            return null;
        }
    }

    private static boolean isProjectFrameAt(int x, int y) {
        for (Project current : Projects.getOpenProjects()) {
            Frame frame = current.getFrame();
            if (frame != null) {
                Point loc = frame.getLocationOnScreen();
                int d = Math.abs(loc.x - x) + Math.abs(loc.y - y);
                if (d <= 3) {
                    return true;
                }

            }
        }
        return false;
    }
}
