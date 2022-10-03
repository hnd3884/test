package com.adventnet.tools.update.installer.log;

import com.adventnet.tools.update.CommonUtil;
import java.text.DateFormat;
import java.util.Calendar;
import java.io.File;
import java.io.FileOutputStream;
import com.adventnet.tools.update.UpdateManagerUtil;
import java.util.Properties;
import java.io.PrintWriter;

public class UpdateManagerLogImpl implements UpdateManagerLogInterface
{
    private static final String LOG_CONTINUED_STR = " ~~~~~~~~~~~~~~~ Logging continued ~~~~~~~~~~~~~~~ ";
    protected String HEADER;
    protected static String c_line_separator;
    protected String logDirectory;
    protected String logFileName;
    protected String currentLogFileName;
    protected int maxFileCount;
    protected int maxLinesInFile;
    protected int lineCountInCurrentFile;
    protected int cacheLineCount;
    protected int maxLinesInMemory;
    protected boolean useTimeStamp;
    protected String[] m_lines;
    LogFileUtil nfu;
    private LogPrintWriter fileWriter;
    private int date;
    private int month;
    private int year;
    private static PrintWriter logWriter;
    
    public UpdateManagerLogImpl() {
        this.HEADER = null;
        this.logDirectory = "logs";
        this.logFileName = "updatemanagerlog.txt";
        this.currentLogFileName = null;
        this.maxFileCount = 10;
        this.maxLinesInFile = 10000;
        this.lineCountInCurrentFile = 0;
        this.cacheLineCount = 0;
        this.maxLinesInMemory = 0;
        this.useTimeStamp = true;
        this.m_lines = null;
        this.fileWriter = null;
    }
    
    public UpdateManagerLogImpl(final String log_dir, final String filename, final int max_file_number, final int max_lines_in_file, final int max_lines_in_memory, final boolean use_timestamps) {
        this.HEADER = null;
        this.logDirectory = "logs";
        this.logFileName = "updatemanagerlog.txt";
        this.currentLogFileName = null;
        this.maxFileCount = 10;
        this.maxLinesInFile = 10000;
        this.lineCountInCurrentFile = 0;
        this.cacheLineCount = 0;
        this.maxLinesInMemory = 0;
        this.useTimeStamp = true;
        this.m_lines = null;
        this.fileWriter = null;
        final Properties prop = new Properties();
        prop.setProperty("FileName", filename);
        prop.setProperty("MaxLines", "" + max_lines_in_file);
        prop.setProperty("FileCount", "" + max_file_number);
        prop.setProperty("MaxLinesCached", "" + max_lines_in_memory);
        prop.setProperty("LogsDirectory", log_dir);
        prop.setProperty("UseTimeStamp", "" + use_timestamps);
        this.init(prop);
    }
    
    @Override
    public void init(final Properties prop) {
        String str = null;
        if ((str = prop.getProperty("FileName")) != null) {
            this.logFileName = str;
        }
        if ((str = prop.getProperty("MaxLines")) != null) {
            this.maxLinesInFile = Integer.parseInt(str);
        }
        if ((str = prop.getProperty("FileCount")) != null) {
            this.maxFileCount = Integer.parseInt(str);
        }
        if ((str = prop.getProperty("MaxLinesCached")) != null) {
            this.maxLinesInMemory = Integer.parseInt(str);
        }
        if ((str = prop.getProperty("LogsDirectory")) != null) {
            this.logDirectory = UpdateManagerUtil.getHomeDirectory() + "/" + str;
        }
        if ((str = prop.getProperty("UseTimeStamp")) != null) {
            this.useTimeStamp = new Boolean(str);
        }
        this.m_lines = new String[this.maxLinesInMemory];
        if (UpdateManagerLogImpl.c_line_separator == null) {
            UpdateManagerLogImpl.c_line_separator = System.getProperty("line.separator");
            if (UpdateManagerLogImpl.c_line_separator == null) {
                UpdateManagerLogImpl.c_line_separator = "\n";
            }
        }
        this.currentLogFileName = this.logFileName;
        this.assignDay();
        this.setTheStartUpValues();
        this.createFileWriter();
    }
    
