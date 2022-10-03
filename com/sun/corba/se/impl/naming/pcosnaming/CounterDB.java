package com.sun.corba.se.impl.naming.pcosnaming;

import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.Serializable;

class CounterDB implements Serializable
{
    private Integer counter;
    private static String counterFileName;
    private transient File counterFile;
    public static final int rootCounter = 0;
    
    CounterDB(final File file) {
        CounterDB.counterFileName = "counter";
        this.counterFile = new File(file, CounterDB.counterFileName);
        if (!this.counterFile.exists()) {
            this.counter = new Integer(0);
            this.writeCounter();
        }
        else {
            this.readCounter();
        }
    }
    
    private void readCounter() {
        try {
            final ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(this.counterFile));
            this.counter = (Integer)objectInputStream.readObject();
            objectInputStream.close();
        }
        catch (final Exception ex) {}
    }
    
    private void writeCounter() {
        try {
            this.counterFile.delete();
            final ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(this.counterFile));
            objectOutputStream.writeObject(this.counter);
            objectOutputStream.flush();
            objectOutputStream.close();
        }
        catch (final Exception ex) {}
    }
    
    public synchronized int getNextCounter() {
        int intValue = this.counter;
        this.counter = new Integer(++intValue);
        this.writeCounter();
        return intValue;
    }
    
    static {
        CounterDB.counterFileName = "counter";
    }
}
