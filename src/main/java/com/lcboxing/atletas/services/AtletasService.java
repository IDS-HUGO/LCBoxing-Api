package com.lcboxing.atletas.services;

import com.lcboxing.atletas.models.Atleta;
import com.lcboxing.atletas.models.ContactoEmergencia;
import com.lcboxing.atletas.models.DatosMedicos;
import com.lcboxing.atletas.repositories.AtletasRepository;

import java.sql.SQLException;
import java.util.List;

public class AtletasService {
    private final AtletasRepository repository;

    public AtletasService() {
        this.repository = new AtletasRepository();
    }

    public List<Atleta> getAllAtletas() throws SQLException {
        return repository.findAll();
    }

    public List<Atleta> getAtletasActivos() throws SQLException {
        return repository.findActivos();
    }

    public Atleta getAtletaById(int id) throws SQLException {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Atleta no encontrado"));
    }

    public Atleta createAtleta(Atleta atleta, int idUsuarioRegistro) throws SQLException {
        atleta.setIdUsuarioRegistro(idUsuarioRegistro);
        return repository.create(atleta);
    }

    public Atleta updateAtleta(int id, Atleta atleta) throws SQLException {
        getAtletaById(id); // Verificar que existe
        atleta.setIdAtleta(id);
        return repository.update(atleta);
    }

    public boolean deleteAtleta(int id) throws SQLException {
        return repository.delete(id);
    }

    public DatosMedicos getDatosMedicos(int idAtleta) throws SQLException {
        return repository.findDatosMedicosByAtleta(idAtleta)
                .orElse(null);
    }

    public DatosMedicos saveDatosMedicos(int idAtleta, DatosMedicos datos) throws SQLException {
        getAtletaById(idAtleta); // Verificar que el atleta existe
        datos.setIdAtleta(idAtleta);
        return repository.saveDatosMedicos(datos);
    }

    public List<ContactoEmergencia> getContactos(int idAtleta) throws SQLException {
        return repository.findContactosByAtleta(idAtleta);
    }

    public ContactoEmergencia addContacto(int idAtleta, ContactoEmergencia contacto) throws SQLException {
        getAtletaById(idAtleta); // Verificar que el atleta existe
        contacto.setIdAtleta(idAtleta);
        return repository.createContacto(contacto);
    }

    public boolean deleteContacto(int idContacto) throws SQLException {
        return repository.deleteContacto(idContacto);
    }
}