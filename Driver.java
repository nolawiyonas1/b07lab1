import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) {
        Polynomial test6 = new Polynomial(new double[] {2.34, -5.67, 8.9}, new int[] {3, 2, 1});
        Polynomial test7 = new Polynomial(new double[] {-3.21, 4.56}, new int[] {2, 5});

        Polynomial result3 = test7.multiply(test6);

        try {
            result3.saveToFile("file1.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(result3.evaluate(-10.5));

        result3 = test6.multiply(test7);
        System.out.println(result3.evaluate(-10.5));

        result3 = test6.add(test7);
        System.out.println(result3.evaluate(10.5));

        result3 = test7.add(test6);
        System.out.println(result3.evaluate(10.5));

        try {
            result3.saveToFile("file2.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Contents of file1.txt:");
        displayFileContents("file1.txt");

        System.out.println("Contents of file2.txt:");
        displayFileContents("file2.txt");
    }

    public static void displayFileContents(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
