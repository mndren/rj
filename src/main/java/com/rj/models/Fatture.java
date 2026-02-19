package com.rj.models;

import com.rj.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "fatture")
public class Fatture {
    public Long id;
    public Long ordine_id;
    public String numero_fattura;
    public LocalDateTime data_fattura;
    public BigDecimal imponibile;
    public BigDecimal iva;
    public BigDecimal totale;
}
