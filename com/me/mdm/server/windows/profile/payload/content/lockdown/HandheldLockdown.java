package com.me.mdm.server.windows.profile.payload.content.lockdown;

import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import java.util.ArrayList;
import java.math.BigInteger;
import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "HandheldLockdown")
public class HandheldLockdown
{
    @XmlElement(name = "Default", required = true)
    protected DefaultRoleT defaultTag;
    @XmlElement(name = "RoleList")
    protected RoleListT roleList;
    @XmlAttribute(name = "version", required = true)
    protected BigDecimal version;
    @XmlAttribute(name = "xmlns:xsi")
    protected String xsi;
    @XmlAttribute(name = "xmlns:xsd")
    protected String xsd;
    
    public DefaultRoleT getDefault() {
        return this.defaultTag;
    }
    
    public void setDefault(final DefaultRoleT value) {
        this.defaultTag = value;
    }
    
    public RoleListT getRoleList() {
        return this.roleList;
    }
    
    public void setRoleList(final RoleListT value) {
        this.roleList = value;
    }
    
    public BigDecimal getVersion() {
        return this.version;
    }
    
    public void setVersion(final BigDecimal value) {
        this.version = value;
    }
    
    public String getXsiNameSpace() {
        return this.xsi;
    }
    
    public void setxsiNameSpace(final String xsi) {
        this.xsi = xsi;
    }
    
    public String getxsdNameSpace() {
        return this.xsd;
    }
    
    public void setxsdNameSpace(final String xsd) {
        this.xsd = xsd;
    }
    
    public static HandheldLockdown getLockDownProfile(final EnterpriseLockDownProperties properties, final String os) {
        final HandheldLockdown handheldLockdown = new HandheldLockdown();
        handheldLockdown.setVersion(BigDecimal.ONE);
        handheldLockdown.setxsiNameSpace("http://www.w3.org/2001/XMLSchema-instance");
        handheldLockdown.setxsdNameSpace("http://www.w3.org/2001/XMLSchema");
        final DefaultRoleT defaultBasicT = new DefaultRoleT();
        final ActioncenterT actioncenterT = new ActioncenterT();
        actioncenterT.setEnabled(false);
        final ApplicationListT applicationListT = new ApplicationListT();
        final List<AppProperties> allowedApps = properties.getAllowedApps();
        for (int i = 0; i < allowedApps.size(); ++i) {
            final AppProperties curApp = allowedApps.get(i);
            final ApplicationT applicationT = new ApplicationT();
            if (os.equals("10")) {
                applicationT.setAumid(curApp.aumid);
            }
            applicationT.setProductId(curApp.productID);
            applicationListT.getApplication().add(applicationT);
        }
        final DefaultBasicT.Buttons Buttons = new DefaultBasicT.Buttons();
        final MenuItemListT menuItemListT = new MenuItemListT();
        final DefaultBasicT.Settings settingT = new DefaultBasicT.Settings();
        final TileManipulationT tileManipulationT = new TileManipulationT();
        final StartscreenSizeT startscreenSizeT = StartscreenSizeT.fromValue("Small");
        defaultBasicT.setActionCenter(actioncenterT);
        defaultBasicT.setApps(applicationListT);
        defaultBasicT.setButtons(Buttons);
        defaultBasicT.setMenuItems(menuItemListT);
        defaultBasicT.setSettings(settingT);
        defaultBasicT.setTiles(tileManipulationT);
        defaultBasicT.setStartScreenSize(startscreenSizeT);
        handheldLockdown.setDefault(defaultBasicT);
        return handheldLockdown;
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "button_event_t", propOrder = {})
    private static class ButtonEventT
    {
        @XmlElement(name = "Application")
        protected ApplicationT application;
        @XmlAttribute(name = "name", required = true)
        protected SupportedButtonEventT name;
        
        public ApplicationT getApplication() {
            return this.application;
        }
        
        public void setApplication(final ApplicationT value) {
            this.application = value;
        }
        
        public SupportedButtonEventT getName() {
            return this.name;
        }
        
