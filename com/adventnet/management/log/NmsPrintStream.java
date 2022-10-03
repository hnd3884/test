package com.adventnet.management.log;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.PrintStream;

public class NmsPrintStream extends PrintStream
{
    static int date;
    static int month;
    static int year;
    String time;
    private static String homeDir;
    private static String outLogsDir;
    private static String errLogsDir;
    static int maxFilesForOut;
    static int maxFilesForErr;
    static int maxNumLinesForOut;
    static int maxNumLinesForErr;
    static int curNumLinesForOut;
    static int curNumLinesForErr;
    static String outFile;
    static String errFile;
    static String curOutFile;
    static String curErrFile;
    private static NmsFileUtil errnfu;
    private static NmsFileUtil outnfu;
    private static OutputStream stdout_stream;
    private static OutputStream stderr_stream;
    private static boolean enableSysout;
    private static boolean enableSyserr;
    private boolean enableLogging;
    private boolean useTimeStamp;
    private static boolean useTimeStampForSysout;
    private static boolean useTimeStampForSyserr;
    public static String HEADER_START;
    public static String HEADER_CONTINUE;
    
    NmsPrintStream(final OutputStream outputStream) {
        this(outputStream, false);
    }
    
    NmsPrintStream(final OutputStream outputStream, final boolean b) {
        this(outputStream, b, true, true);
    }
    
    NmsPrintStream(final OutputStream outputStream, final boolean b, final boolean enableLogging, final boolean useTimeStamp) {
        super(outputStream, b);
        this.enableLogging = false;
        this.useTimeStamp = true;
        this.enableLogging = enableLogging;
        this.useTimeStamp = useTimeStamp;
        this.assignDay();
    }
    
    private static void init() {
        LoggingXMLReader instance = null;
        try {
            instance = LoggingXMLReader.getInstance(new FileInputStream(NmsPrintStream.homeDir + "conf/logging_parameters.conf"));
        }
        catch (final Exception ex) {
            System.err.println(" Error while instantiating  NmsPrintStream");
            ex.printStackTrace();
        }
        final Properties sysoutAttributes = instance.getSysoutAttributes();
        final Properties syserrAttributes = instance.getSyserrAttributes();
        NmsPrintStream.outFile = sysoutAttributes.getProperty("FileName", "stdout.txt");
        NmsPrintStream.errFile = syserrAttributes.getProperty("FileName", "stderr.txt");
        NmsPrintStream.outLogsDir = NmsPrintStream.homeDir + sysoutAttributes.getProperty("LogsDirectory", "logs");
        NmsPrintStream.errLogsDir = NmsPrintStream.homeDir + syserrAttributes.getProperty("LogsDirectory", "logs");
        new File(NmsPrintStream.outLogsDir).mkdirs();
        new File(NmsPrintStream.errLogsDir).mkdirs();
        setSysOutProps(sysoutAttributes);
        setSysErrProps(syserrAttributes);
    }
    
    public static void setSysOutProps(final Properties properties) {
        final String property;
        if ((property = properties.getProperty("MaxLines")) != null) {
            NmsPrintStream.maxNumLinesForOut = Integer.parseInt(property);
        }
        final String property2;
        if ((property2 = properties.getProperty("FileCount")) != null) {
            NmsPrintStream.maxFilesForOut = Integer.parseInt(property2);
        }
        final String property3;
        if ((property3 = properties.getProperty("Logging")) != null) {
            NmsPrintStream.enableSysout = new Boolean(property3);
        }
        final String property4;
        if ((property4 = properties.getProperty("UseTimeStamp")) != null) {
            NmsPrintStream.useTimeStampForSysout = new Boolean(property4);
        }
    }
    
    public static void setSysErrProps(final Properties properties) {
        final String property;
        if ((property = properties.getProperty("MaxLines")) != null) {
            NmsPrintStream.maxNumLinesForErr = Integer.parseInt(property);
        }
        final String property2;
        if ((property2 = properties.getProperty("FileCount")) != null) {
            NmsPrintStream.maxFilesForErr = Integer.parseInt(property2);
        }
        final String property3;
        if ((property3 = properties.getProperty("Logging")) != null) {
            NmsPrintStream.enableSyserr = new Boolean(property3);
        }
        final String property4;
        if ((property4 = properties.getProperty("UseTimeStamp")) != null) {
            NmsPrintStream.useTimeStampForSyserr = new Boolean(property4);
        }
    }
    
