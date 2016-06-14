package com.nicholas.engine;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * @ClassName: DatabaseUtil
 * @Description: 数据库连接工具
 * @author yangchao
 * 
 */
public class DatabaseUtil {
    Logger logger = Logger.getLogger(this.getClass().getName());
    
    //BasicDataSource dataSource = (BasicDataSource) new ClassPathXmlApplicationContext("applicationContext.xml").getBean("dataSource");
    
    private String driver   = "oracle.jdbc.driver.OracleDriver";
    private String url      = "jdbc:oracle:thin:@127.0.0.1:1521:orcl";
    private String user     = "root";
    private String password = "root";

    
    public DatabaseUtil(){}
    
    public DatabaseUtil(String driver, String url, String user, String password) {
        if (!driver.isEmpty()) {
            this.driver = driver;
        }
        if (!url.isEmpty()) {
            this.url = url;
        }
        if (!user.isEmpty()) {
            this.user = user;
        }
        if (!password.isEmpty()) {
            this.password = password;
        }
    }

    /**
     * 
     * <p>
     * Title:
     * </p>
     * <p>
     * 连接数据库:
     * </p>
     * 
     * @param driver
     * @param url
     * @param user
     * @param password
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        
        Connection connection =  DriverManager.getConnection(url, user, password);
        if(connection!=null){
            logger.info("数据库连接成功！");
        }
        
        return connection;
    }
    
    /**
     * 
     * @Title: GeneratorTableModel 
     * @Description: 根据数据库表名查询所有字段名、类型、长度、备注、是否主键等信息
     * @param tableName
     * @return 
     * @return List<Map<String,String>>
     * @throws Exception 
     */
    public Map<String, List<Map<String,String>>> queryTableColumns(List<String> tableNamesList) throws Exception {
        Map<String, List<Map<String,String>>> listMap = new HashMap<String, List<Map<String,String>>>();
        Connection connection = null;
        Statement stmt = null;
        ResultSet rSet = null;
        try{
            connection = this.getConnection();
            stmt = connection.createStatement();
            for(String tableName:tableNamesList){ 
                tableName = tableName.toUpperCase();
                String sqlStr = "select u.table_name,u.comments as CN_NAME,tc.COLUMN_NAME,tc.DATA_TYPE,tc.DATA_LENGTH,tc.NULLABLE,cc.comments as CNNAME,"
                        + " (select tt.COLUMN_NAME from user_tab_columns tt where tt.NULLABLE='N' and tt.TABLE_NAME='"+tableName+"')as PK_COLUMN from user_tab_comments u"
                        + " left join user_tab_columns tc on tc.TABLE_NAME=u.table_name"
                        + " left join user_col_comments cc on cc.TABLE_NAME=tc.TABLE_NAME and tc.COLUMN_NAME=cc.column_name"
                        + " where u.table_name='"+tableName+"'";
        
                rSet = stmt.executeQuery(sqlStr);
                List<Map<String, String>> list = new ArrayList<Map<String,String>>();

                while (rSet.next()) {
                    String table_name = rSet.getString("TABLE_NAME");
                    String cn_name = rSet.getString("CN_NAME");
                    String pk_column = rSet.getString("PK_COLUMN");
                    String columnId = rSet.getString("COLUMN_NAME");
                    String dataType = rSet.getString("DATA_TYPE");
                    String dataLength = rSet.getString("DATA_LENGTH");
                    String nullable = rSet.getString("NULLABLE");
                    String cnname = rSet.getString("CNNAME");
                    
                    String entity = this.tableNameToEntity(table_name);
                    String pk_name = this.ColumnIdToName(pk_column);
                    String columnName = this.ColumnIdToName(columnId);
                    if(""==cn_name||cn_name==null){
                        cn_name = table_name;
                    }
                    if(""==cnname||cnname==null){
                        cnname = columnName;
                    }
                    
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("entity", entity);
                    map.put("tableName", table_name);
                    map.put("cn_name", cn_name);
                    map.put("pk_column", pk_column);
                    map.put("pkName", pk_name);
                    map.put("columnId", columnId);
                    map.put("columnName", columnName);
                    map.put("cnname", cnname);
                    map.put("dataType", dataType);
                    map.put("dataLength", dataLength);
                    map.put("nullable", nullable);

                    list.add(map);
                }
                listMap.put(tableName, list);
            }
        }catch (Exception e) {
            throw new Exception(e);
        }finally{
            this.releaseConnection(connection, stmt, rSet);
        }
        
        return listMap;
    }
    
    public String firstToLower(String str) throws Exception{
        return str.substring(0, 1).toLowerCase()+ str.substring(1, str.length());
    }
    
    public String firstToUpper(String str) throws Exception{
        return str.substring(0, 1).toUpperCase()+ str.substring(1, str.length());
    }

    /**
     * 
     * @Title: tableNameToEntity 
     * @Description: 通过表名获取实体名，如：T_USER转为TUser
     * @param table_name
     * @return
     * @throws Exception
     * @return String
     */
    public String tableNameToEntity(String table_name) throws Exception{
        String[] tableStrs = table_name.toLowerCase().split("_");
        StringBuffer bf = new StringBuffer();
        for(String str:tableStrs){
            str = firstToUpper(str);
            bf.append(str);
        }
        return bf.toString();
    }
    
    /**
     * 
     * @Title: tableNameToEntity 
     * @Description: 通过字段名获取字段别名，如：USER_ID转为userId
     * @param table_name
     * @return
     * @throws Exception
     * @return String
     */
    public String ColumnIdToName(String columnId) throws Exception{
        String[] tableStrs = columnId.toLowerCase().split("_");
        StringBuffer bf = new StringBuffer();
        for(String str:tableStrs){
            if(!bf.toString().isEmpty()){
                str = firstToUpper(str);
            }
            
            bf.append(str);
        }
        return bf.toString();
    }
    
    
    /**
     * 
     * @Title: releaseConnection
     * @Description: 释放数据库连接
     * @param conn
     * @param stmt
     * @param rs
     * @throws SQLException
     * @return void
     */
    public void releaseConnection(Connection conn, Statement stmt, ResultSet rs) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

    /**
     * @return the driver
     */
    public String getDriver() {
        return driver;
    }

    /**
     * @param driver
     *            the driver to set
     */
    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user
     *            the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password
     *            the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