        public void setName(final SupportedButtonEventT value) {
            this.name = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "application_t", propOrder = {})
    private static class ApplicationT
    {
        @XmlElement(name = "PinToStart")
        protected StartTileT pinToStart;
        @XmlAttribute(name = "productId")
        protected String productId;
        @XmlAttribute(name = "aumid")
        protected String aumid;
        @XmlAttribute(name = "folderName")
        protected String folderName;
        @XmlAttribute(name = "folderId")
        protected BigInteger folderId;
        @XmlAttribute(name = "parameters")
        protected String parameters;
        @XmlAttribute(name = "autoRun")
        protected Boolean autoRun;
        
        public StartTileT getPinToStart() {
            return this.pinToStart;
        }
        
        public void setPinToStart(final StartTileT value) {
            this.pinToStart = value;
        }
        
        public String getProductId() {
            return this.productId;
        }
        
        public void setProductId(final String value) {
            this.productId = value;
        }
        
        public String getAumid() {
            return this.aumid;
        }
        
        public void setAumid(final String value) {
            this.aumid = value;
        }
        
        public String getFolderName() {
            return this.folderName;
        }
        
        public void setFolderName(final String value) {
            this.folderName = value;
        }
        
        public BigInteger getFolderId() {
            return this.folderId;
        }
        
        public void setFolderId(final BigInteger value) {
            this.folderId = value;
        }
        
        public String getParameters() {
            return this.parameters;
        }
        
        public void setParameters(final String value) {
            this.parameters = value;
        }
        
        public Boolean isAutoRun() {
            return this.autoRun;
        }
        
        public void setAutoRun(final Boolean value) {
            this.autoRun = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "actioncenter_t")
    private static class ActioncenterT
    {
        @XmlAttribute(name = "enabled", required = true)
        protected boolean enabled;
        @XmlAttribute(name = "actionCenterNotificationEnabled")
        protected BigInteger actionCenterNotificationEnabled;
        @XmlAttribute(name = "aboveLockToastEnabled")
        protected BigInteger aboveLockToastEnabled;
        
        public boolean isEnabled() {
            return this.enabled;
        }
        
        public void setEnabled(final boolean value) {
            this.enabled = value;
        }
        
        public BigInteger getActionCenterNotificationEnabled() {
            return this.actionCenterNotificationEnabled;
        }
        
        public void setActionCenterNotificationEnabled(final BigInteger value) {
            this.actionCenterNotificationEnabled = value;
        }
        
        public BigInteger getAboveLockToastEnabled() {
            return this.aboveLockToastEnabled;
        }
        
        public void setAboveLockToastEnabled(final BigInteger value) {
            this.aboveLockToastEnabled = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "application_list_t", propOrder = { "application" })
    private static class ApplicationListT
    {
        @XmlElement(name = "Application")
        protected List<ApplicationT> application;
        
        public List<ApplicationT> getApplication() {
            if (this.application == null) {
                this.application = new ArrayList<ApplicationT>();
            }
            return this.application;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "button_list_t", propOrder = { "button" })
    private static class ButtonListT
    {
        @XmlElement(name = "Button")
        protected List<ButtonT> button;
        
        public List<ButtonT> getButton() {
            if (this.button == null) {
                this.button = new ArrayList<ButtonT>();
            }
            return this.button;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "button_t", propOrder = { "buttonEvent" })
    private static class ButtonT
    {
        @XmlElement(name = "ButtonEvent")
        protected List<ButtonEventT> buttonEvent;
        @XmlAttribute(name = "name", required = true)
        protected SupportedButtonT name;
        
        public List<ButtonEventT> getButtonEvent() {
            if (this.buttonEvent == null) {
                this.buttonEvent = new ArrayList<ButtonEventT>();
            }
            return this.buttonEvent;
        }
        
        public SupportedButtonT getName() {
            return this.name;
        }
        
