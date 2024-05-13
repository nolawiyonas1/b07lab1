public class Polynomial {
    double coefficients[];

    public Polynomial(){
        coefficients = new double[]{0.0};
    }

    public Polynomial(double arr[]){
        coefficients = new double[arr.length];
        for (int i=0; i < arr.length; i++){
            coefficients[i] = arr[i];
        }
    }

    public Polynomial add(Polynomial polynomial) {
        int maxSize = Math.max(coefficients.length, polynomial.coefficients.length);
        double[] resultCoefficients = new double[maxSize];
        
        for (int i = 0; i < maxSize; i++) {
            double newCoefficient = 0.0; // for coefficients
            double argCoefficient = 0.0; // for argument
            
            if (i < coefficients.length) {
                newCoefficient = coefficients[i];
            }
            
            if (i < polynomial.coefficients.length) {
                argCoefficient = polynomial.coefficients[i];
            }
            
            resultCoefficients[i] = newCoefficient + argCoefficient; // add like terms
        }
        
        return new Polynomial(resultCoefficients); // setting coefficients accordingly
    }
    

    public double evaluate(double x){
        double solution = 0.0;
        for (int i = 0; i < coefficients.length; i++) {
            solution += coefficients[i] * Math.pow(x, i);
        }
        return solution;
    }

    public boolean hasRoot(double x){
        return evaluate(x) == 0.0;
    }
}


// TEST WITH MORE TEST CASES !!!