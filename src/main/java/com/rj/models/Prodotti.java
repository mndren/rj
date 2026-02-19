package com.rj.models;

import java.math.BigDecimal;
import java.time.LocalDate;

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
