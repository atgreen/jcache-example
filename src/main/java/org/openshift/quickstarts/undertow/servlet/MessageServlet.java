/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2014, 2019 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.openshift.quickstarts.undertow.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.cache.Cache;

// import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;

/**
 * @author Stuart Douglas
 */
public class MessageServlet extends HttpServlet {

    public static final String MESSAGE = "message";

    private String message;
    //    RemoteCache<String, String> cache;
    Cache cache;
    RemoteCacheManager remoteCacheManager;
	
    private static final String HOT_ROD_ENDPOINT_SERVICE = "cache-service";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "Redhat1!";
    
    @Override
    public void init(final ServletConfig config) throws ServletException {
        super.init(config);
        message = config.getInitParameter(MESSAGE);

	Configuration c = new ConfigurationBuilder()
	    .addServer()
	    .host(HOT_ROD_ENDPOINT_SERVICE)
	    .port(11222)
	    .security().authentication()
	    .enable()
	    .realm("ApplicationRealm")
	    .username(USERNAME)
	    .password(PASSWORD)
	    .serverName("cache-service")
	    .ssl()
	    .enable()
	    .trustStorePath("/var/run/secrets/kubernetes.io/serviceaccount/service-ca.crt")
	    .build();
	
	remoteCacheManager = new RemoteCacheManager(c);
	cache = remoteCacheManager.getCache();	
    }

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter writer = resp.getWriter();
        writer.write(message + "\n");
	writer.write(req.getMethod());
	Enumeration<String> headerNames = req.getHeaderNames();
	while(headerNames.hasMoreElements()) {
	    String headerName = headerNames.nextElement();
	    writer.write("Header Name - " + headerName + ", Value - " + req.getHeader(headerName) + "\n");
	}
	Enumeration<String> params = req.getParameterNames(); 
	while(params.hasMoreElements()){
	    String paramName = params.nextElement();
	    writer.write("Parameter Name - "+paramName+", Value - "+req.getParameter(paramName)+"\n");
	}        
	writer.close();
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
