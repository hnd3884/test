package org.antlr.v4.runtime.misc;

import java.util.Iterator;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.IOException;
import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class LogManager
{
    protected List<Record> records;
    
    public void log(final String component, final String msg) {
        final Record r = new Record();
        r.component = component;
        r.msg = msg;
        if (this.records == null) {
            this.records = new ArrayList<Record>();
        }
        this.records.add(r);
    }
    
    public void log(final String msg) {
        this.log(null, msg);
    }
    
    public void save(final String filename) throws IOException {
        final FileWriter fw = new FileWriter(filename);
        final BufferedWriter bw = new BufferedWriter(fw);
        try {
            bw.write(this.toString());
        }
        finally {
            bw.close();
        }
    }
    
    public String save() throws IOException {
        final String dir = ".";
        final String defaultFilename = dir + "/antlr-" + new SimpleDateFormat("yyyy-MM-dd-HH.mm.ss").format(new Date()) + ".log";
        this.save(defaultFilename);
        return defaultFilename;
    }
    
    @Override
    public String toString() {
        if (this.records == null) {
            return "";
        }
        final String nl = System.getProperty("line.separator");
        final StringBuilder buf = new StringBuilder();
        for (final Record r : this.records) {
            buf.append(r);
            buf.append(nl);
        }
        return buf.toString();
    }
    
    public static void main(final String[] args) throws IOException {
        final LogManager mgr = new LogManager();
        mgr.log("atn", "test msg");
        mgr.log("dfa", "test msg 2");
        System.out.println(mgr);
        mgr.save();
    }
    
    protected static class Record
    {
        long timestamp;
        StackTraceElement location;
        String component;
        String msg;
        
        public Record() {
            this.timestamp = System.currentTimeMillis();
            this.location = new Throwable().getStackTrace()[0];
        }
        
        @Override
        public String toString() {
            final StringBuilder buf = new StringBuilder();
            buf.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date(this.timestamp)));
            buf.append(" ");
            buf.append(this.component);
            buf.append(" ");
            buf.append(this.location.getFileName());
            buf.append(":");
            buf.append(this.location.getLineNumber());
            buf.append(" ");
            buf.append(this.msg);
            return buf.toString();
        }
    }
}
