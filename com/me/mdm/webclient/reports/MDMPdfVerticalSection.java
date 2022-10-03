package com.me.mdm.webclient.reports;

import java.util.Map;

public class MDMPdfVerticalSection
{
    public String heading;
    public String title;
    public Map<String, String> sectionDetails;
    public int verticalColumnCount;
    public float[] columnWidths;
    public float sectionWidth;
    
    public MDMPdfVerticalSection() {
        this.heading = "";
        this.title = "";
        this.verticalColumnCount = 1;
        this.sectionWidth = 100.0f;
    }
}
