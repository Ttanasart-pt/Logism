package com.cburch.logisim.data;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.util.GraphicsUtil;

import javax.swing.*;
import java.awt.*;

public class JToggleButtonStatic extends JToggleButton {
    JToggleButtonStatic(String name) {
        super(name);

        setPreferredSize(new Dimension(60, 24));
    }

    @Override
    protected void paintComponent(Graphics g) {
        if(isSelected())
            g.setColor(ColorRegistry.Grey);
        else
            g.setColor(ColorRegistry.Grey.brighter());
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(ColorRegistry.GreyLight);
        g.drawRect(0, 0, getWidth(), getHeight());

        g.setColor(ColorRegistry.GreyBright.brighter());
        Font f = new JEditorPane().getFont();
        double tW = g.getFontMetrics().stringWidth(getText());
        if(tW > 56) {
            f = f.deriveFont(10f);
        }
        GraphicsUtil.drawCenteredText(g, getText(), getWidth() / 2, getHeight() / 2 - 2, f);
    }
}
