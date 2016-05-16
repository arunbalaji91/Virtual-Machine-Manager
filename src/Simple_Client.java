//Simple_Client.java               				asivak01@syr.edu       
/*-------------------------------------------------------------*/

/*
 * ******************************************************
 * Copyright VMware, Inc. 2010-2012.  All Rights Reserved.
 * ******************************************************
 *
 * DISCLAIMER. THIS PROGRAM IS PROVIDED TO YOU "AS IS" WITHOUT
 * WARRANTIES OR CONDITIONS # OF ANY KIND, WHETHER ORAL OR WRITTEN,
 * EXPRESS OR IMPLIED. THE AUTHOR SPECIFICALLY # DISCLAIMS ANY IMPLIED
 * WARRANTIES OR CONDITIONS OF MERCHANTABILITY, SATISFACTORY # QUALITY,
 * NON-INFRINGEMENT AND FITNESS FOR A PARTICULAR PURPOSE.
 */

//package com.vmware.general;

//import com.vmware.common.annotations.Action;
import com.vmware.common.annotations.Sample;
import com.vmware.connection.ConnectedVimServiceBase;
import com.vmware.vim25.*;

//import com.vmware.connection.Connection;
import com.vmware.sso.client.utils.TrustAllTrustManager;

import java.util.ArrayList;
import java.util.List;

//import java.util.Date;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.xml.ws.BindingProvider;

/**
 * <pre>
 * SimpleClient
 *
 * This sample lists the inventory contents (managed entities)
 *
 * <b>Parameters:</b>
 * url          [required] : url of the web service
 * username     [required] : username for the authentication
 * password     [required] : password for the authentication
 *
 * <b>Command Line:</b>
 * run.bat com.vmware.general.SimpleClient
 * --url [webserviceurl] --username [username] --password [password]
 * </pre>
 */



@Sample(name = "simple-client", description = "This sample lists the inventory contents (managed entities)")
public class Simple_Client extends ConnectedVimServiceBase {

    private ManagedObjectReference propCollectorRef;
       
    /**
     * Uses the new RetrievePropertiesEx method to emulate the now deprecated
     * RetrieveProperties method
     *
     * @param listpfs
     * @return list of object content
     * @throws Exception
     */

    List<ObjectContent> retrievePropertiesAllObjects(
            List<PropertyFilterSpec> listpfs) throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {

        RetrieveOptions propObjectRetrieveOpts = new RetrieveOptions();

        List<ObjectContent> listobjcontent = new ArrayList<ObjectContent>();

        RetrieveResult rslts =
                vimPort.retrievePropertiesEx(propCollectorRef, listpfs,
                        propObjectRetrieveOpts);
        if (rslts != null && rslts.getObjects() != null
                && !rslts.getObjects().isEmpty()) {
            listobjcontent.addAll(rslts.getObjects());
        }
        String token = null;
        if (rslts != null && rslts.getToken() != null) {
            token = rslts.getToken();
        }
        while (token != null && !token.isEmpty()) {
            rslts =
                    vimPort.continueRetrievePropertiesEx(propCollectorRef, token);
            token = null;
            if (rslts != null) {
                token = rslts.getToken();
                if (rslts.getObjects() != null && !rslts.getObjects().isEmpty()) {
                    listobjcontent.addAll(rslts.getObjects());
                }
            }
        }

        return listobjcontent;
    }

    void getAndPrintInventoryContents() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        TraversalSpec resourcePoolTraversalSpec = new TraversalSpec();
        resourcePoolTraversalSpec.setName("resourcePoolTraversalSpec");
        resourcePoolTraversalSpec.setType("ResourcePool");
        resourcePoolTraversalSpec.setPath("resourcePool");
        resourcePoolTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec rpts = new SelectionSpec();
        rpts.setName("resourcePoolTraversalSpec");
        resourcePoolTraversalSpec.getSelectSet().add(rpts);

        TraversalSpec computeResourceRpTraversalSpec = new TraversalSpec();
        computeResourceRpTraversalSpec.setName("computeResourceRpTraversalSpec");
        computeResourceRpTraversalSpec.setType("ComputeResource");
        computeResourceRpTraversalSpec.setPath("resourcePool");
        computeResourceRpTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec rptss = new SelectionSpec();
        rptss.setName("resourcePoolTraversalSpec");
        computeResourceRpTraversalSpec.getSelectSet().add(rptss);

        TraversalSpec computeResourceHostTraversalSpec = new TraversalSpec();
        computeResourceHostTraversalSpec
                .setName("computeResourceHostTraversalSpec");
        computeResourceHostTraversalSpec.setType("ComputeResource");
        computeResourceHostTraversalSpec.setPath("host");
        computeResourceHostTraversalSpec.setSkip(Boolean.FALSE);

