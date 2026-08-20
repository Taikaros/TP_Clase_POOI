public class Calculadora 
{
    private static final double PI = 3.14159;

    public static int sumar(int a, int b)
    {
        return a + b;
    }

    public static int restar(int a, int b)
    {
        return a - b;
    }

    public static int multiplicar(int a, int b)
    {
        return a * b;
    }

    public static double dividir(double a, double b)
    {
        if (b != 0)
        {
            return a / b;
        } else
        {
            return Double.NaN;
        }
    }

    public static double calcularAreaCirculo(double radio)
    {
        return PI * radio * radio;
    }

    public static long calcularFactorial(int a)
    {
        if (a <= 0)
        {
            return 1; // error
        } else
        {
            long factorial = 1;
            for (int i = 1; i <= a; i++)
            {
                factorial *= i;
            }
            return factorial;
        }
    }

    public static void main(String[] args)
    {
        int suma = sumar(3, 5);
        System.out.println("Resultado suma: " + suma);

        int resta = restar(14, 24);
        System.out.println("Resultado resta: " + resta);

        int multiplicacion = multiplicar(10, 12);
        System.out.println("Resultado multiplicación: " + multiplicacion);

        double division = dividir(7, 0);
        System.out.println("Resultado división: " + division);

        double area = calcularAreaCirculo(34);
        System.out.println("Área círculo: " + area);

        long factorial = calcularFactorial(-2);
        System.out.println("Factorial: " + factorial);
    }
}