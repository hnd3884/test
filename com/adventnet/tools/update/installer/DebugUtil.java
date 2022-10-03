package com.adventnet.tools.update.installer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class DebugUtil
{
    public static final String OPTIONAL = "<OPTIONAL>";
    public static final String NO_VALUE = "<NO_VALUE>";
    public static final String OPTIONAL_NO_VALUE = "<OPTIONAL_NO_VALUE>";
    public static final String SET = "<SET>";
    
    public static final String getString(final Object[] dataArg) {
        final StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < dataArg.length; ++i) {
            strBuf.append(i + " : " + dataArg[i]);
            strBuf.append('\n');
        }
        return strBuf.toString();
    }
    
    public static final String getString(final Object[] dataArg, final String sepArg) {
        final StringBuffer strBuf = new StringBuffer();
        for (int i = 0; i < dataArg.length; ++i) {
            strBuf.append(dataArg[i]);
            strBuf.append(sepArg);
        }
        return strBuf.toString();
    }
    
    public static final String getString(final List listArg) {
        final StringBuffer strBuf = new StringBuffer();
        for (int i = 0, j = listArg.size(); i < j; ++i) {
            strBuf.append(i + " : " + listArg.get(i));
            strBuf.append('\n');
        }
        return strBuf.toString();
    }
    
    public static String getString(final Map mapArg) {
        final StringBuffer strBuf = new StringBuffer();
        int i = 0;
        for (final Map.Entry entry : mapArg.entrySet()) {
            strBuf.append(i + " : " + entry.getKey() + " = " + entry.getValue());
            strBuf.append('\n');
            ++i;
        }
        return strBuf.toString();
    }
    
    public static ArrayList getKeysForValue(final Map mapArg, final Object valueArg) {
        final ArrayList al = new ArrayList();
        for (final Map.Entry entry : mapArg.entrySet()) {
            final Object value = entry.getValue();
            if (value.equals(valueArg)) {
                al.add(entry.getKey());
            }
        }
        return al;
    }
    
    public static final ArrayList removeKeysForValue(final Map mapArg, final Object valueArg) {
        final ArrayList al = new ArrayList();
        final Iterator entries = mapArg.entrySet().iterator();
        while (entries.hasNext()) {
            final Map.Entry entry = entries.next();
            final Object value = entry.getValue();
            if (value.equals(valueArg)) {
                al.add(entry.getKey());
                entries.remove();
            }
        }
        return al;
    }
    
    public static final boolean isValuePresent(final Map mapArg, final Object valueArg) {
        for (final Map.Entry entry : mapArg.entrySet()) {
            final Object value = entry.getValue();
            if (value.equals(valueArg)) {
                return true;
            }
        }
        return false;
    }
    
    public static ArrayList getKeysForValueType(final Map mapArg, final Class typeArg) {
        final ArrayList al = new ArrayList();
        int i = 0;
        final Iterator keys = mapArg.keySet().iterator();
        final Iterator values = mapArg.values().iterator();
        while (keys.hasNext()) {
            final Object value = values.next();
            final Object key = keys.next();
            if (typeArg.isInstance(value)) {
                al.add(key);
            }
            ++i;
        }
        return al;
    }
    
    public static int getIndexFor(final Object valArg, final Object[] listArg) {
        for (int i = 0; i < listArg.length; ++i) {
            if (valArg.equals(listArg[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static final String[] parseOptions(final String[] args, final String[] optionsArg) {
        int count = args.length;
        int i = 0;
    Label_0005:
        while (i < optionsArg.length - 1) {
            final String option = '-' + optionsArg[i];
            while (true) {
                for (int j = 0; j < args.length; ++j) {
                    if (option.equals(args[j])) {
                        args[j] = null;
                        --count;
                        if ("<OPTIONAL_NO_VALUE>".equals(optionsArg[i + 1]) || "<NO_VALUE>".equals(optionsArg[i + 1])) {
                            optionsArg[i + 1] = "<SET>";
                        }
                        else {
                            if (j + 1 == args.length || args[j + 1] == null) {
                                return null;
                            }
                            optionsArg[i + 1] = args[j + 1];
                            args[j + 1] = null;
                            --count;
                        }
                        i += 2;
                        continue Label_0005;
                    }
                }
                if (!"<OPTIONAL>".equals(optionsArg[i + 1]) && !"<OPTIONAL_NO_VALUE>".equals(optionsArg[i + 1])) {
                    return null;
                }
                optionsArg[i + 1] = null;
                continue;
            }
        }
        final String[] remArgs = new String[count];
        int k = 0;
        int j = 0;
        while (k < args.length) {
            if (args[k] != null) {
                remArgs[j++] = args[k];
            }
            ++k;
        }
        return remArgs;
    }
    
    public static String getRepetitiveString(final char patternArg, final int numTimesArg) {
        final char[] arr = new char[numTimesArg];
        for (int i = 0; i < numTimesArg; ++i) {
            arr[i] = patternArg;
        }
        return new String(arr);
    }
    
    public static String getCorrectErrorMsg(final String msgArg, final Throwable throwableArg) {
        return getCorrectErrorMsg(msgArg, throwableArg, null);
    }
    
    public static String getCorrectErrorMsg(final String msgArg, Throwable throwableArg, final String fromPosArg) {
        if (throwableArg instanceof InvocationTargetException && ((InvocationTargetException)throwableArg).getTargetException() != null) {
            throwableArg = ((InvocationTargetException)throwableArg).getTargetException();
        }
        String completeMsg = '\n' + msgArg + "\nException Details :\n   Type : " + throwableArg.getClass().getName();
        if (throwableArg.getMessage() != null) {
            completeMsg = completeMsg + "\n   Message : " + throwableArg.getMessage();
        }
        if (fromPosArg != null) {
            final DebugOutputStream debugOut = new DebugOutputStream();
            final PrintWriter pw = new PrintWriter(debugOut);
            throwableArg.printStackTrace(pw);
            pw.flush();
            pw.close();
            String stackStr = debugOut.getString();
            int startIndex = 0;
            String prefix = throwableArg.getClass().getName() + ": ";
            if (throwableArg.getMessage() != null) {
                prefix += throwableArg.getMessage();
            }
            if (stackStr.startsWith(prefix)) {
                startIndex = prefix.length();
            }
            int index = stackStr.indexOf(fromPosArg);
            if (index > -1) {
                index = stackStr.indexOf(10, index);
                if (index < 0) {
                    index = stackStr.indexOf(fromPosArg);
                }
                if (startIndex >= index) {
                    startIndex = 0;
                }
                stackStr = stackStr.substring(startIndex, index);
            }
            completeMsg = completeMsg + "\n   Partial StackTrace : \n\t" + stackStr + '\n';
        }
        return completeMsg;
    }
    
    private DebugUtil() {
    }
    
    public static class DebugOutputStream extends OutputStream
    {
        String curStr;
        
        public DebugOutputStream() {
            this.curStr = "";
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.curStr = String.valueOf(b);
        }
        
        @Override
        public void write(final byte[] b) throws IOException {
            this.curStr = new String(b);
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.curStr = new String(b, off, len);
        }
        
        @Override
        public void flush() throws IOException {
        }
        
        @Override
        public void close() throws IOException {
        }
        
        public String getString() {
            return this.curStr;
        }
    }
}
