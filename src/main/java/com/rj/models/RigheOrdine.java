package com.rj.models;

import com.rj.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Table(name = "righe_ordine")
public class RigheOrdine {
    public Long id;
    public Long ordine_id;
    public Long prodotto_id;
    public Integer quantita;
    public BigDecimal prezzo_unitario;
    public BigDecimal iva;
    public BigDecimal totale_riga;
}
