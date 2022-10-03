package org.w3c.tidy;

import java.awt.Image;
import java.beans.SimpleBeanInfo;

public class TidyBeanInfo extends SimpleBeanInfo
{
    public Image getIcon(final int n) {
        return this.loadImage("tidy.gif");
    }
}
