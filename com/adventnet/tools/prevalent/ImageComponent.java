package com.adventnet.tools.prevalent;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.io.File;
import java.awt.image.ImageObserver;
import java.awt.Dimension;
import javax.swing.ImageIcon;
import java.awt.Graphics;
import java.awt.Image;
import java.util.Hashtable;
import java.awt.Font;
import java.awt.Color;
import java.applet.Applet;
import java.io.Serializable;
import javax.swing.JPanel;

public class ImageComponent extends JPanel implements Serializable
{
    protected String labelText;
    protected Applet app;
    protected String imageName;
    protected String imageName1;
    protected String imageName2;
    protected String imageName3;
    protected String imageName4;
    protected String imageName5;
    protected String imageName6;
    protected boolean image;
    protected Color bgColor;
    protected Color fgColor;
    protected Font labelFont;
    protected String[] greenTriggers;
    protected String[] redTriggers;
    protected String[] yellowTriggers;
    protected String[] cyanTriggers;
    protected String[] orangeTriggers;
    protected String[] grayTriggers;
    protected String flag;
    protected Hashtable imagesHTable;
    protected int wid;
    protected int hgt;
    protected transient Image buffer;
    protected transient Image img;
    protected transient Image img1;
    protected transient Image img2;
    protected transient Image img3;
    protected transient Image img4;
    protected transient Image img5;
    protected transient Image img6;
    protected transient Image tempimg;
    protected transient Graphics gbuffer;
    private transient ImageIcon imageIcon;
    protected String previousImage;
    
    public ImageComponent() {
        this.labelText = "Image";
        this.app = null;
        this.imageName = "images/bean.jpg";
        this.imageName1 = "";
        this.imageName2 = "";
        this.imageName3 = "";
        this.imageName4 = "";
        this.imageName5 = "";
        this.imageName6 = "";
        this.image = true;
        this.bgColor = Color.lightGray;
        this.fgColor = Color.black;
        this.labelFont = new Font("Helvetica", 1, 12);
        this.greenTriggers = new String[] { "On", "1", "Up" };
        this.redTriggers = new String[] { "Off", "2", "Down", "Critical" };
        this.yellowTriggers = new String[] { "Minor", "3", "6" };
        this.cyanTriggers = new String[] { "Warning", "4" };
        this.orangeTriggers = new String[] { "Major", "5" };
        this.grayTriggers = new String[] { "Unknown", "0", "Default" };
        this.flag = "green";
        this.imagesHTable = null;
        this.wid = 0;
        this.hgt = 0;
        this.buffer = null;
        this.img = null;
        this.img1 = null;
        this.img2 = null;
        this.img3 = null;
        this.img4 = null;
        this.img5 = null;
        this.img6 = null;
        this.tempimg = null;
        this.gbuffer = null;
        this.previousImage = "";
        this.imagesHTable = new Hashtable(13);
    }
    
    public ImageComponent(final Applet app) {
        this();
        this.app = app;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return super.getPreferredSize();
    }
    
    public void setImageResult(final long diff) {
        if (this.image) {
            this.imageName = String.valueOf(diff);
            this.imageName = "images/" + this.imageName + ".jpg";
            if (!this.previousImage.equals(this.imageName)) {
                this.setValues();
                this.repaint();
                this.previousImage = this.imageName;
            }
        }
        else {
            this.labelText = String.valueOf(diff);
            this.repaint();
        }
    }
    
    public void setImageResult(final String diff) {
        if (this.image) {
            this.imageName = diff;
            this.imageName = "images/" + this.imageName + ".jpg";
            if (!this.previousImage.equals(this.imageName)) {
                this.setValues();
                this.repaint();
                this.previousImage = this.imageName;
            }
        }
        else {
            this.labelText = diff;
            this.repaint();
        }
    }
    
    public void setIconResult(final long diff) {
        final Color newbgColor = this.getColorValue(String.valueOf(diff));
        if (newbgColor == this.bgColor) {
            return;
        }
        this.setBackground(this.bgColor = newbgColor);
        this.repaint();
    }
    
    public void setIconResult(final String diff) {
        final Color newbgColor = this.getColorValue(diff);
        if (newbgColor == this.bgColor) {
            return;
        }
        this.setBackground(this.bgColor = newbgColor);
        this.repaint();
    }
    
    public void setValues() {
        this.setValues(this.imageName);
    }
    
    private void setValues(final String imageName) {
        this.setValues(this.img = this.addImage(imageName));
    }
    
    private void setValues(final Image img) {
        this.img = img;
        if (img != null) {
            this.setPreferredSize(new Dimension(img.getWidth(null), img.getHeight(null)));
            this.revalidate();
            if (this.buffer != null) {
                this.gbuffer = this.buffer.getGraphics();
                if (this.gbuffer != null) {
                    this.gbuffer.setColor(this.bgColor);
                    this.gbuffer.fillRect(0, 0, this.wid, this.hgt);
                    this.gbuffer.drawImage(img, 0, 0, this.wid - 1, this.hgt - 1, this);
                }
            }
        }
    }
    
