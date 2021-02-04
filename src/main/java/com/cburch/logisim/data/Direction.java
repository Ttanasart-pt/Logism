/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.data;

import static com.cburch.logisim.util.LocaleString.*;

public class Direction implements AttributeOptionInterface {
    public static final Direction EAST
        = new Direction("east", getFromLocale("directionEastOption"), getFromLocale("directionEastVertical"), 0, "GUI/dir_E.svg");
    public static final Direction WEST
        = new Direction("west", getFromLocale("directionWestOption"), getFromLocale("directionWestVertical"), 1, "GUI/dir_W.svg");
    public static final Direction NORTH
        = new Direction("north", getFromLocale("directionNorthOption"), getFromLocale("directionNorthVertical"), 2, "GUI/dir_N.svg");
    public static final Direction SOUTH
        = new Direction("south", getFromLocale("directionSouthOption"), getFromLocale("directionSouthVertical"), 3, "GUI/dir_S.svg");
    public static final Direction CENTER
        = new Direction("center", getFromLocale("directionSouthOption"), getFromLocale("directionSouthVertical"), 4, "GUI/dir_CENTER.svg");
    public static final Direction[] cardinals = { NORTH, EAST, SOUTH, WEST };

    public static Direction parse(String str) {
        if (str.equals(EAST.name)) {
             return EAST;
        } else if (str.equals(WEST.name)) {
             return WEST;
        } else if (str.equals(NORTH.name)) {
            return NORTH;
        } else if (str.equals(SOUTH.name)) {
            return SOUTH;
        }

        throw new NumberFormatException("illegal direction '" + str + "'");
    }

    private final String name;
    private final String disp;
    private final String vert;
    private final String path;
    private final int id;

    private Direction(String name, String disp, String vert, int id, String path) {
        this.name = name;
        this.disp = disp;
        this.vert = vert;
        this.id = id;
        this.path = path;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toDisplayString() {
        return disp.toString();
    }

    public String iconPath() {
        return path;
    }

    public String getDisplayGetter() {
        return disp;
    }

    public String toVerticalDisplayString() {
        return vert.toString();
    }

    @Override
    public int hashCode() {
        return id;
    }

	public boolean equals( Direction other ) {
		if (other != null) {
			return this.id == other.id;
		}
		return false;
	}
	
    public double toRadians() {
        if (this == Direction.EAST) {
            return 0.0;
        }

        if (this == Direction.WEST) {
            return Math.PI;
        }

        if (this == Direction.NORTH) {
            return Math.PI / 2.0;
        }

        if (this == Direction.SOUTH) {
            return -Math.PI / 2.0;
        }

        return 0.0;
    }

    public int toDegrees() {
        if (this == Direction.EAST) {
            return 0;
        }

        if (this == Direction.WEST) {
            return 180;
        }

        if (this == Direction.NORTH) {
            return 90;
        }

        if (this == Direction.SOUTH) {
            return 270;
        }

        return 0;
    }

    public Direction reverse() {
        if (this == Direction.EAST) {
            return Direction.WEST;
        }

        if (this == Direction.WEST) {
            return Direction.EAST;
        }

        if (this == Direction.NORTH) {
            return Direction.SOUTH;
        }

        if (this == Direction.SOUTH) {
            return Direction.NORTH;
        }

        return Direction.WEST;
    }

    public Direction getRight() {
        if (this == Direction.EAST) {
            return Direction.SOUTH;
        }

        if (this == Direction.WEST) {
            return Direction.NORTH;
        }

        if (this == Direction.NORTH) {
            return Direction.EAST;
        }

        if (this == Direction.SOUTH) {
            return Direction.WEST;
        }

        return Direction.WEST;
    }

    public Direction getLeft() {
        if (this == Direction.EAST) {
            return Direction.NORTH;
        }

        if (this == Direction.WEST) {
            return Direction.SOUTH;
        }

        if (this == Direction.NORTH) {
            return Direction.WEST;
        }

        if (this == Direction.SOUTH) {
            return Direction.EAST;
        }

        return Direction.WEST;
    }

    // for AttributeOptionInterface
    @Override
    public Object getValue() {
        return this;
    }
}
