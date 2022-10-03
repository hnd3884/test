package sun.awt.windows;

import java.awt.datatransfer.DataFlavor;

enum EHTMLReadMode
{
    HTML_READ_ALL, 
    HTML_READ_FRAGMENT, 
    HTML_READ_SELECTION;
    
    public static EHTMLReadMode getEHTMLReadMode(final DataFlavor dataFlavor) {
        EHTMLReadMode ehtmlReadMode = EHTMLReadMode.HTML_READ_SELECTION;
        final String parameter = dataFlavor.getParameter("document");
        if ("all".equals(parameter)) {
            ehtmlReadMode = EHTMLReadMode.HTML_READ_ALL;
        }
        else if ("fragment".equals(parameter)) {
            ehtmlReadMode = EHTMLReadMode.HTML_READ_FRAGMENT;
        }
        return ehtmlReadMode;
    }
}
