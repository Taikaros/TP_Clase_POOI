public enum Categoria
    {
        ELECTRONICA(10.0),
        ROPA(20.0),
        ALIMENTOS(5.0);
    
        private double descuento;

    Categoria(double descuento) {
        this.descuento = descuento;
    }

    public double getDescuento() {
        return descuento;
    }
    }
    
