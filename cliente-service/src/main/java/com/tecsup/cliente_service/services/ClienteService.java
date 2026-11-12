package com.tecsup.cliente_service.services;

import com.tecsup.cliente_service.exception.RecursoNoEncontradoException;
import com.tecsup.cliente_service.model.Cliente;
import com.tecsup.cliente_service.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lógica de negocio de clientes.
 */
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;

    @Transactional
    public Cliente registrar(Cliente cliente) {
        cliente.setId(null);
        if (clienteRepository.existsByNroDni(cliente.getNroDni())) {
            throw new DataIntegrityViolationException("DNI duplicado");
        }
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    // Consulta por código (id)
    @Transactional(readOnly = true)
    public Cliente obtener(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con código " + id + " no encontrado"));
    }

    @Transactional
    public Cliente actualizar(Long id, Cliente datos) {
        Cliente actual = obtener(id);
        if (clienteRepository.existsByNroDniAndIdNot(datos.getNroDni(), id)) {
            throw new DataIntegrityViolationException("DNI duplicado");
        }
        actual.setNombre(datos.getNombre());
        actual.setApellido(datos.getApellido());
        actual.setDireccion(datos.getDireccion());
        actual.setEstado(datos.getEstado());
        actual.setNroDni(datos.getNroDni());
        return clienteRepository.save(actual);
    }

    @Transactional
    public void eliminar(Long id) {
        Cliente actual = obtener(id);
        clienteRepository.delete(actual);
    }
}
