package com.adventnet.tools.prevalent;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public final class Laterality
{
    public static float licenseVersion;
    static String aboutMesg;
    public static String[] productName;
    public static int MAX_NO_OF_PRODUCTS;
    static int SNMP_UTILITIES;
    static int JAVA_AGENT_TOOLKIT;
    static int C_AGENT_TOOLKIT;
    static int SIMULATION_TOOLKIT;
    static int MANAGEMENT_BUILDER;
    static int WEB_NMS;
    static int AUTOTEST_TOOLKIT;
    static int WEB_NMS_MSP_EDITION;
    static int FAULT_MANAGEMENT_TOOLKIT;
    static int CONFIGURATION_MANAGEMENT_TOOLKIT;
    static int SECURITY_MANAGEMENT_API;
    static int SLM_TOOLKIT;
    static int MEDIATION_SERVER;
    static int WEB_SERVICES_FRAMEWORK;
    static int FRAMEWORK_SERVICES;
    static int PROVISIONING_FRAMEWORK;
    static int BEAN_BUILDER;
    static int MANAGE_ENGINE_JMX_STUDIO;
    static int MANAGE_ENGINE_APPLICATIONS_MANAGER;
    static int WEBNMS_STUDIO;
    static int SNMP_API;
    static int AGENT_TESTER;
    static int TL1_API;
    static int TMF_MEDIATOR;
    static int CONSOLE_BUILDER;
    static int MANAGE_ENGINE_APPLICATIONS_MANAGER_WEBSPHERE_EDITION;
    
    private Laterality() {
    }
    
    public static int getMapValue(final String product) {
        if (product == null) {
            return -1;
        }
        for (int i = 0; i < Laterality.MAX_NO_OF_PRODUCTS; ++i) {
            if (product.equals(Laterality.productName[i])) {
                return i;
            }
        }
        return -1;
    }
    
    public static int getHashCode(final String s) {
        int h = 0;
        int off = 0;
        final char[] val = s.toCharArray();
        for (int len = s.length(), i = 0; i < len; ++i) {
            h = 31 * h + val[off++];
        }
        return h;
    }
    
    public static void showAboutDialog(final JFrame frame) {
        JOptionPane.showMessageDialog(frame, Laterality.aboutMesg, "About", 1);
    }
    
    static {
        Laterality.licenseVersion = 2.0f;
        Laterality.aboutMesg = "The AdventNet License Info Editor Version is 2.0";
        Laterality.productName = new String[] { "AdventNet SNMP Utilities", "AdventNet AgentToolkit - Java Edition", "AdventNet AgentToolkit - C Edition", "AdventNet Simulation Toolkit", "AdventNet Management Builder", "AdventNet WebNMS", "AdventNet QEngine", "AdventNet WebNMS MSP Edition", "AdventNet Fault Management Toolkit", "AdventNet Configuration Management Toolkit", "AdventNet Security Management API", "AdventNet SLM Toolkit", "AdventNet Mediation Server", "AdventNet Web Services Framework", "AdventNet Framework Services", "AdventNet Provisioning Framework", "AdventNet Bean Builder", "AdventNet ManageEngine JMX Studio", "AdventNet ManageEngine Applications Manager", "AdventNet WebNMS Studio", "AdventNet SNMP API", "AdventNet AgentTester", "AdventNet TL1 API", "AdventNet TMF Mediator", "AdventNet Console Builder", "AdventNet ManageEngine Applications Manager - WebSphere Edition" };
        Laterality.MAX_NO_OF_PRODUCTS = 26;
        Laterality.SNMP_UTILITIES = 0;
        Laterality.JAVA_AGENT_TOOLKIT = 1;
        Laterality.C_AGENT_TOOLKIT = 2;
        Laterality.SIMULATION_TOOLKIT = 3;
        Laterality.MANAGEMENT_BUILDER = 4;
        Laterality.WEB_NMS = 5;
        Laterality.AUTOTEST_TOOLKIT = 6;
        Laterality.WEB_NMS_MSP_EDITION = 7;
        Laterality.FAULT_MANAGEMENT_TOOLKIT = 8;
        Laterality.CONFIGURATION_MANAGEMENT_TOOLKIT = 9;
        Laterality.SECURITY_MANAGEMENT_API = 10;
        Laterality.SLM_TOOLKIT = 11;
        Laterality.MEDIATION_SERVER = 12;
        Laterality.WEB_SERVICES_FRAMEWORK = 13;
        Laterality.FRAMEWORK_SERVICES = 14;
        Laterality.PROVISIONING_FRAMEWORK = 15;
        Laterality.BEAN_BUILDER = 16;
        Laterality.MANAGE_ENGINE_JMX_STUDIO = 17;
        Laterality.MANAGE_ENGINE_APPLICATIONS_MANAGER = 18;
        Laterality.WEBNMS_STUDIO = 19;
        Laterality.SNMP_API = 20;
        Laterality.AGENT_TESTER = 21;
        Laterality.TL1_API = 22;
        Laterality.TMF_MEDIATOR = 23;
        Laterality.CONSOLE_BUILDER = 24;
        Laterality.MANAGE_ENGINE_APPLICATIONS_MANAGER_WEBSPHERE_EDITION = 25;
    }
}
