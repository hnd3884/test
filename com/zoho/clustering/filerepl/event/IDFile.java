package com.zoho.clustering.filerepl.event;

import java.io.Closeable;
import com.zoho.clustering.util.FileUtil;
import com.zoho.clustering.filerepl.MyRandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;

public class IDFile
{
    private int value;
    private File file;
    private boolean remote;
    
    public static void main(final String[] args) {
        final Logger logger = Logger.getLogger(IDFile.class.getName());
        final IDFile fidFile = new IDFile(new File("slave", "tt.fid"));
        fidFile.setValue(Integer.parseInt(args[0]));
        logger.log(Level.INFO, "{0}", fidFile.getValue());
    }
    
    public IDFile(final File file) {
        this(file, false);
    }
    
    public IDFile(final File file, final boolean remote) {
        this.file = file;
        this.remote = remote;
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IllegalArgumentException("[" + file.getAbsolutePath() + "] is not a file");
            }
            this.value = this.readFromFile();
        }
        else {
            this.setValue(1);
        }
    }
    
    public int getValue() {
        return this.remote ? this.readFromFile() : this.value;
    }
    
    public void incrValue() {
        if (this.remote) {
            throw new UnsupportedOperationException("Not supported for remote JVM");
        }
        this.writeToFile(this.value + 1);
        ++this.value;
    }
    
    public void setValue(final int newValue) {
        if (this.remote) {
            throw new UnsupportedOperationException("Not supported for remote JVM");
        }
        this.writeToFile(newValue);
        this.value = newValue;
    }
    
    private int readFromFile() {
        MyRandomAccessFile raf = null;
        try {
            raf = new MyRandomAccessFile(this.file, "r");
            String str = raf.readLine();
            if (str == null) {
                return -1;
            }
            str = str.trim();
            return (str.length() == 0) ? -1 : Integer.parseInt(str);
        }
        finally {
            FileUtil.Close(raf);
        }
    }
    
    private void writeToFile(final int fileIndex) {
        MyRandomAccessFile raf = null;
        try {
            raf = new MyRandomAccessFile(this.file, "rw");
            raf.writeBytes(EventLogPosition.fileIndexAsPaddedStr(fileIndex));
        }
        finally {
            FileUtil.Close(raf);
        }
    }
}
