/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.memory;

import java.util.List;

import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;

import static com.cburch.logisim.util.LocaleString.*;

public class Memory extends Library {
    protected static final int DELAY = 5;

    private static final FactoryDescription[] DESCRIPTIONS = {
        new FactoryDescription("D Flip-Flop", "D", getFromLocale("dFlipFlopComponent"), "dFlipFlop.svg", "DFlipFlop"),
        new FactoryDescription("T Flip-Flop", "T", getFromLocale("tFlipFlopComponent"), "tFlipFlop.svg", "TFlipFlop"),
        new FactoryDescription("J-K Flip-Flop", "JK", getFromLocale("jkFlipFlopComponent"), "jkFlipFlop.svg", "JKFlipFlop"),
        new FactoryDescription("S-R Flip-Flop", "SR", getFromLocale("srFlipFlopComponent"), "srFlipFlop.svg", "SRFlipFlop"),
        new FactoryDescription("Register", "Register", getFromLocale("registerComponent"), "register.svg", "Register"),
        new FactoryDescription("Counter", "Counter", getFromLocale("counterComponent"), "counter.svg", "Counter"),
        new FactoryDescription("Shift Register", "Shift reg", getFromLocale("shiftRegisterComponent"), "shiftreg.svg", "ShiftRegister"),
        new FactoryDescription("Random", "Random", getFromLocale("randomComponent"), "random.svg", "Random"),
        new FactoryDescription("RAM", "RAM", getFromLocale("ramComponent"), "ram.svg", "Ram"),
        new FactoryDescription("ROM", "ROM", getFromLocale("romComponent"), "rom.svg", "Rom"),
    };

    private List<Tool> tools = null;

    public Memory() { }

    @Override
    public String getName() { return "Memory"; }

    @Override
    public String getDisplayName() { return getFromLocale("memoryLibrary"); }

    @Override
    public List<Tool> getTools() {
        if (tools == null) {
            tools = FactoryDescription.getTools(Memory.class, DESCRIPTIONS);
        }
        return tools;
    }
}
