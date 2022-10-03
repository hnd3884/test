package org.openjsse.legacy8ujsse.sun.security.ssl;

import java.util.Iterator;
import java.util.Collections;
import javax.net.ssl.SSLProtocolException;
import java.util.List;
import java.util.HashMap;
import java.util.LinkedList;

final class HandshakeStateManager
{
    private LinkedList<HandshakeState> upcomingStates;
    private LinkedList<HandshakeState> alternatives;
    private static final boolean debugIsOn;
    private static final HashMap<Byte, String> handshakeTypes;
    
    HandshakeStateManager() {
        this.upcomingStates = new LinkedList<HandshakeState>();
        this.alternatives = new LinkedList<HandshakeState>();
    }
    
    boolean isEmpty() {
        return this.upcomingStates.isEmpty();
    }
    
    List<Byte> check(final byte handshakeType) throws SSLProtocolException {
        final List<Byte> ignoredOptional = new LinkedList<Byte>();
        final String exceptionMsg = "Handshake message sequence violation, " + handshakeType;
        if (HandshakeStateManager.debugIsOn) {
            System.out.println("check handshake state: " + toString(handshakeType));
        }
        if (this.upcomingStates.isEmpty()) {
            if (handshakeType != 0 && handshakeType != 1) {
                throw new SSLProtocolException("Handshake message sequence violation, " + handshakeType);
            }
            return Collections.emptyList();
        }
        else {
            if (handshakeType == 0) {
                return Collections.emptyList();
            }
            for (final HandshakeState handshakeState : this.upcomingStates) {
                if (handshakeState.handshakeType == handshakeType) {
                    return ignoredOptional;
                }
                if (!handshakeState.isOptional) {
                    for (final HandshakeState alternative : this.alternatives) {
                        if (alternative.handshakeType == handshakeType) {
                            return ignoredOptional;
                        }
                        if (alternative.isOptional) {
                            continue;
                        }
                        throw new SSLProtocolException(exceptionMsg);
                    }
                    throw new SSLProtocolException(exceptionMsg);
                }
                ignoredOptional.add(handshakeState.handshakeType);
            }
            throw new SSLProtocolException("Handshake message sequence violation, " + handshakeType);
        }
    }
    
