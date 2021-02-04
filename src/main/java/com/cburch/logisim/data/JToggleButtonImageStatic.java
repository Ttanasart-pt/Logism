package com.cburch.logisim.data;

import com.cburch.draw.tools.SVGIcon;
import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.util.Icons;

import javax.swing.*;
import java.awt.*;

public class JToggleButtonImageStatic extends JToggleButtonStatic {
    private final SVGIcon image;
    private final String tooltip;

    JToggleButtonImageStatic(String path, String tooltip) {
        super("");
        this.tooltip = tooltip;
        setToolTipText(tooltip);
        image = Icons.getIcon(path);

        setPreferredSize(new Dimension(24, 24));
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

        image.paintIcon(this, g, (getWidth() - 16) / 2, (getHeight() - 16) / 2);
    }
}
