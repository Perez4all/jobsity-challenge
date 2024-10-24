package com.jobsity.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@NoArgsConstructor
@SuperBuilder
public abstract class BaseContact {
    private int id;
    private String name;
    private String email;
    private Date created_at;
    private Date updated_at;
}
