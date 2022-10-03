package org.apache.axiom.util.stax.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Version
{
    private static final Pattern pattern;
    private final int[] components;
    
    Version(final String versionString) {
        final Matcher matcher = Version.pattern.matcher(versionString);
        if (matcher.matches()) {
            final String[] componentStrings = matcher.group(1).split("\\.");
            final int l = componentStrings.length;
            this.components = new int[l];
            for (int i = 0; i < l; ++i) {
                this.components[i] = Integer.parseInt(componentStrings[i]);
            }
        }
        else {
            this.components = new int[0];
        }
    }
    
    int getComponent(final int idx) {
        return (idx < this.components.length) ? this.components[idx] : 0;
    }
    
    static {
        pattern = Pattern.compile("([0-9]+(\\.[0-9]+)*)([\\.-].*)?");
    }
}
