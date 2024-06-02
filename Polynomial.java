import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;

public class Polynomial { 
    double nonZeroCoefficients[];
    int exponents[];

    public Polynomial(){
        this.nonZeroCoefficients = new double[0];
        this.exponents = new int[0];
    }

    public Polynomial(double nonZeroCoefficients[], int exponents[]){
        this.nonZeroCoefficients = new double[nonZeroCoefficients.length];
        this.exponents = new int[exponents.length];

        for (int i = 0; i < nonZeroCoefficients.length; i++){
            this.nonZeroCoefficients[i] = nonZeroCoefficients[i];
            this.exponents[i] = exponents[i];
        }
    }

    public Polynomial(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        reader.close();

        String[] terms = line.split("(?=[+-])");

        this.nonZeroCoefficients = new double[terms.length];
        this.exponents = new int[terms.length];

        for (int i = 0; i < terms.length; i++) {
            String term = terms[i].trim();

            String[] parts = term.split("x", 2);
            double coefficient;
            if (parts[0].isEmpty() || parts[0].equals("+")) {
                coefficient = 1.0;
            } else if (parts[0].equals("-")) {
                coefficient = -1.0;
            } else {
                coefficient = Double.parseDouble(parts[0]);
            }

            int exponent = 0;
            if (parts.length == 2) {
                if (parts[1].isEmpty()) {
                    exponent = 1;
                } else {
                    exponent = Integer.parseInt(parts[1]);
                }
            }

            nonZeroCoefficients[i] = coefficient;
            exponents[i] = exponent;
        }
    }

    public Polynomial add(Polynomial polynomial_to_add) {
        // sort in case exponents are not in order
        sortExponentsAndCoefficients(nonZeroCoefficients, exponents);
        sortExponentsAndCoefficients(polynomial_to_add.nonZeroCoefficients, polynomial_to_add.exponents);

        int[] filled_exponents = addPlaceholders(exponents);
        double[] filled_nonZeroCoefficients = addPlaceholders(nonZeroCoefficients, filled_exponents.length, exponents);

        int[] filled_exponents_2 = addPlaceholders(polynomial_to_add.exponents); // putting 0's
        double[] filled_nonZeroCoefficients_2 = addPlaceholders(polynomial_to_add.nonZeroCoefficients, filled_exponents_2.length, polynomial_to_add.exponents); // putting 0's on the same indexes as exponents


        int maxSize = Math.max(filled_nonZeroCoefficients.length, filled_nonZeroCoefficients_2.length);
        double[] resultCoefficients = new double[maxSize];
        
        for (int i = 0; i < maxSize; i++) {
            double newCoefficient = 0.0; // for coefficients
            double argCoefficient = 0.0; // for argument
            
            if (i < filled_nonZeroCoefficients.length) {
                newCoefficient = filled_nonZeroCoefficients[i];
            }
            
            if (i < filled_nonZeroCoefficients_2.length) {
                argCoefficient = filled_nonZeroCoefficients_2[i];
            }
            
            resultCoefficients[i] = newCoefficient + argCoefficient; // add like terms
        }

        // Remove filler zeros and corresponding coefficients
        int maxSizeExponents = Math.max(filled_exponents.length, filled_exponents_2.length);
        int[] resultExponents = new int[maxSizeExponents];
        double[] resultNonZeroCoefficients = new double[resultCoefficients.length];
        int nonZeroCount = 0;

        for (int i = 0; i < maxSizeExponents; i++) {
            if (resultCoefficients[i] != 0) { // if not a filler
                resultExponents[nonZeroCount] = i; // add them to new array as the first elements
                resultNonZeroCoefficients[nonZeroCount] = resultCoefficients[i];
                nonZeroCount++;
            }
        }

        // Trim arrays to remove excess zeros on the right side of the array
        int[] trimmedExponents = new int[nonZeroCount];
        double[] trimmedNonZeroCoefficients = new double[nonZeroCount];

        for (int i = 0; i < nonZeroCount; i++) {
            trimmedExponents[i] = resultExponents[i];
            trimmedNonZeroCoefficients[i] = resultNonZeroCoefficients[i];
        }

        return new Polynomial(trimmedNonZeroCoefficients, trimmedExponents); // setting coefficients accordingly
    }
    