    private void checkLines() {
        if (this.equals(System.out)) {
            ++NmsPrintStream.curNumLinesForOut;
            if (NmsPrintStream.curNumLinesForOut >= NmsPrintStream.maxNumLinesForOut) {
                this.changeOutFile();
            }
        }
        else {
            ++NmsPrintStream.curNumLinesForErr;
            if (NmsPrintStream.curNumLinesForErr >= NmsPrintStream.maxNumLinesForErr) {
                this.changeErrFile();
            }
        }
    }
    
    private void changeOutFile() {
        NmsPrintStream.curOutFile = NmsPrintStream.outnfu.getFileNext(NmsPrintStream.curOutFile);
        NmsPrintStream.curNumLinesForOut = 0;
        setOut(NmsPrintStream.HEADER_CONTINUE);
        this.howManyToDelete(NmsPrintStream.outFile, NmsPrintStream.maxFilesForOut, NmsPrintStream.outnfu, NmsPrintStream.outLogsDir);
    }
    
    private void changeErrFile() {
        NmsPrintStream.curErrFile = NmsPrintStream.errnfu.getFileNext(NmsPrintStream.curErrFile);
        NmsPrintStream.curNumLinesForErr = 0;
        setErr(NmsPrintStream.HEADER_CONTINUE);
        this.howManyToDelete(NmsPrintStream.errFile, NmsPrintStream.maxFilesForErr, NmsPrintStream.errnfu, NmsPrintStream.errLogsDir);
    }
    
    private void howManyToDelete(final String s, final int n, final NmsFileUtil nmsFileUtil, final String s2) {
        int i = nmsFileUtil.getFileCount(s);
        try {
            if (i == -1 || i == -2) {
                throw new Exception();
            }
        }
        catch (final Exception ex) {
            System.err.println(" Directory Not Present or Invalid Format " + ex.toString());
        }
        String s3 = s;
        if (i > n) {
            while (i > n) {
                if (this.deleteFile(s2, s3)) {
                    s3 = nmsFileUtil.getFileNext(s3);
                }
                else {
                    s3 = nmsFileUtil.getFileNext(s3);
                }
                i = nmsFileUtil.getFileCount(s);
                if (nmsFileUtil.getFileIndex(s3) > nmsFileUtil.getFileIndex(nmsFileUtil.getFileMax(s))) {
                    break;
                }
            }
        }
    }
    
    private boolean deleteFile(final String s, final String s2) {
        final File file = new File(s, s2);
        try {
            if (file.exists()) {
                return file.delete();
            }
        }
        catch (final Exception ex) {
            System.err.println("Exception in deletion of log files" + ex.toString());
        }
        return false;
    }
    
    private void checkDay() {
        final int[] day = this.getDay();
        final int n = day[0];
        final int n2 = day[1];
        if (day[2] > NmsPrintStream.year || n2 > NmsPrintStream.month || n > NmsPrintStream.date) {
            this.assignDay();
            this.printOutDay(null);
            this.printErrDay(null);
        }
    }
    
    public void println(final String s) {
        this.print(s, true, this.useTimeStamp);
    }
    
    public void println(final Object o) {
        if (this.useTimeStamp) {
            this.getTime();
            this.print(this.time + " " + String.valueOf(o), true, false);
        }
        else {
            this.print(String.valueOf(o), true, false);
        }
    }
    
    public void println(final long n) {
        this.print(String.valueOf(n), true, this.useTimeStamp);
    }
    
    public void println(final int n) {
        this.print(String.valueOf(n), true, this.useTimeStamp);
    }
    
    public void println(final float n) {
        this.print(String.valueOf(n), true, this.useTimeStamp);
    }
    
    public void println(final double n) {
        this.print(String.valueOf(n), true, this.useTimeStamp);
    }
    
