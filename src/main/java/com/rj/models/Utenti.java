package com.rj.models;

import com.rj.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Table(name = "utenti")
public class Utenti {
    public Long id;
    public String username;
    public String password_hash;
    public String ruolo;
    public Boolean attivo;
    public LocalDateTime created_at;
}
