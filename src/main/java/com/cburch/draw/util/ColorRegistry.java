package com.cburch.draw.util;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.prefs.AppPreferences;

import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class ColorRegistry {
    public static Color White = new Color(255, 254, 245);
    public static Color Black = new Color(20, 22, 26);

    public static Color Grey = new Color(60, 63, 65);
    public static Color GreyLight = new Color(69, 72, 74);
    public static Color GreyBright = new Color(126, 127, 130);
    public static Color GreyDark = new Color(32, 36, 40);

    public static Color Creme = new Color(255, 251, 222);
    public static Color Creme_sat = new Color(255, 248, 194);
    public static Color Blue = new Color(79, 161, 255);
    public static Color Purple = new Color(204, 52, 235);
    public static Color Red = new Color(255, 31, 87);
    public static Color Red_dark = new Color(196, 0, 29);
    public static Color Orange = new Color(255, 172, 84);
    public static Color Lime = new Color(174, 255, 105);
    public static Color Lime_dark = new Color(50, 130, 20);

    public static Color BaseGateBorderColor;
    public static Color HandleColor;

    public static Color CanvasBackground;
    public static Color CanvasGrid;
    public static int CanvasGridValue;
    public static Color WireIdle;
    public static Color TextColor;

    public static void ColorInit() {
        setStyle();
        AppPreferences.STYLE_CANVAS_BG.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_GRID_COLOR.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_TEXT_COLOR.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_GATE_COLOR.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_HANDLE_COLOR.addPropertyChangeListener(evt -> setStyle());

        AppPreferences.STYLE_WIRE_IDLE.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_NIL.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_FALSE.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_TRUE.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_UNKNOW.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_ERROR.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_WIDTH_ERROR.addPropertyChangeListener(evt -> setStyle());
        AppPreferences.STYLE_WIRE_MULTI.addPropertyChangeListener(evt -> setStyle());
    }

    public static void setStyle() {
        CanvasBackground = new Color(AppPreferences.STYLE_CANVAS_BG.get());
        CanvasGridValue = AppPreferences.STYLE_GRID_COLOR.get();
        CanvasGrid = new Color(CanvasGridValue);

        TextColor = new Color(AppPreferences.STYLE_TEXT_COLOR.get());
        BaseGateBorderColor = new Color(AppPreferences.STYLE_GATE_COLOR.get());
        HandleColor = new Color(AppPreferences.STYLE_HANDLE_COLOR.get());

        WireIdle = new Color(AppPreferences.STYLE_WIRE_IDLE.get());
        Value.NIL_COLOR = new Color(AppPreferences.STYLE_WIRE_NIL.get());
        Value.FALSE_COLOR = new Color(AppPreferences.STYLE_WIRE_FALSE.get());
        Value.TRUE_COLOR = new Color(AppPreferences.STYLE_WIRE_TRUE.get());
        Value.UNKNOWN_COLOR = new Color(AppPreferences.STYLE_WIRE_UNKNOW.get());
        Value.ERROR_COLOR = new Color(AppPreferences.STYLE_WIRE_ERROR.get());
        Value.WIDTH_ERROR_COLOR = new Color(AppPreferences.STYLE_WIRE_WIDTH_ERROR.get());
        Value.MULTI_COLOR = new Color(AppPreferences.STYLE_WIRE_MULTI.get());
    }
}