        public void setName(final SupportedButtonT value) {
            this.name = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "default_basic_t", propOrder = { "actionCenter", "wlanssid", "apps", "buttons", "cspRunner", "menuItems", "settings", "tiles" })
    @XmlSeeAlso({ DefaultRoleT.class, RoleT.class })
    private static class DefaultBasicT
    {
        @XmlElement(name = "ActionCenter", required = true)
        protected ActioncenterT actionCenter;
        @XmlElement(name = "WLANSSID")
        protected WlanssidT wlanssid;
        @XmlElement(name = "Apps", required = true)
        protected ApplicationListT apps;
        @XmlElement(name = "Buttons", required = true)
        protected Buttons buttons;
        @XmlElement(name = "CSPRunner")
        protected Object cspRunner;
        @XmlElement(name = "MenuItems", required = true)
        protected MenuItemListT menuItems;
        @XmlElement(name = "Settings", required = true)
        protected Settings settings;
        @XmlElement(name = "Tiles")
        protected TileManipulationT tiles;
        
        public ActioncenterT getActionCenter() {
            return this.actionCenter;
        }
        
        public void setActionCenter(final ActioncenterT value) {
            this.actionCenter = value;
        }
        
        public WlanssidT getWLANSSID() {
            return this.wlanssid;
        }
        
        public void setWLANSSID(final WlanssidT value) {
            this.wlanssid = value;
        }
        
        public ApplicationListT getApps() {
            return this.apps;
        }
        
        public void setApps(final ApplicationListT value) {
            this.apps = value;
        }
        
        public Buttons getButtons() {
            return this.buttons;
        }
        
        public void setButtons(final Buttons value) {
            this.buttons = value;
        }
        
        public Object getCSPRunner() {
            return this.cspRunner;
        }
        
        public void setCSPRunner(final Object value) {
            this.cspRunner = value;
        }
        
        public MenuItemListT getMenuItems() {
            return this.menuItems;
        }
        
        public void setMenuItems(final MenuItemListT value) {
            this.menuItems = value;
        }
        
        public Settings getSettings() {
            return this.settings;
        }
        
        public void setSettings(final Settings value) {
            this.settings = value;
        }
        
        public TileManipulationT getTiles() {
            return this.tiles;
        }
        
        public void setTiles(final TileManipulationT value) {
            this.tiles = value;
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {})
        public static class Buttons
        {
            @XmlElement(name = "ButtonLockdownList")
            protected ButtonListT buttonLockdownList;
            @XmlElement(name = "ButtonRemapList")
            protected ButtonListT buttonRemapList;
            
            public ButtonListT getButtonLockdownList() {
                return this.buttonLockdownList;
            }
            
            public void setButtonLockdownList(final ButtonListT value) {
                this.buttonLockdownList = value;
            }
            
            public ButtonListT getButtonRemapList() {
                return this.buttonRemapList;
            }
            
            public void setButtonRemapList(final ButtonListT value) {
                this.buttonRemapList = value;
            }
        }
        
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = { "system", "application" })
        private static class Settings
        {
            @XmlElement(name = "System")
            protected List<SettingT> system;
            @XmlElement(name = "Application")
            protected List<SettingT> application;
            
            public List<SettingT> getSystem() {
                if (this.system == null) {
                    this.system = new ArrayList<SettingT>();
                }
                return this.system;
            }
            
            public List<SettingT> getApplication() {
                if (this.application == null) {
                    this.application = new ArrayList<SettingT>();
                }
                return this.application;
            }
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "default_role_t", propOrder = { "startScreenSize" })
    private static class DefaultRoleT extends DefaultBasicT
    {
        @XmlElement(name = "StartScreenSize", required = true)
        @XmlSchemaType(name = "string")
        protected StartscreenSizeT startScreenSize;
        
        public StartscreenSizeT getStartScreenSize() {
            return this.startScreenSize;
        }
        
        public void setStartScreenSize(final StartscreenSizeT value) {
            this.startScreenSize = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "menu_item_list_t", propOrder = { "disableMenuItems" })
    private static class MenuItemListT
    {
        @XmlElement(name = "DisableMenuItems")
        protected Object disableMenuItems;
        
        public Object getDisableMenuItems() {
            return this.disableMenuItems;
        }
        
