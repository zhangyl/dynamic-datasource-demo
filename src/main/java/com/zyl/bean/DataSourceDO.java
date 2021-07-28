package com.zyl.bean;

public class DataSourceDO {
	
	private String datasourceName;
    private String databaseIp;
    private String databasePort;
    private String databaseName;
    private String username;
    private String password;
    private String databaseUrlExtraParam;

    public String getDatasourceName() {
        return datasourceName;
    }

    public void setDatasourceName(String datasourceName) {
        this.datasourceName = datasourceName;
    }

    public String getDatabaseIp() {
        return databaseIp;
    }

    public void setDatabaseIp(String databaseIp) {
        this.databaseIp = databaseIp;
    }

    public String getDatabasePort() {
        return databasePort;
    }

    public void setDatabasePort(String databasePort) {
        this.databasePort = databasePort;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	public String getDatabaseUrlExtraParam() {
		return databaseUrlExtraParam;
	}

	public void setDatabaseUrlExtraParam(String databaseUrlExtraParam) {
		this.databaseUrlExtraParam = databaseUrlExtraParam;
	}
    
}
