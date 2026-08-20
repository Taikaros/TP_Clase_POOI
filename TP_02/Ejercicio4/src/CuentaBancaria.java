package TP_02.Ejercicio4.src;

public class CuentaBancaria {
    private String titular = "Desconocido";
    private double saldo = 0.0;
    private int contadorOperaciones = 0;

    // Lanza específicamente MontoInvalidoException si el saldo inicial es negativo
    public CuentaBancaria(String titular, double saldoInicial) throws MontoInvalidoException {
        if (saldoInicial < 0) {
            throw new MontoInvalidoException("El saldo inicial no puede ser negativo.");
        }
        this.titular = titular;
        this.saldo = saldoInicial;
    }

    public void depositar(double monto) throws MontoInvalidoException {
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto a depositar debe ser mayor a cero.");
        }
        this.saldo += monto;
        this.contadorOperaciones++; // Solo suma si la operación fue exitosa
    }

    // Este método puede lanzar DOS tipos de excepciones distintas
    public void retirar(double monto) throws MontoInvalidoException, SaldoInsuficienteException {
        if (monto <= 0) {
            throw new MontoInvalidoException("El monto a retirar debe ser mayor a cero.");
        }
        if (monto > this.saldo) {
            // Le pasamos el saldo actual y cuánto se quiso sacar
            throw new SaldoInsuficienteException(this.saldo, monto); 
        }
        this.saldo -= monto;
        this.contadorOperaciones++; // Solo suma si la operación fue exitosa
    }

    public double getSaldo() {
        return saldo;
    }

    public String getTitular() {
        return titular;
    }

    public int getContadorOperaciones() {
        return contadorOperaciones;
    }

    @Override
    public String toString() {
        return "Titular: " + titular + " | Saldo Actual: $" + saldo + " | Operaciones Exitosas: " + contadorOperaciones;
    }
}

class MontoInvalidoException extends Exception {
    public MontoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
class SaldoInsuficienteException extends Exception {
    private final double saldoActual;
    private final double montoSolicitado;

    public SaldoInsuficienteException(double saldoActual, double montoSolicitado) {
        // Armamos el mensaje descriptivo automáticamente en el constructor
        super("Saldo insuficiente para realizar la operación.");
        this.saldoActual = saldoActual;
        this.montoSolicitado = montoSolicitado;
    }

    public double getSaldoActual() {
        return saldoActual;
    }

    public double getMontoSolicitado() {
        return montoSolicitado;
    }
} 
// Clase con main para pruebas, fuera de la definición de CuentaBancaria
class CuentaBancariaTest {
    public static void main(String[] args) {
        CuentaBancaria cuenta = null;

        try {
            // 1. Crear una cuenta válida
            cuenta = new CuentaBancaria("Juan Perez", 1000.0);
            System.out.println("--- Cuenta Inicializada ---");
            System.out.println(cuenta);

            // 2. Depósitos y retiros exitosos
            System.out.println("\n--- Operaciones Exitosas ---");
            cuenta.depositar(500.0);
            System.out.println("Después de depositar $500: " + cuenta.getSaldo());
            
            cuenta.retirar(200.0);
            System.out.println("Después de retirar $200: " + cuenta.getSaldo());

            // 3. Probar caso de error: Retirar más del saldo disponible
            System.out.println("\n--- Probando SaldoInsuficienteException ---");
            cuenta.retirar(1500.0); 

        } catch (MontoInvalidoException e) {
            System.out.println("Error de Monto: " + e.getMessage());
        } catch (SaldoInsuficienteException e) {
            System.out.println("Error de Saldo: " + e.getMessage());
            System.out.println("  > Saldo disponible: $" + e.getSaldoActual());
            System.out.println("  > Monto solicitado: $" + e.getMontoSolicitado());
        } finally {
            // El bloque finally se ejecuta SIEMPRE, haya o no excepciones
            System.out.println("\n--- Bloque Finally (Estado Final) ---");
            if (cuenta != null) {
                System.out.println(cuenta);
                System.out.println("Total de operaciones exitosas: " + cuenta.getContadorOperaciones());
            } else {
                System.out.println("La cuenta ni siquiera pudo ser creada.");
            }
        }

        // 4. Probar creación de cuenta inválida o depósitos negativos fuera del flujo principal
        System.out.println("\n--- Probando Creación con Saldo Negativo ---");
        try {
            CuentaBancaria cuentaClon = new CuentaBancaria("Inversor Fantasma", -50.0);
        } catch (MontoInvalidoException e) {
            System.out.println("Capturado correctamente: " + e.getMessage());
        }
    }
}