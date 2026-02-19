package com.rj.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Ordini {
    public Long id;
    public Long cliente_id;
    public LocalDateTime data_ordine;
    public String stato;
    public BigDecimal totale;
}
