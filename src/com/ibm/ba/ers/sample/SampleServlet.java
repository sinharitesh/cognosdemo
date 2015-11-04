package com.ibm.ba.ers.sample;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.Map;
import java.util.Set;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.annotation.Resource;

import org.apache.commons.json.JSONArray;
import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;

public class SampleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final int BUFFER_SIZE = 32767;
    
    private static ERSProxy m_ersConnection = null;
       
    private static String m_schemaName = null;

    private void getServiceInfo() {
    	if (m_ersConnection == null) {
			String bundleUri = null;
			String reportingUri = null;
			String reportingUserId = null;
			String reportingPassword = null;
			String jdbcUri = null;
            String dsUserId = null;
            String dsPassword = null;
            boolean isAnalyticsWarehouse = false;

			Map<String, String> env = System.getenv();
			String vcap = env.get("VCAP_SERVICES");
			if (vcap == null) {
				System.out.println("No VCAP_SERVICES found");
				return;
			}

			try {
				JSONObject services = new JSONObject(vcap);

				@SuppressWarnings("unchecked")
				Set<String> serviceList = services.keySet();
				for (String service : serviceList) {
					String name = service.toUpperCase();
					JSONObject credentials = (JSONObject) ((JSONObject) ((JSONArray) services
							.get(service)).get(0)).get("credentials");
					if (name.indexOf("ERSERVICE") != -1) {
						reportingUri = (String) credentials.get("url");
						reportingUserId = (String) credentials.get("userid");
						reportingPassword = (String) credentials.get("password");
					} else if ((name.indexOf("CLOUDANT") != -1) && (bundleUri == null)) {
						bundleUri = (String) credentials.get("url");
					} else if ((name.indexOf("MONGOLAB") != -1) && (bundleUri == null)) {
						bundleUri = (String) credentials.get("uri");
					} else if ((name.indexOf("MONGO") != -1) && (bundleUri == null)) {
						bundleUri = (String) credentials.get("url");
					} else if ((name.indexOf("SQLDB") != -1) && (jdbcUri == null)) {
						jdbcUri = (String) credentials.get("jdbcurl");
						dsUserId = (String) credentials.get("username");
                        dsPassword = (String) credentials.get("password");
					} else if ((name.indexOf("ANALYTICSWAREHOUSE") != -1 || name.indexOf("DASHDB") != -1) && (jdbcUri == null)) {
						jdbcUri = (String) credentials.get("jdbcurl");
						dsUserId = (String) credentials.get("username");
                        dsPassword = (String) credentials.get("password");
                        isAnalyticsWarehouse = true;
					}
				}

				if (reportingUri == null) {
					System.err.println("No reporting service found");
					return;
				}

				if (bundleUri == null) {
					System.err.println("No bundle storage service found");
					return;
				}
				
				if (jdbcUri == null) {
                    System.err.println("No SQL datasource service found");
                    return;
                } 


				synchronized (this) {
					System.out.println("ERSConnection "+ reportingUri+","+reportingUserId+","+reportingPassword+","+bundleUri);
					m_ersConnection = new ERSProxy(reportingUri, reportingUserId, reportingPassword, bundleUri);
					try {
						m_ersConnection.connect();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
                  
                    if(isAnalyticsWarehouse) {
                    	m_schemaName = dsUserId.toUpperCase();
                    } else {
                    	m_schemaName = "DB2INST1";
                    }

				}
			} catch (JSONException e) {
			}
		}
    }

    private void loadStaticPage(String page, HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        byte[] buffer = new byte[BUFFER_SIZE];

        try {
            String file = getServletContext().getRealPath("/")
                    + "/WEB-INF/" + page;
            FileInputStream in = new FileInputStream(file);
            ServletOutputStream out = response.getOutputStream();

            int read = 0;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
            in.close();
            
            response.setStatus(200);
        } catch (IOException e) {
            response.setStatus(404);
        }
    }

    public SampleServlet() {
        super();
        getServiceInfo();
    }


    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo.startsWith("/ers/v1")) {
            if (m_ersConnection != null) {
                m_ersConnection.doPost(request, response);
            } else {
                response.setStatus(404);
            }
        } else {
            response.setStatus(404);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.compareTo("/") == 0) {
			loadStaticPage("index.html", request, response);
		} else if (pathInfo.compareTo("/overview") == 0) {
			loadStaticPage("sample.html", request, response);
		} else if (pathInfo.startsWith("/ers/v1")) {
            if (m_ersConnection != null) {
                m_ersConnection.doGet(request, response);
            } else {
                response.setStatus(404);
            }
        } else {
            loadStaticPage(pathInfo, request, response);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        if (pathInfo.startsWith("/ers/v1")) {
            if (m_ersConnection != null) {
                m_ersConnection.doDelete(request, response);
            } else {
                response.setStatus(404);
            }
        }
    }

    @Override
    public void destroy() {
        if (m_ersConnection != null) {
            try {
                m_ersConnection.disconnect();
            } catch (Exception e) {
            }
        }
    }
          
}
