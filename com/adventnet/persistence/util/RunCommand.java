package com.adventnet.persistence.util;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;

public class RunCommand extends Thread implements Serializable
{
    private boolean stdoutFlag;
    public String command;
    public StringBuffer stdout;
    public StringBuffer stderr;
    public boolean result;
    public int exitValue;
    Process proc;
    RunCommand readErr;
    boolean finished;
    
    RunCommand() {
        this.stdoutFlag = false;
        this.command = null;
        this.stdout = new StringBuffer();
        this.stderr = new StringBuffer();
        this.result = false;
        this.exitValue = -1;
        this.proc = null;
        this.readErr = null;
        this.finished = false;
    }
    
    public RunCommand(final String cmd) {
        super(cmd);
        this.stdoutFlag = false;
        this.command = null;
        this.stdout = new StringBuffer();
        this.stderr = new StringBuffer();
        this.result = false;
        this.exitValue = -1;
        this.proc = null;
        this.readErr = null;
        this.finished = false;
        this.command = cmd;
    }
    
    @Override
    public void run() {
        if (this.command == null) {
            if (this.proc == null) {
                return;
            }
            this.getStdErr();
        }
        else {
            this.result = this.runCommand(this.command);
        }
        this.finished = true;
    }
    
    public boolean runCommand(final String cmd) {
        final String line = null;
        Process p;
        try {
            p = Runtime.getRuntime().exec(cmd);
        }
        catch (final Exception e) {
            this.systemerr("Command did not execute: " + cmd + " \nException: " + e);
            return false;
        }
        this.readErr = new RunCommand();
        this.proc = p;
        this.readErr.proc = p;
        this.readErr.stderr = this.stderr;
        this.readErr.start();
        final InputStreamReader tst = new InputStreamReader(p.getInputStream());
        boolean returnValue = false;
        try {
            char c = (char)tst.read();
            if (c != -1) {
                this.systemout(c);
            }
            while (c != -1) {
                if (!tst.ready()) {
                    try {
                        p.exitValue();
                        break;
                    }
                    catch (final IllegalThreadStateException itse) {
                        try {
                            Thread.sleep(25L);
                        }
                        catch (final InterruptedException ex) {}
                        continue;
                    }
                }
                c = (char)tst.read();
                this.systemout(c);
            }
        }
        catch (final IOException e2) {
            System.err.println("Error running command: " + cmd + " : " + e2);
            return returnValue;
        }
        finally {
            try {
                tst.close();
            }
            catch (final IOException ie) {
                System.err.println("RunCommand : Error closing InputStream " + ie);
            }
            p.destroy();
        }
        final int exitValue = p.exitValue();
        this.exitValue = exitValue;
        if (exitValue == 0) {
            returnValue = true;
        }
        return returnValue;
    }
    
    boolean getStdErr() {
        String line = null;
        final BufferedReader tst = new BufferedReader(new InputStreamReader(this.proc.getErrorStream()));
        try {
            final long l = System.currentTimeMillis();
            while (true) {
                if (!tst.ready()) {
                    try {
                        this.proc.exitValue();
                        break;
                    }
                    catch (final IllegalThreadStateException itse) {
                        try {
                            Thread.sleep(25L);
                        }
                        catch (final InterruptedException ex) {}
                        continue;
                    }
                }
                line = tst.readLine();
                this.systemerr(line + "\n");
            }
        }
        catch (final IOException e) {
            System.err.println("Error running command: " + this.command + " : " + e);
            try {
                tst.close();
            }
            catch (final IOException ie) {
                System.err.println("RunCommand : Error closing ErrorStream " + ie);
            }
            return false;
        }
        try {
            tst.close();
        }
        catch (final IOException iee) {
            System.err.println("RunCommand : Error closing InputStream " + iee);
        }
        return true;
    }
    
    private void systemout(final char ch) {
        if (this.stdoutFlag) {
            System.out.print(ch);
        }
        else {
            this.stdout.append(ch);
        }
    }
    
    private void systemerr(final String s) {
        if (this.stdoutFlag) {
            System.err.println(s);
        }
        else {
            this.stderr.append(s);
        }
    }
    
    public void stopCommand() {
        this.finished = true;
        this.stop();
        if (this.readErr != null) {
            this.readErr.stop();
        }
        if (this.proc != null) {
            this.proc.destroy();
        }
    }
}
