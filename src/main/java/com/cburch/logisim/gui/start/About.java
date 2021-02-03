/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.gui.start;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.cburch.draw.util.ColorRegistry;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.swing.gvt.GVTTreeRendererAdapter;
import org.apache.batik.swing.svg.GVTTreeBuilderAdapter;
import org.apache.batik.swing.svg.SVGDocumentLoaderAdapter;

import com.cburch.logisim.Main;

public class About {
	static final int IMAGE_WIDTH = 300;
	static final int IMAGE_HEIGHT = 300;
	static final Dimension DIMENSION = new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT);
	protected static JSVGCanvas svgCanvas = new JSVGCanvas();
	
	public static JComponent createComponents() {
		final JPanel panel = new JPanel();
		panel.setPreferredSize(DIMENSION);
		svgCanvas.setPreferredSize(DIMENSION);
		panel.add(svgCanvas);
		panel.setBackground(new Color(0,0,0,0));
		svgCanvas.setURI(About.class.getResource("/logisim/drawing.svg").toString());
		svgCanvas.addSVGDocumentLoaderListener(new SVGDocumentLoaderAdapter() {});
		svgCanvas.addGVTTreeBuilderListener(new GVTTreeBuilderAdapter() {});
		svgCanvas.addGVTTreeRendererListener(new GVTTreeRendererAdapter() {});
		return panel;
	}

	public static void showAboutDialog(JFrame owner) {
		JOptionPane.showMessageDialog(owner, createComponents(), "Logisim " + Main.VERSION_NAME, JOptionPane.PLAIN_MESSAGE);
	}
}