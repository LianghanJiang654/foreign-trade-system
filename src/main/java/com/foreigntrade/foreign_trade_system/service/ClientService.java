package com.foreigntrade.foreign_trade_system.service;

import com.foreigntrade.foreign_trade_system.model.Client;
import com.foreigntrade.foreign_trade_system.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }
    public Client createClient( Client clients){
        return clientRepository.save(clients);
    }
}
