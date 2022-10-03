package org.jfree.resources;

import java.util.ListResourceBundle;

public class JCommonResources extends ListResourceBundle
{
    private static final Object[][] CONTENTS;
    
    static {
        CONTENTS = new Object[][] { { "project.name", "JCommon" }, { "project.version", "1.0.5" }, { "project.info", "http://www.jfree.org/jcommon/index.html" }, { "project.copyright", "(C)opyright 2000-2006, by Object Refinery Limited and Contributors" } };
    }
    
    public Object[][] getContents() {
        return JCommonResources.CONTENTS;
    }
}
