//import javafx.util.Pair;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;

public class HMM {

    // the Bicycle class has
    // three fields

    double[][] a;
    double[][] b;
    double[][] pi;
    int[] obs;
    ArrayList<Integer> O;
    int iters;
    int maxIters;
    double logProb;
    double oldLogProb;
    double[][] alphaNoll;
    double c;
    double[] cArray;
    double[][] beta;
    double[][][] diGamma;
    double[][] gamma;
    double[][] alpha;
    double numer;
    double denom;
    int N;
    int M;
    int T;
    public int art;

    // the Bicycle class has
    // one constructor
    public HMM(int states, int observations) {
        Random r = new Random();
        double sumThisRow = 0;
        O = new ArrayList<Integer>(200);

        a = new double[states][states];
        b = new double[states][observations];
        pi = new double[1][states];
        //Give a random values
        for(int i = 0 ; i < a.length ; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (i==j) {
                    double randNr = r.nextDouble() + 1;
                    a[i][j] = randNr;
                    sumThisRow += randNr;
                }
                else {
                    double randNr = r.nextDouble() + 1;
                    a[i][j] = randNr;
                    sumThisRow += randNr;
                }
            }
            for (int k = 0; k < a[0].length; k++) {
                a[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }

        //Give b random values.
        for(int i = 0 ; i < b.length ; i++) {
            for (int j = 0; j < b[0].length; j++) {
                if (i==j) {
                    double randNr = r.nextDouble() + 1;
                    b[i][j] = randNr;
                    sumThisRow += randNr;
                }
                else {
                    double randNr = r.nextDouble() + 1;
                    b[i][j] = randNr;
                    sumThisRow += randNr;
                }
            }
            for (int k = 0; k < b[0].length; k++) {
                b[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }
        //Give pi random values.
        for(int i = 0 ; i < pi.length ; i++) {
            for (int j = 0; j < pi[0].length; j++) {
                double randNr = r.nextDouble() + 1;
                pi[i][j] = randNr;
                sumThisRow += randNr;
            }
            for (int k = 0; k < pi[0].length; k++) {
                pi[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }
    }

    public HMM(double[][] aIn,double[][] bIn,double[][] piIn){
        a = aIn;
        b = bIn;
        pi = piIn;
    }
    // the Bicycle class has
    // four methods
    public void BW() {
        int maxIters = 50;
        iters = 0;
        double oldLogProb = 10000000;


        N = b.length;
        M = b[0].length;
        T = obs.length;
        double[] cArray1 = new double[T];
        double[][] beta1 = new double[N][T];
        double[][][] diGamma1 = new double[N][N][T];
        double[][] gamma1 = new double[N][T];
        double[][] alpha1 = new double[N][T];
        cArray = cArray1;
        beta = beta1;
        diGamma = diGamma1;
        gamma = gamma1;
        alpha = alpha1;

        alphaForward();
        betaBackwards();
        gammaMerge();
        reEstimateing();
        logChange();

        while (iters < maxIters) { //&& oldLogProb > logProb) {
            oldLogProb = logProb;
            alphaForward();
            betaBackwards();
            gammaMerge();
            reEstimateing();
            logChange();
            iters = iters + 1;
            //printMatrix(a);
        }
    }


    public void hmm4() {

        Random r = new Random();
        iters = 0;
        maxIters = 10;
        logProb = -1999;
        oldLogProb = -20000000;
        c = 0;
        numer = 0;
        denom = 0;

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



        do {

            oldLogProb = logProb;
            //
            c = 0;
            for(int i = 0 ; i < pi[0].length ;  i++){
                alpha[i][0] = pi[0][i]*b[i][(int) obs[0]] + 0.01 *r.nextDouble();
                c = c + alpha[i][0];
            }
            c = 1/c;
            cArray[0] = c;
            for(int i = 0 ; i < pi[0].length ;  i++){
                alpha[i][0] = alpha[i][0]*c;
            }


            /*
            for(int i = 0 ; i < alpha.length ; i++){
                alpha[i][0] = alphaNoll[i][0];
            }
            */

            //Starting iterative step

            for(int t = 1 ; t < obs.length; t++){
                c = 0;
                for(int i = 0 ; i < b.length ; i++){
                    alpha[i][t] = 0;
                    for(int j = 0 ; j < b.length ; j++){
                        alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i] + 0.01 *r.nextDouble();
                    }
                    alpha[i][t] = alpha[i][t]*b[i][(int) obs[t]] + 0.01 *r.nextDouble();
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
                        beta[i][t] = beta[i][t] + a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1]  + 0.01 * r.nextDouble();
                    }
                    beta[i][t] = beta[i][t]*cArray[t]  + 0.01 *r.nextDouble();
                }
            }

            //double[][][] diGamma = new double[b.length][b.length][obs.length];
            //double[][] gamma = new double[b.length][obs.length];


            for (int t=0 ; t < obs.length-1 ; t++) {
                denom = 0;
                for (int i = 0 ; i < b.length ; i++ ) {
                    for (int j = 0 ; j < b.length ; j++ ) {
                        denom = denom + alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][(int) obs[t+1]]  + 0.01 * r.nextDouble();
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
                denom = denom + alpha[i][alpha[0].length-1]  + 0.01 * r.nextDouble();
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
                        numer = numer + diGamma[i][j][t]  + 0.01 * r.nextDouble();
                        denom = denom + gamma[i][t]  + 0.01 * r.nextDouble();
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
                            numer = numer + gamma[i][t]  + 0.01 *r.nextDouble();
                        }
                        denom = denom + gamma[i][t]  + 0.01 *r.nextDouble();
                    }
                    b[i][j] = numer/denom; // testa att byta
                }
            }

            logProb = 0;
            for (int t = 0 ; t < obs.length ; t++){
                logProb = logProb + Math.log(((double) cArray[t]));
            }
            logProb = -logProb;
            //System.out.println(logProb);
            iters = iters + 1;

        }while (iters < maxIters);// && logProb >= oldLogProb);


    }

