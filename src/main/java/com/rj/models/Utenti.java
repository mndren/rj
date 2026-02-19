package com.rj.models;

import java.time.LocalDateTime;

public class Utenti {
    public Long id;
    public String username;
    public String password_hash;
    public String ruolo;
    public Boolean attivo;
    public LocalDateTime created_at;
}