        public void setDisableMenuItems(final Object value) {
            this.disableMenuItems = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "role_list_t", propOrder = { "role" })
    private static class RoleListT
    {
        @XmlElement(name = "Role", required = true)
        protected List<RoleT> role;
        
        public List<RoleT> getRole() {
            if (this.role == null) {
                this.role = new ArrayList<RoleT>();
            }
            return this.role;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "role_t")
    private static class RoleT extends DefaultBasicT
    {
        @XmlAttribute(name = "guid", required = true)
        protected String guid;
        @XmlAttribute(name = "name", required = true)
        protected String name;
        
        public String getGuid() {
            return this.guid;
        }
        
        public void setGuid(final String value) {
            this.guid = value;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String value) {
            this.name = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "setting_t")
    private static class SettingT
    {
        @XmlAttribute(name = "name", required = true)
        protected String name;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String value) {
            this.name = value;
        }
    }
    
    @XmlType(name = "startscreen_size_t")
    @XmlEnum
    public enum StartscreenSizeT
    {
        @XmlEnumValue("Small")
        SMALL("Small"), 
        @XmlEnumValue("Large")
        LARGE("Large");
        
        private final String value;
        
        private StartscreenSizeT(final String v) {
            this.value = v;
        }
        
        public String value() {
            return this.value;
        }
        