    void update(final HandshakeMessage handshakeMessage, final boolean isAbbreviated) throws SSLProtocolException {
        final byte handshakeType = (byte)handshakeMessage.messageType();
        final String exceptionMsg = "Handshake message sequence violation, " + handshakeType;
        if (HandshakeStateManager.debugIsOn) {
            System.out.println("update handshake state: " + toString(handshakeType));
        }
        boolean hasPresentState = false;
        switch (handshakeType) {
            case 0: {
                if (!this.upcomingStates.isEmpty()) {
                    this.upcomingStates.add(HandshakeState.HS_CLIENT_HELLO);
                    break;
                }
                break;
            }
            case 1: {
                if (!this.upcomingStates.isEmpty()) {
                    final HandshakeState handshakeState = this.upcomingStates.pop();
                    if (handshakeState != HandshakeState.HS_CLIENT_HELLO) {
                        throw new SSLProtocolException(exceptionMsg);
                    }
                }
                final HandshakeMessage.ClientHello clientHello = (HandshakeMessage.ClientHello)handshakeMessage;
                this.upcomingStates.add(HandshakeState.HS_SERVER_HELLO);
                break;
            }
            case 2: {
                if (this.upcomingStates.isEmpty()) {
                    throw new SSLProtocolException(exceptionMsg);
                }
                final HandshakeState handshakeState2 = this.upcomingStates.pop();
                HandshakeState alternative = null;
                if (!this.alternatives.isEmpty()) {
                    alternative = this.alternatives.pop();
                }
                if (handshakeState2 != HandshakeState.HS_SERVER_HELLO && alternative != HandshakeState.HS_SERVER_HELLO) {
                    throw new SSLProtocolException(exceptionMsg);
                }
                final HandshakeMessage.ServerHello serverHello = (HandshakeMessage.ServerHello)handshakeMessage;
                final HelloExtensions hes = serverHello.extensions;
                if (isAbbreviated) {
                    this.upcomingStates.add(HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC);
                    this.upcomingStates.add(HandshakeState.HS_SERVER_FINISHED);
                    this.upcomingStates.add(HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC);
                    this.upcomingStates.add(HandshakeState.HS_CLIENT_FINISHED);
                    break;
                }
                final CipherSuite.KeyExchange keyExchange = serverHello.cipherSuite.keyExchange;
                if (keyExchange != CipherSuite.KeyExchange.K_KRB5 && keyExchange != CipherSuite.KeyExchange.K_KRB5_EXPORT && keyExchange != CipherSuite.KeyExchange.K_DH_ANON && keyExchange != CipherSuite.KeyExchange.K_ECDH_ANON) {
                    this.upcomingStates.add(HandshakeState.HS_SERVER_CERTIFICATE);
                }
                if (keyExchange == CipherSuite.KeyExchange.K_RSA_EXPORT || keyExchange == CipherSuite.KeyExchange.K_DHE_RSA || keyExchange == CipherSuite.KeyExchange.K_DHE_DSS || keyExchange == CipherSuite.KeyExchange.K_DH_ANON || keyExchange == CipherSuite.KeyExchange.K_ECDHE_RSA || keyExchange == CipherSuite.KeyExchange.K_ECDHE_ECDSA || keyExchange == CipherSuite.KeyExchange.K_ECDH_ANON) {
                    this.upcomingStates.add(HandshakeState.HS_SERVER_KEY_EXCHANGE);
                }
                this.upcomingStates.add(HandshakeState.HS_CERTIFICATE_REQUEST);
                this.upcomingStates.add(HandshakeState.HS_SERVER_HELLO_DONE);
                this.upcomingStates.add(HandshakeState.HS_CLIENT_CERTIFICATE);
                this.upcomingStates.add(HandshakeState.HS_CLIENT_KEY_EXCHANGE);
                this.upcomingStates.add(HandshakeState.HS_CERTIFICATE_VERIFY);
                this.upcomingStates.add(HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC);
                this.upcomingStates.add(HandshakeState.HS_CLIENT_FINISHED);
                this.upcomingStates.add(HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC);
                this.upcomingStates.add(HandshakeState.HS_SERVER_FINISHED);
                break;
            }
            case 11: {
                while (!this.upcomingStates.isEmpty()) {
                    final HandshakeState handshakeState3 = this.upcomingStates.pop();
                    if (handshakeState3.handshakeType == handshakeType) {
                        hasPresentState = true;
                        if (handshakeState3 != HandshakeState.HS_CLIENT_CERTIFICATE && handshakeState3 != HandshakeState.HS_SERVER_CERTIFICATE) {
                            throw new SSLProtocolException(exceptionMsg);
                        }
                        boolean isClientMessage = false;
                        if (!this.upcomingStates.isEmpty()) {
                            final HandshakeState nextState = this.upcomingStates.getFirst();
                            if (nextState == HandshakeState.HS_CLIENT_KEY_EXCHANGE) {
                                isClientMessage = true;
                            }
                        }
                        if (isClientMessage) {
                            if (handshakeState3 != HandshakeState.HS_CLIENT_CERTIFICATE) {
                                throw new SSLProtocolException(exceptionMsg);
                            }
                            break;
                        }
                        else {
                            if (handshakeState3 != HandshakeState.HS_SERVER_CERTIFICATE) {
                                throw new SSLProtocolException(exceptionMsg);
                            }
                            break;
                        }
                    }
                    else {
                        if (!handshakeState3.isOptional) {
                            throw new SSLProtocolException(exceptionMsg);
                        }
                        continue;
                    }
                }
                if (!hasPresentState) {
                    throw new SSLProtocolException(exceptionMsg);
                }
                break;
            }
            default: {
                while (!this.upcomingStates.isEmpty()) {
                    final HandshakeState handshakeState3 = this.upcomingStates.pop();
                    if (handshakeState3.handshakeType == handshakeType) {
                        hasPresentState = true;
                        break;
                    }
                    if (!handshakeState3.isOptional) {
                        throw new SSLProtocolException(exceptionMsg);
                    }
                }
                if (!hasPresentState) {
                    throw new SSLProtocolException(exceptionMsg);
                }
                break;
            }
        }
        if (HandshakeStateManager.debugIsOn) {
            for (final HandshakeState handshakeState2 : this.upcomingStates) {
                System.out.println("upcoming handshake states: " + handshakeState2);
            }
            for (final HandshakeState handshakeState2 : this.alternatives) {
                System.out.println("upcoming handshake alternative state: " + handshakeState2);
            }
        }
    }
    
