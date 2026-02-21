package com.rj.models;

import com.rj.business.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Table(name = "prodotti")
public class Prodotti {
    public Long id;
    public String codice;
    public String nome;
    public String descrizione;
    public BigDecimal prezzo;
    public BigDecimal iva;
    public Boolean attivo;
    public LocalDate created_at;
}
