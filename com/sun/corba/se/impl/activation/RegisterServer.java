package com.sun.corba.se.impl.activation;

import com.sun.corba.se.spi.activation.Activator;
import com.sun.corba.se.spi.activation.BadServerDefinition;
import com.sun.corba.se.spi.activation.ServerAlreadyRegistered;
import com.sun.corba.se.spi.activation.ServerHeldDown;
import com.sun.corba.se.spi.activation.ServerAlreadyActive;
import com.sun.corba.se.spi.activation.ServerNotRegistered;
import com.sun.corba.se.spi.activation.ActivatorHelper;
import com.sun.corba.se.spi.activation.RepositoryPackage.ServerDef;
import com.sun.corba.se.spi.activation.RepositoryHelper;
import org.omg.CORBA.ORB;
import com.sun.corba.se.impl.orbutil.CorbaResourceUtil;
import java.io.PrintStream;

class RegisterServer implements CommandHandler
{
    @Override
    public String getCommandName() {
        return "register";
    }
    
    @Override
    public void printCommandHelp(final PrintStream printStream, final boolean b) {
        if (!b) {
            printStream.println(CorbaResourceUtil.getText("servertool.register"));
        }
        else {
            printStream.println(CorbaResourceUtil.getText("servertool.register1"));
        }
    }
    
    @Override
    public boolean processCommand(final String[] array, final ORB orb, final PrintStream printStream) {
        int i = 0;
        String s = "";
        String s2 = "";
        String s3 = "";
        String s4 = "";
        String s5 = "";
        int registerServer = 0;
        while (i < array.length) {
            final String s6 = array[i++];
            if (s6.equals("-server")) {
                if (i >= array.length) {
                    return true;
                }
                s2 = array[i++];
            }
            else if (s6.equals("-applicationName")) {
                if (i >= array.length) {
                    return true;
                }
                s = array[i++];
            }
            else if (s6.equals("-classpath")) {
                if (i >= array.length) {
                    return true;
                }
                s3 = array[i++];
            }
            else if (s6.equals("-args")) {
                while (i < array.length && !array[i].equals("-vmargs")) {
                    s4 = (s4.equals("") ? array[i] : (s4 + " " + array[i]));
                    ++i;
                }
                if (s4.equals("")) {
                    return true;
                }
                continue;
            }
            else {
                if (!s6.equals("-vmargs")) {
                    return true;
                }
                while (i < array.length && !array[i].equals("-args")) {
                    s5 = (s5.equals("") ? array[i] : (s5 + " " + array[i]));
                    ++i;
                }
                if (s5.equals("")) {
                    return true;
                }
                continue;
            }
        }
        if (s2.equals("")) {
            return true;
        }
        try {
            registerServer = RepositoryHelper.narrow(orb.resolve_initial_references("ServerRepository")).registerServer(new ServerDef(s, s2, s3, s4, s5));
            final Activator narrow = ActivatorHelper.narrow(orb.resolve_initial_references("ServerActivator"));
            narrow.activate(registerServer);
            narrow.install(registerServer);
            printStream.println(CorbaResourceUtil.getText("servertool.register2", registerServer));
        }
        catch (final ServerNotRegistered serverNotRegistered) {}
        catch (final ServerAlreadyActive serverAlreadyActive) {}
        catch (final ServerHeldDown serverHeldDown) {
            printStream.println(CorbaResourceUtil.getText("servertool.register3", registerServer));
        }
        catch (final ServerAlreadyRegistered serverAlreadyRegistered) {
            printStream.println(CorbaResourceUtil.getText("servertool.register4", registerServer));
        }
        catch (final BadServerDefinition badServerDefinition) {
            printStream.println(CorbaResourceUtil.getText("servertool.baddef", badServerDefinition.reason));
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
