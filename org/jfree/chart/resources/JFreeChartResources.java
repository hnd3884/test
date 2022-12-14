package org.jfree.chart.resources;

import java.util.ListResourceBundle;

public class JFreeChartResources extends ListResourceBundle
{
    private static final Object[][] CONTENTS;
    
    public Object[][] getContents() {
        return JFreeChartResources.CONTENTS;
    }
    
    static {
        CONTENTS = new Object[][] { { "project.name", "JFreeChart" }, { "project.version", "1.0.2" }, { "project.info", "http://www.jfree.org/jfreechart/index.html" }, { "project.copyright", "(C)opyright 2000-2006, by Object Refinery Limited and Contributors" } };
    }
}
