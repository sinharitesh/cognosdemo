package com.ibm.ba.ers.sample;

import java.io.IOException;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.xml.bind.DatatypeConverter;

public class ERSProxy {
	private static final int BUFFER_SIZE = 32767;

	private String m_uri = null;
	private String m_bundleUri = null;
	private String m_authenticationInfo = null;
	private String m_cookies = "";

	private void doConnect() throws IOException {
		m_cookies="";
		int read = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		InputStream in = null;

		if (m_uri == null) {
			return;
		}

		String target = m_uri + "/ers/v1/connection";

		URL url = new URL(target);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");

		connection.setInstanceFollowRedirects(false);

		if (m_authenticationInfo != null) {
			connection.setRequestProperty(
					"Authorization",
					"Basic "
							+ DatatypeConverter
									.printBase64Binary(m_authenticationInfo
											.getBytes()));
			System.out.println("Authorization: "+"Basic "
							+ DatatypeConverter
									.printBase64Binary(m_authenticationInfo
											.getBytes()));
		}


		String request = "{ \"bundleUri\":\"" + m_bundleUri + "\"}";

		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("Content-Length",
				Integer.toString(request.length()));

		connection.setDoOutput(true);
		ByteArrayInputStream requestStream = new ByteArrayInputStream(
				request.getBytes());
		OutputStream out = connection.getOutputStream();
		read = 0;
		while ((read = requestStream.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		out.close();

		try {
			connection.connect();

			int status = 200;

			try {
				status = connection.getResponseCode();
				System.out.println("Connect Response Code: "+status);
			} catch (Exception e) {
				e.printStackTrace();
			}

			in = (status >= 400 ? connection.getErrorStream() : connection
					.getInputStream());
			if (in != null) {
				ByteArrayOutputStream response = new ByteArrayOutputStream();

				read = 0;
				while ((read = in.read(buffer)) > 0) {
					response.write(buffer, 0, read);
					System.out.print(new String(buffer, 0, read));
				}
				System.out.println("");

			
				// look in the response for any Set-Cookies
				String name = null;
				for (int idx = 1; (name = connection.getHeaderFieldKey(idx)) != null; idx++) {
					String value = connection.getHeaderField(idx);
					if (name.equals("Set-Cookie")) {
						String cookie = value.substring(0, value.indexOf(";")+1); // +1 to include semi-colon
						m_cookies += cookie;
					}
				}
				System.out.println("Cookie: "+m_cookies);
			}
			
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	private void doDisconnect() throws IOException {
		int read = 0;
		byte[] buffer = new byte[BUFFER_SIZE];
		InputStream in = null;

		if (m_uri == null) {
			return;
		}

		String target = m_uri + "/ers/v1/connection/bundleid";

		URL url = new URL(target);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("DELETE");

		connection.setInstanceFollowRedirects(false);

		// add stored-cookie from connection
		if(!m_cookies.isEmpty()) {
			connection.addRequestProperty("Cookie", m_cookies);
		}
		
		if (m_authenticationInfo != null) {
			connection.setRequestProperty(
					"Authorization",
					"Basic "
							+ DatatypeConverter
									.printBase64Binary(m_authenticationInfo
											.getBytes()));
		}

		try {
			connection.connect();

			int status = 200;

			try {
				status = connection.getResponseCode();
			} catch (Exception e) {
			}

			in = (status >= 400 ? connection.getErrorStream() : connection
					.getInputStream());
			if (in != null) {
				ByteArrayOutputStream response = new ByteArrayOutputStream();

				read = 0;
				while ((read = in.read(buffer)) > 0) {
					response.write(buffer, 0, read);
				}
			}
		} finally {
			if (in != null) {
				in.close();
			}

			m_cookies = null;
		}
	}

	private int doVerb(String method, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		int status = 200;
		byte[] buffer = new byte[BUFFER_SIZE];
		InputStream in = null;

		if ((m_uri == null) || (m_authenticationInfo == null)) {
			return status;
		}

		String target = m_uri + request.getPathInfo();
		String queryString = request.getQueryString();
		if (queryString != null) {
			target += "?" + queryString;
		}
		
		System.out.println("Target: "+target);

		URL url = new URL(target);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod(method);

		connection.setInstanceFollowRedirects(false);

		for (Enumeration<String> headerNames = request.getHeaderNames(); headerNames
				.hasMoreElements();) {
			String headerName = (String) headerNames.nextElement();
			String headerValue = request.getHeader(headerName);
			if (headerName.startsWith("Cookie")) {
				headerValue = headerValue.replaceAll("ERS-", "");
			}
			
			//Do not copy the request property from the original request
			//Or it always returns 404 error
//			connection.addRequestProperty(headerName, headerValue);
//			System.out.println("Set "+headerName+" To "+headerValue);
			
		}

		// add stored-cookie from connection
		if(!m_cookies.isEmpty()) {
			connection.addRequestProperty("Cookie", m_cookies);
			System.out.println("Add Cookie: "+m_cookies);
		}
		
		if (m_authenticationInfo != null) {
			connection.setRequestProperty(
					"Authorization",
					"Basic "
							+ DatatypeConverter
									.printBase64Binary(m_authenticationInfo
											.getBytes()));
		}

		if (request.getContentLength() > 0) {
			connection.setDoOutput(true);
			ServletInputStream servletIn = request.getInputStream();
			OutputStream out = connection.getOutputStream();
			int read = 0;
			while ((read = servletIn.read(buffer)) > 0) {
				out.write(buffer, 0, read);
			}
			out.close();
		}

		try {
			connection.connect();

			try {
				status = connection.getResponseCode();
			} catch (Exception e) {
			}
			
			System.out.println("Response Code: "+status);

			response.setStatus(connection.getResponseCode());

			String name = null;
			for (int idx = 1; (name = connection.getHeaderFieldKey(idx)) != null; idx++) {
				String value = connection.getHeaderField(idx);
				if (name.equals("Set-Cookie")) {
					value = "ERS-" + value;
				}
				response.addHeader(name, value);
			}

			in = (status >= 400 ? connection.getErrorStream() : connection
					.getInputStream());
			if (in != null) {
				ServletOutputStream out = response.getOutputStream();
				int read = 0;
				while ((read = in.read(buffer)) > 0) {
					out.write(buffer, 0, read);
					//System.out.print(new String(buffer, 0, read));
				}
				//System.out.println("");
			}
		} finally {
			if (in != null) {
				in.close();
			}
		}

		return status;
	}

	public ERSProxy(String uri, String username, String password, String bundleUri) {
		m_uri = uri;
		m_authenticationInfo = username + ":" + password;
		m_bundleUri = bundleUri;
	}

	public void connect() throws IOException {
		doConnect();
	}

	public void disconnect() throws IOException {
		doDisconnect();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (doVerb("GET", request, response) == 404) {
			doConnect();
			doVerb("GET", request, response);
		}
	}

	public void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if (doVerb("DELETE", request, response) == 404) {
			doConnect();
			doVerb("DELETE", request, response);
		}
	}

	public void doPut(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (doVerb("PUT", request, response) == 404) {
			doConnect();
			doVerb("PUT", request, response);
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if (doVerb("POST", request, response) == 404) {
			doConnect();
			doVerb("POST", request, response);
		}
	}
}
