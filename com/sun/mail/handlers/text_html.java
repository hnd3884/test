package com.sun.mail.handlers;

import javax.activation.ActivationDataFlavor;

public class text_html extends text_plain
{
    private static ActivationDataFlavor[] myDF;
    
    @Override
    protected ActivationDataFlavor[] getDataFlavors() {
        return text_html.myDF;
    }
    
    static {
        text_html.myDF = new ActivationDataFlavor[] { new ActivationDataFlavor(String.class, "text/html", "HTML String") };
    }
}
