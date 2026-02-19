package com.rj.models;

import com.rj.annotations.Table;
import com.rj.db.DataSource;
import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "clienti")
public class Clienti {
    public Long id;
    public String ragione_sociale;
    public String partita_iva;
    public String codice_fiscale;
    public String email;
    public String telefono;
    public String indirizzo;
    public LocalDate created_at;


    public static List<Clienti> listAll() {
        String q = "select * from " + Clienti.class.getAnnotation(Table.class).name();
        List<Clienti> clienti = new ArrayList<>();
        try (Connection connection = DataSource.getConnection(); PreparedStatement pst = connection.prepareStatement(q);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Clienti cli = new Clienti();
                cli.id = rs.getLong(1);
                cli.ragione_sociale = rs.getString("ragione_sociale");
                cli.partita_iva = rs.getString("partita_iva");
                cli.codice_fiscale = rs.getString("codice_fiscale");
                cli.email = rs.getString("email");
                cli.telefono = rs.getString("telefono");
                cli.indirizzo = rs.getString("indirizzo");
                cli.created_at = rs.getDate("created_at").toLocalDate();
                clienti.add(cli);
            }
            return clienti;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clienti;
    }

}
