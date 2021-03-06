/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.base;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;
import java.awt.Rectangle;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.instance.Instance;
import com.cburch.logisim.instance.InstanceFactory;
import com.cburch.logisim.instance.InstancePainter;
import com.cburch.logisim.instance.InstanceState;
import com.cburch.logisim.util.GraphicsUtil;
import static com.cburch.logisim.util.LocaleString.*;

public class Text extends InstanceFactory {
    public static Attribute<String> ATTR_TEXT = Attributes.forString("text",
            getFromLocale("textTextAttr"));
    public static Attribute<Font> ATTR_FONT = Attributes.forFont("font",
        getFromLocale("textFontAttr"));
    public static Attribute<AttributeOption> ATTR_HALIGN = Attributes.forOptionButton("halign",
        getFromLocale("textHorzAlignAttr"), new AttributeOption[] {
            new AttributeOption(TextField.H_LEFT, "left", getFromLocale("textHorzAlignLeftOpt")),
            new AttributeOption(TextField.H_RIGHT, "right", getFromLocale("textHorzAlignRightOpt")),
            new AttributeOption(TextField.H_CENTER, "center", getFromLocale("textHorzAlignCenterOpt")),
        });

    public static Attribute<AttributeOption> ATTR_VALIGN = Attributes.forOptionButton("valign",
        getFromLocale("textVertAlignAttr"), new AttributeOption[] {
            new AttributeOption(TextField.V_TOP, "top", getFromLocale("textVertAlignTopOpt")),
            new AttributeOption(TextField.V_BASELINE, "base", getFromLocale("textVertAlignBaseOpt")),
            new AttributeOption(TextField.V_BOTTOM, "bottom", getFromLocale("textVertAlignBottomOpt")),
            new AttributeOption(TextField.H_CENTER, "center", getFromLocale("textVertAlignCenterOpt")),
        });

    public static final Text FACTORY = new Text();

    private Text() {
        super("Text", "Text", getFromLocale("textComponent"));
        setIconName("text.svg");
        setShouldSnap(false);
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new TextAttributes();
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrsBase) {
        TextAttributes attrs = (TextAttributes) attrsBase;
        String text = attrs.getText();
        if (text == null || text.equals("")) {
            return Bounds.EMPTY_BOUNDS;
        } else {
            Bounds bds = attrs.getOffsetBounds();
            if (bds == null) {
                bds = estimateBounds(attrs);
                attrs.setOffsetBounds(bds);
            }
            return bds == null ? Bounds.EMPTY_BOUNDS : bds;
        }
    }

    private Bounds estimateBounds(TextAttributes attrs) {
        // TODO - you can imagine being more clever here
        String text = attrs.getText();
        if (text == null || text.length() == 0) {
            return Bounds.EMPTY_BOUNDS;
        }

        int size = attrs.getFont().getSize();
        int h = size;
        int w = size * text.length() / 2;
        int ha = attrs.getHorizontalAlign();
        int va = attrs.getVerticalAlign();
        int x;
        int y;
        if (ha == TextField.H_LEFT) {
            x = 0;
        } else if (ha == TextField.H_RIGHT) {
            x = -w;
        } else {
            x = -w / 2;
        }
        if (va == TextField.V_TOP) {
            y = 0;
        } else if (va == TextField.V_CENTER) {
            y = -h / 2;
        } else {
            y = -h;
        }
        return Bounds.create(x, y, w, h);
    }

    //
    // graphics methods
    //
    @Override
    public void paintGhost(InstancePainter painter) {
        TextAttributes attrs = (TextAttributes) painter.getAttributeSet();
        String text = attrs.getText();
        if (text == null || text.equals("")) {
            return;
        }


        int halign = attrs.getHorizontalAlign();
        int valign = attrs.getVerticalAlign();
        Graphics g = painter.getGraphics();
        Font old = g.getFont();
        g.setFont(attrs.getFont());
        GraphicsUtil.drawText(g, text, 0, 0, halign, valign);

        String textTrim = text.endsWith(" ") ? text.substring(0, text.length() - 1) : text;
        Bounds newBds;
        if (textTrim.equals("")) {
            newBds = Bounds.EMPTY_BOUNDS;
        } else {
            Rectangle bdsOut = GraphicsUtil.getTextBounds(g, textTrim, 0, 0,
                    halign, valign);
            newBds = Bounds.create(bdsOut).expand(4);
        }
        if (attrs.setOffsetBounds(newBds)) {
            Instance instance = painter.getInstance();
            if (instance != null) {
                instance.recomputeBounds();
            }

        }

        g.setFont(old);
    }

    @Override
    public void paintInstance(InstancePainter painter) {
        Location loc = painter.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        Graphics g = painter.getGraphics();
        g.translate(x, y);
        g.setColor(ColorRegistry.BaseGateBorderColor);
        paintGhost(painter);
        g.translate(-x, -y);
    }

    //
    // methods for instances
    //
    @Override
    protected void configureNewInstance(Instance instance) {
        configureLabel(instance);
        instance.addAttributeListener();
    }

    @Override
    protected void instanceAttributeChanged(Instance instance, Attribute<?> attr) {
        if (attr == ATTR_HALIGN || attr == ATTR_VALIGN) {
            configureLabel(instance);
        }
    }

    private void configureLabel(Instance instance) {
        TextAttributes attrs = (TextAttributes) instance.getAttributeSet();
        Location loc = instance.getLocation();
        instance.setTextField(ATTR_TEXT, ATTR_FONT, loc.getX(), loc.getY(),
                attrs.getHorizontalAlign(), attrs.getVerticalAlign());
    }

    @Override
    public void propagate(InstanceState state) { }
}
