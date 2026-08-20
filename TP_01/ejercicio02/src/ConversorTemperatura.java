
public class ConversorTemperatura {
    private static double convertir(double temperatura, String escalaOrigen, String escalaDestino) {
        
        if (escalaOrigen.equals("C") && temperatura < -273.15) return Double.NaN;
        if (escalaOrigen.equals("K") && temperatura < 0) return Double.NaN;
        
            Double resul = switch (escalaOrigen) {
            case "C" -> switch (escalaDestino) {
                case "F" -> (temperatura * 9.0 / 5.0) + 32.0;
                case "K" -> temperatura + 273.15;
                case "C" -> temperatura; 
                default  -> Double.NaN;
            };
            case "F" -> switch (escalaDestino) {
                case "C" -> (temperatura - 32.0) * 5.0 / 9.0;
                case "K" -> (temperatura - 32.0) * 5.0 / 9.0 + 273.15;
                case "F" -> temperatura;
                default  -> Double.NaN;
            };
            case "K" -> switch (escalaDestino) {
                case "C" -> temperatura - 273.15;
                case "F" -> (temperatura - 273.15) * 9.0 / 5.0 + 32.0;
                case "K" -> temperatura;
                default  -> Double.NaN;
            };
            default -> Double.NaN; 
            
        };
        if (escalaDestino.equals("K") && resul < 0){
            return Double.NaN;
            
        }
        if (escalaDestino.equals("C") && resul< -273.15) return Double.NaN;
        return resul;
    }

    public static void main(String args[]){


        System.out.println("\n0°C a Fahrenheit (debe dar 32°F): " + convertir(0, "C","F"));
        System.out.println("100°C a Fahrenheit (debe dar 212°F): " + convertir(100,"C" ,"F" ));
        System.out.println("0°C a Kelvin(debe dar 273.15K): " + convertir(0,"C", "K"));
        
        System.out.println("\n--- Casos Invalidos (Deben dar NaN) ---");
        System.out.println("Escalas incorrectas (C a X): " + convertir(25, "C", "X"));
        System.out.println("Celsius bajo cero absoluto (-300 C a F): " + convertir(-300, "C", "F"));
        System.out.println("Kelvin negativo (-5K a C): " + convertir(-5, "K", "C"));
    
    }

}
