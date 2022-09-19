package com.github.npcdw.storeapi.entity.builder;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class SqlWhereBuilder {
    /**
     * WHERE 语句
     */
    private final StringBuilder where = new StringBuilder();
    /**
     * 参数
     */
    private final List<Object> paramList = new ArrayList<>();

    public SqlWhereBuilder(SqlWhereBuilder sqlWhereBuilder) {
        this.where.append("(").append(sqlWhereBuilder.toString()).append(")");
        this.paramList.addAll(sqlWhereBuilder.getParamList());
    }

    public SqlWhereBuilder(String field, String operator, Object value) {
        this.where.append(field).append(" ").append(operator).append(" ").append(getSqlShowValue(value));
    }

    public SqlWhereBuilder and(SqlWhereBuilder sqlWhereBuilder) {
        this.where.append(" AND ").append("(").append(sqlWhereBuilder.toString()).append(")");
        this.paramList.addAll(sqlWhereBuilder.getParamList());
        return this;
    }

    public SqlWhereBuilder and(String field, String operator, Object value) {
        if (isIllegalArgument(field, operator, value)) {
            return this;
        }
        this.where.append(" AND ").append(field).append(" ").append(operator).append(" ").append(getSqlShowValue(value));
        return this;
    }

    public SqlWhereBuilder or(SqlWhereBuilder sqlWhereBuilder) {
        this.where.append(" OR ").append("(").append(sqlWhereBuilder.toString()).append(")");
        this.paramList.addAll(sqlWhereBuilder.getParamList());
        return this;
    }

    public SqlWhereBuilder or(String field, String operator, Object value) {
        if (isIllegalArgument(field, operator, value)) {
            return this;
        }
        this.where.append(" OR ").append(field).append(" ").append(operator).append(" ").append(getSqlShowValue(value));
        return this;
    }

    private boolean isIllegalArgument(String field, String operator, Object value) {
        if (StringUtils.isBlank(field) || StringUtils.isBlank(operator) || value == null) {
            return true;
        }
        if (value instanceof String) {
            String tmp = value.toString();
            return StringUtils.isBlank(tmp);
        } else if (value instanceof Collection) {
            return ((Collection<?>) value).isEmpty();
        } else if (value.getClass().isArray()) {
            return Array.getLength(value) <= 0;
        }
        return false;
    }

    private String getSqlShowValue(Object value) {
        if (value instanceof Date) {
            this.paramList.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(value));
            return "?";
        } else if (value instanceof Collection) {
            StringBuilder show = new StringBuilder("(");
            for (Object o : (Collection<?>) value) {
                this.paramList.add(o);
                show.append("?, ");
            }
            show.delete(show.length() - 2, show.length());
            return show.append(")").toString();
        } else if (value.getClass().isArray()) {
            int length = Array.getLength(value);
            StringBuilder show = new StringBuilder("(?");
            this.paramList.add(Array.get(value, 0));
            for (int i = 0; i < length; i++) {
                show.append(", ?");
                this.paramList.add(Array.get(value, i));
            }
            return show.append(")").toString();
        } else {
            this.paramList.add(value);
            return "?";
        }
    }

    public String builder() {
        return where.toString();
    }

    @Override
    public String toString() {
        return where.toString();
    }

    public List<Object> getParamList() {
        return paramList;
    }

}
