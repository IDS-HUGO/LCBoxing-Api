package com.lcboxing.pagos.services;

import com.lcboxing.pagos.models.Pago;
import com.lcboxing.pagos.repositories.PagosRepository;
import java.sql.SQLException;
import java.util.List;

public class PagosService {
    private final PagosRepository repository;

    public PagosService() {
        this.repository = new PagosRepository();
    }

    public List<Pago> getAll() throws SQLException {
        return repository.findAll();
    }

    public Pago getById(int id) throws SQLException {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado"));
    }

    public List<Pago> getByMembresia(int idMembresia) throws SQLException {
        return repository.findByMembresia(idMembresia);
    }

    public Pago create(Pago pago, int idUsuario) throws SQLException {
        pago.setIdUsuarioRegistro(idUsuario);
        return repository.create(pago);
    }

    public boolean delete(int id) throws SQLException {
        return repository.delete(id);
    }
}
