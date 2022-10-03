package com.google.zxing.client.j2se;

import com.google.zxing.Result;
import com.google.zxing.LuminanceSource;
import java.awt.image.BufferedImage;
import com.google.zxing.ReaderException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.common.HybridBinarizer;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import java.net.MalformedURLException;
import java.awt.Container;
import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.FlowLayout;
import javax.swing.JPanel;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JFrame;

public final class GUIRunner extends JFrame
{
    private final JLabel imageLabel;
    private final JTextArea textArea;
    
    private GUIRunner() {
        this.imageLabel = new JLabel();
        (this.textArea = new JTextArea()).setEditable(false);
        this.textArea.setMaximumSize(new Dimension(400, 200));
        final Container panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.add(this.imageLabel);
        panel.add(this.textArea);
        this.setTitle("ZXing");
        this.setSize(400, 400);
        this.setDefaultCloseOperation(3);
        this.setContentPane(panel);
        this.setLocationRelativeTo(null);
    }
    
    public static void main(final String[] args) throws MalformedURLException {
        final GUIRunner runner = new GUIRunner();
        runner.setVisible(true);
        runner.chooseImage();
    }
    
    private void chooseImage() throws MalformedURLException {
        final JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(this);
        final File file = fileChooser.getSelectedFile();
        final Icon imageIcon = new ImageIcon(file.toURI().toURL());
        this.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight() + 100);
        this.imageLabel.setIcon(imageIcon);
        final String decodeText = getDecodeText(file);
        this.textArea.setText(decodeText);
    }
    
    private static String getDecodeText(final File file) {
        BufferedImage image;
        try {
            image = ImageIO.read(file);
        }
        catch (final IOException ioe) {
            return ioe.toString();
        }
        if (image == null) {
            return "Could not decode image";
        }
        final LuminanceSource source = new BufferedImageLuminanceSource(image);
        final BinaryBitmap bitmap = new BinaryBitmap((Binarizer)new HybridBinarizer(source));
        Result result;
        try {
            result = new MultiFormatReader().decode(bitmap);
        }
        catch (final ReaderException re) {
            return re.toString();
        }
        return String.valueOf(result.getText());
    }
}
