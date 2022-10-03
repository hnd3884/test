package sun.security.tools.policytool;

import java.awt.event.MouseListener;
import javax.swing.ListModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ConfirmRemovePolicyEntryOKButtonListener implements ActionListener
{
    private PolicyTool tool;
    private ToolWindow tw;
    private ToolDialog us;
    
    ConfirmRemovePolicyEntryOKButtonListener(final PolicyTool tool, final ToolWindow tw, final ToolDialog us) {
        this.tool = tool;
        this.tw = tw;
        this.us = us;
    }
    
    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        this.tool.removeEntry(this.tool.getEntry()[((JList)this.tw.getComponent(3)).getSelectedIndex()]);
        final DefaultListModel defaultListModel = new DefaultListModel();
        final JList list = new JList(defaultListModel);
        list.setVisibleRowCount(15);
        list.setSelectionMode(0);
        list.addMouseListener(new PolicyListListener(this.tool, this.tw));
        final PolicyEntry[] entry = this.tool.getEntry();
        if (entry != null) {
            for (int i = 0; i < entry.length; ++i) {
                defaultListModel.addElement(entry[i].headerToString());
            }
        }
        this.tw.replacePolicyList(list);
        this.us.setVisible(false);
        this.us.dispose();
    }
}