    public void println(final char[] array) {
        this.print(String.valueOf(array), true, false);
    }
    
    public void println(final char c) {
        this.print(String.valueOf(c), true, this.useTimeStamp);
    }
    
    public void println(final boolean b) {
        this.print(b ? "true" : "false", true, this.useTimeStamp);
    }
    
    public void println() {
        this.checkDay();
        this.getTime();
        super.println();
    }
    
    private void print(final String s, final boolean b, final boolean b2) {
        if (!this.enableLogging) {
            return;
        }
        synchronized (this) {
            try {
                this.checkDay();
                if (b2) {
                    this.getTime();
                    super.print(this.time + " " + s);
                }
                else {
                    super.print(s);
                }
                if (b) {
                    super.println();
                }
                this.checkLines();
            }
            catch (final Exception ex) {
                System.err.println("Error in redirecting System.err and System.out streams");
                ex.printStackTrace();
            }
        }
    }
    
    public void print(final String s) {
        this.print(s, false, false);
    }
    
    public void print(final Object o) {
        this.print(String.valueOf(o), false, false);
    }
    
    public void print(final long n) {
        this.print(String.valueOf(n), false, false);
    }
    
    public void print(final int n) {
        this.print(String.valueOf(n), false, false);
    }
    
    public void print(final float n) {
        this.print(String.valueOf(n), false, false);
    }
    
    public void print(final double n) {
        this.print(String.valueOf(n), false, false);
    }
    
    public void print(final char[] array) {
        this.print(String.valueOf(array), false, false);
    }
    
    public void print(final char c) {
        this.print(String.valueOf(c), false, false);
    }
    
    public void print(final boolean b) {
        this.print(b ? "true" : "false", false, false);
    }
    
    private void getTime() {
        final Calendar instance = Calendar.getInstance();
        String s;
        if (instance.get(9) == 1) {
            s = "PM";
        }
        else {
            s = "AM";
        }
        this.time = this.add0(instance.get(11)) + ":" + this.add0(instance.get(12)) + ":" + this.add0(instance.get(13)) + ":" + this.add00(instance.get(14)) + " " + s;
    }
    
    private String add0(final int n) {
        if (n <= 9) {
            return "0" + n;
        }
        return String.valueOf(n);
    }
    
    private String add00(final int n) {
        if (n < 10) {
            return "00" + n;
        }
        if (n < 100) {
            return "0" + n;
        }
        return "" + n;
    }
    
    private void printOutDay(final String s) {
        final Calendar instance = Calendar.getInstance();
        if (s != null) {
            System.out.println(s);
        }
        System.out.println("Messages on ********" + DateFormat.getDateInstance(0).format(instance.getTime()) + "********");
    }
    
    private void printErrDay(final String s) {
        final Calendar instance = Calendar.getInstance();
        if (s != null) {
            System.err.println(s);
        }
        System.err.println("Messages on ********" + DateFormat.getDateInstance(0).format(instance.getTime()) + "********");
    }
    
    private int[] getDay() {
        final Calendar instance = Calendar.getInstance();
        return new int[] { instance.get(5), instance.get(2) + 1, instance.get(1), 0 };
    }
    
    private void assignDay() {
        final int[] day = this.getDay();
        NmsPrintStream.date = day[0];
        NmsPrintStream.month = day[1];
        NmsPrintStream.year = day[2];
    }
    
