package com.github.everything.core.dao.imp;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.FileType;
import com.github.everything.core.model.Thing;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * 业务层
 */
public class FileIndexDaoImpl implements FileIndexDao {

    private final DataSource dataSource;

    public FileIndexDaoImpl(DataSource dataSource){
        this.dataSource = dataSource;
    }
    @Override
    public void insert(Thing thing) {
        //
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //准备SQL语句
            String sql = "insert into file_index(name, path, depth, file_type) values (?,?,?,?)";
            //准备命令
            statement = connection.prepareStatement(sql);
            //设置参数
            statement.setString(1,thing.getName());
            statement.setString(2,thing.getPath());
            statement.setInt(3,thing.getDepth());
//            FileType.DOC ->DOC
            statement.setString(4,thing.getFileType().name());
            statement.execute();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            releaseResource(null,statement,connection);
        }

    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //准备SQL语句
            String sql = "select name, path, depth, file_type from file_index";

            //name: like
            //fileType: =
            //limit ： limit offset
            //orderbyAsc : order by

            //在方法内部，执行完就销毁不会被多线程共享
            //放在属性上会出现多线程访问
            StringBuilder sqlBuild = new StringBuilder();
            sqlBuild.append("select name, path, depth, file_type from file_index");
            //name匹配原则：前模糊 后模糊 前后模糊
            sqlBuild.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");


            if(condition.getFileType() != null){
                sqlBuild.append(" and file_type = '")
                        .append(condition.getFileType().toUpperCase()).append("'");
            }
            //limit order 必选

            //TODO
            sqlBuild.append(" order by depth ")
                    .append("asc")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0");

            System.out.println(sqlBuild);

//            append(condition.getOrderByArc() ? "asc": "desc")
            //准备命令
            statement = connection.prepareStatement(sqlBuild.toString());
            resultSet = statement.executeQuery();
            //处理结果
            while (resultSet.next()){
//                System.out.println(resultSet.next());
//                数据库的行记录  -——>java中的对象
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));

                String fileType = resultSet.getString("file_type");
                thing.setFileType(FileType.lookupByName(fileType));
                things.add(thing);
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            releaseResource(resultSet,statement,connection);

        }

        return things;
    }

    @Override
    public void delete(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            //获取数据库连接
            connection = dataSource.getConnection();
            //准备SQL语句
            String sql = "delete from file_index where path like '"+thing.getPath() +"%'";
            //准备命令
            statement = connection.prepareStatement(sql);
            //设置参数
            statement.setString(1,thing.getPath());
            statement.executeUpdate();

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            releaseResource(null,statement,connection);
        }

    }

    //解决内部代码大量重复问题，重构
    private void releaseResource(ResultSet resultSet, PreparedStatement statement, Connection connection){
        if(resultSet != null){
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(statement != null){
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if(connection != null){
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
