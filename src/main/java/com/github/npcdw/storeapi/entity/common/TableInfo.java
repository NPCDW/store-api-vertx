package com.github.npcdw.storeapi.entity.common;

import io.vertx.sqlclient.RowSet;

import java.util.ArrayList;
import java.util.List;

public class TableInfo<T> {
    /**
     * 记录总数
     */
    private Integer count;
    /**
     * 记录列表
     */
    private List<T> list;

    public TableInfo(Integer count, List<T> list) {
        this.count = count;
        this.list = list;
    }

    public TableInfo(Integer count, RowSet<T> list) {
        this.count = count;
        this.list = new ArrayList<T>();
        for (T t : list) {
            this.list.add(t);
        }
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
