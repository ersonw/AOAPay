package com.example.aoapay.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "roleList")
public class RoleList {
    @Id
    @GeneratedValue
    private String id;
    private int role=0;
    private String routerId;
    private long addTime = System.currentTimeMillis();
    private long updateTime = System.currentTimeMillis();
}
