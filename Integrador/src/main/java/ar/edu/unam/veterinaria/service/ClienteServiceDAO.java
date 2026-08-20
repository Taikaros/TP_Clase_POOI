package ar.edu.unam.veterinaria.service;

import java.util.List;
import ar.edu.unam.veterinaria.DAO.ClienteDAO;
import ar.edu.unam.veterinaria.model.Cliente;

public class ClienteServiceDAO {
    
    private ClienteDAO clienteDAO = new ClienteDAO();

    public void guardarCliente(Cliente cliente) {
        clienteDAO.insertar(cliente);
    }

    public List<Cliente> obtenerTodos() {
        return clienteDAO.obtenerTodos();
    }

    public void actualizarCliente(Cliente cliente) {
        clienteDAO.actualizar(cliente);
    }

    public void darDeBaja(Cliente cliente) {
        cliente.setActivo(false); 
        clienteDAO.actualizar(cliente);
    }
}