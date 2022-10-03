package com.sun.imageio.plugins.jpeg;

import java.util.ListResourceBundle;

public class JPEGImageReaderResources extends ListResourceBundle
{
    @Override
    protected Object[][] getContents() {
        return new Object[][] { { Integer.toString(0), "Truncated File - Missing EOI marker" }, { Integer.toString(1), "JFIF markers not allowed in JFIF JPEG thumbnail; ignored" }, { Integer.toString(2), "Embedded color profile is invalid; ignored" } };
    }
}