    public Polynomial multiply(Polynomial polynomial_to_multiply) {
        int length_coefficients = nonZeroCoefficients.length;
        int length_coefficients2 = polynomial_to_multiply.nonZeroCoefficients.length;

        int length_exponents = exponents.length;
        int length_exponents2 = polynomial_to_multiply.exponents.length;

        double[] first_iteration_coefficients = new double[length_coefficients * length_coefficients2];
        int[] first_iteration_exponents = new int[length_exponents * length_exponents2];

        // multiplication (unsorted and redundant exponents)
        int index = 0;
        for (int i = 0; i < length_coefficients; i++) {
            for (int j = 0; j < length_coefficients2; j++){
                first_iteration_coefficients[index] = nonZeroCoefficients[i] * polynomial_to_multiply.nonZeroCoefficients[j];
                first_iteration_exponents[index] = exponents[i] + polynomial_to_multiply.exponents[j];
                index++;
            }
        }

        // addition (unsorted)
        for (int i = 0; i < first_iteration_exponents.length; i++) {
            for (int j = i + 1; j < first_iteration_exponents.length; j++) {
                if (first_iteration_exponents[i] == first_iteration_exponents[j]) {
                    first_iteration_coefficients[i] += first_iteration_coefficients[j];
                    first_iteration_coefficients[j] = 0.0; // Zero out the coefficient at index j
                }
            }
        }

        int nonZeroCount = 0;
        for (int i = 0; i < first_iteration_coefficients.length; i++) {
            if (first_iteration_coefficients[i] != 0.0) {
                nonZeroCount++;
            }
        }

        double[] second_iteration_coefficients = new double[nonZeroCount];
        int[] second_iteration_exponents = new int[nonZeroCount];

        int index2 = 0;
        for (int i = 0; i < first_iteration_coefficients.length; i++) {
            if (first_iteration_coefficients[i] != 0.0) {
                second_iteration_coefficients[index2] = first_iteration_coefficients[i];
                second_iteration_exponents[index2] = first_iteration_exponents[i];
                index2++;
            }
        }        

        // sorting
        sortExponentsAndCoefficients(second_iteration_coefficients, second_iteration_exponents);

        return new Polynomial(second_iteration_coefficients, second_iteration_exponents);
    }

    public int[] addPlaceholders(int[] array) {
        // Determine the maximum value in the array
        int max = array[0]; // first element
        for (int num = 0; num < array.length; num++) {
            if (array[num] > max) {
                max = array[num];
            }
        }

        // new array with one more element than max num
        int[] resultArray = new int[max + 1];
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = 0;
        }

        // place values into the original array accordingly
        for (int num1 = 0; num1 < resultArray.length; num1++) {
            resultArray[num1] = num1;
        }

        return resultArray;
    }

    public double[] addPlaceholders(double[] coefficients, int newSize, int[] new_exponents) {
        double[] resultCoefficients = new double[newSize];

        for (int j = 0; j < resultCoefficients.length; j++) {
            resultCoefficients[j] = 0;
        }

        for (int i = 0; i < coefficients.length; i++) {
            resultCoefficients[new_exponents[i]] = coefficients[i]; // notice not filled exponents array
        }
        return resultCoefficients;
    }

    public void sortExponentsAndCoefficients(double[] coefficients, int[] sortexponents) { // bubble sort
        // sort exponents and rearrange coefficients accordingly
        for (int i = 0; i < sortexponents.length - 1; i++) {
            for (int j = 0; j < sortexponents.length - i - 1; j++) {
                if (sortexponents[j] > sortexponents[j + 1]) {
                    // swap exponents
                    int tempExponent = sortexponents[j];
                    sortexponents[j] = sortexponents[j + 1];
                    sortexponents[j + 1] = tempExponent;
    
                    // swap coefficients
                    double tempCoefficient = coefficients[j];
                    coefficients[j] = coefficients[j + 1];
                    coefficients[j + 1] = tempCoefficient;
                }
            }
        }
    }

    public void saveToFile(String fileName) throws IOException {
        File file = new File(fileName);
        FileWriter writer = new FileWriter(file);

        if (this.exponents.length == 0) {
            writer.close();
            return;
        }

        for (int i = 0; i < exponents.length; i++) {
            double coefficient = nonZeroCoefficients[i];
            int exponent = exponents[i];

            if (coefficient != 0) {
                if (coefficient > 0 && i != 0) {
                    writer.write("+");
                }
                if (exponent == 0) {
                    writer.write(Double.toString(coefficient));
                } else if (exponent == 1) {
                    writer.write(coefficient + "x");
                } else {
                    writer.write(coefficient + "x" + exponent);
                }
            }
        }

        writer.flush();
        writer.close();
    }

    public double evaluate(double x){
        double solution = 0.0;

        for (int i = 0; i < nonZeroCoefficients.length; i++) {  
            solution += nonZeroCoefficients[i] * Math.pow(x, exponents[i]);
        }

        return solution;
    }

    public boolean hasRoot(double x){
        return evaluate(x) == 0.0;
    }

}
