package ar.edu.unam.veterinaria.service;

import ar.edu.unam.veterinaria.DAO.MascotaDAO;
import ar.edu.unam.veterinaria.model.Mascota;

public class MascotaService {
    private MascotaDAO mascotaDAO = new MascotaDAO();

    public void guardarMascota(Mascota mascota) {
        mascotaDAO.insertar(mascota);
    }

    public void actualizarMascota(Mascota mascota) {
        mascotaDAO.actualizar(mascota);
    }

    public void eliminarMascota(Mascota mascota) {
        mascotaDAO.eliminarFisico(mascota);
    }
}