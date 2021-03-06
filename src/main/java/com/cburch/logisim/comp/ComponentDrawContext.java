/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.comp;

import java.awt.*;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.WireSet;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.prefs.AppPreferences;
import com.cburch.logisim.util.GraphicsUtil;

public class ComponentDrawContext {
    private static final int PIN_OFFS = 2;
    private static final int PIN_RAD = 4;

    private java.awt.Component dest;
    private Circuit circuit;
    private CircuitState circuitState;
    private Graphics base;
    private Graphics g;
    private boolean showState;
    private boolean showColor;
    private boolean printView;
    private WireSet highlightedWires;
    private InstancePainter instancePainter;

    public ComponentDrawContext(java.awt.Component dest,
            Circuit circuit, CircuitState circuitState,
            Graphics base, Graphics g, boolean printView) {
        this.dest = dest;
        this.circuit = circuit;
        this.circuitState = circuitState;
        this.base = base;
        this.g = g;
        this.showState = true;
        this.showColor = true;
        this.printView = printView;
        this.highlightedWires = WireSet.EMPTY;
        this.instancePainter = new InstancePainter(this, null);
    }

    public ComponentDrawContext(java.awt.Component c,
            Circuit circuit, CircuitState circuitState,
            Graphics base, Graphics g) {
        this(c, circuit, circuitState, base, g, false);
    }

    public void setShowState(boolean value) {
        showState = value;
    }

    public void setShowColor(boolean value) {
        showColor = value;
    }

    public InstancePainter getInstancePainter() {
        return instancePainter;
    }

    public void setHighlightedWires(WireSet value) {
        this.highlightedWires = value == null ? WireSet.EMPTY : value;
    }

    public WireSet getHighlightedWires() {
        return highlightedWires;
    }

    public boolean getShowState() {
        return !printView && showState;
    }

    public boolean isPrintView() {
        return printView;
    }

    public boolean shouldDrawColor() {
        return !printView && showColor;
    }

    public java.awt.Component getDestination() {
        return dest;
    }

    public Graphics getGraphics() {
        return g;
    }

    public Circuit getCircuit() {
        return circuit;
    }

    public CircuitState getCircuitState() {
        return circuitState;
    }

    public void setGraphics(Graphics g) {
        this.g = g;
    }

    public Object getGateShape() {
        return AppPreferences.GATE_SHAPE.get();
    }

    //
    // helper methods
    //
    public void drawBounds(Component comp) {
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(ColorRegistry.BaseGateBorderColor);
        Bounds bds = comp.getBounds();
        g.drawRect(bds.getX(), bds.getY(),
                bds.getWidth(), bds.getHeight());
        GraphicsUtil.switchToWidth(g, 1);
    }

    public void drawRectangle(Component comp) {
        drawRectangle(comp, "");
    }

    public void drawRectangle(Component comp, String label) {
        Bounds bds = comp.getBounds(g);
        drawRectangle(bds.getX(), bds.getY(), bds.getWidth(),
            bds.getHeight(), label);
    }

    public void drawRectangle(int x, int y,
            int width, int height, String label) {
        GraphicsUtil.switchToWidth(g, 2);
        g.drawRect(x, y, width, height);
        if (label != null && !label.equals("")) {
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
            FontMetrics fm = base.getFontMetrics(g.getFont());
            int lwid = fm.stringWidth(label);
            g.drawString(label, x + (width - lwid) / 2,
                y + (height + fm.getAscent()) / 2 - 1);
        }
    }

    public void drawRectangle(ComponentFactory source, int x, int y,
            AttributeSet attrs, String label) {
        Bounds bds = source.getOffsetBounds(attrs);
        drawRectangle(source, x + bds.getX(), y + bds.getY(), bds.getWidth(),
            bds.getHeight(), label);
    }

    public void drawRectangle(ComponentFactory source, int x, int y,
            int width, int height, String label) {
        GraphicsUtil.switchToWidth(g, 2);
        g.drawRect(x + 1, y + 1, width - 1, height - 1);
        if (label != null && !label.equals("")) {
            FontMetrics fm = base.getFontMetrics(g.getFont());
            int lwid = fm.stringWidth(label);
            // centered at top edge
            if (height > 20) {
                g.drawString(label, x + (width - lwid) / 2,
                    y + 2 + fm.getAscent());
            // centered overall
            } else {
                g.drawString(label, x + (width - lwid) / 2,
                    y + (height + fm.getAscent()) / 2 - 1);
            }
        }
    }

    public void drawDongle(int x, int y) {
        GraphicsUtil.switchToWidth(g, 2);
        g.drawOval(x - 4, y - 4, 9, 9);
    }

