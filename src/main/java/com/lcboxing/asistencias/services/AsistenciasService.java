package com.lcboxing.asistencias.services;

import com.lcboxing.asistencias.models.Asistencia;
import com.lcboxing.asistencias.repositories.AsistenciasRepository;
import java.sql.Date;
import java.time.LocalDate;
import java.sql.SQLException;
import java.sql.Time;
import java.util.List;

public class AsistenciasService {
    private final AsistenciasRepository repository;

    public AsistenciasService() {
        this.repository = new AsistenciasRepository();
    }

    public List<Asistencia> getByFecha(LocalDate fecha) throws SQLException {
        return repository.findByFecha(fecha);
    }

    public List<Asistencia> getByAtleta(int idAtleta) throws SQLException {
        return repository.findByAtleta(idAtleta);
    }

    public Asistencia getById(int id) throws SQLException {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asistencia no encontrada"));
    }

    public Asistencia registrarEntrada(Asistencia asistencia, int idUsuario) throws SQLException {
        asistencia.setIdUsuarioRegistroEntrada(idUsuario);
        return repository.create(asistencia);
    }

    public Asistencia registrarSalida(int idAsistencia, Time horaSalida, int idUsuario) throws SQLException {
        return repository.registrarSalida(idAsistencia, horaSalida, idUsuario);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
