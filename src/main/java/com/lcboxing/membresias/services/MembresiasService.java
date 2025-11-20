package com.lcboxing.membresias.services;

import com.lcboxing.membresias.models.Membresia;
import com.lcboxing.membresias.repositories.MembresiasRepository;

import java.sql.SQLException;
import java.util.List;

public class MembresiasService {
    private final MembresiasRepository repository;

    public MembresiasService() {
        this.repository = new MembresiasRepository();
    }

    public List<Membresia> getAll() throws SQLException {
        return repository.findAll();
    }

    public List<Membresia> getByAtleta(int idAtleta) throws SQLException {
        return repository.findByAtleta(idAtleta);
    }

    public Membresia getById(int id) throws SQLException {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Membresía no encontrada"));
    }

    public Membresia create(Membresia membresia, int idUsuario) throws SQLException {
        membresia.setIdUsuarioRegistro(idUsuario);
        return repository.create(membresia);
    }

    public Membresia update(int id, Membresia membresia) throws SQLException {
        getById(id);
        membresia.setIdMembresia(id);
        return repository.update(membresia);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }

    public List<Membresia> getVencimientos(int dias) throws SQLException {
        return repository.findVencimientos(dias);
    }
}
