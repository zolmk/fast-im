package com.feiyu.core.communicate.config;

import java.util.Properties;

/**
 * @author zhufeifei 2023/9/9
 **/

public class SslConfig {
    private String jksPath;
    private String keyStorePassword;
    private String keyStoreAlias;
    private String privatePassword;
    private String privateKeyAlias;
    private String cerPath;
    private String certAlias;


    public SslConfig() {
    }

    public void load(Properties properties) {
        this.jksPath = properties.getProperty("chat-server.ssl.jks-path", "");
        this.keyStorePassword = properties.getProperty("chat-server.ssl.keystore-password", "");
        this.keyStoreAlias = properties.getProperty("chat-server.ssl.keystore-alias", "");
        this.privateKeyAlias = properties.getProperty("chat-server.ssl.private-alias","");
        this.certAlias = properties.getProperty("chat-server.ssl.cert-alias", "");
        this.cerPath = properties.getProperty("chat-server.ssl.cer-path", "");
        this.privatePassword = properties.getProperty("chat-server.ssl.private-password", "");
    }

    public void setJksPath(String jksPath) {
        this.jksPath = jksPath;
    }

    public String getJksPath() {
        return this.jksPath;
    }

    public String getKeyStoreAlias() {
        return keyStoreAlias;
    }

    public void setKeyStoreAlias(String keyStoreAlias) {
        this.keyStoreAlias = keyStoreAlias;
    }

    public String getCertAlias() {
        return certAlias;
    }

    public void setCertAlias(String certAlias) {
        this.certAlias = certAlias;
    }

    public String getPrivateKeyAlias() {
        return privateKeyAlias;
    }

    public void setPrivateKeyAlias(String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }

    public String getKeyStorePassword() {
        return keyStorePassword;
    }

    public void setKeyStorePassword(String keyStorePassword) {
        this.keyStorePassword = keyStorePassword;
    }

    public String getPrivatePassword() {
        return privatePassword;
    }

    public void setPrivatePassword(String privatePassword) {
        this.privatePassword = privatePassword;
    }

    public String getCerPath() {
        return cerPath;
    }

    public void setCerPath(String cerPath) {
        this.cerPath = cerPath;
    }
}
