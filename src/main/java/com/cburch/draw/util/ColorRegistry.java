package com.cburch.draw.util;

import com.cburch.logisim.data.Value;

import java.awt.*;

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
    public static Color Red = new Color(255, 31, 87);
    public static Color Red_dark = new Color(196, 0, 29);
    public static Color Orange = new Color(255, 172, 84);
    public static Color Lime = new Color(174, 255, 105);
    public static Color Lime_dark = new Color(12, 92, 12);

    public static Color BaseGateBorderColor;
    public static Color BaseGateBorderAccent;

    public static Color WireIdle;
    public static Color TextColor;

    public static void ColorInit() {
        BaseGateBorderColor = Creme;
        BaseGateBorderAccent = Creme_sat;

        WireIdle = Black;

        Value.NIL_COLOR = Black;
        Value.FALSE_COLOR = Red;
        Value.TRUE_COLOR = Lime;
        Value.UNKNOWN_COLOR = Black;
        Value.ERROR_COLOR = Red_dark;
        Value.WIDTH_ERROR_COLOR = Orange;
        Value.MULTI_COLOR = Blue;

        TextColor = White;
    }
}
