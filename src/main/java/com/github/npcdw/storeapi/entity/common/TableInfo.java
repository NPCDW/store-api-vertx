package com.github.npcdw.storeapi.entity.common;

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
