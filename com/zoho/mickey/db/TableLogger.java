package com.zoho.mickey.db;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import java.util.Objects;
import org.json.JSONObject;

public class TableLogger
{
    private final int tablePadding = 2;
    private final char seperatorChar = '-';
    private final char seperatorTab = '|';
    private final char space = ' ';
    private final int headerSpacing = 20;
    private String[] headers;
    private String[][] content;
    private String name;
    private int[] maxLength;
    
    public TableLogger(final String name, final JSONObject table) {
        Objects.requireNonNull(name, "Headers cannot be null");
        Objects.requireNonNull(table, "table cannot be null");
        this.name = name;
        final JSONArray columns = table.getJSONArray("columns");
        final int noOfColumns = columns.length();
        this.headers = new String[noOfColumns];
        for (int i = 0; i < noOfColumns; ++i) {
            this.headers[i] = columns.get(i).toString();
        }
        final JSONArray data = table.getJSONArray("data");
        final int noOfRows = data.length();
        this.content = new String[noOfRows][noOfColumns];
        for (int j = 0; j < noOfRows; ++j) {
            final JSONArray row = data.getJSONArray(j);
            for (int k = 0; k < noOfColumns; ++k) {
                if (row.isNull(k)) {
                    this.content[j][k] = "";
                }
                else {
                    this.content[j][k] = row.getString(k).replaceAll("\\r\\n|\\r|\\n", " ");
                }
            }
        }
        this.postProcess();
    }
    
    public TableLogger(final String name, final String[] headers, final String[][] content) {
        Objects.requireNonNull(name, "Name cannot be null");
        Objects.requireNonNull(headers, "Headers cannot be null");
        Objects.requireNonNull(content, "Content cannot be null");
        this.name = name;
        this.headers = headers;
        this.content = content;
        this.postProcess();
    }
    
    private void postProcess() {
        this.maxLength = new int[this.headers.length];
        for (int i = 0; i < this.headers.length; ++i) {
            this.maxLength[i] = this.headers[i].length();
        }
        for (int i = 0; i < this.content.length; ++i) {
            final String[] temp = this.content[i];
            for (int j = 0; j < temp.length; ++j) {
                if (temp[j].length() > this.maxLength[j]) {
                    this.maxLength[j] = temp[j].length();
                }
            }
        }
    }
    
    public String getTableContent() {
        final String newLine = System.lineSeparator();
        final int totalPadding = 4;
        final String padder = StringUtils.repeat(' ', 2);
        final int columnMaxSize = this.maxLength.length * (totalPadding + 1) + this.sum(this.maxLength) - 1;
        final StringBuilder sb = new StringBuilder((4 + columnMaxSize) * (this.content.length * 2 + 2 + 3));
        sb.append('|');
        sb.append(StringUtils.repeat('-', columnMaxSize));
        sb.append('|');
        sb.append(newLine);
        final String headerRowSeperator = sb.toString();
        sb.setLength(0);
        for (int i = 0; i < this.maxLength.length; ++i) {
            sb.append('|');
            sb.append(StringUtils.repeat('-', this.maxLength[i] + totalPadding));
        }
        sb.append('|');
        final String rowSeperator = sb.toString();
        sb.setLength(0);
        sb.append(headerRowSeperator);
        sb.append('|');
        sb.append(StringUtils.repeat(' ', 20));
        sb.append(this.name);
        sb.append(StringUtils.repeat(' ', columnMaxSize - this.name.length() - 20));
        sb.append('|');
        sb.append(newLine);
        sb.append(rowSeperator);
        sb.append(newLine);
        sb.append('|');
        for (int j = 0; j < this.headers.length; ++j) {
            sb.append(padder);
            sb.append(this.headers[j]);
            sb.append(StringUtils.repeat(' ', this.maxLength[j] - this.headers[j].length()));
            sb.append(padder);
            sb.append('|');
        }
        sb.append(newLine);
        sb.append(rowSeperator);
        sb.append(newLine);
        for (int j = 0; j < this.content.length; ++j) {
            final String[] tempRow = this.content[j];
            sb.append('|');
            for (int k = 0; k < tempRow.length; ++k) {
                sb.append(padder);
                sb.append(tempRow[k]);
                sb.append(StringUtils.repeat(' ', this.maxLength[k] - tempRow[k].length()));
                sb.append(padder);
                sb.append('|');
            }
            sb.append(newLine);
            sb.append(rowSeperator);
            sb.append(newLine);
        }
        return sb.toString();
    }
    
    private int sum(final int[] maxLength2) {
        int sum = 0;
        for (final int i : maxLength2) {
            sum += i;
        }
        return sum;
    }
}