    void changeCipherSpec(final boolean isInput, final boolean isClient) throws SSLProtocolException {
        if (HandshakeStateManager.debugIsOn) {
            System.out.println("update handshake state: change_cipher_spec");
        }
        final String exceptionMsg = "ChangeCipherSpec message sequence violation";
        HandshakeState expectedState;
        if ((isClient && isInput) || (!isClient && !isInput)) {
            expectedState = HandshakeState.HS_SERVER_CHANGE_CIPHER_SPEC;
        }
        else {
            expectedState = HandshakeState.HS_CLIENT_CHANGE_CIPHER_SPEC;
        }
        boolean hasPresentState = false;
        while (!this.upcomingStates.isEmpty()) {
            final HandshakeState handshakeState = this.upcomingStates.pop();
            if (handshakeState == expectedState) {
                hasPresentState = true;
                break;
            }
            if (!handshakeState.isOptional) {
                throw new SSLProtocolException(exceptionMsg);
            }
        }
        if (!hasPresentState) {
            throw new SSLProtocolException(exceptionMsg);
        }
        if (HandshakeStateManager.debugIsOn) {
            for (final HandshakeState handshakeState2 : this.upcomingStates) {
                System.out.println("upcoming handshake states: " + handshakeState2);
            }
            for (final HandshakeState handshakeState2 : this.alternatives) {
                System.out.println("upcoming handshake alternative state: " + handshakeState2);
            }
        }
    }
    
    private static String toString(final byte handshakeType) {
        String s = HandshakeStateManager.handshakeTypes.get(handshakeType);
        if (s == null) {
            s = "unknown";
        }
        return s + "[" + handshakeType + "]";
    }
    
    static {
        debugIsOn = (Handshaker.debug != null && Debug.isOn("handshake") && Debug.isOn("verbose"));
        (handshakeTypes = new HashMap<Byte, String>(8)).put(0, "hello_request");
        HandshakeStateManager.handshakeTypes.put((Byte)1, "client_hello");
        HandshakeStateManager.handshakeTypes.put((Byte)2, "server_hello");
        HandshakeStateManager.handshakeTypes.put((Byte)11, "certificate");
        HandshakeStateManager.handshakeTypes.put((Byte)12, "server_key_exchange");
        HandshakeStateManager.handshakeTypes.put((Byte)14, "server_hello_done");
        HandshakeStateManager.handshakeTypes.put((Byte)15, "certificate_verify");
        HandshakeStateManager.handshakeTypes.put((Byte)16, "client_key_exchange");
        HandshakeStateManager.handshakeTypes.put((Byte)20, "finished");
    }
    
    enum HandshakeState
    {
        HS_HELLO_REQUEST("hello_request", (byte)0), 
        HS_CLIENT_HELLO("client_hello", (byte)1), 
        HS_SERVER_HELLO("server_hello", (byte)2), 
        HS_SERVER_CERTIFICATE("server certificate", (byte)11), 
        HS_SERVER_KEY_EXCHANGE("server_key_exchange", (byte)12, true), 
        HS_CERTIFICATE_REQUEST("certificate_request", (byte)13, true), 
        HS_SERVER_HELLO_DONE("server_hello_done", (byte)14), 
        HS_CLIENT_CERTIFICATE("client certificate", (byte)11, true), 
        HS_CLIENT_KEY_EXCHANGE("client_key_exchange", (byte)16), 
        HS_CERTIFICATE_VERIFY("certificate_verify", (byte)15, true), 
        HS_CLIENT_CHANGE_CIPHER_SPEC("client change_cipher_spec", (byte)(-1)), 
        HS_CLIENT_FINISHED("client finished", (byte)20), 
        HS_SERVER_CHANGE_CIPHER_SPEC("server change_cipher_spec", (byte)(-1)), 
        HS_SERVER_FINISHED("server finished", (byte)20);
        
        final String description;
        final byte handshakeType;
        final boolean isOptional;
        
        private HandshakeState(final String description, final byte handshakeType) {
            this.description = description;
            this.handshakeType = handshakeType;
            this.isOptional = false;
        }
        
        private HandshakeState(final String description, final byte handshakeType, final boolean isOptional) {
            this.description = description;
            this.handshakeType = handshakeType;
            this.isOptional = isOptional;
        }
        
        @Override
        public String toString() {
            return this.description + "[" + this.handshakeType + "]" + (this.isOptional ? "(optional)" : "");
        }
    }
}
