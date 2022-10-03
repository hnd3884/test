package com.adventnet.tools.prevalent;

import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.awt.Image;
import java.io.File;
import java.net.URL;
import java.util.Map;
import javax.swing.ImageIcon;
import java.applet.Applet;
import java.util.WeakHashMap;
import java.util.Hashtable;

public class Utility
{
    private static final Hashtable parametersHt;
    private static String usage;
    private static WeakHashMap resourceMap;
    private static WeakHashMap imageMap;
    
    private Utility() {
    }
    
    public static final ImageIcon findImage(final String nameArg, final Applet appletArg) {
        return findImage(nameArg, appletArg, false);
    }
    
    public static final ImageIcon findImage(final String nameArg, final Applet appletArg, final boolean reuseArg) {
        ImageIcon imgIcon = null;
        try {
            if (nameArg.equals("")) {
                return null;
            }
            if (reuseArg) {
                imgIcon = (ImageIcon)getKeyIfValuePresent(nameArg, Utility.imageMap);
            }
            if (imgIcon == null) {
                imgIcon = getFileNameFromJar(nameArg);
            }
            if (imgIcon == null && appletArg == null) {
                imgIcon = getFileFromAbsolutePath(nameArg);
            }
            if (imgIcon == null && appletArg != null) {
                imgIcon = getTheImage(getURLFromWebServerBase(nameArg, appletArg));
                if (imgIcon == null) {
                    imgIcon = getTheImage(getURLFromDocumentBase(nameArg, appletArg));
                }
                if (imgIcon == null) {
                    imgIcon = getTheImage(getURLFromCodeBase(nameArg, appletArg));
                }
            }
        }
        finally {
            if (imgIcon != null && reuseArg) {
                Utility.imageMap.put(imgIcon, nameArg);
            }
        }
        if (imgIcon == null) {
            System.err.println("Image " + nameArg + " not found. ");
        }
        return imgIcon;
    }
    
    public static final URL findURL(final String nameArg, final Applet appletArg) {
        URL fileURL = null;
        if (nameArg.equals("")) {
            return null;
        }
        if (appletArg != null) {
            fileURL = getURLFromWebServerBase(nameArg, appletArg);
            if (fileURL == null) {
                fileURL = getURLFromDocumentBase(nameArg, appletArg);
            }
            if (fileURL == null) {
                fileURL = getURLFromCodeBase(nameArg, appletArg);
            }
        }
        return fileURL;
    }
    
    public static ImageIcon getFileFromAbsolutePath(final String nameArg) {
        ImageIcon imgIcon = null;
        try {
            final File f = new File(nameArg);
            if (f.exists()) {
                String path = f.getAbsolutePath();
                path = path.replace('\\', '/');
                final URL url = new URL("file", "", "//" + path);
                if (url != null) {
                    imgIcon = getTheImage(url);
                }
            }
        }
        catch (final Exception e3) {
            System.out.println("Exception getting Image from Absoulte Path");
        }
        return imgIcon;
    }
    
    public static URL getURLFromAbsolutePath(final String nameArg) {
        URL url = null;
        try {
            final File f = new File(nameArg);
            if (f.exists()) {
                String path = f.getAbsolutePath();
                path = path.replace('\\', '/');
                url = new URL("file", "", "//" + path);
            }
        }
        catch (final Exception e3) {
            System.out.println("Exception getting Resource from Absoulte Path");
        }
        return url;
    }
    
    public static URL getURLFromGetResource(final String fileName) {
        String str = fileName;
        if (fileName.indexOf(47) > -1 && !fileName.startsWith("/")) {
            if (fileName.startsWith("./")) {
                str = fileName.substring(1, fileName.length());
            }
            else {
                str = '/' + fileName;
            }
        }
        URL url = null;
        try {
            url = Utility.class.getResource(str);
        }
        catch (final Exception ex) {
            System.out.println("Exception getting Resource from : " + str);
            ex.printStackTrace();
        }
        return url;
    }
    
    public static ImageIcon getFileNameFromJar(final String imageFile) {
        ImageIcon imgIcon = null;
        final URL url = getURLFromGetResource(imageFile);
        try {
            if (url != null) {
                imgIcon = getTheImage(url);
            }
        }
        catch (final RuntimeException e1) {
            System.out.println("Exception getting image from the Component Jar");
        }
        return imgIcon;
    }
    
    public static URL getURLFromCodeBase(final String fileName, final Applet appletArg) {
        URL url = null;
        try {
            final URL codeBase = appletArg.getCodeBase();
            String codeBaseString = codeBase.toString();
            codeBaseString = codeBaseString.replace('\\', '/');
            final int index1 = codeBaseString.lastIndexOf("/");
            if (index1 != -1) {
                codeBaseString = codeBaseString.substring(0, index1 + 1);
            }
            url = new URL(codeBaseString + "/" + fileName);
        }
        catch (final Exception eee1) {
            System.out.println("Exception getting fileName " + fileName + " from CodeBase");
        }
        return url;
    }
    
