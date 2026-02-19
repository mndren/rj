package com.rj.models;

import com.rj.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "ordini")
public class Ordini {
    public Long id;
    public Long cliente_id;
    public LocalDateTime data_ordine;
    public String stato;
    public BigDecimal totale;
}
