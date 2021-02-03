/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.generic;

import com.cburch.draw.util.ColorRegistry;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.MemoryImageSource;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;

public class GridPainter {
    public static final String ZOOM_PROPERTY = "zoom";
    public static final String SHOW_GRID_PROPERTY = "showgrid";

    private class Listener implements PropertyChangeListener {
        @Override
        public void propertyChange(PropertyChangeEvent event) {
            String prop = event.getPropertyName();
            Object val = event.getNewValue();
            if (prop.equals(ZoomModel.ZOOM)) {
                setZoomFactor((Double) val);
                destination.repaint();
            } else if (prop.equals(ZoomModel.SHOW_GRID)) {
                setShowGrid((Boolean) val);
                destination.repaint();
            }
        }
    }

    private final Component destination;
    private final PropertyChangeSupport support;
    private Listener listener;
    private ZoomModel zoomModel;
    private boolean showGrid;
    private final int gridSize;
    private double zoomFactor;
    private Image gridImage;
    private int gridImageWidth;

    public GridPainter(Component destination) {
        this.destination = destination;
        support = new PropertyChangeSupport(this);
        showGrid = true;
        gridSize = 10;
        zoomFactor = 1.0;
        updateGridImage(gridSize, zoomFactor);
    }

    public void addPropertyChangeListener(String prop, PropertyChangeListener listener) {
        support.addPropertyChangeListener(prop, listener);
    }

    public void removePropertyChangeListener(String prop, PropertyChangeListener listener) {
        support.removePropertyChangeListener(prop, listener);
    }

    public boolean getShowGrid() {
        return showGrid;
    }

    public void setShowGrid(boolean value) {
        if (showGrid != value) {
            showGrid = value;
            support.firePropertyChange(SHOW_GRID_PROPERTY, !value, value);
        }
    }

    public double getZoomFactor() {
        return zoomFactor;
    }

    public void setZoomFactor(double value) {
        double oldValue = zoomFactor;
        if (oldValue != value) {
            zoomFactor = value;
            updateGridImage(gridSize, value);
            support.firePropertyChange(ZOOM_PROPERTY, oldValue, value);
        }
    }

    public ZoomModel getZoomModel() {
        return zoomModel;
    }

    public void setZoomModel(ZoomModel model) {
        ZoomModel old = zoomModel;
        if (model != old) {
            if (listener == null) {
                listener = new Listener();
            }
            if (old != null) {
                old.removePropertyChangeListener(ZoomModel.ZOOM, listener);
                old.removePropertyChangeListener(ZoomModel.SHOW_GRID, listener);
            }
            zoomModel = model;
            if (model != null) {
                model.addPropertyChangeListener(ZoomModel.ZOOM, listener);
                model.addPropertyChangeListener(ZoomModel.SHOW_GRID, listener);
            }
            setShowGrid(model.getShowGrid());
            setZoomFactor(model.getZoomFactor());
            destination.repaint();
        }
    }

    public void paintGrid(Graphics g) {
        Rectangle clip = g.getClipBounds();
        double zoom = zoomFactor;

        if (!showGrid) {
            return;
        }


        Image img = gridImage;
        int w = gridImageWidth;
        if (img == null) {
            paintGridOld(g, gridSize, zoom, clip);
            return;
        }
        // round down to multiple of w
        int x0 = (clip.x / w) * w;
        int y0 = (clip.y / w) * w;
        for (int x = 0; x < clip.width + w; x += w) {
            for (int y = 0; y < clip.height + w; y += w) {
                g.drawImage(img, x0 + x, y0 + y, destination);
            }
        }
    }

    private void paintGridOld(Graphics g, int size, double f, Rectangle clip) {
        g.setColor(Color.GRAY);
        if (f == 1.0) {
            int start_x = ((clip.x + 9) / size) * size;
            int start_y = ((clip.y + 9) / size) * size;
            for (int x = 0; x < clip.width; x += size) {
                for (int y = 0; y < clip.height; y += size) {
                    g.fillRect(start_x + x, start_y + y, 1, 1);
                }
            }
        } else {
            /* Kevin Walsh of Cornell suggested the code below instead. */
            int x0 = size * (int) Math.ceil(clip.x / f / size);
            int x1 = x0 + (int) (clip.width / f);
            int y0 = size * (int) Math.ceil(clip.y / f / size);
            int y1 = y0 + (int) (clip.height / f);
            if (f <= 0.5) {
                g.setColor(ColorRegistry.CanvasGrid);
            }

            for (double x = x0; x < x1; x += size) {
                for (double y = y0; y < y1; y += size) {
                    int sx = (int) Math.round(f * x);
                    int sy = (int) Math.round(f * y);
                    g.fillRect(sx, sy, 1, 1);
                }
            }
        }
    }

    //
    // creating the grid image
    //
    private void updateGridImage(int size, double f) {
        double ww = f * size * 5;
        while (2 * ww < 150) ww *= 2;
        int w = (int) Math.round(ww);
        int[] pix = new int[w * w];
        Arrays.fill(pix, 0xFFFFFF);

        if (f == 1.0) {
            int lineStep = size * w;
            for (int j = 0; j < pix.length; j += lineStep) {
                for (int i = 0; i < w; i += size) {
                    pix[i + j] = ColorRegistry.CanvasGridValue;
                }
            }
        } else {
            int off0 = 0;
            int off1 = 1;
            // we'll draw several pixels for each grid point
            if (f >= 2.0) {
                int num = (int) (f + 0.001);
                off0 = -(num / 2);
                off1 = off0 + num;
            }

            int dotColor = ColorRegistry.CanvasGridValue;
            for (int j = 0; true; j += size) {
                int y = (int) Math.round(f * j);
                if (y + off0 >= w) {
                    break;
                }

                for (int yo = y + off0; yo < y + off1; yo++) {
                    if (yo >= 0 && yo < w) {
                        int base = yo * w;
                        for (int i = 0; true; i += size) {
                            int x = (int) Math.round(f * i);
                            if (x + off0 >= w) {
                                break;
                            }

                            for (int xo = x + off0; xo < x + off1; xo++) {
                                if (xo >= 0 && xo < w) {
                                    pix[base + xo] = dotColor;
                                }
                            }
                        }
                    }
                }
            }
        }
        gridImage = destination.createImage(new MemoryImageSource(w, w, pix, 0, w));
        gridImageWidth = w;
    }
}
