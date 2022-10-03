package org.apache.xmlbeans.impl.repackage;

import java.io.InputStream;
import java.io.StringWriter;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.Reader;
import java.io.Writer;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.List;
import java.io.File;

public class Repackage
{
    private File _sourceBase;
    private File _targetBase;
    private List _fromPackages;
    private List _toPackages;
    private Pattern _packagePattern;
    private Repackager _repackager;
    private Map _movedDirs;
    private List _moveAlongFiles;
    private int _skippedFiles;
    
    public static void main(final String[] args) throws Exception {
        new Repackage(args).repackage();
    }
    
    private Repackage(final String[] args) {
        String sourceDir = null;
        String targetDir = null;
        String repackageSpec = null;
        boolean failure = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-repackage") && i + 1 < args.length) {
                repackageSpec = args[++i];
            }
            else if (args[i].equals("-f") && i + 1 < args.length) {
                sourceDir = args[++i];
            }
            else if (args[i].equals("-t") && i + 1 < args.length) {
                targetDir = args[++i];
            }
            else {
                failure = true;
            }
        }
        if (failure || repackageSpec == null || (sourceDir == null ^ targetDir == null)) {
            throw new RuntimeException("Usage: repackage -repackage [spec] [ -f [sourcedir] -t [targetdir] ]");
        }
        this._repackager = new Repackager(repackageSpec);
        if (sourceDir == null || targetDir == null) {
            return;
        }
        this._sourceBase = new File(sourceDir);
        this._targetBase = new File(targetDir);
    }
    
    public void repackage() throws Exception {
        if (this._sourceBase == null || this._targetBase == null) {
            System.out.println(this._repackager.repackage(this.readInputStream(System.in)).toString());
            return;
        }
        this._fromPackages = this._repackager.getFromPackages();
        this._toPackages = this._repackager.getToPackages();
        this._packagePattern = Pattern.compile("^\\s*package\\s+((?:\\w|\\.)*)\\s*;", 8);
        this._moveAlongFiles = new ArrayList();
        this._movedDirs = new HashMap();
        this._targetBase.mkdirs();
        final ArrayList files = new ArrayList();
        this.fillFiles(files, this._sourceBase);
        System.out.println("Repackaging " + files.size() + " files ...");
        final int prefixLength = this._sourceBase.getCanonicalPath().length();
        for (int i = 0; i < files.size(); ++i) {
            final File from = files.get(i);
            final String name = from.getCanonicalPath().substring(prefixLength + 1);
            this.repackageFile(name);
        }
        this.finishMovingFiles();
        if (this._skippedFiles > 0) {
            System.out.println("Skipped " + this._skippedFiles + " unmodified files.");
        }
    }
    
    private boolean fileIsUnchanged(final String name) {
        final File sourceFile = new File(this._sourceBase, name);
        final File targetFile = new File(this._targetBase, name);
        return sourceFile.lastModified() < targetFile.lastModified();
    }
    
    public void repackageFile(final String name) throws IOException {
        if (name.endsWith(".java")) {
            this.repackageJavaFile(name);
        }
        else if (name.endsWith(".xsdconfig") || name.endsWith(".xml") || name.endsWith(".g")) {
            this.repackageNonJavaFile(name);
        }
        else if (name.startsWith("bin" + File.separatorChar)) {
            this.repackageNonJavaFile(name);
        }
        else {
            this.moveAlongWithJavaFiles(name);
        }
    }
    
    public void moveAlongWithJavaFiles(final String name) {
        this._moveAlongFiles.add(name);
    }
    
    public void finishMovingFiles() throws IOException {
        for (String toName : this._moveAlongFiles) {
            final String name = toName;
            final String srcDir = Repackager.dirForPath(name);
            final String toDir = this._movedDirs.get(srcDir);
            if (toDir != null) {
                toName = new File(toDir, new File(name).getName()).toString();
            }
            if (name.endsWith(".html")) {
                this.repackageNonJavaFile(name, toName);
            }
            else {
                this.justMoveNonJavaFile(name, toName);
            }
        }
    }
    
    public void repackageNonJavaFile(final String name) throws IOException {
        final File sourceFile = new File(this._sourceBase, name);
        final File targetFile = new File(this._targetBase, name);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        }
        else {
            this.writeFile(targetFile, this._repackager.repackage(this.readFile(sourceFile)));
        }
    }
    
    public void repackageNonJavaFile(final String sourceName, final String targetName) throws IOException {
        final File sourceFile = new File(this._sourceBase, sourceName);
        final File targetFile = new File(this._targetBase, targetName);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        }
        else {
            this.writeFile(targetFile, this._repackager.repackage(this.readFile(sourceFile)));
        }
    }
    
    public void justMoveNonJavaFile(final String sourceName, final String targetName) throws IOException {
        final File sourceFile = new File(this._sourceBase, sourceName);
        final File targetFile = new File(this._targetBase, targetName);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
        }
        else {
            copyFile(sourceFile, targetFile);
        }
    }
    
    public void repackageJavaFile(String name) throws IOException {
        final File sourceFile = new File(this._sourceBase, name);
        final StringBuffer sb = this.readFile(sourceFile);
        final Matcher packageMatcher = this._packagePattern.matcher(sb);
        if (packageMatcher.find()) {
            final String pkg = packageMatcher.group(1);
            final int pkgStart = packageMatcher.start(1);
            final int pkgEnd = packageMatcher.end(1);
            if (packageMatcher.find()) {
                throw new RuntimeException("Two package specifications found: " + name);
            }
            final List filePath = Repackager.splitPath(name, File.separatorChar);
            final String srcDir = Repackager.dirForPath(name);
            boolean swapped;
            do {
                swapped = false;
                for (int i = 1; i < filePath.size(); ++i) {
                    final String spec1 = filePath.get(i - 1);
                    final String spec2 = filePath.get(i);
                    if (spec1.indexOf(58) < spec2.indexOf(58)) {
                        filePath.set(i - 1, spec2);
                        filePath.set(i, spec1);
                        swapped = true;
                    }
                }
            } while (swapped);
            final List pkgPath = Repackager.splitPath(pkg, '.');
            int f = filePath.size() - 2;
            if (f < 0 || filePath.size() - 1 < pkgPath.size()) {
                throw new RuntimeException("Package spec differs from file path: " + name);
            }
            for (int j = pkgPath.size() - 1; j >= 0; --j) {
                if (!pkgPath.get(j).equals(filePath.get(f))) {
                    throw new RuntimeException("Package spec differs from file path: " + name);
                }
                --f;
            }
            List changeTo = null;
            List changeFrom = null;
        Label_0496:
            for (int k = 0; k < this._fromPackages.size(); ++k) {
                final List from = this._fromPackages.get(k);
                if (from.size() <= pkgPath.size()) {
                    for (int l = 0; l < from.size(); ++l) {
                        if (!from.get(l).equals(pkgPath.get(l))) {
                            continue Label_0496;
                        }
                    }
                    changeFrom = from;
                    changeTo = this._toPackages.get(k);
                    break;
                }
            }
            if (changeTo != null) {
                String newPkg = "";
                String newName = "";
                for (int m = 0; m < changeTo.size(); ++m) {
                    if (m > 0) {
                        newPkg += ".";
                        newName += File.separatorChar;
                    }
                    newPkg += changeTo.get(m);
                    newName += changeTo.get(m);
                }
                for (int m = filePath.size() - pkgPath.size() - 2; m >= 0; --m) {
                    newName = filePath.get(m) + File.separatorChar + newName;
                }
                for (int m = changeFrom.size(); m < pkgPath.size(); ++m) {
                    newName = newName + File.separatorChar + pkgPath.get(m);
                    newPkg = newPkg + '.' + pkgPath.get(m);
                }
                newName = newName + File.separatorChar + filePath.get(filePath.size() - 1);
                sb.replace(pkgStart, pkgEnd, newPkg);
                name = newName;
                final String newDir = Repackager.dirForPath(name);
                if (!srcDir.equals(newDir)) {
                    this._movedDirs.put(srcDir, newDir);
                }
            }
        }
        final File targetFile = new File(this._targetBase, name);
        if (sourceFile.lastModified() < targetFile.lastModified()) {
            ++this._skippedFiles;
            return;
        }
        this.writeFile(new File(this._targetBase, name), this._repackager.repackage(sb));
    }
    
    void writeFile(final File f, final StringBuffer chars) throws IOException {
        f.getParentFile().mkdirs();
        final OutputStream out = new FileOutputStream(f);
        final Writer w = new OutputStreamWriter(out);
        final Reader r = new StringReader(chars.toString());
        copy(r, w);
        r.close();
        w.close();
        out.close();
    }
    
    StringBuffer readFile(final File f) throws IOException {
        final InputStream in = new FileInputStream(f);
        final Reader r = new InputStreamReader(in);
        final StringWriter w = new StringWriter();
        copy(r, w);
        w.close();
        r.close();
        in.close();
        return w.getBuffer();
    }
    
    StringBuffer readInputStream(final InputStream is) throws IOException {
        final Reader r = new InputStreamReader(is);
        final StringWriter w = new StringWriter();
        copy(r, w);
        w.close();
        r.close();
        return w.getBuffer();
    }
    
    public static void copyFile(final File from, final File to) throws IOException {
        to.getParentFile().mkdirs();
        final FileInputStream in = new FileInputStream(from);
        final FileOutputStream out = new FileOutputStream(to);
        copy(in, out);
        out.close();
        in.close();
    }
    
    public static void copy(final InputStream in, final OutputStream out) throws IOException {
        final byte[] buffer = new byte[16384];
        while (true) {
            final int n = in.read(buffer, 0, buffer.length);
            if (n < 0) {
                break;
            }
            out.write(buffer, 0, n);
        }
    }
    
    public static void copy(final Reader r, final Writer w) throws IOException {
        final char[] buffer = new char[16384];
        while (true) {
            final int n = r.read(buffer, 0, buffer.length);
            if (n < 0) {
                break;
            }
            w.write(buffer, 0, n);
        }
    }
    
    public void fillFiles(final ArrayList files, final File file) throws IOException {
        if (!file.isDirectory()) {
            files.add(file);
            return;
        }
        if (file.getName().equals("build")) {
            return;
        }
        if (file.getName().equals("CVS")) {
            return;
        }
        final String[] entries = file.list();
        for (int i = 0; i < entries.length; ++i) {
            this.fillFiles(files, new File(file, entries[i]));
        }
    }
    
    public void recursiveDelete(final File file) throws IOException {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            final String[] entries = file.list();
            for (int i = 0; i < entries.length; ++i) {
                this.recursiveDelete(new File(file, entries[i]));
            }
        }
        file.delete();
    }
}