    private static void setFiles() {
        NmsPrintStream.outnfu = new NmsFileUtil(NmsPrintStream.outLogsDir);
        final String fileMax = NmsPrintStream.outnfu.getFileMax(NmsPrintStream.outFile);
        if (fileMax.equalsIgnoreCase("NOT_PRESENT")) {
            NmsPrintStream.curOutFile = NmsPrintStream.outFile;
        }
        else if (fileMax.equalsIgnoreCase("INVALID_FORMAT")) {
            NmsPrintStream.outFile = "stdout.txt";
            NmsPrintStream.curOutFile = NmsPrintStream.outFile;
        }
        else {
            NmsPrintStream.curOutFile = NmsPrintStream.outnfu.getFileNext(fileMax);
        }
        NmsPrintStream.errnfu = new NmsFileUtil(NmsPrintStream.errLogsDir);
        final String fileMax2 = NmsPrintStream.errnfu.getFileMax(NmsPrintStream.errFile);
        if (fileMax2.equalsIgnoreCase("NOT_PRESENT")) {
            NmsPrintStream.curErrFile = NmsPrintStream.errFile;
        }
        else if (fileMax2.equalsIgnoreCase("INVALID_FORMAT")) {
            NmsPrintStream.errFile = "stderr.txt";
            NmsPrintStream.curErrFile = NmsPrintStream.errFile;
        }
        else {
            NmsPrintStream.curErrFile = NmsPrintStream.errnfu.getFileNext(fileMax2);
        }
    }
    
    public static void setOut(final String s) {
        try {
            final String string = NmsPrintStream.outLogsDir + "/" + NmsPrintStream.curOutFile;
            if (NmsPrintStream.stdout_stream != null) {
                NmsPrintStream.stdout_stream.close();
            }
            if (NmsPrintStream.enableSysout) {
                NmsPrintStream.stdout_stream = new FileOutputStream(string, true);
            }
            else {
                NmsPrintStream.stdout_stream = new ByteArrayOutputStream();
            }
            final NmsPrintStream out = new NmsPrintStream(NmsPrintStream.stdout_stream, true, NmsPrintStream.enableSysout, NmsPrintStream.useTimeStampForSysout);
            System.setOut(out);
            if (s != null) {
                out.printOutDay(s);
            }
            out.howManyToDelete(NmsPrintStream.outFile, NmsPrintStream.maxFilesForOut, NmsPrintStream.outnfu, NmsPrintStream.outLogsDir);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setErr(final String s) {
        try {
            final String string = NmsPrintStream.errLogsDir + "/" + NmsPrintStream.curErrFile;
            if (NmsPrintStream.stderr_stream != null) {
                NmsPrintStream.stderr_stream.close();
            }
            if (NmsPrintStream.enableSyserr) {
                NmsPrintStream.stderr_stream = new FileOutputStream(string, true);
            }
            else {
                NmsPrintStream.stderr_stream = new ByteArrayOutputStream();
            }
            final NmsPrintStream err = new NmsPrintStream(NmsPrintStream.stderr_stream, true, NmsPrintStream.enableSyserr, NmsPrintStream.useTimeStampForSyserr);
            System.setErr(err);
            if (s != null) {
                err.printErrDay(s);
            }
            err.howManyToDelete(NmsPrintStream.errFile, NmsPrintStream.maxFilesForErr, NmsPrintStream.errnfu, NmsPrintStream.errLogsDir);
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static void setErrAndOut(final String homeDir) throws IOException {
        NmsPrintStream.homeDir = homeDir;
        init();
        setFiles();
        setOut(NmsPrintStream.HEADER_START);
        setErr(NmsPrintStream.HEADER_START);
    }
    
    static {
        NmsPrintStream.homeDir = "./";
        NmsPrintStream.maxFilesForOut = 10;
        NmsPrintStream.maxFilesForErr = 10;
        NmsPrintStream.maxNumLinesForOut = 10000;
        NmsPrintStream.maxNumLinesForErr = 10000;
        NmsPrintStream.curNumLinesForOut = 0;
        NmsPrintStream.curNumLinesForErr = 0;
        NmsPrintStream.stdout_stream = null;
        NmsPrintStream.stderr_stream = null;
        NmsPrintStream.enableSysout = true;
        NmsPrintStream.enableSyserr = true;
        NmsPrintStream.useTimeStampForSysout = true;
        NmsPrintStream.useTimeStampForSyserr = true;
        NmsPrintStream.HEADER_START = "~~~~~~~~~~~~~~~ Logging started ~~~~~~~~~~~~~~~~";
        NmsPrintStream.HEADER_CONTINUE = "~~~~~~~~~~~~~~~ Logging continued ~~~~~~~~~~~~~~~ ";
    }
}