    public Image addImage(String name) {
        name = name.trim();
        this.tempimg = this.imagesHTable.get(name);
        if (this.tempimg != null) {
            return this.tempimg;
        }
        final ImageIcon ic = this.findImage(name);
        if (ic != null) {
            this.tempimg = ic.getImage();
            this.imagesHTable.put(name, this.tempimg);
            return this.tempimg;
        }
        return null;
    }
    
    private ImageIcon findImage(String name) {
        final String imageFile = name;
        ImageIcon imgIcon = null;
        if (imageFile.equals("")) {
            return null;
        }
        try {
            final String replacer = name = name.replace('\\', '/');
            String str = imageFile;
            if (imageFile.indexOf(47) > -1 && !imageFile.startsWith("/")) {
                str = '/' + imageFile;
            }
            URL url = null;
            try {
                url = this.getClass().getResource(str);
            }
            catch (final Exception ex) {
                System.out.println("Exception getting Image from : " + str);
            }
            if (url != null) {
                imgIcon = this.getTheImage(url);
            }
        }
        catch (final Exception e1) {
            System.out.println("Exception getting image from the Component Jar");
        }
        if (imgIcon == null && this.app == null) {
            try {
                final File f = new File(name);
                if (f.exists()) {
                    String path = f.getAbsolutePath();
                    path = path.replace('\\', '/');
                    final URL url = new URL("file", "", "//" + path);
                    if (url != null) {
                        imgIcon = this.getTheImage(url);
                    }
                }
            }
            catch (final Exception e2) {
                System.out.println("Exception getting Image from Absoulte Path");
            }
        }
        if (imgIcon == null && this.app != null) {
            final URL docBaseUrl = this.app.getDocumentBase();
            final int port = docBaseUrl.getPort();
            final String host = docBaseUrl.getHost();
            try {
                URL url2;
                if (port != -1) {
                    url2 = this.app.getClass().getResource("/http://" + host + ":" + String.valueOf(port) + "/" + imageFile);
                }
                else {
                    url2 = this.app.getClass().getResource("/http://" + host + "/" + imageFile);
                }
                imgIcon = this.getTheImage(url2);
            }
            catch (final Exception eee2) {
                System.out.println("Exception getting image from WebServer Base!");
            }
            if (imgIcon == null) {
                try {
                    final URL docBase = this.app.getDocumentBase();
                    String documentBase = docBase.toString();
                    documentBase = documentBase.replace('\\', '/');
                    final int index1 = documentBase.lastIndexOf("/");
                    if (index1 != -1) {
                        documentBase = documentBase.substring(0, index1 + 1);
                    }
                    final URL url3 = new URL(documentBase + "/" + imageFile);
                    imgIcon = this.getTheImage(url3);
                }
                catch (final Exception e3) {
                    System.out.println("Exception getting image from Document Base");
                }
            }
            if (imgIcon == null) {
                try {
                    final URL codeBase = this.app.getCodeBase();
                    String codeBaseString = codeBase.toString();
                    codeBaseString = codeBaseString.replace('\\', '/');
                    final int index1 = codeBaseString.lastIndexOf("/");
                    if (index1 != -1) {
                        codeBaseString = codeBaseString.substring(0, index1 + 1);
                    }
                    final URL url3 = new URL(codeBaseString + "/" + imageFile);
                    imgIcon = this.getTheImage(url3);
                }
                catch (final Exception eee3) {
                    System.out.println("Exception getting image from CodeBase");
                }
            }
        }
        return imgIcon;
    }
    
    private ImageIcon getTheImage(final URL urlArg) {
        if (urlArg == null) {
            return null;
        }
        ImageIcon icon = null;
        if (urlArg != null && urlArg.toString().endsWith(".png")) {
            try {
                final Class cls = Class.forName("com.sun.jimi.core.component.JimiCanvas");
                final Constructor cons = cls.getConstructor(URL.class);
                final Method meth = cls.getMethod("getImage", (Class[])null);
                final Object obj = cons.newInstance(urlArg);
                final Image image = (Image)meth.invoke(obj, (Object[])null);
                icon = new ImageIcon(image);
            }
            catch (final Throwable th) {
                if (th instanceof ClassNotFoundException || th instanceof NoClassDefFoundError) {
                    System.err.println("ERROR : " + th.getClass().getName() + " occured. JimiProClasses.zip might not have been added to the classpath.");
                }
                th.printStackTrace();
            }
        }
        else {
            icon = new ImageIcon(urlArg);
        }
        if (icon != null && icon.getIconHeight() != -1 && icon.getIconWidth() != -1) {
            return icon;
        }
        return null;
    }
    
