package com.example.aoapay.dao;

import com.example.aoapay.config.MongoAnimal;
import com.example.aoapay.table.Client;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Repository
public class ClientDao extends MongoAnimal {
    public ClientDao(){
        super(Client.class);
    }

    @Override
    public Client findById(String id) {
        Object o = super.findById(id);
        return o instanceof Client? (Client) o : null;
    }

    @Override
    public List<Client> findAllById(String id) {
        List objects = super.findAllById(id);
        List<Client> list = new ArrayList<>();
        for(Object o : objects) {
            Client c = (Client) o;
            list.add(c);
        }
        return list;
    }
}
