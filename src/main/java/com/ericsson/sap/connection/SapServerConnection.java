package com.ericsson.sap.connection;

/**
 * Created by vanja on 2017-03-16.
 */
public interface SapServerConnection {
    public String recreateConfigFromXml(String configXml) throws Exception;
}