        TraversalSpec datacenterHostTraversalSpec = new TraversalSpec();
        datacenterHostTraversalSpec.setName("datacenterHostTraversalSpec");
        datacenterHostTraversalSpec.setType("Datacenter");
        datacenterHostTraversalSpec.setPath("hostFolder");
        datacenterHostTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftspec = new SelectionSpec();
        ftspec.setName("folderTraversalSpec");
        datacenterHostTraversalSpec.getSelectSet().add(ftspec);

        TraversalSpec datacenterVmTraversalSpec = new TraversalSpec();
        datacenterVmTraversalSpec.setName("datacenterVmTraversalSpec");
        datacenterVmTraversalSpec.setType("Datacenter");
        datacenterVmTraversalSpec.setPath("vmFolder");
        datacenterVmTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftspecs = new SelectionSpec();
        ftspecs.setName("folderTraversalSpec");
        datacenterVmTraversalSpec.getSelectSet().add(ftspecs);

        TraversalSpec folderTraversalSpec = new TraversalSpec();
        folderTraversalSpec.setName("folderTraversalSpec");
        folderTraversalSpec.setType("Folder");
        folderTraversalSpec.setPath("childEntity");
        folderTraversalSpec.setSkip(Boolean.FALSE);
        SelectionSpec ftrspec = new SelectionSpec();
        ftrspec.setName("folderTraversalSpec");
        List<SelectionSpec> ssarray = new ArrayList<SelectionSpec>();
        ssarray.add(ftrspec);
        ssarray.add(datacenterHostTraversalSpec);
        ssarray.add(datacenterVmTraversalSpec);
        ssarray.add(computeResourceRpTraversalSpec);
        ssarray.add(computeResourceHostTraversalSpec);
        ssarray.add(resourcePoolTraversalSpec);

        folderTraversalSpec.getSelectSet().addAll(ssarray);
        PropertySpec props = new PropertySpec();
        props.setAll(Boolean.FALSE);
        props.getPathSet().add("name");
        props.setType("ManagedEntity");
        List<PropertySpec> propspecary = new ArrayList<PropertySpec>();
        propspecary.add(props);

        PropertyFilterSpec spec = new PropertyFilterSpec();
        spec.getPropSet().addAll(propspecary);

        spec.getObjectSet().add(new ObjectSpec());
        spec.getObjectSet().get(0).setObj(rootRef);
        spec.getObjectSet().get(0).setSkip(Boolean.FALSE);
        spec.getObjectSet().get(0).getSelectSet().add(folderTraversalSpec);

        List<PropertyFilterSpec> listpfs = new ArrayList<PropertyFilterSpec>(1);
        listpfs.add(spec);
        List<ObjectContent> listobjcont = retrievePropertiesAllObjects(listpfs);

