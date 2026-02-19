package com.rj.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Fatture {
    public Long id;
    public Long ordine_id;
    public String numero_fattura;
    public LocalDateTime data_fattura;
    public BigDecimal imponibile;
    public BigDecimal iva;
    public BigDecimal totale;
}
