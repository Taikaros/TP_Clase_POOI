package ar.edu.unam.veterinaria.model;

public enum EstadoTurno {
    PENDIENTE(0, "Pendiente"),
    CONFIRMADO(1, "Confirmado"),
    ATENDIDO(2, "Atendido"),
    CANCELADO(3, "Cancelado");

    private final int id;
    private final String descripcion;

    EstadoTurno(int id, String descripcion) {
        this.id = id;
        this.descripcion = descripcion;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }
}