Embeddable Reporting Service: Leverage Cognos on Bluemix

IBM Cognos is a well known world class data analysis and reporting platform. Do we have a chance to leverage such powerful platform on Bluemix? The answer is absolutely YES by using Bluemix Embeddable Reporting Service. In this article, you will find in details about (1) How to setup/configure/develop an app employing Embeddable Reporting Service; (2) How to design the report in Cognos and export it to Bluemix.

I. Introduction
	a. You may use this app to demonstrate: 
		1. How to build an Java Liberty app by leveraging the Embeddable Reporting Service.
		2. Enjoy the experience of brilliant and powerful Cognos reports.
	b.  App URL: http://cognos008.mybluemix.net/
	c.  Code URL: https://hub.jazz.net/project/bingotang/erssample/overview
II. Before getting started
	a.  Bluemix Account
	b.  Java Web programming skills
	c.  Optional: Cognos skills
III. How to create the app on Bluemix
	a.  Create the app on Dashboard
		1. Open the Catalog menu.
		2. From the Runtimes section, click Liberty for Java.
		3. In the App field, specify the name of your app, in this case, it is set to cognos008.
		4. Click Create.Wait for your application to provision.
	b.  Add the MongoDB/ MongoLab service
	    1. Click the App created in the Dashboard. Open the Catalog menu.
		2. Click Add A Service.
		3. Choose MongoDB under Data Management. If the MongoDB service isn't listed, you may need to select the Experimental option in the top-right corner of the catalog.
		4. Click Create. Click OK if it is prompted to restart the application.
		5. NOTE: You may use the MongoLab instead of MongoDB service. However, you may encounter some error while pushing the app when the MongoLab Service is bound. The walk around is to first push the app without binding MongoLab service, then bind the MongoLab service after the app is running, and finally restart the app to take effect.
	c.  Add the SQL Database service
		1. Click the App created in the Dashboard. Open the Catalog menu.
		2. Click Add A Service.
		3. Choose SQL Database under Data Management.
		4. Click Create. Click OK if it is prompted to restart the application.
    d.  Add the Embeddable Reporting service
		1. Click the App created in the Dashboard. Open the Catalog menu.
		2. Click Add A Service.
		3. Choose Embeddable Reporting under Business Analytics section.
		4. Click Create. Click OK if it is prompted to restart the application.
    e.  Gather VCAP_SERVICES environment variables
		1. Navigate to your application overview page.
		2. Click App Runtime and go to the VCAP_SERVICES environment variables section.
		3. Copy the relevant info for further usage.
IV. Configure Embeddable Reporting service
	a. Open the Embeddable Reporting console
		1. Navigate to your application overview page.
		2. From the Overview menu, click Embeddable Reporting.
		3. Click Launch to open the Embeddable Reporting console.
		4. Click Connect. When prompted for the mongo URI, enter the MongoDB URI from the VCAP_SERVICES variables and click Update.
	b. Create A new Application
		1. Click to New An Application
		2. Click Create when prompted for creating an application.
		3. Select the application, on the right pane, click edit to modify the Name. In my case, it is set to "Cognos Demo Application". You may choose another.
	c.  Create A Data Sources
		1. Open the Data Source Tab.
		2. Click to New a Data Source.
		3. Fill the Name as your data source name. Please fill dswb001. The reason is that we are using dswb001 as data source later on. Click create to new a data source.
		4. Click the data source (dswb001), on the right pane, update corresponding properties.
		5. Update Driver Class to com.ibm.db2.jcc.DB2Driver
		6. Update Driver Url: to sqldb jdbcurl in VCAP_SERVICES
		7. Update User Name: to sqldb username in VCAP_SERVICES
		8. Update Password: to sqldb password in VCAP_SERVICES
	d.  Create the Package
		1. Open the Package Tab.
		2. Click to New a Package.
		3. Fill the Name as pkwb001 (the reason is that we are using pkwb001 as data source later on), choose Data Source as dswb001, copy the content from the iicmodel.xml and paste it to Model section. The iidmodel.xml is under erssample/MyData/ref from JazzHub.
	e.  Create A Report
		1. Open the Report Tab.
		2. Click to New a Report.
		3. Fill the report name, you may choose it as "Cognos Report Demo". Choose Package as pkwb001, copy the content from the iicreport.xml and paste it to Model section. The iidreport.xml is under erssample/MyData/ref from JazzHub.
V. Build and Run the App
	a. Fetch all the source code from JazzHub compile and export WAR Archive as "erssample.war". You may find the compiled war archive under erssample/MyData/war/erssample.war from JazzHub.
	b. Use cf tools to upload the app, cf push -p erssample.war.
	c. Navigate the app, the link is usually <app_name>.mybluemix.net. In my case, it is cognos008.mybluemix.net.
		1. Click "Initialize the target database" to initialize the database, you may find that the relevant table is created and some data are inserted.
		2. Click "View the overall report" to view the reports from Embeddable Reporting Service.
VI. Development Details
	a. SampleServlet.java, you may take it as a reverse proxy to connect to Embeddable Reporting Service.
	b. SQLDBSample.java, a class to initialize the tables in the database for analysis later.
	c. sample.html, a page which uses AJAX to retrieve and display the reports from Embeddable Reporting Service.
	d. index.html, the main page.
VII. OPITIONAL: How to export the relevant xml from Cognos 10 environment
	a.  Report xml (iicreport.xml)
		1. We suppose that you already has a Cognos 10 environment ready. And then you can open the "IBM Cognos Report Studio" to create and generate your own report view as you wanted. 
		2. You may refer to the Cognos Administrator to design the report. The details on report design is not covered in this app.
		3. Once the report is ready, then you can save and export the report XML for Bluemix. 
		4. Considering that the table name might be different in Cognos Datasource and SQL DB in Bluemix, please modify the SQL query in the xml accordingly.
VIII. References
　　a. Embeddable Reporting doc: https://www.ng.bluemix.net/docs/#services/EmbeddableReporting/index.html#gettingstartedtemplate.
	b. SQL DB doc: https://www.ng.bluemix.net/docs/#services/SQLDB/index.html#sqldb_002.
	c. Cognos Info Center: http://pic.dhe.ibm.com/infocenter/cbi/v10r1m1/index.jsp?topic=%2Fcom.ibm.swg.ba.cognos.cbi.doc%2Fwelcome.html.