        public static StartscreenSizeT fromValue(final String v) {
            for (final StartscreenSizeT c : values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "start_tile_t", propOrder = {})
    private static class StartTileT
    {
        @XmlElement(name = "Size", required = true)
        @XmlSchemaType(name = "string")
        protected TileSizeT size;
        @XmlElement(name = "Location", required = true)
        protected TileLocationT location;
        @XmlElement(name = "ParentFolderId")
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger parentFolderId;
        
        public TileSizeT getSize() {
            return this.size;
        }
        
        public void setSize(final TileSizeT value) {
            this.size = value;
        }
        
        public TileLocationT getLocation() {
            return this.location;
        }
        
        public void setLocation(final TileLocationT value) {
            this.location = value;
        }
        
        public BigInteger getParentFolderId() {
            return this.parentFolderId;
        }
        
        public void setParentFolderId(final BigInteger value) {
            this.parentFolderId = value;
        }
    }
    
    @XmlType(name = "supported_button_event_t")
    @XmlEnum
    public enum SupportedButtonEventT
    {
        @XmlEnumValue("All")
        ALL("All"), 
        @XmlEnumValue("Press")
        PRESS("Press"), 
        @XmlEnumValue("PressAndHold")
        PRESS_AND_HOLD("PressAndHold");
        
        private final String value;
        
        private SupportedButtonEventT(final String v) {
            this.value = v;
        }
        
        public String value() {
            return this.value;
        }
        
        public static SupportedButtonEventT fromValue(final String v) {
            for (final SupportedButtonEventT c : values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
    
    @XmlType(name = "supported_button_t")
    @XmlEnum
    public enum SupportedButtonT
    {
        @XmlEnumValue("Back")
        BACK("Back"), 
        @XmlEnumValue("Start")
        START("Start"), 
        @XmlEnumValue("Search")
        SEARCH("Search"), 
        @XmlEnumValue("Camera")
        CAMERA("Camera"), 
        @XmlEnumValue("Custom1")
        CUSTOM_1("Custom1"), 
        @XmlEnumValue("Custom2")
        CUSTOM_2("Custom2"), 
        @XmlEnumValue("Custom3")
        CUSTOM_3("Custom3");
        
        private final String value;
        
        private SupportedButtonT(final String v) {
            this.value = v;
        }
        
        public String value() {
            return this.value;
        }
        
        public static SupportedButtonT fromValue(final String v) {
            for (final SupportedButtonT c : values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "tile_location_t", propOrder = { "locationX", "locationY" })
    private static class TileLocationT
    {
        @XmlElement(name = "LocationX")
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger locationX;
        @XmlElement(name = "LocationY")
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger locationY;
        
        public BigInteger getLocationX() {
            return this.locationX;
        }
        
        public void setLocationX(final BigInteger value) {
            this.locationX = value;
        }
        
        public BigInteger getLocationY() {
            return this.locationY;
        }
        
        public void setLocationY(final BigInteger value) {
            this.locationY = value;
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "tile_manipulation_t", propOrder = { "enableTileManipulation" })
    private static class TileManipulationT
    {
        @XmlElement(name = "EnableTileManipulation")
        protected Object enableTileManipulation;
        
        public Object getEnableTileManipulation() {
            return this.enableTileManipulation;
        }
        
        public void setEnableTileManipulation(final Object value) {
            this.enableTileManipulation = value;
        }
    }
    
    @XmlType(name = "tile_size_t")
    @XmlEnum
    public enum TileSizeT
    {
        @XmlEnumValue("Small")
        SMALL("Small"), 
        @XmlEnumValue("Medium")
        MEDIUM("Medium"), 
        @XmlEnumValue("Large")
        LARGE("Large");
        
        private final String value;
        
        private TileSizeT(final String v) {
            this.value = v;
        }
        
        public String value() {
            return this.value;
        }
        
        public static TileSizeT fromValue(final String v) {
            for (final TileSizeT c : values()) {
                if (c.value.equals(v)) {
                    return c;
                }
            }
            throw new IllegalArgumentException(v);
        }
    }
    
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "wlanssid_t", propOrder = { "data", "exclusive" })
    private static class WlanssidT
    {
        @XmlElement(name = "Data")
        protected String data;
        @XmlElement(name = "Exclusive")
        protected Boolean exclusive;
        
        public String getData() {
            return this.data;
        }
        
        public void setData(final String value) {
            this.data = value;
        }
        
        public Boolean isExclusive() {
            return this.exclusive;
        }
        
        public void setExclusive(final Boolean value) {
            this.exclusive = value;
        }
    }
    
    public static class EnterpriseLockDownProperties
    {
        List<AppProperties> allowedApps;
        
        public EnterpriseLockDownProperties() {
            this.allowedApps = new ArrayList<AppProperties>();
        }
        
        public List<AppProperties> getAllowedApps() {
            return this.allowedApps;
        }
    }
    
    @XmlRegistry
    private static class ObjectFactory
    {
        public ObjectFactory() {
        }
        
        public DefaultBasicT createDefaultBasicT() {
            return new DefaultBasicT();
        }
        
        public HandheldLockdown createHandheldLockdown() {
            return new HandheldLockdown();
        }
        
        public DefaultRoleT createDefaultRoleT() {
            return new DefaultRoleT();
        }
        
        public RoleListT createRoleListT() {
            return new RoleListT();
        }
        
        public StartTileT createStartTileT() {
            return new StartTileT();
        }
        
        public ButtonListT createButtonListT() {
            return new ButtonListT();
        }
        
        public MenuItemListT createMenuItemListT() {
            return new MenuItemListT();
        }
        
        public ButtonEventT createButtonEventT() {
            return new ButtonEventT();
        }
        
        public ApplicationListT createApplicationListT() {
            return new ApplicationListT();
        }
        
        public ActioncenterT createActioncenterT() {
            return new ActioncenterT();
        }
        
        public SettingT createSettingT() {
            return new SettingT();
        }
        
        public ApplicationT createApplicationT() {
            return new ApplicationT();
        }
        
        public ButtonT createButtonT() {
            return new ButtonT();
        }
        
        public WlanssidT createWlanssidT() {
            return new WlanssidT();
        }
        
        public TileManipulationT createTileManipulationT() {
            return new TileManipulationT();
        }
        
        public RoleT createRoleT() {
            return new RoleT();
        }
        
        public TileLocationT createTileLocationT() {
            return new TileLocationT();
        }
        
        public DefaultBasicT.Buttons createDefaultBasicTButtons() {
            return new DefaultBasicT.Buttons();
        }
        
        public DefaultBasicT.Settings createDefaultBasicTSettings() {
            return new DefaultBasicT.Settings();
        }
    }
}
