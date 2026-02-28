package com.rj.models;

import com.rj.business.BaseModel;
import com.rj.business.annotations.Column;
import com.rj.business.annotations.Table;
import com.rj.db.DataSource;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "sessioni")
public class Sessioni extends BaseModel<Sessioni> {


    @Column(name = "token")
    public String token;

    @Column(name = "utente_id")
    public Long utenteId;

    @Column(name = "expires_at")
    public LocalDateTime expiresAt;

    public void destroy(String token) {
        try (Connection c = DataSource.getConnection(); PreparedStatement pst = c.prepareStatement("DELETE FROM sessioni WHERE token = ?")) {
            pst.setString(1, token);
            pst.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getUtenteIdByToken(String token) {
        try (Connection c = DataSource.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT utente_id FROM sessioni WHERE token = ?")) {
            pst.setString(1, token);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getLong("utente_id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Sessioni> getSessioniByUtenteId(Long utenteId) {
        List<Sessioni> sessioniList = new ArrayList<>();
        try (Connection c = DataSource.getConnection(); PreparedStatement pst = c.prepareStatement("SELECT * from sessioni WHERE utente_id = ?")) {
            pst.setLong(1, utenteId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Sessioni s = new Sessioni();
                s.setUtenteId(rs.getLong("utente_id"));
                s.setToken(rs.getString("token"));
                s.setExpiresAt(rs.getObject("expires_at", LocalDateTime.class));
                sessioniList.add(s);
            }
            return sessioniList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sessioniList;
    }


}