    private void createFileWriter() {
        try {
            if (this.fileWriter != null) {
                this.fileWriter.close();
            }
            this.fileWriter = new LogPrintWriter(new FileOutputStream(this.logDirectory + "/" + this.currentLogFileName), true);
            this.howManyToDelete();
        }
        catch (final Exception ie) {
            System.err.println("LogFileWriter: error opening " + this.currentLogFileName + ":" + ie);
            ie.printStackTrace();
        }
    }
    
    @Override
    public void log(String message) {
        String timestamp = " ";
        if (this.useTimeStamp) {
            timestamp = this.getTimeStamp();
        }
        message = timestamp + message;
        if (this.maxLinesInMemory > 0) {
            if (this.cacheLineCount < this.maxLinesInMemory) {
                this.m_lines[this.cacheLineCount] = message;
                ++this.cacheLineCount;
            }
            else {
                this.flush();
                this.m_lines[this.cacheLineCount] = message;
                ++this.cacheLineCount;
            }
            return;
        }
        if (this.lineCountInCurrentFile >= this.maxLinesInFile) {
            this.lineCountInCurrentFile = 0;
            this.changeFile();
        }
        if (this.checkDay()) {
            this.logOne(this.getDayStartMessage());
        }
        this.logOne(message);
        this.flushFileContents();
    }
    
    private void flushFileContents() {
        this.fileWriter.flush();
    }
    
    private void logOne(final String message) {
        final String message_ts_ind_eol = message + UpdateManagerLogImpl.c_line_separator;
        try {
            this.fileWriter.write(message_ts_ind_eol, 0, message_ts_ind_eol.length());
            ++this.lineCountInCurrentFile;
        }
        catch (final Exception ie) {
            System.err.println("LogFileWriter: error closing " + this.currentLogFileName + ":" + ie);
            ie.printStackTrace();
        }
    }
    
    private void changeFile() {
        final String name = this.nfu.getFileNext(this.currentLogFileName);
        try {
            if (name.equalsIgnoreCase("INVALID_FORMAT")) {
                throw new Exception();
            }
            this.currentLogFileName = name;
        }
        catch (final Exception e) {
            System.err.println("Invalid Format for log files ");
        }
        this.createFileWriter();
        final String timestamp = this.getTimeStamp();
        String tempmessage = timestamp + " ~~~~~~~~~~~~~~~ Logging continued ~~~~~~~~~~~~~~~ ";
        this.logOne(tempmessage);
        tempmessage = timestamp;
        this.logOne(tempmessage);
        this.flushFileContents();
    }
    
    @Override
    public void log(final String message, final int level) {
        this.log(message);
    }
    
    @Override
    public void fail(final String message) {
        this.log(message);
    }
    
    @Override
    public void fail(final String message, final Throwable exception) {
        this.flush();
        if (this.checkDay()) {
            this.logOne(this.getDayStartMessage());
        }
        this.fileWriter.lineCount = 1;
        exception.printStackTrace(this.fileWriter);
        this.lineCountInCurrentFile += this.fileWriter.lineCount;
        this.flushFileContents();
    }
    
    private void setTheStartUpValues() {
        final File dir = new File(this.logDirectory);
        dir.mkdirs();
        this.nfu = new LogFileUtil(dir);
        final String f_name = this.nfu.getFileMax(this.logFileName);
        try {
            if (f_name == null || f_name.equalsIgnoreCase("INVALID_FORMAT")) {
                throw new Exception();
            }
            if (f_name.equalsIgnoreCase("NOT_PRESENT")) {
                this.currentLogFileName = this.logFileName;
            }
            else {
                this.currentLogFileName = this.nfu.getFileNext(f_name);
            }
        }
        catch (final Exception e) {
            System.err.println(" Directory Not Present or Invalid Format " + e.toString());
        }
    }
    
