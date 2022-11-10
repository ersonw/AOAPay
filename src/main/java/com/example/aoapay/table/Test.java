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
@Document(collection = "test")
public class Test {
    @Id
    @GeneratedValue
    private String id;
    private String remark = null;
    private boolean admin =false;
    private Long  addTime=System.currentTimeMillis();
    private Long updateTime = System.currentTimeMillis();
}
