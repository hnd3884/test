package com.me.mdm.webclient.reports;

import java.util.List;
import java.util.Map;

public class MDMPdfHorizontalSection
{
    public String heading;
    public String title;
    public Map<String, String> columnDetails;
    public float[] columnWidths;
    public List<List> valuesList;
    public List<String> dateColumns;
    public float sectionWidth;
    
    public MDMPdfHorizontalSection() {
        this.heading = "";
        this.title = "";
        this.sectionWidth = 100.0f;
    }
}