    public Color getColorValue(final String val) {
        if (val == null) {
            return this.bgColor;
        }
        if (this.isTheColor(this.greenTriggers, Color.green, val)) {
            this.flag = "green";
            return Color.green;
        }
        if (this.isTheColor(this.redTriggers, Color.red, val)) {
            this.flag = "red";
            return Color.red;
        }
        if (this.isTheColor(this.yellowTriggers, Color.yellow, val)) {
            this.flag = "yellow";
            return Color.yellow;
        }
        if (this.isTheColor(this.orangeTriggers, Color.orange, val)) {
            this.flag = "orange";
            return Color.orange;
        }
        if (this.isTheColor(this.cyanTriggers, Color.cyan, val)) {
            this.flag = "cyan";
            return Color.cyan;
        }
        if (this.isTheColor(this.grayTriggers, Color.gray, val)) {
            this.flag = "gray";
            return Color.gray;
        }
        return this.bgColor;
    }
    
    public boolean isTheColor(final String[] s, final Color c, final String val) {
        if (s != null) {
            for (int i = 0; i < s.length; ++i) {
                if (val.equals(s[i])) {
                    return true;
                }
                if (s[i] != null && s[i].equals("default")) {
                    this.bgColor = c;
                }
            }
        }
        return false;
    }
    
    public String getLabelText() {
        return this.labelText;
    }
    
    public void setLabelText(final String s) {
        this.labelText = s;
        this.repaint();
    }
    
    public String getImageName() {
        return this.imageName;
    }
    
    public void setImageName(final String s) {
        this.imageName = s;
        this.setValues();
        this.repaint();
    }
    
    public void setImageIcon(final ImageIcon icon) {
        this.imageIcon = icon;
        this.setValues(icon.getImage());
        this.repaint();
    }
    
    public ImageIcon getImageIcon() {
        return this.imageIcon;
    }
    
    public String getImageName1() {
        return this.imageName1;
    }
    
    public void setImageName1(final String s) {
        this.imageName1 = s;
        this.setValues();
        this.repaint();
    }
    
    public String getImageName2() {
        return this.imageName2;
    }
    
    public void setImageName2(final String s) {
        this.imageName2 = s;
        this.setValues();
        this.repaint();
    }
    
    public String getImageName3() {
        return this.imageName3;
    }
    
    public void setImageName3(final String s) {
        this.imageName3 = s;
        this.setValues();
        this.repaint();
    }
    
    public String getImageName4() {
        return this.imageName4;
    }
    
    public void setImageName4(final String s) {
        this.imageName4 = s;
        this.setValues();
        this.repaint();
    }
    
    public String getImageName5() {
        return this.imageName5;
    }
    
    public void setImageName5(final String s) {
        this.imageName5 = s;
        this.setValues();
        this.repaint();
    }
    
    public String getImageName6() {
        return this.imageName6;
    }
    
    public void setImageName6(final String s) {
        this.imageName6 = s;
        this.setValues();
        this.repaint();
    }
    
    public boolean isImage() {
        return this.image;
    }
    
    public void setImage(final boolean b) {
        this.image = b;
        this.repaint();
    }
    
    public Color getBgColor() {
        return this.bgColor;
    }
    
    public void setBgColor(final Color c) {
        this.bgColor = c;
        this.repaint();
    }
    
    public Color getFgColor() {
        return this.fgColor;
    }
    
    public void setFgColor(final Color c) {
        this.fgColor = c;
        this.repaint();
    }
    
    public Font getLabelFont() {
        return this.labelFont;
    }
    
    public void setLabelFont(final Font f) {
        this.labelFont = f;
        this.repaint();
    }
    
    public String[] getGreenTriggers() {
        return this.greenTriggers;
    }
    
    public void setGreenTriggers(final String[] s) {
        this.greenTriggers = s;
    }
    
    public String[] getRedTriggers() {
        return this.redTriggers;
    }
    
    public void setRedTriggers(final String[] s) {
        this.redTriggers = s;
    }
    
    public String[] getYellowTriggers() {
        return this.yellowTriggers;
    }
    
    public void setYellowTriggers(final String[] s) {
        this.yellowTriggers = s;
    }
    
    public String[] getCyanTriggers() {
        return this.cyanTriggers;
    }
    
    public void setCyanTriggers(final String[] s) {
        this.cyanTriggers = s;
    }
    
    public String[] getOrangeTriggers() {
        return this.orangeTriggers;
    }
    
    public void setOrangeTriggers(final String[] s) {
        this.orangeTriggers = s;
    }
    
    public String[] getGrayTriggers() {
        return this.grayTriggers;
    }
    
    public void setGrayTriggers(final String[] s) {
        this.grayTriggers = s;
    }
}
