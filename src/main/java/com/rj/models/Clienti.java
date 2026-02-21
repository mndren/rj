package com.rj.models;

import com.rj.business.BaseModel;
import com.rj.business.annotations.Column;
import com.rj.business.annotations.Label;
import com.rj.business.annotations.OrderBy;
import com.rj.business.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@OrderBy(value = "ragione_sociale desc")
@Table(name = "clienti")
public class Clienti extends BaseModel<Clienti> {

    @Column(id = true)
    public Long id;

    @Label("Ragione Sociale")
    public String ragione_sociale;
    @Label("Partita iva")
    public String partita_iva;
    @Label("Codice Fiscale")
    public String codice_fiscale;
    @Label("Email")
    public String email;
    @Label("Telefono")
    public String telefono;
    @Label("Indirizzo")
    public String indirizzo;

    @Label(value = "Creato Il", visible = false)
    public LocalDate created_at;


}
