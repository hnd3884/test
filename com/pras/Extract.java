package com.pras;

import com.googlecode.dex2jar.v3.Dex2jar;
import com.pras.abx.BXCallback;
import com.pras.abx.Android_BX2;
import java.util.zip.ZipEntry;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipInputStream;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import com.pras.utils.Log;
import java.util.ArrayList;

public class Extract
{
    final int BUFFER = 2048;
    ArrayList<String> xmlFiles;
    String dexFile;
    String resFile;
    boolean debug;
    String tag;
    
    public Extract() {
        this.xmlFiles = new ArrayList<String>();
        this.dexFile = null;
        this.resFile = null;
        this.debug = true;
        this.tag = this.getClass().getSimpleName();
    }
    
    public void unZip(final String apkFile) throws Exception {
        Log.p(this.tag, apkFile);
        final File file = new File(apkFile);
        String apkFileName = file.getName();
        if (apkFileName.indexOf(46) != -1) {
            apkFileName = apkFileName.substring(0, apkFileName.indexOf(46));
        }
        Log.d(this.tag, "Folder name: " + apkFileName);
        final File extractFolder = new File(String.valueOf((file.getParent() == null) ? "" : new StringBuilder(String.valueOf(file.getParent())).append(File.separator).toString()) + apkFileName);
        if (!extractFolder.exists()) {
            extractFolder.mkdir();
        }
        final FileInputStream fin = new FileInputStream(apkFile);
        final ZipInputStream zin = new ZipInputStream(new BufferedInputStream(fin));
        ZipEntry ze = null;
        while ((ze = zin.getNextEntry()) != null) {
            Log.d(this.tag, "Zip entry: " + ze.getName() + " Size: " + ze.getSize());
            String zeFolder;
            String zeName = zeFolder = ze.getName();
            if (ze.isDirectory()) {
                zeName = null;
            }
            else if (zeName.indexOf("/") == -1) {
                zeFolder = null;
            }
            else {
                zeFolder = zeName.substring(0, zeName.lastIndexOf("/"));
                zeName = zeName.substring(zeName.lastIndexOf("/") + 1);
            }
            Log.d(this.tag, "zeFolder: " + zeFolder + " zeName: " + zeName);
            File zeFile = extractFolder;
            if (zeFolder != null) {
                zeFile = new File(String.valueOf(extractFolder.getPath()) + File.separator + zeFolder);
                if (!zeFile.exists()) {
                    zeFile.mkdirs();
                }
            }
            if (zeName == null) {
                continue;
            }
            if (zeName.endsWith(".xml")) {
                this.xmlFiles.add(String.valueOf(zeFile.getPath()) + File.separator + zeName);
            }
            if (zeName.endsWith(".dex") || zeName.endsWith(".odex")) {
                this.dexFile = String.valueOf(zeFile.getPath()) + File.separator + zeName;
            }
            if (zeName.endsWith(".arsc")) {
                this.resFile = String.valueOf(zeFile.getPath()) + File.separator + zeName;
            }
            final byte[] data = new byte[2048];
            final FileOutputStream fos = new FileOutputStream(String.valueOf(zeFile.getPath()) + File.separator + zeName);
            final BufferedOutputStream dest = new BufferedOutputStream(fos, 2048);
            int count;
            while ((count = zin.read(data, 0, 2048)) != -1) {
                dest.write(data, 0, count);
            }
            dest.flush();
            dest.close();
        }
        zin.close();
        fin.close();
    }
    
    public void decodeBX() throws Exception {
        Log.p(this.tag, "Decode Binary XML...");
        Log.d(this.tag, "Number of Binary XML files: " + this.xmlFiles.size());
        Log.d(this.tag, "-> " + this.xmlFiles);
        for (int i = 0; i < this.xmlFiles.size(); ++i) {
            Log.p(this.tag, "XML File: " + this.xmlFiles.get(i));
            try {
                final Android_BX2 abx2 = new Android_BX2(new GenXML());
                abx2.parse(this.xmlFiles.get(i));
            }
            catch (final Exception ex) {
                Log.e(this.tag, "Fail to parse - " + this.xmlFiles.get(i), ex);
            }
            finally {
                final Android_BX2 abx2 = null;
            }
        }
    }
    
    public void decodeDex() throws Exception {
        Log.p(this.tag, "Decode DEX/ODEX...");
        if (this.dexFile == null) {
            Log.p("No .dex/.odex file. Skip decodeDex()");
            return;
        }
        final String jarFile = String.valueOf(this.dexFile) + ".jar";
        final Dex2jar dj = Dex2jar.from(new File(this.dexFile));
        dj.to(jarFile);
        Log.p("Converted Dex/ODex to Jar.");
        Log.p("I'm Done! ....huh :-)");
    }
    
    public void decodeResource() throws Exception {
        final Android_BX2 abx = new Android_BX2(null);
        abx.parseResourceTable(this.resFile);
    }
    
    public static void main(final String[] args) {
        try {
            final Extract ex = new Extract();
            if (args == null || args.length == 0) {
                throw new Exception("Please mention APK file.\nUsage java -cp CLASSPATH com.pras.Extract test.apk");
            }
            final String file = args[0];
            if (args.length > 1) {
                try {
                    System.out.println("Log Level: " + args[1]);
                    System.out.println("1- Debug, 2- Production. Debug is slow.");
                    final int logLevel = Integer.parseInt(args[1]);
                    if (logLevel != Log.DEBUG_LEVEL && logLevel != Log.PRODUCTION_LEVEL) {
                        throw new Exception("Unsupported loglevel " + logLevel + ". Supported values: Debug- 1, Production - 2");
                    }
                    Log.setLogLevel(logLevel);
                }
                catch (final Exception e) {
                    throw new Exception("Incorrect Log Level. Please mention APK file.\nUsage java -cp CLASSPATH com.pras.Extract test.apk");
                }
            }
            System.out.println("Parsing data, please wait...");
            ex.unZip(file);
            ex.decodeBX();
            ex.decodeDex();
            Log.exitLogger();
            System.out.println("Done!");
        }
        catch (final Exception ex2) {
            ex2.printStackTrace();
        }
    }
}
