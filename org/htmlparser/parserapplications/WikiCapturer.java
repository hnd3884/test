package org.htmlparser.parserapplications;

import java.io.IOException;
import java.net.MalformedURLException;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.TagNameFilter;
import javax.swing.JFileChooser;
import java.io.File;
import java.net.URL;
import javax.swing.Icon;
import java.awt.Component;
import javax.swing.JOptionPane;

public class WikiCapturer extends SiteCapturer
{
    protected boolean isToBeCaptured(final String link) {
        boolean ret = super.isToBeCaptured(link);
        if (ret) {
            if (link.endsWith("PhpWikiAdministration")) {
                ret = false;
            }
            else if (link.endsWith("PhpWikiDocumentation")) {
                ret = false;
            }
            else if (link.endsWith("TextFormattingRules")) {
                ret = false;
            }
            else if (link.endsWith("NewMarkupTestPage")) {
                ret = false;
            }
            else if (link.endsWith("OldMarkupTestPage")) {
                ret = false;
            }
            else if (link.endsWith("OldTextFormattingRules")) {
                ret = false;
            }
            else if (link.endsWith("PgsrcTranslation")) {
                ret = false;
            }
            else if (link.endsWith("HowToUseWiki")) {
                ret = false;
            }
            else if (link.endsWith("MoreAboutMechanics")) {
                ret = false;
            }
            else if (link.endsWith("AddingPages")) {
                ret = false;
            }
            else if (link.endsWith("WikiWikiWeb")) {
                ret = false;
            }
            else if (link.endsWith("UserPreferences")) {
                ret = false;
            }
            else if (link.endsWith("PhpWiki")) {
                ret = false;
            }
            else if (link.endsWith("WabiSabi")) {
                ret = false;
            }
            else if (link.endsWith("EditText")) {
                ret = false;
            }
            else if (link.endsWith("FindPage")) {
                ret = false;
            }
            else if (link.endsWith("RecentChanges")) {
                ret = false;
            }
            else if (link.endsWith("RecentEdits")) {
                ret = false;
            }
            else if (link.endsWith("RecentVisitors")) {
                ret = false;
            }
            else if (link.endsWith("SteveWainstead")) {
                ret = false;
            }
        }
        return ret;
    }
    
    public static void main(final String[] args) throws MalformedURLException, IOException {
        final WikiCapturer worker = new WikiCapturer();
        if (0 >= args.length) {
            final String url = (String)JOptionPane.showInputDialog(null, "Enter the URL to capture:", "Web Site", -1, null, null, "http://htmlparser.sourceforge.net/wiki");
            if (null != url) {
                worker.setSource(url);
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setSource(args[0]);
        }
        if (1 >= args.length) {
            final String url = worker.getSource();
            final URL source = new URL(url);
            final String path = new File(new File("." + File.separator), source.getHost() + File.separator).getCanonicalPath();
            final File target = new File(path);
            final JFileChooser chooser = new JFileChooser(target);
            chooser.setDialogType(1);
            chooser.setFileSelectionMode(1);
            chooser.setSelectedFile(target);
            chooser.setMultiSelectionEnabled(false);
            chooser.setDialogTitle("Target Directory");
            final int ret = chooser.showSaveDialog(null);
            if (ret == 0) {
                worker.setTarget(chooser.getSelectedFile().getAbsolutePath());
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setTarget(args[1]);
        }
        if (2 >= args.length) {
            final Boolean capture = (Boolean)JOptionPane.showInputDialog(null, "Should resources be captured:", "Capture Resources", -1, null, new Object[] { Boolean.TRUE, Boolean.FALSE }, Boolean.TRUE);
            if (null != capture) {
                worker.setCaptureResources(capture);
            }
            else {
                System.exit(1);
            }
        }
        else {
            worker.setCaptureResources(Boolean.valueOf(args[2]));
        }
        worker.setFilter(new NotFilter(new OrFilter(new AndFilter(new TagNameFilter("DIV"), new HasAttributeFilter("id", "navbar")), new OrFilter(new AndFilter(new TagNameFilter("DIV"), new HasAttributeFilter("id", "actionbar")), new AndFilter(new TagNameFilter("DIV"), new HasAttributeFilter("id", "xhtml-validator"))))));
        worker.capture();
        System.exit(0);
    }
}
