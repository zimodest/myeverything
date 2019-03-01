package com.github.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * DataSource接口
 * JdbcDataSource
 * 创建数据源 连接池
 */
public class DataSourceFactory {

    /**
     * 数据源(单例)
     */

    private static volatile DruidDataSource dataSource;

    private DataSourceFactory(){

    }

    public static DataSource dataSource(){
        //保证多线程下不会产生竞争
        if(dataSource == null){
            synchronized (DataSourceFactory.class){
                if(dataSource == null){
                    //实例化
                    dataSource = new DruidDataSource();
                    //JDBC driver class
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url username password
                    //采用的是H2的嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //JDBC中关于MySQL的规范 jdbc:mysql://ip:port/databaseName


                    //嵌入式时使用
                    //JDBC 中关于H2  jdbc:h2:filepath  存储到本地文件
                    //JDBC 中关于H2  jdbc:h2:~/filepath  存储到当前用户的home目录

                    //JDBC 中关于H2  jdbc:h2://ip:port/databaseName  存储到服务器
                    //获取当前工程路径
                    String workDir = System.getProperty("user.dir");   //获取当前工程路径
                    //创建本地文件存储数据
                    dataSource.setUrl("jdbc:h2:"+workDir+ File.separator+"everything");
                }
            }
        }

        return dataSource;
    }

    public static void initDataBase(){
        //1、获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        //2、获取SQL语句
        //不采取绝对路径读取文件
        //采用读取classpath路径下的文件
        try(InputStream in = DataSourceFactory.class.getClassLoader().
                getResourceAsStream("everything_plus.sql");){
            if (in == null){
                throw new RuntimeException("Not read init database script please check it");
            }

            StringBuilder sqlBuilder = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
                String line = null;
                while((line = reader.readLine())!= null){
                    if(!line.startsWith("--")){
                        sqlBuilder.append(line);
                    }
                }
            }
            //获取数据库连接和名称执行SQL
            String sql = sqlBuilder.toString();
            //JDBC
            //获取数据库的连接
            //1、DriverManager.getConnection()
            //2、数据源获取链接
            Connection connection = dataSource.getConnection();

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();

            connection.close();
            statement.close();


        }catch (IOException e){

        } catch (SQLException e) {
            e.printStackTrace();
        }


        //



    }



    public static void main(String[] args) {
//        DataSource dataSource = DataSourceFactory.dataSource();
//        System.out.println(dataSource);
        DataSourceFactory.initDataBase();
    }
}
