package com.ericsson.sap.sscws;


import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionBeanHome;
import com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionBeanRemote;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import java.rmi.RemoteException;
import java.util.Hashtable;

public class SscwsSapServerConnection implements com.ericsson.sap.connection.SapServerConnection {

    /**
     * Name of the configurable material.
     */
    private static final String MATERIAL_NAME = "FAP23002"; //OBJ_KEY

    /**
     * Logical system of the KBRTV to be used.
     */
    private static final String LOGICAL_SYSTEM = "ESAPS08400";

    private static final String FBS_WORKPLACE_TYPE_NS = "FBS_WORKPLACE_TYPE_NS";
    private static final String FBS_NO_WORKPLACES_NF = "FBS_NO_WORKPLACES_NF";
    private static Hashtable<String, String> jndiCtxProps;
    private IConfigSessionBeanRemote configSessionBean;
    private static IConfigSessionBeanHome configSessionBeanHome;


    private String host;

    /**
     * Overloaded Constructor. Runs {@link #setUpSapServerConnection} with the supplied credentials.
     *
     * @param host     hostname:p4-port
     * @param userName username
     * @param password password
     * @throws Exception
     */
    public SscwsSapServerConnection(String host, String userName, String password) throws Exception {
        // Try to set up a connection
        setUpSapServerConnection(host, userName, password);
    }

    /**
     * Sets up a connection to the IPC server.
     * Creates a connection to a SAP SSC EJB-IPC Server with the supplied
     * credentials.
     *
     * @param host     hostname:p4-port
     * @param userName username
     * @param password password
     * @throws Exception
     */
    private void setUpSapServerConnection(String host, String userName, String password) throws Exception {
        // create the initial context for the JNDI lookup using properties
        jndiCtxProps = new Hashtable<String, String>();
        jndiCtxProps.put(Context.INITIAL_CONTEXT_FACTORY, "com.sap.engine.services.jndi.InitialContextFactoryImpl");
        jndiCtxProps.put(Context.URL_PKG_PREFIXES, "com.sap.engine.services");

        jndiCtxProps.put(Context.PROVIDER_URL, host);
        jndiCtxProps.put(Context.SECURITY_PRINCIPAL, userName);
        jndiCtxProps.put(Context.SECURITY_CREDENTIALS, password);
        InitialContext ctx = new InitialContext(jndiCtxProps);

        // execute the lookup using the object name as it can be found in the EJB Explorer of the NW AS Java
        String lookupString = "ejb:/appName=sap.com/cdev~fbs_slc_java, jarName=sap.com~cdev~fbs_slc_jee~ejbjar.jar, beanName=ConfigSessionBeanBean, interfaceName=com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionBeanHome";
        Object remoteHome = ctx.lookup(lookupString);

        // obtain instance of the bean object
        configSessionBeanHome = (IConfigSessionBeanHome) PortableRemoteObject.narrow(remoteHome,
                IConfigSessionBeanHome.class);
        //String kboSwitch = "true";
        String sessionId = null;
        String dataSourceName = null;
        boolean onDemandCsticCreation = false;
        //String languageISOCode = "EN";
        configSessionBean = create(sessionId, dataSourceName, onDemandCsticCreation);
        if (configSessionBean == null) {
            System.out.println("Unable to create configSessionBean");
        }

        this.host = host;

    }

    /**
     * Returns the XML representation of the current configuration, by using
     * {@linkplain com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionClientRemote#getConfigItemInfoXML}
     * .
     *
     * @param configXml {@code String} containing the XML to be recreated
     * @return {@code String} containing a XML representation of the current
     * configuration.
     * @throws IpcCommandException
     * @throws RemoteException
     */
    @Override
    public String recreateConfigFromXml(String configXml)
            throws IpcCommandException, RemoteException {
        String cfgId = configSessionBean.recreateConfigFromXml(configXml);
        return configSessionBean.getConfigItemInfoXML(false);
    }

    private IConfigSessionBeanRemote create(String sessionId,
                                            String dataSourceName, boolean onDemandCsticCreation) throws CreateException, RemoteException {
        String kboSwitch = "true";
        String languageISOCode = "EN";
        return configSessionBeanHome.create(kboSwitch, sessionId, dataSourceName, onDemandCsticCreation,
                languageISOCode);
    }
}
