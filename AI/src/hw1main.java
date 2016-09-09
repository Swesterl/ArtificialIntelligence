package AIproject;


import java.util.Scanner;

public class hw1main {

    static double[][] a;
    static double[][] b;
    static double[][] pi;


    public static void main(String[] args) {
        //createMockData();
        readData();
        hmm1();
        //matrixMult(a, b);
        //ElementWiseMatrixMult(a, b);

    }

    public static void hmm1() {
        double[][] currentState = matrixMult(pi, a);
        double[][] currentProbableObservation = matrixMult(currentState, b);
        printMatrixForKattis(currentProbableObservation);
    }



    public static void readData() {
        Scanner sc = new Scanner(System.in);
        long rowA = sc.nextLong();
        long colA = sc.nextLong();
        double[][] trans = new double[(int) rowA][(int) colA];
        for (int i = 0; i < rowA; i++) {
            for (int j = 0; j < colA; j++) {
                double tempus = sc.nextDouble();
                trans[i][j] = tempus;
            }
        }
        a = trans;
        long rowB = sc.nextLong();
        long colB = sc.nextLong();
        double[][] emit = new double[(int) rowB][(int) colB];
        for (int i = 0; i < rowB; i++) {
            for (int j = 0; j < colB; j++) {
                double tempus = sc.nextDouble();
                emit[i][j] = tempus;
            }
        }
        b = emit;
        long rowPI = sc.nextLong();
        long colPI = sc.nextLong();
        double[][] initial = new double[(int) rowPI][(int) colPI];
        for (int i = 0; i < colPI; i++) {
            double tempus = sc.nextDouble();
            initial[0][i] = tempus;
        }
        pi = initial;
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
        double[][] piTemp = {{0.2, 0.4, 0.4, 0.0}};
        a = aTemp;
        b = bTemp;
        pi = piTemp;
    }


    public static double[][] matrixMult(double[][] first, double[][] second) {
        double[][] answer = new double[first.length][second[0].length];
        if (first[0].length == second.length) {
            for (int i = 0; i < answer[0].length; i++) {
                for (int j = 0; j < answer.length; j++) {
                    answer[j][i] = 0;
                    for (int k = 0; k < first[0].length; k++) {
                        answer[j][i] = answer[j][i] + first[j][k] * second[k][i];
                    }

                }
            }
            return answer;
        } else {

            return null;

        }


    }

    public static double[][] ElementWiseMatrixMult(double[][] first, double[][] second) {
        double[][] answer = new double[first.length][first[0].length];
        System.out.print("elemntwise multi!");
        if (first.length == second.length && first[0].length == second[0].length) {
            for (int i = 0; i < answer.length; i++) {
                for (int j = 0; j < answer[0].length; j++) {
                    answer[i][j] = first[i][j] * second[i][j];
                }
            }
            return answer;
        } else {
            System.out.print("fel dimensioner för matris multi!");
            return null;
        }


    }


    public static void printMatrix(double[][] m) {
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + " ; ");
            }
            System.out.println();
        }
    }

    public static void printMatrixForKattis(double[][] m) {
        System.out.print(m.length + " " + m[0].length + " ");
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.out.print(m[i][j] + " ");
            }
        }
    }
}