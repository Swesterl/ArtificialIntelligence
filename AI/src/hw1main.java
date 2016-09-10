package AIproject;

//Code by Simon Westerlind & Johan Fredin Haslum

import java.util.Scanner;

public class hw1main {

    static double[][] a;
    static double[][] b;
    static double[][] pi;
    static double[] obs;

    public static void main(String[] args) {
        //createMockData();
        //readDataHMM1();
        readDataHMM2();

        hmm2();
        //hmm1();

        //matrixMult(a, b);
        //ElementWiseMatrixMult(a, b);

    }

    public static void hmm1() {
        double[][] currentState = matrixMult(pi, a);
        double[][] currentProbableObservation = matrixMult(currentState, b);
        printMatrixForKattis(currentProbableObservation);
    }

    public static void hmm2() {

        double[][] alpha0 = initAlpha(pi, b);
        double[][] alpha = new double[b.length][obs.length];

        for(int i = 0 ; i < alpha.length ; i++){
            alpha[i][0] = alpha0[i][0];
        }


        //Starting iterative step
        for(int t = 1 ; t < obs.length ; t++){
            for(int i = 0 ; i < b.length ; i++){
                for(int j = 0 ; j < b.length ; j++){
                    alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i];
                }
                alpha[i][t] = alpha[i][t]*b[i][(int)obs[t]];
            }

        }
        double evalSum = 0;
        for(int i  = 0 ; i < alpha.length ; i++){
            evalSum += alpha[i][alpha[0].length-1];
        }
        System.out.println(evalSum);
    }

    public static double[][] initAlpha(double[][] pi, double[][] b){
        double[][] alphaNoll = new double[pi[0].length][1];
        for(int i = 0 ; i < pi[0].length ;  i++){
            //Elementsiwe mult on two vectors
            alphaNoll[i][0] = pi[0][i]*b[i][(int)obs[0]];
        }


        return alphaNoll;
    }

    public static void readDataHMM1() {
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

    public static void readDataHMM2() {
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

        long colObs = sc.nextLong();
        double[] obsTemp = new double[(int) colObs];
        for (int i = 0; i < colObs; i++) {
            double tempus = sc.nextDouble();
            obsTemp[i] = tempus;
        }
        obs = obsTemp;
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
            System.out.print("fel dimensioner för matris multi!");
            return null;

        }


    }

    public static double[][] ElementWiseMatrixMult(double[][] first, double[][] second) {
        double[][] answer = new double[first.length][first[0].length];
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
    public static void printVector(double[] v) {

        System.out.println("printing vector--------------------------------------------");
        for (int i = 0; i < v.length; i++) {
                System.out.print(v[i] + " ; ");
        }
        System.out.println();
    }

    public static void printMatrix(double[][] m) {

        System.out.println("printing matrix------------------------------------");
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