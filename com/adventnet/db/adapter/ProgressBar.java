package com.adventnet.db.adapter;

import java.util.logging.Level;
import com.adventnet.mfw.ConsoleOut;
import java.util.logging.Logger;

public class ProgressBar extends Thread
{
    private static final Logger LOGGER;
    private volatile boolean isRunning;
    private int completed;
    private int progressBarLength;
    private Thread thread;
    private long waitTime;
    private PROGRESS_BAR_TYPE progressBarType;
    private String prefixMessage;
    private boolean inConsole;
    
    public ProgressBar(final long waitTime, final PROGRESS_BAR_TYPE progressBarType, final String prefixMessage, final boolean inConsole) {
        this.isRunning = false;
        this.completed = 0;
        this.prefixMessage = "";
        this.inConsole = true;
        this.waitTime = waitTime;
        this.progressBarType = progressBarType;
        this.inConsole = inConsole;
        if (prefixMessage != null) {
            this.prefixMessage = prefixMessage;
        }
        if (PROGRESS_BAR_TYPE.SIMPLE_PERCENTAGE == progressBarType) {
            this.progressBarLength = 10;
        }
        else if (PROGRESS_BAR_TYPE.FLOW_BAR == progressBarType) {
            this.progressBarLength = 50;
        }
        (this.thread = new Thread(this)).setPriority(1);
    }
    
    public void startProgressBar() {
        this.isRunning = true;
        this.thread.start();
    }
    
    public void endProgressBar() throws InterruptedException {
        this.isRunning = false;
        this.thread.join();
        this.completed = 100;
        if (this.progressBarType == PROGRESS_BAR_TYPE.SIMPLE_PERCENTAGE) {
            this.printForSimplePersentage();
        }
        else if (this.progressBarType == PROGRESS_BAR_TYPE.FLOW_BAR) {
            final StringBuilder sb = new StringBuilder();
            sb.append("\r[");
            for (int i = 0; i < this.progressBarLength; ++i) {
                sb.append('=');
            }
            sb.append("] " + this.completed + " %\t");
            ConsoleOut.println(sb.toString() + "\n");
        }
        else if (this.progressBarType == PROGRESS_BAR_TYPE.LOADING_CIRCLE) {
            ConsoleOut.print("\rCompleted ...\t\t\n\n");
        }
    }
    
    @Override
    public void run() {
        try {
            if (this.progressBarType == PROGRESS_BAR_TYPE.SIMPLE_PERCENTAGE) {
                this.waitTime /= this.progressBarLength;
                final byte increment = (byte)(100 / this.progressBarLength);
                for (int i = 0; i < this.progressBarLength; ++i) {
                    this.printForSimplePersentage();
                    if (this.isRunning) {
                        Thread.sleep(this.waitTime);
                    }
                    this.completed += increment;
                }
            }
            else if (this.progressBarType == PROGRESS_BAR_TYPE.FLOW_BAR) {
                ConsoleOut.println('\n' + this.prefixMessage + '\n');
                this.waitTime /= this.progressBarLength;
                final byte increment = (byte)(100 / this.progressBarLength);
                final StringBuilder sb = new StringBuilder();
                for (int j = 0; j < this.progressBarLength - 1 && this.isRunning; ++j) {
                    this.completed += increment;
                    sb.setLength(0);
                    sb.append("\r[");
                    int k;
                    for (k = 0; k <= j; ++k) {
                        sb.append('=');
                    }
                    sb.append('>');
                    while (k < this.progressBarLength - 1) {
                        sb.append(' ');
                        ++k;
                    }
                    sb.append("| " + this.completed + " % ...");
                    ConsoleOut.print(sb.toString());
                    Thread.sleep(this.waitTime);
                }
            }
            else if (this.progressBarType == PROGRESS_BAR_TYPE.LOADING_CIRCLE) {
                ConsoleOut.print("\n" + this.prefixMessage + "\t");
                final char[] progressCharArray = { '|', '/', '-', '\\' };
                this.progressBarLength = progressCharArray.length;
                int i = 0;
                while (this.isRunning) {
                    ConsoleOut.print("\b" + progressCharArray[i]);
                    Thread.sleep(250L);
                    i = ((i == this.progressBarLength - 1) ? 0 : (i + 1));
                }
            }
        }
        catch (final Exception e) {
            ProgressBar.LOGGER.log(Level.WARNING, e.getMessage());
        }
    }
    
    private void printForSimplePersentage() {
        final String message = this.prefixMessage + " " + this.completed + " % Completed ...";
        ProgressBar.LOGGER.log(Level.INFO, message);
        if (this.inConsole) {
            ConsoleOut.println(message);
        }
    }
    
    static {
        LOGGER = Logger.getLogger(ProgressBar.class.getName());
    }
    
    public enum PROGRESS_BAR_TYPE
    {
        SIMPLE_PERCENTAGE(1), 
        FLOW_BAR(2), 
        LOADING_CIRCLE(3);
        
        private int progressBarType;
        
        private PROGRESS_BAR_TYPE(final int t) {
            this.progressBarType = t;
        }
        
        public int getValue() {
            return this.progressBarType;
        }
        
        @Override
        public String toString() {
            return String.valueOf(this.progressBarType);
        }
    }
}
