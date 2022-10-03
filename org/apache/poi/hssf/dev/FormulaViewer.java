package org.apache.poi.hssf.dev;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.ExpPtg;
import org.apache.poi.ss.formula.ptg.FuncPtg;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.io.InputStream;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;

public class FormulaViewer
{
    private String file;
    private boolean list;
    
    public void run() throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem(new File(this.file), true);
             final InputStream is = BiffViewer.getPOIFSInputStream(fs)) {
            final List<Record> records = RecordFactory.createRecords(is);
            for (final Record record : records) {
                if (record.getSid() == 6) {
                    if (this.list) {
                        this.listFormula((FormulaRecord)record);
                    }
                    else {
                        this.parseFormulaRecord((FormulaRecord)record);
                    }
                }
            }
        }
    }
    
    private void listFormula(final FormulaRecord record) {
        final String sep = "~";
        final Ptg[] tokens = record.getParsedExpression();
        final int numptgs = tokens.length;
        Ptg token = tokens[numptgs - 1];
        String numArg;
        if (token instanceof FuncPtg) {
            numArg = String.valueOf(numptgs - 1);
        }
        else {
            numArg = String.valueOf(-1);
        }
        final StringBuilder buf = new StringBuilder();
        if (token instanceof ExpPtg) {
            return;
        }
        buf.append(token.toFormulaString());
        buf.append(sep);
        switch (token.getPtgClass()) {
            case 0: {
                buf.append("REF");
                break;
            }
            case 32: {
                buf.append("VALUE");
                break;
            }
            case 64: {
                buf.append("ARRAY");
                break;
            }
            default: {
                throwInvalidRVAToken(token);
                break;
            }
        }
        buf.append(sep);
        if (numptgs > 1) {
            token = tokens[numptgs - 2];
            switch (token.getPtgClass()) {
                case 0: {
                    buf.append("REF");
                    break;
                }
                case 32: {
                    buf.append("VALUE");
                    break;
                }
                case 64: {
                    buf.append("ARRAY");
                    break;
                }
                default: {
                    throwInvalidRVAToken(token);
                    break;
                }
            }
        }
        else {
            buf.append("VALUE");
        }
        buf.append(sep);
        buf.append(numArg);
        System.out.println(buf);
    }
    
    public void parseFormulaRecord(final FormulaRecord record) {
        System.out.println("==============================");
        System.out.print("row = " + record.getRow());
        System.out.println(", col = " + record.getColumn());
        System.out.println("value = " + record.getValue());
        System.out.print("xf = " + record.getXFIndex());
        System.out.print(", number of ptgs = " + record.getParsedExpression().length);
        System.out.println(", options = " + record.getOptions());
        System.out.println("RPN List = " + this.formulaString(record));
        System.out.println("Formula text = " + composeFormula(record));
    }
    
    private String formulaString(final FormulaRecord record) {
        final StringBuilder buf = new StringBuilder();
        final Ptg[] parsedExpression;
        final Ptg[] tokens = parsedExpression = record.getParsedExpression();
        for (final Ptg token : parsedExpression) {
            buf.append(token.toFormulaString());
            switch (token.getPtgClass()) {
                case 0: {
                    buf.append("(R)");
                    break;
                }
                case 32: {
                    buf.append("(V)");
                    break;
                }
                case 64: {
                    buf.append("(A)");
                    break;
                }
                default: {
                    throwInvalidRVAToken(token);
                    break;
                }
            }
            buf.append(' ');
        }
        return buf.toString();
    }
    
    private static void throwInvalidRVAToken(final Ptg token) {
        throw new IllegalStateException("Invalid RVA type (" + token.getPtgClass() + "). This should never happen.");
    }
    
    private static String composeFormula(final FormulaRecord record) {
        return HSSFFormulaParser.toFormulaString(null, record.getParsedExpression());
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
    public void setList(final boolean list) {
        this.list = list;
    }
    
    public static void main(final String[] args) throws IOException {
        if (args == null || args.length > 2 || args[0].equals("--help")) {
            System.out.println("FormulaViewer .8 proof that the devil lies in the details (or just in BIFF8 files in general)");
            System.out.println("usage: Give me a big fat file name");
        }
        else if (args[0].equals("--listFunctions")) {
            final FormulaViewer viewer = new FormulaViewer();
            viewer.setFile(args[1]);
            viewer.setList(true);
            viewer.run();
        }
        else {
            final FormulaViewer viewer = new FormulaViewer();
            viewer.setFile(args[0]);
            viewer.run();
        }
    }
}
