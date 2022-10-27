package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ClientDao extends MongoAnimal<Client> {
    public ClientDao(){
        super(Client.class);
    }
}
