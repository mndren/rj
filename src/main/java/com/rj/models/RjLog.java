package com.rj.models;

import com.rj.business.BaseModel;
import com.rj.business.annotations.Column;
import com.rj.business.annotations.OrderBy;
import com.rj.business.annotations.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@OrderBy("date desc limit 200")
@Table(name = "rj_logs")
public class RjLog extends BaseModel<RjLog> {

    @Column(id = true)
    public Long id;
    @Column(name = "date")
    public LocalDateTime date;
    @Column(name = "level")
    public String level;
    @Column(name = "error")
    public String error;
    @Column(name = "ctx")
    public String context;
}
