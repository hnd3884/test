package com.me.devicemanagement.framework.server.tree;

import java.util.Properties;

public class TreeNode
{
    public String id;
    public String parent_id;
    public String text;
    public String style;
    public String imageClosed;
    public String imageOpen;
    public String imageLeaf;
    public String radio;
    public boolean checked;
    public boolean child;
    public boolean nocheckbox;
    public Properties userData;
    
    public TreeNode() {
        this.id = null;
        this.parent_id = null;
        this.text = null;
        this.style = null;
        this.imageClosed = null;
        this.imageOpen = null;
        this.imageLeaf = null;
        this.radio = null;
        this.checked = false;
        this.child = false;
        this.nocheckbox = false;
        this.userData = null;
    }
}
