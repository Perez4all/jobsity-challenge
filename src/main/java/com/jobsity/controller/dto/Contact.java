package com.jobsity.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jobsity.model.BaseContact;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contact extends BaseContact {

    private String source;

    @Override
    @JsonProperty("createdAt")
    public Date getCreated_at() {
        return super.getCreated_at();
    }

    @Override
    @JsonProperty("updatedAt")
    public Date getUpdated_at() {
        return super.getUpdated_at();
    }
}
