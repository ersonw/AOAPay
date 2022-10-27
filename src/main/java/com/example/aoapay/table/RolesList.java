package com.example.aoapay.table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.GeneratedValue;
import java.util.ArrayList;
import java.util.List;

@ToString(includeFieldNames = true)
@Setter
@Getter
@Document(collection = "rolesList")
public class RolesList {
    @Id
    @GeneratedValue
    private String id;
    private String name=null;
    private String description=null;
    private List<RoleList> rolesList = new ArrayList<>();
    private long addTime = System.currentTimeMillis();
    private long updateTime = System.currentTimeMillis();
}