        // If we get contents back. print them out.
        if (listobjcont != null) {
            ObjectContent oc = null;
            ManagedObjectReference mor = null;
            DynamicProperty pc = null;
            for (int oci = 0; oci < listobjcont.size(); oci++) {
                oc = listobjcont.get(oci);
                mor = oc.getObj();

                List<DynamicProperty> listdp = oc.getPropSet();
                System.out.println("Object Type : " + mor.getType());
                System.out.println("Reference Value : " + mor.getValue());

                if (listdp != null) {
                    for (int pci = 0; pci < listdp.size(); pci++) {
                        pc = listdp.get(pci);
                        System.out.println("   Property Name : " + pc.getName());
                        if (pc != null) {
                            if (!pc.getVal().getClass().isArray()) {
                                System.out
                                        .println("   Property Value : " + pc.getVal());
                            } else {
                                List<Object> ipcary = new ArrayList<Object>();
                                ipcary.add(pc.getVal());
                                System.out.println("Val : " + pc.getVal());
                                for (int ii = 0; ii < ipcary.size(); ii++) {
                                    Object oval = ipcary.get(ii);
                                    if (oval.getClass().getName()
                                            .indexOf("ManagedObjectReference") >= 0) {
                                        ManagedObjectReference imor =
                                                (ManagedObjectReference) oval;

                                        System.out.println("Inner Object Type : "
                                                + imor.getType());
                                        System.out.println("Inner Reference Value : "
                                                + imor.getValue());
                                    } else {
                                        System.out.println("Inner Property Value : "
                                                + oval);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            System.out.println("No Managed Entities retrieved!");
        }
    }

//    @Action
    /*
    public void main() throws RuntimeFaultFaultMsg, InvalidPropertyFaultMsg {
        propCollectorRef = serviceContent.getPropertyCollector();
        getAndPrintInventoryContents();
    }
    */
    

    
    /**
     * Runs the TestConnect sample code, which establishes a connection to a vCenter or ESX server
     * and prints out a little information about that server. Run with a command similar to this:<br>
     * <code>java -cp vim25.jar;TestConnection.jar com.vmware.scia.complete.TestConnection <i>ip_or_name</i> <i>user</i> <i>password</i></code>
     * <br>
     * <code>java -cp vim25.jar;TestConnection.jar com.vmware.scia.complete.TestConnection 10.20.30.40 JoeUser JoePasswd</code>
     * <br>
     * More details in the TestConnection_ReadMe.txt file.
     */
    public static void main(String[] args) {

        if (args.length != 4) {
            System.out.println("Wrong number of arguments, must provide three arguments:");
            System.out.println("[1] The server name or IP address");
            System.out.println("[2] The user name to log in as");
            System.out.println("[3] The password to use");
            System.exit(1);
        }

        // Server URL and credentials.
        String serverName = args[0];
        String userName = args[1];
        String password = args[2];
        String task = args[3];
        String url = "https://" + serverName + "/sdk/vimService"; // ** check for bad serverName

        com.vmware.vim25.VimPortType vimPort = null;
        com.vmware.vim25.ServiceContent serviceContent = null;

        try {
            // Variables of the following types for access to the API methods
            // and to the vSphere inventory.
            // -- ManagedObjectReference for the ServiceInstance on the Server
            // -- VimService for access to the vSphere Web service
            // -- VimPortType for access to methods
            // -- ServiceContent for access to managed object services
            com.vmware.vim25.ManagedObjectReference servicesInstance = new com.vmware.vim25.ManagedObjectReference();
            com.vmware.vim25.VimService vimService;

            // Declare a host name verifier that will automatically enable
            // the connection. The host name verifier is invoked during
            // the SSL handshake.
            javax.net.ssl.HostnameVerifier verifier = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            // Create the trust manager.
            javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
            javax.net.ssl.TrustManager trustManager = new TrustAllTrustManager();
            trustAllCerts[0] = trustManager;

            // Create the SSL context
            javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext.getInstance("SSL");

            // Create the session context
            javax.net.ssl.SSLSessionContext sslsc = sc.getServerSessionContext();

            // Initialize the contexts; the session context takes the trust manager.
            sslsc.setSessionTimeout(0);
            sc.init(null, trustAllCerts, null);

            // Use the default socket factory to create the socket for the secure connection
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            // Set the default host name verifier to enable the connection.
            HttpsURLConnection.setDefaultHostnameVerifier(verifier);

            // Set up the manufactured managed object reference for the ServiceInstance
            servicesInstance.setType("ServiceInstance");
            servicesInstance.setValue("ServiceInstance");

            // Create a VimService object to obtain a VimPort binding provider.
            // The BindingProvider provides access to the protocol fields
            // in request/response messages. Retrieve the request context
            // which will be used for processing message requests.
            
            //UserSession userSession = new UserSession();///
            vimService = new com.vmware.vim25.VimService();
            vimPort = vimService.getVimPort();
            Map<String, Object> ctxt = ((BindingProvider) vimPort).getRequestContext();

            // Store the Server URL in the request context and specify true
            // to maintain the connection between the client and server.
            // The client API will include the Server's HTTP cookie in its
            // requests to maintain the session. If you do not set this to true,
            // the Server will start a new session with each request.
            ctxt.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
            ctxt.put(BindingProvider.SESSION_MAINTAIN_PROPERTY, true);
            
            UserSession userSession = new UserSession();
            // Retrieve the ServiceContent object and login
            serviceContent = vimPort.retrieveServiceContent(servicesInstance);
            try {
            	userSession = vimPort.login(serviceContent.getSessionManager(), userName, password, null);
            } catch (com.vmware.vim25.InvalidLoginFaultMsg ilfm) {
                System.out
                        .printf("Either your username (%s) was wrong, or the password (%s) was not the right one.",
                                userName, password);
                System.out.println(ilfm.getMessage());
                System.exit(0);
            }
            // Notice that all other exceptions are caught down below, and are handled very
            // genericly.

            // print out the product name, server type, and product version
            System.out.println(serviceContent.getAbout().getFullName());
            System.out.printf("Server type is %s\n", serviceContent.getAbout().getApiType());
            System.out.printf("API version is %s", serviceContent.getAbout().getVersion());
            
            if (task.equalsIgnoreCase("1"))
            {
            	RealTime obj = new RealTime();
            
            	obj.run(serviceContent, vimPort);
            }
            else if (task.equalsIgnoreCase("2"))
            {
            	History obj_his = new History();
            
            	obj_his.run(serviceContent, vimPort,userSession ); 
            }
             

        } catch (Exception e) {
            System.err.println("Sample code failed ");
            e.printStackTrace();
            System.exit(1);
        } finally {
            // Always close connectionl, even if errors occure.
            if (vimPort != null && serviceContent != null) {
                try {
                    vimPort.logout(serviceContent.getSessionManager());
                } catch (com.vmware.vim25.RuntimeFaultFaultMsg rffm) {
                    System.out.println("Sample code failed while logging out after a previous failure.");
                    rffm.printStackTrace();
                }
            }
        }

    }
}
