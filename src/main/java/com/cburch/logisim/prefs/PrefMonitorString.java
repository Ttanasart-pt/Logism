/* Copyright (c) 2010, Carl Burch. License information is located in the
 * com.cburch.logisim.Main source code and at www.cburch.com/logisim/. */

package com.cburch.logisim.prefs;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.Preferences;

class PrefMonitorString extends AbstractPrefMonitor<String> {
    private final String dflt;
    private String value;

    PrefMonitorString(String name, String dflt) {
        super(name);
        this.dflt = dflt;
        Preferences prefs = AppPreferences.getPrefs();
        this.value = prefs.get(name, dflt);
        prefs.addPreferenceChangeListener(this);
    }

    @Override
    public void restore() { value = dflt; }

    @Override
    public String get() {
        return value;
    }

    @Override
    public void set(String newValue) {
        String oldValue = value;
        if (!isSame(oldValue, newValue)) {
            value = newValue;
            AppPreferences.getPrefs().put(getIdentifier(), newValue);
        }
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent event) {
        Preferences prefs = event.getNode();
        String prop = event.getKey();
        String name = getIdentifier();
        if (prop.equals(name)) {
            String oldValue = value;
            String newValue = prefs.get(name, dflt);
            if (!isSame(oldValue, newValue)) {
                value = newValue;
                AppPreferences.firePropertyChange(name, oldValue, newValue);
            }
        }
    }

    private static boolean isSame(String a, String b) {
        return a == null ? b == null : a.equals(b);
    }
}
