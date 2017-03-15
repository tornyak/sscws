package com.tornyak.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;


@Path("configure")
public class DataForwarder {
    private static final Logger LOGGER = Logger.getLogger(DataForwarder.class.getName());

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String sendXmlConfigToSap(
            @DefaultValue("Q") @QueryParam("environment") String environment,
            String body) throws Exception {
        LOGGER.info("sendXmlConfigToSap: environment=" + environment);
        try {
            SapServerConnection sapServerConnection =
                    SapServerConnection.createServerConnectionFromPropertyFile(environment.toUpperCase());
            return sapServerConnection.recreateConfigFromXml(body);
        } catch (Exception e) {
            LOGGER.info("sendXmlConfigToSap failed: " + e.getMessage());
            return e.getMessage();
        }
    }
}
