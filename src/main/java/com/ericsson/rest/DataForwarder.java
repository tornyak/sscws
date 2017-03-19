package com.ericsson.rest;

import com.ericsson.sap.connection.SapServerConnection;
import com.ericsson.sap.connection.SapServerConnectionFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.logging.Logger;


@Path("configure")
public class DataForwarder {
    private static final Logger LOGGER = Logger.getLogger(DataForwarder.class.getName());

    SapServerConnectionFactory sapServerConnectionFactory;

    public DataForwarder(SapServerConnectionFactory sapServerConnectionFactory) {
        this.sapServerConnectionFactory = sapServerConnectionFactory;
    }

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String sendXmlConfigToSap(
            @DefaultValue("Q") @QueryParam("environment") String environment,
            String body) throws Exception {
        LOGGER.info("sendXmlConfigToSap: environment=" + environment);
        LOGGER.info(body);
        try {
            SapServerConnection sapServerConnection =
                    sapServerConnectionFactory.serverConnectionfromPropertyFile(environment.toUpperCase());
            return sapServerConnection.recreateConfigFromXml(body);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.info("sendXmlConfigToSap failed: " + e.getMessage());
            return e.getMessage();
        }
    }
}
