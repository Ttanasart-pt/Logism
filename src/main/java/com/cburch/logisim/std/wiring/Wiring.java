/* Copyright (c) 2011, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.std.wiring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.cburch.logisim.circuit.SplitterFactory;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.tools.FactoryDescription;
import com.cburch.logisim.tools.Library;
import com.cburch.logisim.tools.Tool;
import static com.cburch.logisim.util.LocaleString.*;

public class Wiring extends Library {

    static final AttributeOption GATE_TOP_LEFT = new AttributeOption("tl", getFromLocale("wiringGateTopLeftOption"));
    static final AttributeOption GATE_BOTTOM_RIGHT = new AttributeOption("br", getFromLocale("wiringGateBottomRightOption"));
    static final Attribute<AttributeOption> ATTR_GATE = Attributes.forOption("gate", getFromLocale("wiringGateAttr"),
            new AttributeOption[] { GATE_TOP_LEFT, GATE_BOTTOM_RIGHT });

    private static final Tool[] ADD_TOOLS = {
        new AddTool(SplitterFactory.instance),
        new AddTool(Pin.FACTORY),
        new AddTool(Probe.FACTORY),
        new AddTool(Tunnel.FACTORY),
        new AddTool(PullResistor.FACTORY),
        new AddTool(Clock.FACTORY),
        new AddTool(Constant.FACTORY),
    };

    private static final FactoryDescription[] DESCRIPTIONS = {
        new FactoryDescription("Power", "Power", getFromLocale("powerComponent"), "power.svg", "Power"),
        new FactoryDescription("Ground", "GND", getFromLocale("groundComponent"), "ground.svg", "Ground"),
        new FactoryDescription("Transistor", "Transis", getFromLocale("transistorComponent"), "trans0.svg", "Transistor"),
        new FactoryDescription("Transmission Gate", "tGate", getFromLocale("transmissionGateComponent"), "transmis.svg", "TransmissionGate"),
        new FactoryDescription("Bit Extender", "bExtend", getFromLocale("extenderComponent"), "extender.svg", "BitExtender"),
    };

    private List<Tool> tools = null;

    public Wiring() { }

    @Override
    public String getName() { return "Wiring"; }

    @Override
    public String getDisplayName() { return getFromLocale("wiringLibrary"); }

    @Override
    public List<Tool> getTools() {
        if (tools == null) {
            List<Tool> ret = new ArrayList<>(ADD_TOOLS.length + DESCRIPTIONS.length);
            Collections.addAll(ret, ADD_TOOLS);
            ret.addAll(FactoryDescription.getTools(Wiring.class, DESCRIPTIONS));
            tools = ret;
        }
        return tools;
    }
}
