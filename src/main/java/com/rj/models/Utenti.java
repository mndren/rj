package com.rj.models;

import com.rj.business.BaseModel;
import com.rj.business.annotations.Column;
import com.rj.business.annotations.Form;
import com.rj.business.annotations.OrderBy;
import com.rj.business.annotations.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@OrderBy("username")
@Table(name = "utenti")
public class Utenti extends BaseModel<Utenti> {
    @Column(id = true)
    public Long id;
    @Form(value = "Username", autofocus = true)
    public String username;
    @Form(value = "Password", type = "password", required = true, hideValueInTable = true)
    public String password_hash;
    @Form(value = "Ruolo", type = "ruolo")
    public String ruolo;
    @Form(value = "Attivo")
    public Boolean attivo;

    //    @Form(value = "Creato Il", visible = false)
    //    public LocalDateTime created_at;
}
