package com.octo.captcha.component.image.backgroundgenerator;

import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import java.io.FileInputStream;
import java.awt.image.ImageObserver;
import java.awt.Image;
import java.awt.Graphics2D;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.net.URL;
import java.io.File;
import java.awt.image.BufferedImage;
import com.octo.captcha.CaptchaException;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

public class FileReaderRandomBackgroundGenerator extends AbstractBackgroundGenerator
{
    private List images;
    private String rootPath;
    protected static final Map cachedDirectories;
    
    public FileReaderRandomBackgroundGenerator(final Integer n, final Integer n2, final String rootPath) {
        super(n, n2);
        this.images = new ArrayList();
        this.rootPath = ".";
        if (rootPath != null) {
            this.rootPath = rootPath;
        }
        final File[] listFiles = this.findDirectory(this.rootPath).listFiles();
        if (listFiles != null) {
            for (final File file : listFiles) {
                BufferedImage image = null;
                if (file.isFile()) {
                    image = getImage(file);
                }
                if (image != null) {
                    this.images.add(this.images.size(), image);
                }
            }
            if (this.images.size() == 0) {
                throw new CaptchaException("Root path directory is valid but does not contains any image (jpg) files");
            }
            for (int j = 0; j < this.images.size(); ++j) {
                this.images.set(j, this.tile((BufferedImage)this.images.get(j)));
            }
        }
    }
    
    protected File findDirectory(final String s) {
        if (FileReaderRandomBackgroundGenerator.cachedDirectories.containsKey(s)) {
            return FileReaderRandomBackgroundGenerator.cachedDirectories.get(s);
        }
        File file = new File(s);
        final StringBuffer sb = new StringBuffer();
        this.appendFilePath(sb, file);
        if (this.isNotReadable(file)) {
            file = new File(".", s);
            this.appendFilePath(sb, file);
            if (this.isNotReadable(file)) {
                file = new File("/", s);
                this.appendFilePath(sb, file);
                if (this.isNotReadable(file)) {
                    final URL resource = FileReaderRandomBackgroundGenerator.class.getClassLoader().getResource(s);
                    if (resource != null) {
                        file = new File(this.getFilePath(resource));
                        this.appendFilePath(sb, file);
                    }
                    else {
                        final URL resource2 = ClassLoader.getSystemClassLoader().getResource(s);
                        if (resource2 != null) {
                            file = new File(this.getFilePath(resource2));
                            this.appendFilePath(sb, file);
                        }
                    }
                }
            }
        }
        if (this.isNotReadable(file)) {
            final StringTokenizer classpathFromSystemProperty = this.getClasspathFromSystemProperty();
            while (classpathFromSystemProperty.hasMoreElements()) {
                final String nextToken = classpathFromSystemProperty.nextToken();
                if (!nextToken.endsWith(".jar")) {
                    file = new File(nextToken, s);
                    this.appendFilePath(sb, file);
                    if (file.canRead() && file.isDirectory()) {
                        break;
                    }
                    continue;
                }
            }
        }
        if (this.isNotReadable(file)) {
            throw new CaptchaException("All tried paths :'" + sb.toString() + "' is not" + " a directory or cannot be read");
        }
        FileReaderRandomBackgroundGenerator.cachedDirectories.put(s, file);
        return file;
    }
    
    private String getFilePath(final URL url) {
        String decode = null;
        try {
            decode = URLDecoder.decode(url.getFile(), "UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {}
        return decode;
    }
    
    private boolean isNotReadable(final File file) {
        return !file.canRead() || !file.isDirectory();
    }
    
    private StringTokenizer getClasspathFromSystemProperty() {
        return new StringTokenizer(System.getProperty("java.class.path"), File.pathSeparator);
    }
    
    private void appendFilePath(final StringBuffer sb, final File file) {
        sb.append(file.getAbsolutePath());
        sb.append("\n");
    }
    
    private BufferedImage tile(final BufferedImage bufferedImage) {
        final BufferedImage bufferedImage2 = new BufferedImage(this.getImageWidth(), this.getImageHeight(), bufferedImage.getType());
        final Graphics2D graphics2D = (Graphics2D)bufferedImage2.getGraphics();
        final int n = this.getImageWidth() / bufferedImage.getWidth();
        for (int n2 = this.getImageHeight() / bufferedImage.getHeight(), i = 0; i <= n2; ++i) {
            for (int j = 0; j <= n; ++j) {
                graphics2D.drawImage(bufferedImage, j * bufferedImage.getWidth(), i * bufferedImage.getHeight(), Math.min(bufferedImage.getWidth(), this.getImageWidth()), Math.min(bufferedImage.getHeight(), this.getImageHeight()), null);
            }
        }
        graphics2D.dispose();
        return bufferedImage2;
    }
    
    private static BufferedImage getImage(final File file) {
        try {
            final FileInputStream fileInputStream = new FileInputStream(file);
            final BufferedImage read = ImageIO.read(fileInputStream);
            fileInputStream.close();
            return read;
        }
        catch (final IOException ex) {
            throw new CaptchaException("Unknown error during file reading ", ex);
        }
    }
    
    public BufferedImage getBackground() {
        return this.images.get(this.myRandom.nextInt(this.images.size()));
    }
    
    static {
        cachedDirectories = new HashMap();
    }
}
