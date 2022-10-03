package com.sun.java.swing.plaf.motif;

import sun.awt.AppContext;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicLabelUI;

public class MotifLabelUI extends BasicLabelUI
{
    private static final Object MOTIF_LABEL_UI_KEY;
    
    public static ComponentUI createUI(final JComponent component) {
        final AppContext appContext = AppContext.getAppContext();
        MotifLabelUI motifLabelUI = (MotifLabelUI)appContext.get(MotifLabelUI.MOTIF_LABEL_UI_KEY);
        if (motifLabelUI == null) {
            motifLabelUI = new MotifLabelUI();
            appContext.put(MotifLabelUI.MOTIF_LABEL_UI_KEY, motifLabelUI);
        }
        return motifLabelUI;
    }
    
    static {
        MOTIF_LABEL_UI_KEY = new Object();
    }
}