    public void drawPin(Component comp, int i, String label, Direction dir) {
        Color curColor = g.getColor();
        if (i < 0 || i >= comp.getEnds().size()) {
            return;
        }

        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        int x = pt.getX();
        int y = pt.getY();
        if (getShowState()) {
            CircuitState state = getCircuitState();
            g.setColor(state.getValue(pt).getColor());
        } else {
            g.setColor(ColorRegistry.BaseGateBorderColor);
        }

        g.fillOval(x - PIN_OFFS, y - PIN_OFFS, PIN_RAD, PIN_RAD);
        g.setColor(curColor);
        if (dir == Direction.EAST) {
            GraphicsUtil.drawText(g, label, x + 3, y, GraphicsUtil.H_LEFT, GraphicsUtil.V_CENTER);
        } else if (dir == Direction.WEST) {
            GraphicsUtil.drawText(g, label, x - 3, y, GraphicsUtil.H_RIGHT, GraphicsUtil.V_CENTER);
        } else if (dir == Direction.SOUTH) {
            GraphicsUtil.drawText(g, label, x, y - 3, GraphicsUtil.H_CENTER, GraphicsUtil.V_BASELINE);
        } else if (dir == Direction.NORTH) {
            GraphicsUtil.drawText(g, label, x, y + 3, GraphicsUtil.H_CENTER, GraphicsUtil.V_TOP);
        }
    }

    public void drawPin(Component comp, int i) {
        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        Color curColor = g.getColor();
        if (getShowState()) {
            CircuitState state = getCircuitState();
            g.setColor(state.getValue(pt).getColor());
        } else {
            g.setColor(ColorRegistry.BaseGateBorderColor);
        }
        g.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD, PIN_RAD);
        g.setColor(curColor);
    }

    public void drawPins(Component comp) {
        Color curColor = g.getColor();
        for (EndData e : comp.getEnds()) {
            Location pt = e.getLocation();
            if (getShowState()) {
                CircuitState state = getCircuitState();
                g.setColor(state.getValue(pt).getColor());
            } else {
                g.setColor(ColorRegistry.BaseGateBorderColor);
            }
            g.fillOval(pt.getX() - PIN_OFFS, pt.getY() - PIN_OFFS, PIN_RAD, PIN_RAD);
        }
        g.setColor(curColor);
    }

    public void drawClock(Component comp, int i, Direction dir) {
        Color curColor = g.getColor();
        g.setColor(ColorRegistry.BaseGateBorderColor);
        GraphicsUtil.switchToWidth(g, 2);

        EndData e = comp.getEnd(i);
        Location pt = e.getLocation();
        int x = pt.getX();
        int y = pt.getY();
        final int CLK_SZ = 4;
        final int CLK_SZD = CLK_SZ - 1;
        if (dir == Direction.NORTH) {
            int[] _x = {x - CLK_SZD - 2, x - CLK_SZD, x, x + CLK_SZD, x + CLK_SZD + 2};
            int[] _y = {y, y, y - CLK_SZ, y, y};
            g.drawPolyline(_x, _y, 5);
        } else if (dir == Direction.SOUTH) {
            int[] _x = {x - CLK_SZD - 2, x - CLK_SZD, x, x + CLK_SZD, x + CLK_SZD + 2};
            int[] _y = {y, y, y + CLK_SZ, y, y};
            g.drawPolyline(_x, _y, 5);
        } else if (dir == Direction.EAST) {
            int[] _x = {x, x, x + CLK_SZ, x, x};
            int[] _y = {y - CLK_SZD - 2, y - CLK_SZD, y, y + CLK_SZD, y + CLK_SZD + 2};
            g.drawPolyline(_x, _y, 5);
        } else if (dir == Direction.WEST) {
            int[] _x = {x, x, x - CLK_SZ, x, x};
            int[] _y = {y - CLK_SZD - 2, y - CLK_SZD, y, y + CLK_SZD, y + CLK_SZD + 2};
            g.drawPolyline(_x, _y, 5);
        }

        g.setColor(curColor);
        GraphicsUtil.switchToWidth(g, 1);
    }

    public void drawHandles(Component comp) {
        Bounds b = comp.getBounds(g);
        int left = b.getX();
        int right = left + b.getWidth();
        int top = b.getY();
        int bot = top + b.getHeight();
        drawHandle(right, top);
        drawHandle(left,  bot);
        drawHandle(right, bot);
        drawHandle(left,  top);
    }

    public void drawHandle(Location loc) {
        drawHandle(loc.getX(), loc.getY());
    }

    public void drawHandle(int x, int y) {
        g.setColor(ColorRegistry.HandleColor);
        g.fillOval(x - 2, y - 2, 5, 5);
    }

}
