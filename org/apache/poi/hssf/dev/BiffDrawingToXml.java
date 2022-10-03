package org.apache.poi.hssf.dev;

import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import java.util.Iterator;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.DrawingGroupRecord;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class BiffDrawingToXml
{
    private static final String SHEET_NAME_PARAM = "-sheet-name";
    private static final String SHEET_INDEXES_PARAM = "-sheet-indexes";
    private static final String EXCLUDE_WORKBOOK_RECORDS = "-exclude-workbook";
    
    private static int getAttributeIndex(final String attribute, final String[] params) {
        for (int i = 0; i < params.length; ++i) {
            final String param = params[i];
            if (attribute.equals(param)) {
                return i;
            }
        }
        return -1;
    }
    
    private static boolean isExcludeWorkbookRecords(final String[] params) {
        return -1 != getAttributeIndex("-exclude-workbook", params);
    }
    
    private static List<Integer> getIndexesByName(final String[] params, final HSSFWorkbook workbook) {
        final List<Integer> list = new ArrayList<Integer>();
        final int pos = getAttributeIndex("-sheet-name", params);
        if (-1 != pos) {
            if (pos >= params.length) {
                throw new IllegalArgumentException("sheet name param value was not specified");
            }
            final String sheetName = params[pos + 1];
            final int sheetPos = workbook.getSheetIndex(sheetName);
            if (-1 == sheetPos) {
                throw new IllegalArgumentException("specified sheet name has not been found in xls file");
            }
            list.add(sheetPos);
        }
        return list;
    }
    
    private static List<Integer> getIndexesByIdArray(final String[] params) {
        final List<Integer> list = new ArrayList<Integer>();
        final int pos = getAttributeIndex("-sheet-indexes", params);
        if (-1 != pos) {
            if (pos >= params.length) {
                throw new IllegalArgumentException("sheet list value was not specified");
            }
            final String sheetParam = params[pos + 1];
            final String[] split;
            final String[] sheets = split = sheetParam.split(",");
            for (final String sheet : split) {
                list.add(Integer.parseInt(sheet));
            }
        }
        return list;
    }
    
    private static List<Integer> getSheetsIndexes(final String[] params, final HSSFWorkbook workbook) {
        final List<Integer> list = new ArrayList<Integer>();
        list.addAll(getIndexesByIdArray(params));
        list.addAll(getIndexesByName(params, workbook));
        if (0 == list.size()) {
            for (int size = workbook.getNumberOfSheets(), i = 0; i < size; ++i) {
                list.add(i);
            }
        }
        return list;
    }
    
    private static String getInputFileName(final String[] params) {
        return params[params.length - 1];
    }
    
    private static String getOutputFileName(final String input) {
        if (input.contains("xls")) {
            return input.replace(".xls", ".xml");
        }
        return input + ".xml";
    }
    
    public static void main(final String[] params) throws IOException {
        if (0 == params.length) {
            System.out.println("Usage: BiffDrawingToXml [options] inputWorkbook");
            System.out.println("Options:");
            System.out.println("  -exclude-workbook            exclude workbook-level records");
            System.out.println("  -sheet-indexes   <indexes>   output sheets with specified indexes");
            System.out.println("  -sheet-namek  <names>        output sheets with specified name");
            return;
        }
        final String input = getInputFileName(params);
        final String output = getOutputFileName(input);
        try (final FileInputStream inp = new FileInputStream(input);
             final FileOutputStream outputStream = new FileOutputStream(output)) {
            writeToFile(outputStream, inp, isExcludeWorkbookRecords(params), params);
        }
    }
    
    public static void writeToFile(final OutputStream fos, final InputStream xlsWorkbook, final boolean excludeWorkbookRecords, final String[] params) throws IOException {
        try (final HSSFWorkbook workbook = new HSSFWorkbook(xlsWorkbook)) {
            final InternalWorkbook internalWorkbook = workbook.getInternalWorkbook();
            final DrawingGroupRecord r = (DrawingGroupRecord)internalWorkbook.findFirstRecordBySid((short)235);
            final StringBuilder builder = new StringBuilder();
            builder.append("<workbook>\n");
            final String tab = "\t";
            if (!excludeWorkbookRecords && r != null) {
                r.decode();
                final List<EscherRecord> escherRecords = r.getEscherRecords();
                for (final EscherRecord record : escherRecords) {
                    builder.append(record.toXml(tab));
                }
            }
            final List<Integer> sheets = getSheetsIndexes(params, workbook);
            for (final Integer i : sheets) {
                final HSSFPatriarch p = workbook.getSheetAt(i).getDrawingPatriarch();
                if (p != null) {
                    builder.append(tab).append("<sheet").append(i).append(">\n");
                    builder.append(p.getBoundAggregate().toXml(tab + "\t"));
                    builder.append(tab).append("</sheet").append(i).append(">\n");
                }
            }
            builder.append("</workbook>\n");
            fos.write(builder.toString().getBytes(StringUtil.UTF8));
        }
    }
}
