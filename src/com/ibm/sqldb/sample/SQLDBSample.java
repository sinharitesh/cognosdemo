/*
 * Copyright 2015 IBM Corp. All Rights Reserved
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0 
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.sqldb.sample;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

import com.ibm.db2.jcc.DB2SimpleDataSource;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ibm.nosql.json.api.BasicDBList;
import com.ibm.nosql.json.api.BasicDBObject;
import com.ibm.nosql.json.util.JSON;


@WebServlet("/SQLDBSample")
public class SQLDBSample extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// set defaults
	private String databaseHost = "localhost";
	private int port = 50000;
	private String databaseName = "mydb";
	private String user = "myuser";
	private String password = "mypass";
	private String url = "myurl";

	public SQLDBSample() {
		super();
	}

	private boolean processVCAP(PrintWriter writer) {
		// VCAP_SERVICES is a system environment variable
		// Parse it to obtain the for DB2 connection info
		String VCAP_SERVICES = System.getenv("VCAP_SERVICES");
		// writer.println("VCAP_SERVICES content: " + VCAP_SERVICES);

		if (VCAP_SERVICES != null) {
			// parse the VCAP JSON structure
			BasicDBObject obj = (BasicDBObject) JSON.parse(VCAP_SERVICES);
			String thekey = null;
			Set<String> keys = obj.keySet();
			writer.println("Searching through VCAP keys");
			// Look for the VCAP key that holds the SQLDB information
			for (String eachkey : keys) {
				writer.println("Key is: " + eachkey);
				// Just in case the service name gets changed to lower case in
				// the future, use toUpperCase
				if (eachkey.toUpperCase().startsWith("SQLDB")) {
					thekey = eachkey;
				}
			}
			if (thekey == null) {
				writer.println("Cannot find any SQLDB service in the VCAP; exiting");
				return false;
			}
			BasicDBList list = (BasicDBList) obj.get(thekey);
			obj = (BasicDBObject) list.get("0");
			writer.println("Service found: " + obj.get("name"));
			// parse all the credentials from the vcap env variable
			obj = (BasicDBObject) obj.get("credentials");
			databaseHost = (String) obj.get("host");
			databaseName = (String) obj.get("db");
			port = (int) obj.get("port");
			user = (String) obj.get("username");
			password = (String) obj.get("password");
			url = (String) obj.get("jdbcurl");
		} else {
			writer.println("VCAP_SERVICES is null");
			return false;
		}
		writer.println();
		writer.println("database host: " + databaseHost);
		writer.println("database port: " + port);
		writer.println("database name: " + databaseName);
		writer.println("username: " + user);
		writer.println("password: " + password);
		writer.println("url: " + url);
		return true;
	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("IBM SQL Database, initialize the database for Embeddable Reporting Demo");
		writer.println("Servlet: " + this.getClass().getName());
		writer.println();
		writer.println("Host IP:" + InetAddress.getLocalHost().getHostAddress());

		// process the VCAP env variable and set all the global connection
		// parameters
		if (processVCAP(writer)) {

			// Connect to the Database
			Connection con = null;
			try {
				writer.println();
				writer.println("Connecting to the database");
				DB2SimpleDataSource dataSource = new DB2SimpleDataSource();
				dataSource.setServerName(databaseHost);
				dataSource.setPortNumber(port);
				dataSource.setDatabaseName(databaseName);
				dataSource.setUser(user);
				dataSource.setPassword(password);
				dataSource.setDriverType(4);
				con = dataSource.getConnection();
				writer.println();
				con.setAutoCommit(false);
			} catch (SQLException e) {
				writer.println("Error connecting to database");
				writer.println("SQL Exception: " + e);
				return;
			}

			// Try out some dynamic SQL Statements
			Statement stmt = null;
			String tableName = "";
			String sqlStatement = "";
			// It is recommend NOT to use the default schema since it is
			// correlated
			// to the generated user ID
			// String schemaName = "CTOJSJZT.LOCATIONS";
			// create a unique table name to make sure we deal with our own
			// table
			// If another version of this sample app binds to the same database,
			// this gives us some level of isolation
			tableName = "LOCATION";

			// Remove the table from the database
			try {
				stmt = con.createStatement();
				sqlStatement = "DROP TABLE " + tableName;
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
				writer.println("Error dropping table: " + e);
			}


			// create a table
			try {
				// Create the CREATE TABLE SQL statement and execute it
				sqlStatement = "CREATE TABLE " + tableName
						+ " (CITY VARCHAR(20), NUMBERS INTEGER)";
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);
			} catch (SQLException e) {
				writer.println("Error creating table: " + e);
			}

			// Execute some SQL statements on the table: Insert, Select and
			// Delete
			try {
				sqlStatement = "INSERT INTO " + tableName
						+ " VALUES (\'Shanghai\', 500)";
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);

				sqlStatement = "INSERT INTO " + tableName
						+ " VALUES (\'Beijing\', 2000)";
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);
				
				sqlStatement = "INSERT INTO " + tableName
						+ " VALUES (\'Tianjin\', 1000)";
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);
				
				sqlStatement = "INSERT INTO " + tableName
						+ " VALUES (\'Guangzhou\', 500)";
				writer.println("Executing: " + sqlStatement);
				stmt.executeUpdate(sqlStatement);

			} catch (SQLException e) {
				writer.println("Error executing:" + sqlStatement);
				writer.println("SQL Exception: " + e);
			}

			// Close everything off
			try {
				// Close the Statement
				stmt.close();
				// Connection must be on a unit-of-work boundary to allow close
				con.commit();
				// Close the connection
				con.close();
				writer.println("Finished");

			} catch (SQLException e) {
				writer.println("Error closing things off");
				writer.println("SQL Exception: " + e);
			}
		}
		writer.close();
	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
	}

}