    private void howManyToDelete() {
        int numFiles = this.nfu.getFileCount(this.logFileName);
        try {
            if (numFiles == -1 || numFiles == -2) {
                throw new Exception();
            }
        }
        catch (final Exception e) {
            System.err.println(" Directory Not Present or Invalid Format " + e.toString());
        }
        String name = this.logFileName;
        if (numFiles > this.maxFileCount) {
            while (numFiles > this.maxFileCount) {
                if (this.deleteFile(name)) {
                    name = this.nfu.getFileNext(name);
                }
                else {
                    name = this.nfu.getFileNext(name);
                }
                numFiles = this.nfu.getFileCount(this.logFileName);
                if (this.nfu.getFileIndex(name) > this.nfu.getFileIndex(this.nfu.getFileMax(this.logFileName))) {
                    break;
                }
            }
        }
    }
    
    private boolean deleteFile(final String fileName) {
        final File dir = new File(this.logDirectory);
        final File f_delete = new File(dir, fileName);
        try {
            if (f_delete.exists()) {
                return f_delete.delete();
            }
        }
        catch (final Exception ex) {
            System.err.println("Exception in deletion of log files" + ex.toString());
        }
        return false;
    }
    
    private String getDayStartMessage() {
        final Calendar cal = Calendar.getInstance();
        return "Messages on ********" + DateFormat.getDateInstance(0).format(cal.getTime()) + "********";
    }
    
    protected String getTimeStamp() {
        final Calendar curr_calendar = Calendar.getInstance();
        String time_stamp = null;
        final int hour = curr_calendar.get(11);
        final String hour_str = this.add0(hour);
        final int minute = curr_calendar.get(12);
        final String minute_str = this.add0(minute);
        final int second = curr_calendar.get(13);
        final String second_str = this.add0(second);
        final int millisecond = curr_calendar.get(14);
        final String millisecond_str = this.add00(millisecond);
        time_stamp = hour_str + ":" + minute_str + ":" + second_str + ":" + millisecond_str;
        return time_stamp + " ";
    }
    
    private String add0(final int n) {
        if (n < 10) {
            return "0" + n;
        }
        return "" + n;
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
    
    private boolean checkDay() {
        final Calendar cal = Calendar.getInstance();
        if (cal.get(1) > this.year || cal.get(2) > this.month || cal.get(5) > this.date) {
            this.date = cal.get(5);
            this.month = cal.get(2) + 1;
            this.year = cal.get(1);
            return true;
        }
        return false;
    }
    
    private synchronized void flush() {
        if (this.cacheLineCount == 0) {
            return;
        }
        if (this.lineCountInCurrentFile >= this.maxLinesInFile) {
            this.lineCountInCurrentFile = 0;
            this.changeFile();
        }
        for (int i = 0; i < this.m_lines.length; ++i) {
            if (this.checkDay()) {
                this.logOne(this.getDayStartMessage());
            }
            if (this.m_lines[i] != null) {
                this.logOne(this.m_lines[i]);
            }
        }
        this.m_lines = new String[this.maxLinesInMemory];
        this.flushFileContents();
        this.cacheLineCount = 0;
    }
    
    private void assignDay() {
        final Calendar cal = Calendar.getInstance();
        this.date = cal.get(5);
        this.month = cal.get(2) + 1;
        this.year = cal.get(1);
    }
    
    @Override
    public String getString(final String text) {
        return CommonUtil.getString(text);
    }
    
    @Override
    public void close() {
        try {
            if (this.fileWriter != null) {
                this.fileWriter.close();
            }
        }
        catch (final Exception ex) {}
    }
    
    static {
        UpdateManagerLogImpl.c_line_separator = null;
        UpdateManagerLogImpl.logWriter = null;
    }
}
