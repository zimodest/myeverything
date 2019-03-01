package com.github.everything.core.search.impl;

import com.github.everything.core.dao.FileIndexDao;
import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;
import com.github.everything.core.search.FileSearch;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务代码：
 */
public class FileSearchImpl implements FileSearch {

//    private final DataSource dataSource;
//
//    public FileSearchImpl(DataSource dataSource){
//        this.dataSource = dataSource;
//    }
    private final FileIndexDao fileIndexDao;

    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {

        return this.fileIndexDao.search(condition);
    }
}
