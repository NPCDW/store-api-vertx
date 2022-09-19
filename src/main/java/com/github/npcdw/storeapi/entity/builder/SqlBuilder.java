package com.github.npcdw.storeapi.entity.builder;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SqlBuilder {
    /**
     * 执行SQL的方法
     */
    private SqlMethod method;
    /**
     * 字段
     */
    private String fields;
    /**
     * 表名
     */
    private String tableName;
    /**
     * 查询条件
     */
    private SqlWhereBuilder where;
    /**
     * 排序
     */
    private String orderBy;
    /**
     * 分页
     */
    private String limit;
    /**
     * 分组
     */
    private String groupBy;
    /**
     * 更新语句
     */
    private Map<String, Object> set;
    /**
     * 值
     */
    private Map<String, Object> values;
    /**
     * 参数
     */
    private final List<Object> paramList = new ArrayList<>();

    public SqlBuilder select(String fields) {
        this.method = SqlMethod.SELECT;
        this.fields = fields;
        return this;
    }

    public SqlBuilder update(String tableName) {
        this.method = SqlMethod.UPDATE;
        this.tableName = tableName;
        return this;
    }

    public SqlBuilder deleteFrom(String tableName) {
        this.method = SqlMethod.DELETE;
        this.tableName = tableName;
        return this;
    }

    public SqlBuilder insertInto(String tableName, String fields) {
        this.method = SqlMethod.INSERT;
        this.tableName = tableName;
        this.fields = fields;
        return this;
    }

    public SqlBuilder from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SqlBuilder set(Map<String, Object> set) {
        this.set = set;
        return this;
    }

    public SqlBuilder values(Map<String, Object> values) {
        this.values = values;
        return this;
    }

    public SqlBuilder where(SqlWhereBuilder sqlWhereBuilder) {
        this.where = new SqlWhereBuilder(sqlWhereBuilder);
        return this;
    }

    public SqlBuilder where(String field, String operator, Object value) {
        this.where = new SqlWhereBuilder(field, operator, value);
        return this;
    }

    public SqlBuilder and(SqlWhereBuilder sqlWhereBuilder) {
        this.where.and(sqlWhereBuilder);
        return this;
    }

    public SqlBuilder and(String field, String operator, Object value) {
        this.where.and(field, operator, value);
        return this;
    }

    public SqlBuilder or(SqlWhereBuilder sqlWhereBuilder) {
        this.where.or(sqlWhereBuilder);
        return this;
    }

    public SqlBuilder or(String field, String operator, Object value) {
        this.where.or(field, operator, value);
        return this;
    }

    public SqlBuilder orderBy(String order) {
        if (StringUtils.isBlank(order)) {
            return this;
        }
        this.orderBy = order;
        return this;
    }

    public SqlBuilder orderBy(String field, String method) {
        if (StringUtils.isBlank(field)) {
            return this;
        }
        this.orderBy = field + " " + method;
        return this;
    }

    public SqlBuilder limit(Integer start, Integer length) {
        this.limit = start + "," + length;
        return this;
    }

    public SqlBuilder groupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public String builder() {
        StringBuilder sql = new StringBuilder();
        switch (this.method) {
            case SELECT:
                sql.append("SELECT ").append(this.fields)
                        .append(" FROM ").append(this.tableName);
                if (this.where != null) {
                    sql.append(" WHERE ").append(this.where.toString());
                    this.paramList.addAll(this.where.getParamList());
                }
                if (StringUtils.isNotBlank(this.groupBy)) {
                    sql.append(" GROUP BY ").append(this.groupBy);
                }
                if (StringUtils.isNotBlank(this.orderBy)) {
                    sql.append(" ORDER BY ").append(this.orderBy);
                }
                if (StringUtils.isNotBlank(this.limit)) {
                    sql.append(" LIMIT ").append(this.limit);
                }
                break;
            case UPDATE:
                StringBuilder setStr = new StringBuilder();
                for (Map.Entry<String, Object> item : this.set.entrySet()) {
                    setStr.append(item.getKey()).append(" = ?, ");
                    this.paramList.add(item.getValue());
                }
                setStr.deleteCharAt(setStr.length() - 2);
                sql.append("UPDATE ").append(this.tableName)
                        .append(" SET ").append(setStr)
                        .append(" WHERE ").append(this.where.toString());
                this.paramList.addAll(this.where.getParamList());
                break;
            case DELETE:
                sql.append("DELETE FROM ").append(this.tableName)
                        .append(" WHERE ").append(this.where.toString());
                this.paramList.addAll(this.where.getParamList());
                break;
            case INSERT:
                sql.append("INSERT ").append(this.tableName);
                StringBuilder fields = new StringBuilder(" (");
                StringBuilder values = new StringBuilder(" (");
                for (Map.Entry<String, Object> item : this.values.entrySet()) {
                    fields.append(item.getKey()).append(", ");
                    values.append("?, ");
                    this.paramList.add(item.getValue());
                }
                fields.deleteCharAt(fields.length() - 2).append(")");
                values.deleteCharAt(values.length() - 2).append(")");
                break;
            default:
                break;
        }
        return sql.toString();
    }

    @Override
    public String toString() {
        return this.builder();
    }

    public List<Object> getParamList() {
        return paramList;
    }

    private enum SqlMethod {
        SELECT, UPDATE, DELETE, INSERT
    }

}
