/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.arith;

import java.util.List;

import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import static com.cburch.logisim.util.LocaleString.*;

public class Arithmetic extends Library {
    private static final FactoryDescription[] DESCRIPTIONS = {
        new FactoryDescription("Adder", "Add", getFromLocale("adderComponent"), "adder.svg", "Adder"),
        new FactoryDescription("Subtractor", "Substract", getFromLocale("subtractorComponent"), "subtractor.svg", "Subtractor"),
        new FactoryDescription("Multiplier", "Multiply", getFromLocale("multiplierComponent"), "multiplier.svg", "Multiplier"),
        new FactoryDescription("Divider", "Divider", getFromLocale("dividerComponent"), "divider.svg", "Divider"),
        new FactoryDescription("Negator", "Negate", getFromLocale("negatorComponent"), "negator.svg", "Negator"),
        new FactoryDescription("Comparator", "Compare", getFromLocale("comparatorComponent"), "comparator.svg", "Comparator"),
        new FactoryDescription("Shifter", "Shift", getFromLocale("shifterComponent"), "shifter.svg", "Shifter"),
        new FactoryDescription("BitAdder", "bAdd", getFromLocale("bitAdderComponent"), "bitadder.svg", "BitAdder"),
        new FactoryDescription("BitFinder", "bFind", getFromLocale("bitFinderComponent"), "bitfindr.svg", "BitFinder"),
        new FactoryDescription("Pow", "Pow", getFromLocale("powComponent"), "pow.svg", "Pow"),
    };

    private List<Tool> tools = null;

    public Arithmetic() { }

    @Override
    public String getName() { return "Arithmetic"; }

    @Override
    public String getDisplayName() { return getFromLocale("arithmeticLibrary"); }

    @Override
    public List<Tool> getTools() {
        if (tools == null) {
            tools = FactoryDescription.getTools(Arithmetic.class, DESCRIPTIONS);
        }
        return tools;
    }
}
