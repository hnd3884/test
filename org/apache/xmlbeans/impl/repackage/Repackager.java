package org.apache.xmlbeans.impl.repackage;

import java.io.File;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.List;

public class Repackager
{
    private List _fromPackages;
    private List _toPackages;
    private Matcher[] _fromMatchers;
    private String[] _toPackageNames;
    
    public Repackager(final String repackageSpecs) {
        this._fromPackages = new ArrayList();
        this._toPackages = new ArrayList();
        final List repackages = splitPath(repackageSpecs, ';');
        boolean swapped;
        do {
            swapped = false;
            for (int i = 1; i < repackages.size(); ++i) {
                final String spec1 = repackages.get(i - 1);
                final String spec2 = repackages.get(i);
                if (spec1.indexOf(58) < spec2.indexOf(58)) {
                    repackages.set(i - 1, spec2);
                    repackages.set(i, spec1);
                    swapped = true;
                }
            }
        } while (swapped);
        for (int j = 0; j < repackages.size(); ++j) {
            final String spec3 = repackages.get(j);
            final int k = spec3.indexOf(58);
            if (k < 0 || spec3.indexOf(58, k + 1) >= 0) {
                throw new RuntimeException("Illegal repackage specification: " + spec3);
            }
            final String from = spec3.substring(0, k);
            final String to = spec3.substring(k + 1);
            this._fromPackages.add(splitPath(from, '.'));
            this._toPackages.add(splitPath(to, '.'));
        }
        this._fromMatchers = new Matcher[this._fromPackages.size() * 2];
        this._toPackageNames = new String[this._fromPackages.size() * 2];
        this.addPatterns('.', 0);
        this.addPatterns('/', this._fromPackages.size());
    }
    
    void addPatterns(final char sep, final int off) {
        for (int i = 0; i < this._fromPackages.size(); ++i) {
            final List from = this._fromPackages.get(i);
            final List to = this._toPackages.get(i);
            String pattern = "";
            for (int j = 0; j < from.size(); ++j) {
                if (j > 0) {
                    pattern = pattern + "\\" + sep;
                }
                pattern += from.get(j);
            }
            String toPackage = "";
            for (int k = 0; k < to.size(); ++k) {
                if (k > 0) {
                    toPackage += sep;
                }
                toPackage += to.get(k);
            }
            this._fromMatchers[off + i] = Pattern.compile(pattern).matcher("");
            this._toPackageNames[off + i] = toPackage;
        }
    }
    
    public StringBuffer repackage(StringBuffer sb) {
        StringBuffer result = null;
        for (int i = 0; i < this._fromMatchers.length; ++i) {
            final Matcher m = this._fromMatchers[i];
            m.reset(sb);
            for (boolean found = m.find(); found; found = m.find()) {
                if (result == null) {
                    result = new StringBuffer();
                }
                m.appendReplacement(result, this._toPackageNames[i]);
            }
            if (result != null) {
                m.appendTail(result);
                sb = result;
                result = null;
            }
        }
        return sb;
    }
    
    public List getFromPackages() {
        return this._fromPackages;
    }
    
    public List getToPackages() {
        return this._toPackages;
    }
    
    public static ArrayList splitPath(String path, final char separator) {
        final ArrayList components = new ArrayList();
        while (true) {
            final int i = path.indexOf(separator);
            if (i < 0) {
                break;
            }
            components.add(path.substring(0, i));
            path = path.substring(i + 1);
        }
        if (path.length() > 0) {
            components.add(path);
        }
        return components;
    }
    
    public static String dirForPath(final String path) {
        return new File(path).getParent();
    }
}
