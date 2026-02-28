package com.rj.models;

import com.rj.business.BaseModel;
import com.rj.business.annotations.Column;
import com.rj.business.annotations.Form;
import com.rj.business.annotations.OrderBy;
import com.rj.business.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@OrderBy(value = "ragione_sociale asc limit 100")
@Table(name = "clienti")
public class Clienti extends BaseModel<Clienti> {

    @Column(id = true)
    public Long id;

    @Form(value = "Ragione Sociale", autofocus = true)
    public String ragione_sociale;
    @Form(value = "Partita iva", maxlength = "13", minlength = "11")
    public String partita_iva;
    @Form(value = "Codice Fiscale", maxlength = "16", minlength = "16")
    public String codice_fiscale;
    @Form(value = "Email", type = "email")
    public String email;
    @Form(value = "Telefono", type = "tel", pattern = "[0-9]{10}", placeholder = "1234657879")
    public String telefono;
    @Form("Indirizzo")
    public String indirizzo;

    @Form(value = "Creato Il", visible = false)
    public LocalDate created_at;


}