    public static URL getURLFromDocumentBase(final String fileName, final Applet appletArg) {
        URL url = null;
        try {
            final URL docBase = appletArg.getDocumentBase();
            String documentBase = docBase.toString();
            documentBase = documentBase.replace('\\', '/');
            final int index1 = documentBase.lastIndexOf("/");
            if (index1 != -1) {
                documentBase = documentBase.substring(0, index1);
            }
            url = new URL(documentBase + "/" + fileName);
        }
        catch (final Exception e2) {
            System.out.println("Exception getting fileName " + fileName + " from Document Base");
        }
        return url;
    }
    
    public static URL getURLFromWebServerBase(final String fileName, final Applet appletArg) {
        URL url = null;
        final URL docBaseUrl = appletArg.getDocumentBase();
        final int port = docBaseUrl.getPort();
        final String host = docBaseUrl.getHost();
        try {
            if (port != -1) {
                url = new URL("http://" + host + ":" + String.valueOf(port) + "/" + fileName);
            }
            else {
                url = new URL("http://" + host + ":" + "80" + "/" + fileName);
            }
        }
        catch (final Exception eee2) {
            System.out.println("Exception getting fileName " + fileName + " from WebServer Base!");
        }
        return url;
    }
    
    public static ImageIcon getTheImage(final URL urlArg) {
        ImageIcon icon = null;
        if (urlArg != null && urlArg.toString().endsWith(".png")) {
            try {
                final Class cls = Class.forName("com.sun.jimi.core.component.JimiCanvas");
                final Constructor cons = cls.getConstructor(URL.class);
                final Method meth = cls.getMethod("getImage", (Class[])null);
                final Object obj = cons.newInstance(urlArg);
                final Image image = (Image)meth.invoke(obj, (Object[])null);
                if (image != null) {
                    icon = new ImageIcon(image);
                }
            }
            catch (final ClassNotFoundException cnf) {
                icon = new ImageIcon(urlArg);
            }
            catch (final NoClassDefFoundError ncf) {
                icon = new ImageIcon(urlArg);
            }
            catch (final Throwable th) {
                System.err.println("ERROR : " + th.getClass().getName() + " occured. JimiProClasses.zip might not have been added to the classpath.");
                th.printStackTrace();
            }
        }
        else {
            icon = new ImageIcon(urlArg);
        }
        return icon;
    }
    
    public static Object getParameter(final String keyArg) {
        return Utility.parametersHt.get(keyArg);
    }
    
    public static void setParameter(final String keyArg, final Object objArg) {
        Utility.parametersHt.put(keyArg, objArg);
    }
    
    public static void parseAndSetParameters(final String[] param, final String[] arg) {
        createUsage(param);
        for (int i = 0; i < arg.length; i += 2) {
            final int index = arg[i].indexOf("-");
            if (index == -1) {
                usage_error();
            }
            if (arg[i].length() == 1) {
                usage_error();
            }
            final String key = arg[i].substring(index + 1);
            final String value = arg[i + 1];
            if (value == null) {
                usage_error();
            }
            Utility.parametersHt.put(key, value);
        }
        for (int i = 0; i < param.length; ++i) {
            if (!Utility.parametersHt.containsKey(param[i])) {
                usage_error();
            }
        }
    }
    
    private static void createUsage(final String[] param) {
        for (int i = 0; i < param.length; ++i) {
            Utility.usage = Utility.usage + "-" + param[i] + " " + param[i].toLowerCase() + " ";
        }
    }
    
    static void usage_error() {
        System.out.println("Warning Message : Application needs certain parameters. ");
        System.out.println("Usage: " + Utility.usage);
    }
    
    public static BuilderResourceBundle getBundle(final String resourceFile, final String locale, final Applet appletArg) {
        String nmsResourceDir = null;
        String nmsResourceClassName = null;
        if (appletArg != null) {
            nmsResourceDir = appletArg.getParameter("NMS_RESOURCE_DIRECTORY");
            nmsResourceClassName = appletArg.getParameter("NMS_RESOURCE_CLASSNAME");
        }
        else {
            nmsResourceDir = (String)getParameter("NMS_RESOURCE_DIRECTORY");
            nmsResourceClassName = (String)getParameter("NMS_RESOURCE_CLASSNAME");
        }
        if (Utility.resourceMap == null) {
            Utility.resourceMap = new WeakHashMap();
        }
        BuilderResourceBundle resourceBundle = (BuilderResourceBundle)getKeyIfValuePresent(nmsResourceDir + ":" + resourceFile + ":" + locale + ":" + nmsResourceClassName, Utility.resourceMap);
        if (resourceBundle == null) {
            resourceBundle = new BuilderResourceBundle(nmsResourceDir, resourceFile, locale, nmsResourceClassName, appletArg);
            Utility.resourceMap.put(resourceBundle, resourceBundle.getIdKey());
        }
        return resourceBundle;
    }
    
    private static final Object getKeyIfValuePresent(final Object valueArg, final Map hmArg) {
        if (hmArg.containsValue(valueArg)) {
            for (final Map.Entry e : hmArg.entrySet()) {
                if (valueArg.equals(e.getValue())) {
                    return e.getKey();
                }
            }
        }
        return null;
    }
    
    static {
        parametersHt = new Hashtable();
        Utility.usage = "";
        Utility.resourceMap = null;
        Utility.imageMap = new WeakHashMap(17);
    }
}
