package org.apache.poi.hssf.dev;

import java.io.IOException;
import org.apache.poi.hssf.record.Record;
import java.io.InputStream;
import org.apache.poi.hssf.record.RecordFactory;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.File;

public class RecordLister
{
    String file;
    
    public void run() throws IOException {
        try (final POIFSFileSystem fs = new POIFSFileSystem(new File(this.file), true);
             final InputStream din = BiffViewer.getPOIFSInputStream(fs)) {
            final RecordInputStream rinp = new RecordInputStream(din);
            while (rinp.hasNextRecord()) {
                final int sid = rinp.getNextSid();
                rinp.nextRecord();
                final int size = rinp.available();
                final Class<? extends Record> clz = RecordFactory.getRecordClass(sid);
                System.out.print(formatSID(sid) + " - " + formatSize(size) + " bytes");
                if (clz != null) {
                    System.out.print("  \t");
                    System.out.print(clz.getName().replace("org.apache.poi.hssf.record.", ""));
                }
                System.out.println();
                final byte[] data = rinp.readRemainder();
                if (data.length > 0) {
                    System.out.print("   ");
                    System.out.println(formatData(data));
                }
            }
        }
    }
    
    private static String formatSID(final int sid) {
        final String hex = Integer.toHexString(sid);
        final String dec = Integer.toString(sid);
        final StringBuilder s = new StringBuilder();
        s.append("0x");
        for (int i = hex.length(); i < 4; ++i) {
            s.append('0');
        }
        s.append(hex);
        s.append(" (");
        for (int i = dec.length(); i < 4; ++i) {
            s.append('0');
        }
        s.append(dec);
        s.append(")");
        return s.toString();
    }
    
    private static String formatSize(final int size) {
        final String hex = Integer.toHexString(size);
        final String dec = Integer.toString(size);
        final StringBuilder s = new StringBuilder();
        for (int i = hex.length(); i < 3; ++i) {
            s.append('0');
        }
        s.append(hex);
        s.append(" (");
        for (int i = dec.length(); i < 3; ++i) {
            s.append('0');
        }
        s.append(dec);
        s.append(")");
        return s.toString();
    }
    
    private static String formatData(final byte[] data) {
        if (data == null || data.length == 0) {
            return "";
        }
        final StringBuilder s = new StringBuilder();
        if (data.length > 9) {
            s.append(byteToHex(data[0]));
            s.append(' ');
            s.append(byteToHex(data[1]));
            s.append(' ');
            s.append(byteToHex(data[2]));
            s.append(' ');
            s.append(byteToHex(data[3]));
            s.append(' ');
            s.append(" .... ");
            s.append(' ');
            s.append(byteToHex(data[data.length - 4]));
            s.append(' ');
            s.append(byteToHex(data[data.length - 3]));
            s.append(' ');
            s.append(byteToHex(data[data.length - 2]));
            s.append(' ');
            s.append(byteToHex(data[data.length - 1]));
        }
        else {
            for (final byte aData : data) {
                s.append(byteToHex(aData));
                s.append(' ');
            }
        }
        return s.toString();
    }
    
    private static String byteToHex(final byte b) {
        int i = b;
        if (i < 0) {
            i += 256;
        }
        final String s = Integer.toHexString(i);
        if (i < 16) {
            return "0" + s;
        }
        return s;
    }
    
    public void setFile(final String file) {
        this.file = file;
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length == 1 && !args[0].equals("--help")) {
            final RecordLister viewer = new RecordLister();
            viewer.setFile(args[0]);
            viewer.run();
        }
        else {
            System.out.println("RecordLister");
            System.out.println("Outputs the summary of the records in file order");
            System.out.println("usage: java org.apache.poi.hssf.dev.RecordLister filename");
        }
    }
}
