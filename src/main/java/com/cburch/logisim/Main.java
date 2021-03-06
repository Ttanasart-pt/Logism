/*
 * Copyright (c) 2012, Carl Burch.
 *
 * This file is part of the Logisim source code. The latest
 * version is available at http://www.cburch.com/logisim/.
 *
 * Logisim is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * Logisim is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Logisim.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.cburch.logisim;

import com.cburch.draw.util.ColorRegistry;
import com.cburch.logisim.gui.start.Startup;

import com.cburch.logisim.util.GraphicsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main entry point into Logisim.
 *
 * @version 3.0
 * @author Carl Burch
 */
public class Main {
    public static final LogisimVersion VERSION = LogisimVersion.get(3, 0, 0);
    public static final String VERSION_NAME = VERSION.toString();
    public static final int COPYRIGHT_YEAR = 2020;

    /** Logger */
    public static final Logger logger = LoggerFactory.getLogger( Main.class );

    /**
     * Entry point
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Startup startup = Startup.parseArgs(args);
        if (startup == null) {
            System.exit(0);
        } else {
            logger.info( "Starting Logisim" );
            ColorRegistry.ColorInit();
            GraphicsUtil.GraphicInit();
            startup.run();
        }
    }
}
