/*
 * Estado: Muti cluster Hadoop job status metric collector
 * Author: Pranab Ghosh
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */


package org.estado.spi;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.sql.Timestamp;

public class JobStatusRdbmsConsumer implements JobStatusConsumer {

	private String url;
	
	@Override
	public void handle(List<JobStatus> jobStatuses) {
		Connection connect = null;
		PreparedStatement crPrepStmt = null;
		try {
			//load the driver and get connection
			Class.forName("com.mysql.jdbc.Driver");
			connect = DriverManager.getConnection(url);
			String stmt = "insert into  jobs(jobid,cluster,user,start_time,end_time,duration,name,status,notes,estimated_time,created_at,updated_at)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?)";
			crPrepStmt = connect.prepareStatement(stmt);
			Timestamp curDateTime = new Timestamp(System.currentTimeMillis());
			
	        for (JobStatus jobStatus : jobStatuses){
	        	String jobId = jobStatus.getJobId();
	        	String cluster = jobStatus.getCluster();
	        	String status = getJobStatus(cluster, jobId);
	        	if (null == status){
	        		//insert
		        	crPrepStmt.setString(1, jobId);
		        	crPrepStmt.setString(2, cluster);
		        	crPrepStmt.setString(3, jobStatus.getUser());
		        	crPrepStmt.setTimestamp(4, new Timestamp(jobStatus.getStartTime()));
		        	crPrepStmt.setTimestamp(5, new Timestamp(jobStatus.getEndTime()));
		        	crPrepStmt.setLong(6, jobStatus.getDuration());
		        	crPrepStmt.setString(7, jobStatus.getJobName());
		        	crPrepStmt.setString(8, jobStatus.getStatus());
		        	crPrepStmt.setString(9, jobStatus.getNotes());
		        	crPrepStmt.setString(10, null);
		        	crPrepStmt.setTimestamp(11, curDateTime);
		        	crPrepStmt.setTimestamp(12, curDateTime);
		        	
		        	crPrepStmt.executeUpdate();
	        	} else {
	        		//update
	        		
	        	}
	        }
			connect.commit();
	        
			
		} catch (Exception ex) {
			System.out.println("Failed in rdbms consumer" + ex);
		} finally {
			try {
			if (null != crPrepStmt){
				crPrepStmt.close();
			}
			if (null != connect){
				connect.close();
			}
			} catch (SQLException sqEx){
				System.out.println("Exception closing db resource" + sqEx);
			}
		}
	}
	
	private String getJobStatus(String cluster, String jobId){
		String status = null;
		
		return status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