    public void setO(int lastObs) {
        O.add(lastObs);

        int[] observationTemp = new int[O.size()];
        for (int i = 0; i < O.size() ; i++){
            observationTemp[i] = O.get(i);
        }

        obs = observationTemp;
    }

    public void setOL(int[] lastObs) {
        for (int i = 0 ; i < lastObs.length ; i++){
            setO(lastObs[i]);
        }

    }

    public int[] returnO(){
        return obs;
    }


    public double[] delta() {

        Random r = new Random();
        iters = 0;
        maxIters = 10;
        logProb = -1999;
        oldLogProb = -20000000;
        c = 0;
        numer = 0;
        denom = 0;

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



        oldLogProb = logProb;
        //
        c = 0;
        for(int i = 0 ; i < pi[0].length ;  i++){
            alpha[i][0] = pi[0][i]*b[i][(int) obs[0]] + 0.01 *r.nextDouble();
            c = c + alpha[i][0];
        }
        c = 1/c;
        cArray[0] = c;
        for(int i = 0 ; i < pi[0].length ;  i++){
            alpha[i][0] = alpha[i][0]*c;
        }


        /*
        for(int i = 0 ; i < alpha.length ; i++){
            alpha[i][0] = alphaNoll[i][0];
        }
        */

        //Starting iterative step

        for(int t = 1 ; t < obs.length; t++){
            c = 0;
            for(int i = 0 ; i < b.length ; i++){
                alpha[i][t] = 0;
                for(int j = 0 ; j < b.length ; j++){
                    alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i] + 0.01 *r.nextDouble();
                }
                alpha[i][t] = alpha[i][t]*b[i][(int) obs[t]] + 0.01 *r.nextDouble();
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
                    beta[i][t] = beta[i][t] + a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1]  + 0.01 * r.nextDouble();
                }
                beta[i][t] = beta[i][t]*cArray[t]  + 0.01 *r.nextDouble();
            }
        }

        //double[][][] diGamma = new double[b.length][b.length][obs.length];
        //double[][] gamma = new double[b.length][obs.length];


        for (int t=0 ; t < obs.length-1 ; t++) {
            denom = 0;
            for (int i = 0 ; i < b.length ; i++ ) {
                for (int j = 0 ; j < b.length ; j++ ) {
                    denom = denom + alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][(int) obs[t+1]]  + 0.01 * r.nextDouble();
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
            denom = denom + alpha[i][alpha[0].length-1];//  + 0.01 * r.nextDouble();
        }
        for (int i = 0; i < b.length ; i++) {
            gamma[i][obs.length-1] = (alpha[i][alpha[0].length-1])/denom;
        }
        /*
        for (int i = 0; i < pi[0].length ; i++) {
            pi[0][i] = gamma[i][0];
        }
        */

        double[][] firstMatrixMult = new double[1][a.length];
        double[][] gammaResault = new double[1][b[0].length];



        for (int i = 0; i < a.length ; i++) {
            firstMatrixMult[0][i] = 0;
            for (int j = 0; j < a.length ; j++) {
                firstMatrixMult[0][i] += a[i][j]*gamma[j][T-1];
            }
        }
        for (int i = 0; i < b[0].length ; i++) {
            gammaResault[0][i] = 0;
            for (int j = 0; j < b.length ; j++) {
                gammaResault[0][i] += b[j][i]*firstMatrixMult[0][j];
            }
        }



        double largest = 0;
        int index = -1;
        for (int i = 0;i<gammaResault[0].length;i++){
            if (gammaResault[0][i] > largest){
                largest = gammaResault[0][i];
                index = i;
            }
        }

        double[] returnArray = {index,largest};
        return returnArray;
        /*
        for (int i = 0; i < b.length ; i++) {
            for (int j = 0; j < b.length ; j++) {
                numer = 0;
                denom = 0;
                for (int t = 0; t < obs.length - 1; t++){
                    numer = numer + diGamma[i][j][t]  + 0.01 * r.nextDouble();
                    denom = denom + gamma[i][t]  + 0.01 * r.nextDouble();
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
                        numer = numer + gamma[i][t]  + 0.01 *r.nextDouble();
                    }
                    denom = denom + gamma[i][t]  + 0.01 *r.nextDouble();
                }
                b[i][j] = numer/denom; // testa att byta
            }
        }

        logProb = 0;
        for (int t = 0 ; t < obs.length ; t++){
            logProb = logProb + Math.log(((double) cArray[t]));
        }
        logProb = -logProb;
        //System.out.println(logProb);
        iters = iters + 1;

        */


    }

    public  void alphaForward() {
        Random r = new Random();
        c = 0;

        for(int i = 0 ; i < N ;  i++){
            alpha[i][0] = pi[0][i]*b[i][(int) obs[0]] + r.nextDouble();
            c = c + alpha[i][0];
        }
        c = 1/c;
        cArray[0] = c;
        for(int i = 0 ; i < N ;  i++){
            alpha[i][0] = alpha[i][0]*c;
        }


        //Starting iterative step

        for(int t = 1 ; t < T; t++){
            c = 0;
            for(int i = 0 ; i < N ; i++){
                alpha[i][t] = 0;
                for(int j = 0 ; j < b.length ; j++){
                    alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i]  + 0.01 *r.nextDouble();
                }
                alpha[i][t] = alpha[i][t]*b[i][(int) obs[t]] + r.nextDouble();
                c = c + alpha[i][t];
            }
            c = 1/c;
            for (int i=0; i < N; i++){
                alpha[i][t] = alpha[i][t]*c;
            }
            cArray[t] = c;

        }
    }

    public  void betaBackwards() {
        Random r = new Random();
        for (int i=0; i < N; i++){
            beta[i][T-1] = c;
        }


        for (int t = T - 2 ; t >= 0 ; t--){ // ska det igentligen vara 0 eller -1?
            for (int i=0; i < N; i++){
                beta[i][t] = 0;
                for(int j = 0 ; j < N ; j++){
                    beta[i][t] = beta[i][t] + a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1] + 0.1 * r.nextDouble();
                }
                beta[i][t] = beta[i][t]*cArray[t];
            }
        }
    }

    public  void gammaMerge() {

        Random r = new Random();
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
                    diGamma[i][j][t] = (alpha[i][t]*a[i][j]*b[j][(int) obs[t+1]]*beta[j][t+1])/denom;
                    gamma[i][t] = gamma[i][t] + diGamma[i][j][t];
                }
            }
        }

        denom = 0;
        for (int i = 0; i < N ; i++) {
            denom = denom + alpha[i][T-1] + 0.1 * r.nextDouble() ;
        }
        for (int i = 0; i < N ; i++) {
            gamma[i][T-1] = (alpha[i][T-1])/denom;
        }
    }

    public  void reEstimateing() {

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

    public  void logChange() {
        logProb = 0;
        for (int t = 0 ; t < T ; t++){
            logProb = logProb + Math.log(1/((double) cArray[t]));
        }
        logProb = -logProb;
    }

    public  double[] calculateNext(double[][] piTemp){
        double[][] currentState = matrixMult(piTemp, a);
        double[][] currentProbableObservation = matrixMult(currentState, b);
        double sumOfRow = 0;
        for (int j = 0;j<currentProbableObservation[0].length;j++){
            sumOfRow += currentProbableObservation[0][j];
        }
        /*
        for (int j = 0;j<currentProbableObservation[0].length;j++){
            currentProbableObservation[0][j] /= sumOfRow;
        }
        */
        double largest = currentProbableObservation[0][0];
        int index = 0;
        for (int j = 1;j<currentProbableObservation[0].length;j++){
            if (currentProbableObservation[0][j] > largest){
                largest = currentProbableObservation[0][j];
                index = j;
            }
        };
        double[] returnArray = {index,largest};
        return returnArray;
    }

    public  double[] viterbi(int option) {
        double[][] delta = new double[N][obs.length];
        double[][] deltaIDX = new double[N][obs.length];
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
                    list[j] = a[j][i]*delta[j][t-1]*b[i][(int) obs[t]];
                }
                delta[i][t] = findMaxVal(list);
                deltaIDX[i][t] = findMaxIndex(list);
            }
        }

        //Backtracking most likely states-steps
        double[] mostLikelyState = new double[delta[0].length];
        double maxIndex = 0;
        for(int i = 1 ; i < a.length ; i++){
            if(delta[(int)maxIndex][delta[0].length-1] < delta[i][delta[0].length-1]){
                maxIndex = i;
            }
        }


        mostLikelyState[mostLikelyState.length-1] = maxIndex;
        double sumColumn = 0;
        for (int i = 0 ; i < delta.length ; i++) {
            sumColumn += delta[i][delta[0].length-1];
        }

        for (int i = 0 ; i < delta.length ; i++) {
            delta[i][delta[0].length-1] /= sumColumn;
        }

        for(int i  = delta[0].length-1 ; i > 0 ; i--){
            mostLikelyState[i-1] = deltaIDX[(int)mostLikelyState[i]][i];
        }

        /*

        for(int i  = 0 ; i < mostLikelyState.length  ; i++){
            System.out.print((int)mostLikelyState[i] + " ");
        }
        */
        if (option == 1){
            return mostLikelyState;
        }
        else {
            double[] returnArray = {(double) maxIndex,(double) delta[(int) maxIndex][delta[0].length-1]};

            return returnArray;
        }

    }

    public double findMaxVal(double[] list){
        double max = list[0];
        for(int i = 0 ; i < list.length ; i++){
            if(list[i]>max){
                max = list[i];
            }
        }
        return max;
    }

    public double findMaxIndex(double[] list){
        double max = 0;
        for(int i = 0 ; i < list.length ; i++){
            if(list[i]>list[(int)max]){
                max = i;
            }
        }
        return max;
    }

    public static void printMatrix(double[][] m) {

        System.err.println("printing matrix------------------------------------");
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.err.print(m[i][j] + " ; ");
            }
            System.err.println();
        }
    }

    public double[][] matrixMult(double[][] first, double[][] second) {
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
        }
        else {
            System.out.print("fel dimensioner för matris multi!");
            return null;
        }

    }

    public double[] nextValue(){

        Random r = new Random();
        c = 0;
        for(int i = 0 ; i < N ;  i++){
            alpha[i][0] = pi[0][i]*b[i][(int) obs[0]];
            c = c + alpha[i][0];
        }
        c = 1/c;
        cArray[0] = c;
        for(int i = 0 ; i < N ;  i++){
            alpha[i][0] = alpha[i][0]*c;
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
        double[][] alphaLast = new double[1][N];
        int index = -1;
        double largest = -1;
        double sumOverAlpha = 0;
        //double[] alphaSumObs = new double[M];
        for (int g = 0 ; g < M ; g++){
            c = 0;
            for(int i = 0 ; i < N ; i++){
                alphaLast[0][i] = 0;
                for(int j = 0 ; j < b.length ; j++){
                    alphaLast[0][i] = alphaLast[0][i] + alpha[j][T-1]*a[j][i] ;
                }
                alphaLast[0][i] = alphaLast[0][i]*b[i][g];
                c = c + alphaLast[0][i];
            }
            c = 1/c;
            double alphaSum = 0;
            for (int i=0; i < N; i++){
                //alphaLast[0][i] = alphaLast[0][i]*c;
                alphaSum += alphaLast[0][i];
            }
            sumOverAlpha += alphaSum;
            if (largest < alphaSum){
                index = g;
                largest = alphaSum;
            }

        }

        double[] answer = {index,largest/sumOverAlpha};

        return answer;

    }

    public double alphaPass(int[] observationSequenceIn){


        double[][] alpha = new double[b.length][observationSequenceIn.length];

        for(int i = 0 ; i < pi[0].length ;  i++){
            //Elementsiwe mult on two vectors
            alpha[i][0] = pi[0][i]*b[i][(int)observationSequenceIn[0]];
        }

        //Starting iterative step
        for(int t = 1 ; t < observationSequenceIn.length ; t++){
            for(int i = 0 ; i < b.length ; i++){
                for(int j = 0 ; j < b.length ; j++){
                    alpha[i][t] = alpha[i][t] + alpha[j][t-1]*a[j][i];
                }
                alpha[i][t] = alpha[i][t]*b[i][(int)observationSequenceIn[t]];
            }

        }

        double evalSum = 0;
        for(int i  = 0 ; i < alpha.length ; i++){
            evalSum += alpha[i][alpha[0].length-1];
        }
        return evalSum;
    }

    public double[][] returnA(){
        return a;
    }

    public double[][] returnB(){
        return b;
    }

    public double[][] returnPi(){
        return pi;
    }
}