package com.ericsson.sap.stub;

import com.ericsson.sap.connection.SapServerConnection;
import com.ericsson.sap.connection.SapServerConnectionFactory;

/**
 * Created by vanja on 2017-03-16.
 */
public class StubSapServerConnectionFactory implements SapServerConnectionFactory {
    @Override
    public SapServerConnection serverConnectionfromPropertyFile(String environment) throws Exception {
        return new StubSapServerConnection();
    }

    @Override
    public SapServerConnection serverConnection(String host, String userName, String password) throws Exception {
        return new StubSapServerConnection();
    }
}
