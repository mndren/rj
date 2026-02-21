package com.rj.business;


import com.rj.business.annotations.Column;
import com.rj.business.annotations.OrderBy;
import com.rj.business.annotations.Table;
import com.rj.db.DataSource;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

public abstract class BaseModel<T extends BaseModel<T>> implements Db<T> {

    private String tableName() {
        Table ann = this.getClass().getAnnotation(Table.class);
        if (ann == null)
            throw new RuntimeException("annotazione @Table mancante su " + this.getClass().getSimpleName());
        return ann.name();
    }

    private String getOrderBy() {
        OrderBy ann = this.getClass().getAnnotation(OrderBy.class);
        if (ann == null)
            throw new RuntimeException("annotazione @OrderBy mancante su " + this.getClass().getSimpleName());
        return ann.value();
    }

    private List<Field> getFields() {
        List<Field> fields = new ArrayList<>();
        Collections.addAll(fields, this.getClass().getFields());
        return fields;
    }

    private Field getIdField() {
        for (Field f : getFields()) {
            Column col = f.getAnnotation(Column.class);
            if (col != null && col.id()) return f;
            // se non trovo id a true nell'annotazione column dei field
            if (f.getName().equals("id")) return f;
        }
        throw new RuntimeException("nessun campo id trovato in " + this.getClass().getSimpleName());
    }

    private String columnName(Field f) {
        Column col = f.getAnnotation(Column.class);
        if (col != null && !col.name().isEmpty()) return col.name();
        return f.getName();
    }

    void setFieldFromRs(Field f, ResultSet rs) throws Exception {
        f.setAccessible(true);
        String col = columnName(f);
        Class<?> type = f.getType();

        if (type == Long.class || type == long.class) f.set(this, rs.getLong(col));
        else if (type == Integer.class || type == int.class) f.set(this, rs.getInt(col));
        else if (type == String.class) f.set(this, rs.getString(col));
        else if (type == Boolean.class || type == boolean.class) f.set(this, rs.getBoolean(col));
        else if (type == LocalDate.class) {
            Date d = rs.getDate(col);
            f.set(this, d != null ? d.toLocalDate() : null);
        } else if (type == BigDecimal.class) {
            BigDecimal bd = rs.getBigDecimal(col);
            f.set(this, bd);
        }
    }

    private void setParamFromField(PreparedStatement pst, int idx, Field f) throws Exception {
        f.setAccessible(true);
        Object val = f.get(this);
        if (val == null) pst.setNull(idx, Types.NULL);
        else if (val instanceof LocalDate) pst.setDate(idx, Date.valueOf((LocalDate) val));
        else pst.setObject(idx, val);
    }


    @SuppressWarnings("unchecked")
    private T newInstance() {
        try {
            return (T) this.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("costruttore no-arg mancante in " + this.getClass().getSimpleName(), e);
        }
    }


    @Override
    public List<T> listAll() {
        String sql = "SELECT * FROM " + tableName() + " ORDER BY " + getOrderBy();
        List<T> result = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            List<Field> fields = getFields();
            while (rs.next()) {
                T obj = newInstance();
                for (Field f : fields) obj.setFieldFromRs(f, rs);
                result.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Optional<T> findById(Long id) {
        Field idField = getIdField();
        String sql = "SELECT * FROM " + tableName() + " WHERE " + columnName(idField) + " = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {

            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    T obj = newInstance();
                    for (Field f : getFields()) obj.setFieldFromRs(f, rs);
                    return Optional.of(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean insert() {
        Field idField = getIdField();
        List<Field> insertable = new ArrayList<>();
        for (Field f : getFields()) {
            if (f.equals(idField)) continue;
            Column col = f.getAnnotation(Column.class);
            if (col != null && !col.insertable()) continue;
            insertable.add(f);
        }

        StringJoiner cols = new StringJoiner(", ");
        StringJoiner placeholders = new StringJoiner(", ");
        for (Field f : insertable) {
            cols.add(columnName(f));
            placeholders.add("?");
        }

        String sql = "INSERT INTO " + tableName() + " (" + cols + ") VALUES (" + placeholders + ")";
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < insertable.size(); i++) {
                setParamFromField(pst, i + 1, insertable.get(i));
            }
            int rows = pst.executeUpdate();

            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    idField.setAccessible(true);
                    idField.set(this, keys.getLong(1));
                }
            }
            return rows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update() {
        Field idField = getIdField();
        List<Field> updatable = new ArrayList<>();
        for (Field f : getFields()) {
            if (f.equals(idField)) continue;
            Column col = f.getAnnotation(Column.class);
            if (col != null && !col.updatable()) continue;
            updatable.add(f);
        }

        StringJoiner sets = new StringJoiner(", ");
        for (Field f : updatable) sets.add(columnName(f) + " = ?");

        String sql = "UPDATE " + tableName() + " SET " + sets + " WHERE " + columnName(idField) + " = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {

            for (int i = 0; i < updatable.size(); i++) {
                setParamFromField(pst, i + 1, updatable.get(i));
            }
            idField.setAccessible(true);
            pst.setObject(updatable.size() + 1, idField.get(this));

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete() {
        Field idField = getIdField();
        String sql = "DELETE FROM " + tableName() + " WHERE " + columnName(idField) + " = ?";
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql)) {

            idField.setAccessible(true);
            pst.setObject(1, idField.get(this));
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<T> listAllFiltered(Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName()).append(" WHERE 1=1");
        List<String> values = new ArrayList<>();

        for (Map.Entry<String, String> entry : filters.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isBlank()) {
                sql.append(" AND ").append(entry.getKey()).append(" ILIKE ?");
                values.add("%" + entry.getValue() + "%");
            }
        }

        List<T> result = new ArrayList<>();
        try (Connection c = DataSource.getConnection();
             PreparedStatement pst = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < values.size(); i++) {
                pst.setString(i + 1, values.get(i));
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    T obj = newInstance();
                    for (Field f : getFields()) obj.setFieldFromRs(f, rs);
                    result.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
