package com.tornyak.rest;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;


@Path("configure")
public class DataForwarder {
    private static final Logger LOGGER = Logger.getLogger(DataForwarder.class.getName());
    private static AtomicLong requestCnt = new AtomicLong(0);

    @POST
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    public String configure(String body) throws InterruptedException {
        long cnt = requestCnt.incrementAndGet();
        LOGGER.info("Request start: " + cnt);
        return body;
    }
}
