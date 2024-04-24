package org.javastart.main.Database;

import java.sql.Connection;
import java.sql.DriverManager;

//connecting database
public class DbConnect {
    public Connection databaseLink;
    public Connection getDbConnection(){
        String databaseName="java";
        String databaseUser="root";
        String databasePassword="123123";
        String url="jdbc:mysql://localhost/"+databaseName;

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            databaseLink=DriverManager.getConnection(url,databaseUser,databasePassword);
        }catch (Exception e){
            e.printStackTrace();
        }
        return databaseLink;
    }
}
