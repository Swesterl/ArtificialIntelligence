package AIproject;

//Code by Simon Westerlind & Johan Fredin Haslum

import java.util.Scanner;

public class hw1main {

    static double[][] a;
    static double[][] b;
    static double[][] pi;
    static double[] obs;
    static double[][] delta;
    static double[][] deltaIDX;
    //
    static int iters;
    static int maxIters;
    static double logProb;
    static double oldLogProb;
    static double[][] alphaNoll;
    static double c;
    static double[] cArray;
    static double[][] beta;
    static double[][][] diGamma;
    static double[][] gamma;
    static double[][] alpha;
    static double numer;
    static double denom;
    static int N;
    static int M;
    static int T;
    //

    public static void main(String[] args) {
        //createMockData();
        //readDataHMM1();
        //hmm1();
        //readDataHMM2();
        //hmm2();

        //readDataHMMC();

        readDataHMM4();
        hmm4();


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

    public static void hmm3() {
        delta = new double[a.length][obs.length];
        deltaIDX = new double[a.length][obs.length];
        //Initialize delta
        for(int i = 0 ; i < a.length  ; i++) {
            double pLoL = pi[0][i];
            double obsLoL = b[i][(int) obs[0]];
            delta[i][0] = pLoL * obsLoL;
            deltaIDX[i][0] = i;
        }
        //System.out.println("Delta efter första raden");
        //printMatrix(delta);

        for(int t = 1 ;  t < obs.length ; t++){
            for(int i = 0 ;  i < a.length ; i++){
                double[] list = new double[a.length];
                for(int j = 0 ; j < a.length ; j++){
                    list[j] = a[j][i]*delta[j][t-1]*b[i][(int)obs[t]];
                }
                delta[i][t] = findMaxVal(list);
                deltaIDX[i][t] = findMaxIndex(list);
            }
        }
        /*
        System.out.println("Delta efter hela");
        printMatrix(delta);
        System.out.println("DeltaIDX efter första raden");
        printMatrix(deltaIDX);
        */
        //Backtracking most likely states-steps
        double[] mostLikelyState = new double[delta[0].length];
        double maxIndex = 0;
        for(int i = 1 ; i < a.length ; i++){
            if(delta[(int)maxIndex][delta[0].length-1] < delta[i][delta[0].length-1]){
                maxIndex = i;
            }
        }

        mostLikelyState[mostLikelyState.length-1] = maxIndex;

        for(int i  = delta[0].length-1 ; i > 0 ; i--){
            mostLikelyState[i-1] = deltaIDX[(int)mostLikelyState[i]][i];
        }

        for(int i  = 0 ; i < mostLikelyState.length  ; i++){
            System.out.print((int)mostLikelyState[i] + " ");
        }

    }

    public static void hmm4old() {
        iters = 0;
        maxIters = 100;
        logProb = -1999;
        oldLogProb = -20000000;
        c = 0;
        numer = 0;
        denom = 0;

        printAllGiven();

        do {

            oldLogProb = logProb;
            //
            c = 0;
            for(int i = 0 ; i < pi[0].length ;  i++){
                alphaNoll[i][0] = pi[0][i]*b[i][(int) obs[0]];
                c = c + pi[0][i]*b[i][(int)obs[0]];
            }
            c = 1/c;
            cArray[0] = c;
            for(int i = 0 ; i < pi[0].length ;  i++){
                alphaNoll[i][0] = alphaNoll[i][0]*c;
            }

            double[][] alpha = new double[b.length][obs.length];


            for(int i = 0 ; i < alpha.length ; i++){
                alpha[i][0] = alphaNoll[i][0];
            }


            //Starting iterative step

            for(int t = 1 ; t < obs.length; t++){
                c = 0;
                for(int i = 0 ; i < b.length ; i++){
                    alpha[i][t] = 0;
                    for(int j = 0 ; j < b.length ; j++){
                        alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i];
                    }
                    alpha[i][t] = alpha[i][t]*b[i][(int) obs[t]];
                    c = c + alpha[i][t];
                }
                c = 1/c;
                for (int i=0; i < b.length; i++){
                    alpha[i][t] = alpha[i][t]*c;
                }
                cArray[t] = c;

            }
            //
            //double[][] beta0 = new double[b.length][1];
            //double[][] beta = new double[b.length][obs.length];


            for (int i=0; i < b.length; i++){
                beta[i][obs.length-1] = c;
            }


            for (int t = obs.length - 2 ; t > -1 ; t--){ // ska det igentligen vara 0 eller -1?
                for (int i=0; i < b.length; i++){
                    beta[i][t] = 0;
                    for(int j = 0 ; j < b.length ; j++){
                        beta[i][t] = beta[i][t] + a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1];
                    }
                    beta[i][t] = beta[i][t]*cArray[t];
                }
            }

            //double[][][] diGamma = new double[b.length][b.length][obs.length];
            //double[][] gamma = new double[b.length][obs.length];


            for (int t=0 ; t < obs.length-1 ; t++) {
                denom = 0;
                for (int i = 0 ; i < b.length ; i++ ) {
                    for (int j = 0 ; j < b.length ; j++ ) {
                        denom = denom + alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][(int) obs[t+1]];
                    }
                }
                for (int i = 0 ; i < b.length ; i++ ) {
                    gamma[i][t] = 0;
                    for (int j = 0 ; j < b.length ; j++ ) {
                        diGamma[i][j][t] = (alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1])/denom;
                        gamma[i][t] = gamma[i][t] + diGamma[i][j][t];
                    }
                }
            }


            denom = 0;

            for (int i = 0; i < b.length ; i++) {
                denom = denom + alpha[i][alpha[0].length-1];
            }
            for (int i = 0; i < b.length ; i++) {
                gamma[i][obs.length-1] = (alpha[i][alpha[0].length-1])/denom;
            }

            for (int i = 0; i < pi[0].length ; i++) {
                pi[0][i] = gamma[i][0];
            }

            for (int i = 0; i < b.length ; i++) {
                for (int j = 0; j < b.length ; j++) {
                    numer = 0;
                    denom = 0;
                    for (int t = 0; t < obs.length - 1; t++){
                        numer = numer + diGamma[i][j][t];
                        denom = denom + gamma[i][t];
                    }
                    a[i][j] = numer/denom;
                }
            }

            for (int i = 0; i < b.length ; i++) {
                for (int j = 0; j < b[0].length ; j++) {
                    numer = 0;
                    denom = 0;
                    for (int t = 0; t < obs.length; t++){
                        if (((int) obs[t]) == j){
                            numer = numer + gamma[i][t];
                        }
                        denom = denom + gamma[i][t];
                    }
                    b[i][j] = numer/denom; // testa att byta
                }
            }

            logProb = 0;
            for (int t = 0 ; t < obs.length ; t++){
                logProb = logProb + Math.log(1/((double) cArray[t]));
            }
            logProb = -logProb;
            System.out.println(logProb);
            iters = iters + 1;

        }while (iters < maxIters);// && logProb >= oldLogProb);

        System.out.println(iters);
        printMatrixForKattis(a);
        System.out.print("\n");
        printMatrixForKattis(b);
    }

    public static void hmm4() {
        int maxIters = 400;
        iters = 0;
        double oldLogProb = 10000000;

        alphaForward();
        betaBackwards();
        gammaMerge();
        reEstimateing();
        logChange();

        while (iters < maxIters) {
            oldLogProb = logProb;
            //System.out.println(logProb);
            alphaForward();
            betaBackwards();
            gammaMerge();
            reEstimateing();
            logChange();
            iters = iters + 1;
            //System.out.println(logProb);
        }
        //System.out.println(iters);
        printMatrixForKattis(a);
        System.out.print("\n");
        printMatrixForKattis(b);
    }

    public static void alphaForward() {

        c = 0;

        for(int i = 0 ; i < N ;  i++){
            alphaNoll[i][0] = pi[0][i]*b[i][(int) obs[0]];
            c = c + alphaNoll[i][0];
        }
        c = 1/c;
        cArray[0] = c;
        for(int i = 0 ; i < N ;  i++){
            alphaNoll[i][0] = alphaNoll[i][0]*c;
        }

        for(int i = 0 ; i < N ; i++){
            alpha[i][0] = alphaNoll[i][0];
        }


        //Starting iterative step

        for(int t = 1 ; t < T; t++){
            c = 0;
            for(int i = 0 ; i < N ; i++){
                alpha[i][t] = 0;
                for(int j = 0 ; j < b.length ; j++){
                    alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i];
                }
                alpha[i][t] = alpha[i][t]*b[i][(int) obs[t]];
                c = c + alpha[i][t];
            }
            c = 1/c;
            for (int i=0; i < N; i++){
                alpha[i][t] = alpha[i][t]*c;
            }
            cArray[t] = c;

        }
    }

    public static void betaBackwards() {

        for (int i=0; i < N; i++){
            beta[i][T-1] = c;
        }


        for (int t = T - 2 ; t >= 0 ; t--){ // ska det igentligen vara 0 eller -1?
            for (int i=0; i < N; i++){
                beta[i][t] = 0;
                for(int j = 0 ; j < N ; j++){
                    beta[i][t] = beta[i][t] + a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1];
                }
                beta[i][t] = beta[i][t]*cArray[t];
            }
        }
    }

    public static void gammaMerge() {

        double evalSum = 0;
        for(int i  = 0 ; i < N ; i++){
            evalSum += alpha[i][T-1];
        }


        for (int t=0 ; t < T - 1 ; t++) {
            denom = 0;
            for (int i = 0 ; i < N ; i++ ) {
                for (int j = 0 ; j < N ; j++ ) {
                    denom = denom + alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][(int) obs[t+1]];
                }
            }
            for (int i = 0 ; i < N ; i++ ) {
                gamma[i][t] = 0;
                for (int j = 0 ; j < N ; j++ ) {
                    diGamma[i][j][t] = (alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1])/evalSum;//denom;
                    gamma[i][t] = gamma[i][t] + diGamma[i][j][t];
                }
            }
        }

        denom = 0;
        for (int i = 0; i < N ; i++) {
            denom = denom + alpha[i][T-1];
        }
        for (int i = 0; i < N ; i++) {
            gamma[i][T-1] = (alpha[i][T-1])/denom;
        }
    }

    public static void reEstimateing() {

        for (int i = 0; i < N ; i++) {
            pi[0][i] = gamma[i][0];
        }

        for (int i = 0; i < N ; i++) {
            for (int j = 0; j < N ; j++) {
                numer = 0;
                denom = 0;
                for (int t = 0; t < T - 1; t++){
                    numer = numer + diGamma[i][j][t];
                    denom = denom + gamma[i][t];
                }
                a[i][j] = numer/denom;
            }
        }

        for (int i = 0; i < N ; i++) {
            for (int j = 0; j < M ; j++) {
                numer = 0;
                denom = 0;
                for (int t = 0 ; t < T - 1; t++){
                    if (((int) obs[t]) == j){
                        numer = numer + gamma[i][t];
                    }
                    denom = denom + gamma[i][t];
                }
                b[i][j] = numer/denom;
            }
        }
    }

    public static void logChange() {
        logProb = 0;
        for (int t = 0 ; t < T ; t++){
            logProb = logProb + Math.log(1/((double) cArray[t]));
        }
        logProb = -logProb;
    }

    public static double[][] initAlpha(double[][] pi, double[][] b){
        double[][] alphaNoll = new double[pi[0].length][1];
        for(int i = 0 ; i < pi[0].length ;  i++){
            //Elementsiwe mult on two vectors
            alphaNoll[i][0] = pi[0][i]*b[i][(int)obs[0]];
        }


        return alphaNoll;
    }

    public static double[][] initAlphaNorm(double[][] pi, double[][] b){
        double[][] alphaNoll = new double[pi[0].length][1];
        double c = 0;
        for(int i = 0 ; i < pi[0].length ;  i++){
            //Elementsiwe mult on two vectors
            alphaNoll[i][0] = pi[0][i]*b[i][(int)obs[0]];
            c = c + pi[0][i]*b[i][(int)obs[0]];
        }
        c = 1/c;

        for(int i = 0 ; i < pi[0].length ;  i++){
            alphaNoll[i][0] = alphaNoll[i][0]*c;
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

    public static void readDataHMM3() {
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

    public static void readDataHMM4() {
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

        N = b.length;
        M = b[0].length;
        T = obs.length;
        double[][] alphaNoll1 = new double[pi[0].length][1];
        double[] cArray1 = new double[T];
        double[][] beta1 = new double[N][T];
        double[][][] diGamma1 = new double[N][N][T];
        double[][] gamma1 = new double[N][T];
        double[][] alpha1 = new double[N][T];
        alphaNoll = alphaNoll1;
        cArray = cArray1;
        beta = beta1;
        diGamma = diGamma1;
        gamma = gamma1;
        alpha = alpha1;



    }

    public static void readDataHMMC() {

    	/*
    	Question 7:

    	int rowA = 3;
        int colA = 3;
    	double[] inputA = {0.54,0.26,0.2,0.19,0.53,0.28,0.22,0.18,0.6};

    	int rowB = 3;
        int colB = 4;
        double[] inputB = {0.5,0.2,0.11,0.19,0.22,0.28,0.23,0.27,0.19,0.21,0.15,0.45};

    	int rowPI = 1;
        int colPI = 3;
        double[] inputPI = {0.3,0.2,0.5};


        Question 8:
        int rowA = 3;
        int colA = 3;
    	double[] inputA = {0.64,0.16,0.2,0.29,0.43,0.28,0.12,0.28,0.6};

    	int rowB = 3;
        int colB = 4;
        double[] inputB = {0.4,0.3,0.11,0.19,0.12,0.38,0.23,0.27,0.09,0.31,0.15,0.45};

    	int rowPI = 1;
        int colPI = 3;
        double[] inputPI = {0.3,0.2,0.5};

        Question 9:
        a:


    	int rowA = 2;
        int colA = 2;
    	double[] inputA = {0.64,0.34,0.2,0.8};

    	int rowB = 2;
        int colB = 4;
        double[] inputB = {0.4,0.3,0.11,0.19,0.12,0.38,0.23,0.27};

    	int rowPI = 1;
        int colPI = 2;
        double[] inputPI = {0.4,0.6};

        b:

    	int rowA = 4;
        int colA = 4;
    	double[] inputA = {0.54,0.1,0.1,0.24,0.1,0.1,0.1,0.7,0.25,0.35,0.15,0.25,0.3,0.2,0.1,0.4};

    	int rowB = 4;
        int colB = 4;
        double[] inputB = {0.4,0.3,0.11,0.19,0.12,0.38,0.23,0.27,0.15,0.35,0.4,0.1,0.22,0.28,0.21,0.29};

    	int rowPI = 1;
        int colPI = 4;
        double[] inputPI = {0.22,0.28,0.26,0.24};
        */
        /*
        Question 10:
        a:

        int rowA = 3;
        int colA = 3;
        double[] inputA = {0.33,0.33,0.34,0.33,0.34,0.33,0.34,0.33,0.33};

        int rowB = 3;
        int colB = 4;
        double[] inputB = {0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25,0.25};

        int rowPI = 1;
        int colPI = 3;
        double[] inputPI = {0.33,0.34,0.33};

        b:

        int rowA = 3;
        int colA = 3;
        double[] inputA = {0.5,0.25,0.25,0.25,0.5,0.25,0.25,0.25,0.5};

        int rowB = 3;
        int colB = 4;
        double[] inputB = {0.5,0.2,0.11,0.19,0.22,0.28,0.23,0.27,0.19,0.21,0.15,0.45};

        int rowPI = 1;
        int colPI = 3;
        double[] inputPI = {0,0,1};

        */

        int rowA = 3;
        int colA = 3;
        double[] inputA = {0.68,0.06,0.26,0.09,0.82,0.09,0.22,0.27,0.51};

        int rowB = 3;
        int colB = 4;
        double[] inputB = {0.68,0.24,0.07,0.01,0.12,0.37,0.29,0.22,0.02,0.12,0.18,0.68};

        int rowPI = 1;
        int colPI = 3;
        double[] inputPI = {0.98,0.01,0.01};






        Scanner sc = new Scanner(System.in);

        double[][] trans = new double[(int) rowA][(int) colA];
        int g = 0;
        for (int i = 0; i < rowA; i++) {
            for (int j = 0; j < colA; j++) {
                trans[i][j] = inputA[g];
                g = g + 1;
            }
        }
        a = trans;
        double[][] emit = new double[(int) rowB][(int) colB];
        g = 0;
        for (int i = 0; i < rowB; i++) {
            for (int j = 0; j < colB; j++) {
                emit[i][j] = inputB[g];
                g = g + 1;
            }
        }
        b = emit;
        double[][] initial = new double[(int) rowPI][(int) colPI];
        for (int i = 0; i < colPI; i++) {
            initial[0][i] = inputPI[i];
        }
        pi = initial;

        long colObs = sc.nextLong();
        double[] obsTemp = new double[(int) colObs];
        for (int i = 0; i < colObs; i++) {
            double tempus = sc.nextDouble();
            obsTemp[i] = tempus;
        }
        obs = obsTemp;

        N = b.length;
        M = b[0].length;
        T = obs.length;
        double[][] alphaNoll1 = new double[pi[0].length][1];
        double[] cArray1 = new double[T];
        double[][] beta1 = new double[N][T];
        double[][][] diGamma1 = new double[N][N][T];
        double[][] gamma1 = new double[N][T];
        double[][] alpha1 = new double[N][T];
        alphaNoll = alphaNoll1;
        cArray = cArray1;
        beta = beta1;
        diGamma = diGamma1;
        gamma = gamma1;
        alpha = alpha1;



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

    public static double findMaxVal(double[] list){
        double max = list[0];
        for(int i = 0 ; i < list.length ; i++){
            if(list[i]>max){
                max = list[i];
            }
        }
        return max;
    }

    public static double findMaxIndex(double[] list){
        double max = 0;
        for(int i = 0 ; i < list.length ; i++){
            if(list[i]>list[(int)max]){
                max = i;
            }
        }
        return max;
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

    public static void printAllGiven(){
        System.out.println("A");
        printMatrix(a);
        System.out.println("B");
        printMatrix(b);
        System.out.println("pi");
        printMatrix(pi);
        System.out.println("obs");
        printVector(obs);
    }
}