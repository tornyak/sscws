package com.ericsson.sap.stub;

import com.ericsson.sap.connection.SapServerConnection;

/**
 * Created by vanja on 2017-03-16.
 */
public class StubSapServerConnection implements SapServerConnection {

    @Override
    public String recreateConfigFromXml(String configXml) throws Exception {
        return configXml;
    }
}
