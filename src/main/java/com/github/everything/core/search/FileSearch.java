package com.github.everything.core.search;

import com.github.everything.core.dao.DataSourceFactory;
import com.github.everything.core.dao.imp.FileIndexDaoImpl;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.search.impl.FileSearchImpl;

import java.util.List;

public interface FileSearch {


    /**
     * 根据condition条件进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);


//    public static void main(String[] args) {
//        FileSearch fileSearch = new FileSearchImpl(new FileIndexDaoImpl(
//                DataSourceFactory.dataSource()
//        ));
//        List<Thing> list = fileSearch.search(new Condition());
//        System.out.println(list);
//    }
}
