package AIproject;


import java.util.Scanner;

public class hw1main {

    static double[][] a;
    static double[][] b;
    static double[] pi;


    public static void main(String[] args) {
        System.out.println("fuck you kattis");
        //createMockData();
        readData();

    }

    public static void readData() {
        Scanner sc = new Scanner(System.in);
        long rowA = sc.nextLong();
        long colA = sc.nextLong();
        double[][] trans = new double[(int)rowA][(int)colA];
        for(int i=0; i<rowA; i++) {
            for(int j=0; j<colA; j++) {
                double tempus = sc.nextDouble();
                trans[i][j] = tempus;
            }
        }
        a = trans;
        System.out.print(a);
        long rowB = sc.nextLong();
        long colB = sc.nextLong();
        double[][] emit = new double[(int)rowB][(int)colB];
        for(int i=0; i<rowB; i++) {
            for(int j=0; j<colB; j++) {
                double tempus = sc.nextDouble();
                emit[i][j] = tempus;
            }
        }
        b = emit;
        System.out.print(b);
        long rowPI = sc.nextLong();
        long colPI = sc.nextLong();
        double[] initial = new double[(int)colPI];
        for(int i=0; i<colPI; i++) {
            double tempus = sc.nextDouble();
            initial[i] = tempus;
        }
        pi = initial;
        System.out.print(pi);
    }

    public static void createMockData() {
        double[][] aTemp = {
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {0, 1, 2, 3}
        };
        double[][] bTemp = {
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {0, 1, 2, 3}
        };
        double[] piTemp = {0.2, 0.4, 0.4, 0.0};
        a = aTemp;
        b = bTemp;
        pi = piTemp;
    }

}

