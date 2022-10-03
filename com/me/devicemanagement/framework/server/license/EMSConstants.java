package com.me.devicemanagement.framework.server.license;

import java.util.Hashtable;
import java.util.Properties;
import java.util.HashMap;

public interface EMSConstants
{
    public static final HashMap<String, HashMap<String, HashMap<String, Properties>>> DEFAULT_PRODUCT_TO_COMPONENTS_MAP = new HashMap<String, HashMap<String, HashMap<String, Properties>>>() {
        {
            ((HashMap<String, EMSConstants$1$1>)this).put("DCEE", new HashMap<String, HashMap<String, Properties>>() {
                {
                    ((HashMap<String, EMSConstants$1$1$1>)this).put("registered", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$1$1$1>)this).put("SOM", new Properties() {});
                            ((HashMap<String, EMSConstants$1$1$1$2>)this).put("Patch", new Properties() {});
                            this.put("Tools", new Properties());
                            ((HashMap<String, EMSConstants$1$1$1$3>)this).put("OSDeployer", new Properties() {});
                            ((HashMap<String, EMSConstants$1$1$1$4>)this).put("MobileDevices", new Properties() {});
                        }
                    });
                    ((HashMap<String, EMSConstants$1$1$2>)this).put("free", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$1$2$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "25");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$3>)this).put("Tools", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$4>)this).put("OSDeployer", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfWorkstationMachines", "4");
                                    ((Hashtable<String, String>)this).put("NumberOfServerMachines", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$5>)this).put("MobileDevices", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfMobileDevices", "25");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$6>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$2$7>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$1$3>)this).put("trial", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$1$3$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$3>)this).put("Tools", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$4>)this).put("OSDeployer", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfWorkstationMachines", "4");
                                    ((Hashtable<String, String>)this).put("NumberOfServerMachines", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$5>)this).put("MobileDevices", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfMobileDevices", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$6>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$1$3$7>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                }
            });
            ((HashMap<String, EMSConstants$1$2>)this).put("PMP", new HashMap<String, HashMap<String, Properties>>() {
                {
                    ((HashMap<String, EMSConstants$1$2$1>)this).put("trial", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$2$1$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$1$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$1$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$1$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$2$2>)this).put("free", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$2$2$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "25");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$2$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$2$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$2$2$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$2$3>)this).put("registered", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$2$3$1>)this).put("SOM", new Properties() {});
                            ((HashMap<String, EMSConstants$1$2$3$2>)this).put("Patch", new Properties() {});
                        }
                    });
                }
            });
            ((HashMap<String, EMSConstants$1$3>)this).put("RAP", new HashMap<String, HashMap<String, Properties>>() {
                {
                    ((HashMap<String, EMSConstants$1$3$1>)this).put("trial", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$3$1$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Tools_Professional");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$1$2>)this).put("Tools", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Tools_Professional");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$1$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$1$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$3$2>)this).put("free", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$3$2$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Tools_Professional");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "10");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$2$2>)this).put("Tools", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Tools_Professional");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$2$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$3$2$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$3$3>)this).put("registered", new HashMap<String, Properties>() {
                        {
                            this.put("SOM", new Properties());
                            this.put("Tools", new Properties());
                        }
                    });
                }
            });
            ((HashMap<String, EMSConstants$1$4>)this).put("VMP", new HashMap<String, HashMap<String, Properties>>() {
                {
                    ((HashMap<String, EMSConstants$1$4$1>)this).put("trial", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$4$1$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$1$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$1$3>)this).put("Vulnerability", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$1$4>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "unlimited");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$4$2>)this).put("free", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$4$2$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "25");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$2$2>)this).put("Patch", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$2$3>)this).put("Vulnerability", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Enterprise");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$2$4>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$4$2$5>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$4$3>)this).put("registered", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$4$3$1>)this).put("SOM", new Properties() {});
                            ((HashMap<String, EMSConstants$1$4$3$2>)this).put("Patch", new Properties() {});
                            ((HashMap<String, EMSConstants$1$4$3$3>)this).put("Vulnerability", new Properties() {});
                        }
                    });
                }
            });
            ((HashMap<String, EMSConstants$1$5>)this).put("DCP", new HashMap<String, HashMap<String, Properties>>() {
                {
                    ((HashMap<String, EMSConstants$1$5$1>)this).put("trial", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$5$1$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$1$2>)this).put("DeviceControl", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                    ((Hashtable<String, String>)this).put("DCP_ENABLED", "true");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$1$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "unlimited");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$1$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$5$2>)this).put("free", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$5$2$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                    ((Hashtable<String, String>)this).put("NumberOfComputers", "25");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$2$2>)this).put("DeviceControl", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("DCP_ENABLED", "true");
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$2$3>)this).put("Technicians", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("NumberOfTechnicians", "1");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$2$4>)this).put("AddOnModules", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Multi-lang-Pack", "true");
                                    ((Hashtable<String, String>)this).put("FOSEnabled", "false");
                                    ((Hashtable<String, String>)this).put("FwsEnabled", "false");
                                }
                            });
                        }
                    });
                    ((HashMap<String, EMSConstants$1$5$3>)this).put("registered", new HashMap<String, Properties>() {
                        {
                            ((HashMap<String, EMSConstants$1$5$3$1>)this).put("SOM", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                }
                            });
                            ((HashMap<String, EMSConstants$1$5$3$2>)this).put("DeviceControl", new Properties() {
                                {
                                    ((Hashtable<String, String>)this).put("Edition", "Professional");
                                    ((Hashtable<String, String>)this).put("DCP_ENABLED", "true");
                                }
                            });
                        }
                    });
                }
            });
        }
    };
    public static final String DEVICE_CONTROL = "DeviceControl";
    public static final String PATCH = "Patch";
    public static final String SOM = "SOM";
    public static final String TOOLS = "Tools";
    public static final String MDM = "MobileDevices";
    public static final String OS_DEPLOYER = "OSDeployer";
    public static final String VULNERABILITY = "Vulnerability";
    public static final String ADD_ON = "AddOnModules";
    public static final String TECHNICIANS = "Technicians";
}
