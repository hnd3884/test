package org.jfree.ui.about;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import java.awt.Component;
import org.jfree.ui.RefineryUtilities;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.awt.BorderLayout;
import java.awt.Image;
import javax.swing.JPanel;

public class AboutPanel extends JPanel
{
    public AboutPanel(final String application, final String version, final String copyright, final String info) {
        this(application, version, copyright, info, null);
    }
    
    public AboutPanel(final String application, final String version, final String copyright, final String info, final Image logo) {
        this.setLayout(new BorderLayout());
        final JPanel textPanel = new JPanel(new GridLayout(4, 1, 0, 4));
        final JPanel appPanel = new JPanel();
        final Font f1 = new Font("Dialog", 1, 14);
        final JLabel appLabel = RefineryUtilities.createJLabel(application, f1, Color.black);
        appLabel.setHorizontalTextPosition(0);
        appPanel.add(appLabel);
        final JPanel verPanel = new JPanel();
        final Font f2 = new Font("Dialog", 0, 12);
        final JLabel verLabel = RefineryUtilities.createJLabel(version, f2, Color.black);
        verLabel.setHorizontalTextPosition(0);
        verPanel.add(verLabel);
        final JPanel copyrightPanel = new JPanel();
        final JLabel copyrightLabel = RefineryUtilities.createJLabel(copyright, f2, Color.black);
        copyrightLabel.setHorizontalTextPosition(0);
        copyrightPanel.add(copyrightLabel);
        final JPanel infoPanel = new JPanel();
        final JLabel infoLabel = RefineryUtilities.createJLabel(info, f2, Color.black);
        infoLabel.setHorizontalTextPosition(0);
        infoPanel.add(infoLabel);
        textPanel.add(appPanel);
        textPanel.add(verPanel);
        textPanel.add(copyrightPanel);
        textPanel.add(infoPanel);
        this.add(textPanel);
        if (logo != null) {
            final JPanel imagePanel = new JPanel(new BorderLayout());
            imagePanel.add(new JLabel(new ImageIcon(logo)));
            imagePanel.setBorder(BorderFactory.createLineBorder(Color.black));
            final JPanel imageContainer = new JPanel(new BorderLayout());
            imageContainer.add(imagePanel, "North");
            this.add(imageContainer, "West");
        }
    }
}
