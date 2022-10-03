package com.sun.java.swing.plaf.windows;

import javax.swing.JMenuItem;

interface WindowsMenuItemUIAccessor
{
    JMenuItem getMenuItem();
    
    TMSchema.State getState(final JMenuItem p0);
    
    TMSchema.Part getPart(final JMenuItem p0);
}
