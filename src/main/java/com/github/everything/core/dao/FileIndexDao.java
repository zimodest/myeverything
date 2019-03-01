package com.github.everything.core.dao;

import com.github.everything.core.model.Condition;
import com.github.everything.core.model.Thing;

import java.util.List;

/**
 * 关于业务层访问数据库的CRUD
 */
public interface FileIndexDao {

    /**
     * 插入数据Thing
     * @param thing
     */
    void insert(Thing thing);

    /**
     * 根据condition条件进行数据库的检索
     * @param condition
     * @return
     */
    List<Thing> search(Condition condition);
}
