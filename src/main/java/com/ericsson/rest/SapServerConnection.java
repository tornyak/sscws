package com.ericsson.rest;

//import static ssc.regression.utilities.other.RegressionTestingUtility.CJD;

import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.ejb.CreateException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.client.ICsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDecompTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IDeltaBean;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IInstanceTypeData;
import com.sap.custdev.projects.fbs.slc.cfg.client.IKnowledgeBaseData;
import com.sap.custdev.projects.fbs.slc.cfg.client.ISpecializationData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticData;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticHeader;
import com.sap.custdev.projects.fbs.slc.cfg.command.beans.CsticValueData;
import com.sap.custdev.projects.fbs.slc.cfg.exception.IpcCommandException;
import com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionBeanHome;
import com.sap.custdev.projects.fbs.slc.ejb.IConfigSessionBeanRemote;

public class SapServerConnection {

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

    private static IKnowledgeBaseData[] knowledgebaseData;
    private static String configId;
    private static IInstanceData rootInstance;
    private static IDecompTypeData[] decompDatas;
    private static IDeltaBean deltaBean;
    private static IInstanceTypeData[] instanceTypeData;
    private static Map<String, String> context;
    private static ISpecializationData[] specializationData;
    private static byte[] configBlob;
    private static IConfigSessionBeanHome configSessionBeanHome;
    private static byte[] attachmentData;
    private static IInstanceData[] fbsComputerInstances;
    private static String richXMLAsString;
    private static String[] configResultXMLAsStringArrays;
    private static IInstanceData[] instances;
    //public static String ConnectionFileServer = CJD + "Credentials_server.cfg";

    private String host;

    /**
     * Overloaded Constructor. Runs {@link #setUpSapServerConnection} with the supplied credentials.
     *
     * @param host     hostname:p4-port
     * @param userName username
     * @param password password
     * @throws Exception
     */
    public SapServerConnection(String host, String userName, String password) throws Exception {
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

    IKnowledgeBaseData[] findKnowledgeBases(String objType, String material_name, String logical_system,
                                            boolean withKbProfiles)
            throws RemoteException, IpcCommandException {
        knowledgebaseData = configSessionBean.findKnowledgeBases(objType, material_name, logical_system, null, null,
                null, null, null, true);
        return knowledgebaseData;
    }

    public String createConfig(String kbName,
                               String kbVersion, String kbProfile) throws IpcCommandException,
            RemoteException {
        String rfcConfigId = null; //let the configurator create the id
        String productId = null;
        String productType = null;
        String kbLogSys = knowledgebaseData[0].getKbLogsys();
        //		String kbName = knowledgebaseData[0].getKbName();
        //		String kbVersion = knowledgebaseData[0].getKbVersion();
        //		String kbProfile = knowledgebaseData[0].getKbProfile();
        Integer kbIdInt = null; //knowledge base is already specified by the triple kbLogsys, kbName, kbVersion
        String kbDateStr = null; //is only required for the search when the knowledge base is not already given as input
        String kbBuild = null;
        String useTraceStr = null;
        Hashtable<String, String> context = null;
        boolean setRichConfigId = true;
        configId = configSessionBean.createConfig(rfcConfigId, productId, productType, kbLogSys, kbName, kbVersion,
                kbProfile, kbIdInt, kbDateStr, kbBuild, useTraceStr, context, setRichConfigId, false);
        return configId;

    }

    public final String getConfigItemInfoXML(boolean isZipped) throws RemoteException, IpcCommandException {
        return configSessionBean.getConfigItemInfoXML(false);
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
    public String recreateConfigFromXml(String configXml)
            throws IpcCommandException, RemoteException {
        //		Thread t = new Thread(new YourClassThatImplementsRunnable());
        //This part is reeeeeeeeeeally sloooooooow.
        String cfgId = configSessionBean.recreateConfigFromXml(configXml);

        //TODO configSessionBean.recreateConfigFromXml(paramDocument)
        //TODO configSessionBean.setConfigScenario(arg0);
        //		adjustValueForTest(cfgId);
        return configSessionBean.getConfigItemInfoXML(false);
    }

    private IConfigSessionBeanRemote create(String sessionId,
                                            String dataSourceName, boolean onDemandCsticCreation) throws CreateException, RemoteException {
        String kboSwitch = "true";
        String languageISOCode = "EN";
        return configSessionBeanHome.create(kboSwitch, sessionId, dataSourceName, onDemandCsticCreation,
                languageISOCode);
    }

    public void adjustValueForTest(String cfgId) throws IpcCommandException, RemoteException {
        ICsticHeader header = new CsticHeader();
        header.setCsticName("SW_TRACK");
        ICsticData[] valuesToSet = new ICsticData[1];
        valuesToSet[0] = new CsticData();
        valuesToSet[0].setCsticHeader(header);
        ICsticValueData[] datas = new ICsticValueData[1];
        datas[0] = new CsticValueData();
        String valueName = "L14B";
        datas[0].setValueName(valueName);
        valuesToSet[0].setCsticValues(datas);
        boolean mimeObj = true;

        String rfcConfigId = null; //let the configurator create the id
        String productId = null;
        String productType = null;
        String kbLogSys = knowledgebaseData[0].getKbLogsys();
        //		String kbName = knowledgebaseData[0].getKbName();
        //		String kbVersion = knowledgebaseData[0].getKbVersion();
        //		String kbProfile = knowledgebaseData[0].getKbProfile();
        String kbName = "RBS_6000_P";
        String kbVersion = "001:010:003";
        String kbProfile = "4";
        Integer kbIdInt = null; //knowledge base is already specified by the triple kbLogsys, kbName, kbVersion
        String kbDateStr = null; //is only required for the search when the knowledge base is not already given as input
        String kbBuild = null;
        String useTraceStr = null;
        Hashtable<String, String> context = null;
        boolean setRichConfigId = true;
        cfgId = configSessionBean.createConfig(rfcConfigId, productId, productType, kbLogSys, kbName, kbVersion,
                kbProfile, kbIdInt, kbDateStr, kbBuild, useTraceStr, context, setRichConfigId, false);

        rootInstance = configSessionBean.getRootInstance(cfgId, mimeObj);
        IDeltaBean deltaBean = configSessionBean.setCsticsValues(rootInstance.getInstId(), cfgId, false,
                valuesToSet);
        System.out.println(deltaBean.toString());
    }

    public String getHost() {
        return host;
    }


    /**
     * This method is used to create a SapServerConnection to a server by the
     * <br />
     * nickname (eg. Q). It can be used for Servers where address and<br />
     * credentials have been stored.
     *
     * @param environment {@link String} the nickname of the Server, eg. Q.
     * @return {@link SapServerConnection} a connection to the server.
     * @throws Exception
     */
    public static SapServerConnection createServerConnectionFromPropertyFile(String environment) throws Exception {
        environment.replaceAll(" ", "");
        Properties props = loadProperties("env_var.properties");
        String host = "";
        String userName = "";
        String password = "";

        host = props.getProperty("ServerAddress_" + environment) + ":" + props.getProperty("port_" + environment);
        userName = props.getProperty("userName_" + environment);
        password = props.getProperty("password_" + environment);

        return new SapServerConnection(host, userName, password);
    }

    private static Properties loadProperties(String propertiesFile) throws IOException {
        Properties props = new Properties();
        InputStream is = ClassLoader.getSystemResourceAsStream(propertiesFile);
        props.load(is);
        return props;
    }
}
