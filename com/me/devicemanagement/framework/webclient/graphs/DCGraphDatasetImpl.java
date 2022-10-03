package com.me.devicemanagement.framework.webclient.graphs;

import java.util.Date;
import java.util.Iterator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import java.util.LinkedList;
import java.util.Map;

public class DCGraphDatasetImpl
{
    public Object produceDataset(final Map params) {
        final String chartType = params.get("chartType");
        final LinkedList<GraphEntry> graphData = params.get("graphData");
        if (chartType.equals("Bar3D")) {
            final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
            if (graphData != null) {
                for (final GraphEntry entry : graphData) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        dataSet.addValue((Number)entry.getValue(), (Comparable)entry.getLabel(), (Comparable)"");
                    }
                }
            }
            return dataSet;
        }
        if (chartType.equals("Pie3D")) {
            final DefaultPieDataset dataSet2 = new DefaultPieDataset();
            if (graphData != null) {
                for (final GraphEntry entry : graphData) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        dataSet2.setValue((Comparable)entry.getLabel(), (Number)entry.getValue());
                    }
                }
            }
            return dataSet2;
        }
        if (chartType.equals("Pie")) {
            final DefaultPieDataset dataSet2 = new DefaultPieDataset();
            if (graphData != null) {
                for (final GraphEntry entry : graphData) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        dataSet2.setValue((Comparable)entry.getLabel(), (Number)entry.getValue());
                    }
                }
            }
            return dataSet2;
        }
        if (chartType.equals("Bar")) {
            final DefaultCategoryDataset dataSet = new DefaultCategoryDataset();
            if (graphData != null) {
                for (final GraphEntry entry : graphData) {
                    if (!entry.getName().equalsIgnoreCase("TOTAL")) {
                        dataSet.addValue((Number)entry.getValue(), (Comparable)entry.getLabel(), (Comparable)"");
                    }
                }
            }
            return dataSet;
        }
        return null;
    }
    
    public boolean hasExpired(final Map params, final Date since) {
        return since == null || System.currentTimeMillis() - since.getTime() > 3000L;
    }
    
    public String getProducerId() {
        return "DatasetProducer";
    }
}
