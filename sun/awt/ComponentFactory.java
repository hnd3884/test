package sun.awt;

import sun.awt.datatransfer.DataTransferer;
import java.awt.AWTException;
import java.awt.peer.RobotPeer;
import java.awt.GraphicsDevice;
import java.awt.Robot;
import java.awt.peer.FontPeer;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.dnd.DragGestureEvent;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.CheckboxMenuItem;
import java.awt.peer.MenuItemPeer;
import java.awt.MenuItem;
import java.awt.peer.PopupMenuPeer;
import java.awt.PopupMenu;
import java.awt.peer.MenuPeer;
import java.awt.Menu;
import java.awt.peer.MenuBarPeer;
import java.awt.MenuBar;
import java.awt.peer.FileDialogPeer;
import java.awt.FileDialog;
import java.awt.peer.TextAreaPeer;
import java.awt.TextArea;
import java.awt.peer.ScrollPanePeer;
import java.awt.ScrollPane;
import java.awt.peer.ScrollbarPeer;
import java.awt.Scrollbar;
import java.awt.peer.CheckboxPeer;
import java.awt.Checkbox;
import java.awt.peer.ListPeer;
import java.awt.List;
import java.awt.peer.LabelPeer;
import java.awt.Label;
import java.awt.peer.ChoicePeer;
import java.awt.Choice;
import java.awt.peer.TextFieldPeer;
import java.awt.TextField;
import java.awt.peer.ButtonPeer;
import java.awt.Button;
import java.awt.peer.DialogPeer;
import java.awt.Dialog;
import java.awt.peer.FramePeer;
import java.awt.Frame;
import java.awt.peer.WindowPeer;
import java.awt.Window;
import java.awt.peer.PanelPeer;
import java.awt.Panel;
import java.awt.HeadlessException;
import java.awt.peer.CanvasPeer;
import java.awt.Canvas;

public interface ComponentFactory
{
    CanvasPeer createCanvas(final Canvas p0) throws HeadlessException;
    
    PanelPeer createPanel(final Panel p0) throws HeadlessException;
    
    WindowPeer createWindow(final Window p0) throws HeadlessException;
    
    FramePeer createFrame(final Frame p0) throws HeadlessException;
    
    DialogPeer createDialog(final Dialog p0) throws HeadlessException;
    
    ButtonPeer createButton(final Button p0) throws HeadlessException;
    
    TextFieldPeer createTextField(final TextField p0) throws HeadlessException;
    
    ChoicePeer createChoice(final Choice p0) throws HeadlessException;
    
    LabelPeer createLabel(final Label p0) throws HeadlessException;
    
    ListPeer createList(final List p0) throws HeadlessException;
    
    CheckboxPeer createCheckbox(final Checkbox p0) throws HeadlessException;
    
    ScrollbarPeer createScrollbar(final Scrollbar p0) throws HeadlessException;
    
    ScrollPanePeer createScrollPane(final ScrollPane p0) throws HeadlessException;
    
    TextAreaPeer createTextArea(final TextArea p0) throws HeadlessException;
    
    FileDialogPeer createFileDialog(final FileDialog p0) throws HeadlessException;
    
    MenuBarPeer createMenuBar(final MenuBar p0) throws HeadlessException;
    
    MenuPeer createMenu(final Menu p0) throws HeadlessException;
    
    PopupMenuPeer createPopupMenu(final PopupMenu p0) throws HeadlessException;
    
    MenuItemPeer createMenuItem(final MenuItem p0) throws HeadlessException;
    
    CheckboxMenuItemPeer createCheckboxMenuItem(final CheckboxMenuItem p0) throws HeadlessException;
    
    DragSourceContextPeer createDragSourceContextPeer(final DragGestureEvent p0) throws InvalidDnDOperationException, HeadlessException;
    
    FontPeer getFontPeer(final String p0, final int p1);
    
    RobotPeer createRobot(final Robot p0, final GraphicsDevice p1) throws AWTException, HeadlessException;
    
    DataTransferer getDataTransferer();
}
