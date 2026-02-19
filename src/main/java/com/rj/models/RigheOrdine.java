package com.rj.models;

import java.math.BigDecimal;

public class RigheOrdine {
    public Long id;
    public Long ordine_id;
    public Long prodotto_id;
    public Integer quantita;
    public BigDecimal prezzo_unitario;
    public BigDecimal iva;
    public BigDecimal totale_riga;
}
