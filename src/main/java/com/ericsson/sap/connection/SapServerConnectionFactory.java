package com.ericsson.sap.connection;

/**
 * Created by vanja on 2017-03-16.
 */
public interface SapServerConnectionFactory {
    SapServerConnection serverConnectionfromPropertyFile(String environment) throws Exception;
    SapServerConnection serverConnection(String host, String userName, String password) throws Exception;
}
