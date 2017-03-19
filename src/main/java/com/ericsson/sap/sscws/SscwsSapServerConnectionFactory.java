package com.ericsson.sap.sscws;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by vanja on 2017-03-16.
 */
public class SscwsSapServerConnectionFactory implements com.ericsson.sap.connection.SapServerConnectionFactory {
    /**
     * This method is used to create a SscwsSapServerConnection to a server by the
     * <br />
     * nickname (eg. Q). It can be used for Servers where address and<br />
     * credentials have been stored.
     *
     * @param environment {@link String} the nickname of the Server, eg. Q.
     * @return {@link SscwsSapServerConnection} a connection to the server.
     * @throws Exception
     */
    @Override
    public SscwsSapServerConnection serverConnectionfromPropertyFile(String environment) throws Exception {
        environment.replaceAll(" ", "");
        Properties props = loadProperties("env_var.properties");
        String host = "";
        String userName = "";
        String password = "";

        host = props.getProperty("ServerAddress_" + environment) + ":" + props.getProperty("port_" + environment);
        userName = props.getProperty("userName_" + environment);
        password = props.getProperty("password_" + environment);

        return new SscwsSapServerConnection(host, userName, password);
    }

    @Override
    public SscwsSapServerConnection serverConnection(String host, String userName, String password) throws Exception {
        return new SscwsSapServerConnection(host, userName, password);
    }

    private Properties loadProperties(String propertiesFile) throws IOException {
        Properties props = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFile);
        props.load(is);
        return props;
    }
}
