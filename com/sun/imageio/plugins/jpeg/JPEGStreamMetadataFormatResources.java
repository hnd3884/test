package com.sun.imageio.plugins.jpeg;

public class JPEGStreamMetadataFormatResources extends JPEGMetadataFormatResources
{
    @Override
    protected Object[][] getContents() {
        final Object[][] array = new Object[JPEGStreamMetadataFormatResources.commonContents.length][2];
        for (int i = 0; i < JPEGStreamMetadataFormatResources.commonContents.length; ++i) {
            array[i][0] = JPEGStreamMetadataFormatResources.commonContents[i][0];
            array[i][1] = JPEGStreamMetadataFormatResources.commonContents[i][1];
        }
        return array;
    }
}
