
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

class Player {
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


    public Player() {
    }

    /**
     * Shoot!
     *
     * This is the function where you start your work.
     *
     * You will receive a variable pState, which contains information about all
     * birds, both dead and alive. Each bird contains all past moves.
     *
     * The state also contains the scores for all players and the number of
     * time steps elapsed since the last time this function was called.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return the prediction of a bird we want to shoot at, or cDontShoot to pass
     */
    public Action shoot(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to get the best action.
         * This skeleton never shoots.
         */

        // This line chooses not to shoot.
        System.err.println("Ey, bruh vi har: " +  pState.getNumBirds() +  " st birds");
        System.err.println("Ey, bruh vi har bird nummer 1 som är dead: " +  pState.getBird(1).isAlive() +  "");
        System.err.println("Ey, bruh vi har: " +  pState.getNumNewTurns() +  " st NewTurns");
        return cDontShoot;

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    }

    /**
     * Guess the species!
     * This function will be called at the end of each round, to give you
     * a chance to identify the species of the birds for extra points.
     *
     * Fill the vector with guesses for the all birds.
     * Use SPECIES_UNKNOWN to avoid guessing.
     *
     * @param pState the GameState object with observations etc
     * @param pDue time before which we must have returned
     * @return a vector with guesses for all the birds
     */
    public int[] guess(GameState pState, Deadline pDue) {
        /*
         * Here you should write your clever algorithms to guess the species of
         * each bird. This skeleton makes no guesses, better safe than sorry!
         */
        System.err.println("I wonder which bird is which!");
        int[] lGuess = new int[pState.getNumBirds()];
        for (int i = 0; i < pState.getNumBirds(); ++i)
            lGuess[i] = Constants.SPECIES_UNKNOWN;
        return lGuess;
    }

    /**
     * If you hit the bird you were trying to shoot, you will be notified
     * through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pBird the bird you hit
     * @param pDue time before which we must have returned
     */
    public void hit(GameState pState, int pBird, Deadline pDue) {
        System.err.println("HIT BIRD!!!");
    }

    /**
     * If you made any guesses, you will find out the true species of those
     * birds through this function.
     *
     * @param pState the GameState object with observations etc
     * @param pSpecies the vector with species
     * @param pDue time before which we must have returned
     */
    public void reveal(GameState pState, int[] pSpecies, Deadline pDue) {
    }

    public static final Action cDontShoot = new Action(-1, -1);


    //TODO Denna kan needa en cArray!
    public static double[][] alphaForward(double[][] alphaTemp, double[][] alphaNollTemp, double[][] aTemp, double[][] bTemp, double[][] piTemp, double[] obsTemp) {

        c = 0;

        for(int i = 0 ; i < N ;  i++){
            alphaNollTemp[i][0] = piTemp[0][i]*bTemp[i][(int) obs[0]];
            c = c + alphaNollTemp[i][0];
        }
        c = 1/c;
        cArray[0] = c;
        for(int i = 0 ; i < N ;  i++){
            alphaNollTemp[i][0] = alphaNollTemp[i][0]*c;
        }

        for(int i = 0 ; i < N ; i++){
            alphaTemp[i][0] = alphaNollTemp[i][0];
        }


        //Starting iterative step

        for(int t = 1 ; t < T; t++){
            c = 0;
            for(int i = 0 ; i < N ; i++){
                alphaTemp[i][t] = 0;
                for(int j = 0 ; j < bTemp.length ; j++){
                    alphaTemp[i][t] = alphaTemp[i][t] + alphaTemp[j][t-1]*aTemp[j][i];
                }
                alphaTemp[i][t] = alphaTemp[i][t]*bTemp[i][(int) obsTemp[t]];
                c = c + alphaTemp[i][t];
            }
            c = 1/c;
            for (int i=0; i < N; i++){
                alphaTemp[i][t] = alphaTemp[i][t]*c;
            }
            cArray[t] = c;

        }

        return alphaTemp;

    }

    //TODO Denna kan needa en cArray också!
    public static double[][] betaBackwards(double[][] betaTemp, double[][] aTemp, double[][] bTemp, double[][] piTemp, double[] obsTemp) {

        for (int i=0; i < N; i++){
            betaTemp[i][T-1] = c;
        }


        for (int t = T - 2 ; t >= 0 ; t--){ // ska det igentligen vara 0 eller -1?
            for (int i=0; i < N; i++){
                betaTemp[i][t] = 0;
                for(int j = 0 ; j < N ; j++){
                    betaTemp[i][t] = betaTemp[i][t] + aTemp[i][j]*bTemp[j][(int) obsTemp[t+1]]*betaTemp[j][t+1];
                }
                betaTemp[i][t] = betaTemp[i][t]*cArray[t];
            }
        }

        return betaTemp;
    }

    //Den borde inte needa en denom eftersom den nollställs direkt
    //Pair borde funka!
    public static List gammaMerge(double[][] gammaTemp, double[][][] diGammaTemp, double[][] aTemp, double[][] bTemp, double[][] alphaTemp, double[][] betaTemp, double[][] piTemp, double[] obsTemp) {

        double evalSum = 0;
        for(int i  = 0 ; i < N ; i++){
            evalSum += alphaTemp[i][T-1];
        }


        for (int t=0 ; t < T - 1 ; t++) {
            denom = 0;
            for (int i = 0 ; i < N ; i++ ) {
                for (int j = 0 ; j < N ; j++ ) {
                    denom = denom + alphaTemp[i][t]*a[i][j]*b[j][(int) obsTemp[t+1]]*betaTemp[j][(int) obsTemp[t+1]];
                }
            }
            for (int i = 0 ; i < N ; i++ ) {
                gammaTemp[i][t] = 0;
                for (int j = 0 ; j < N ; j++ ) {
                    diGammaTemp[i][j][t] = (alphaTemp[i][t]*aTemp[i][j]*bTemp[j][(int) obsTemp[t+1]]*betaTemp[j][t+1])/evalSum;//denom;
                    gammaTemp[i][t] = gammaTemp[i][t] + diGammaTemp[i][j][t];
                }
            }
        }

        denom = 0;
        for (int i = 0; i < N ; i++) {
            denom = denom + alphaTemp[i][T-1];
        }
        for (int i = 0; i < N ; i++) {
            gammaTemp[i][T-1] = (alphaTemp[i][T-1])/denom;
        }


        Pair gammaAndDiGamma = new Pair(gammaTemp, diGammaTemp);
        List gDg = new LinkedList();
        gDg.add(gammaTemp);
        gDg.add(diGammaTemp);
        return gDg;

    }

    //TODO Alla N kan behöva bli specifika N!
    public static LinkedList reEstimateing(double[][] gammaTemp, double[][][] diGammaTemp, double[][] aTemp, double[][] bTemp, double[][] alphaTemp, double[][] betaTemp, double[][] piTemp, double[] obsTemp) {

        for (int i = 0; i < N ; i++) {
            piTemp[0][i] = gammaTemp[i][0];
        }

        for (int i = 0; i < N ; i++) {
            for (int j = 0; j < N ; j++) {
                numer = 0;
                denom = 0;
                for (int t = 0; t < T - 1; t++){
                    numer = numer + diGammaTemp[i][j][t];
                    denom = denom + gammaTemp[i][t];
                }
                aTemp[i][j] = numer/denom;
            }
        }

        for (int i = 0; i < N ; i++) {
            for (int j = 0; j < M ; j++) {
                numer = 0;
                denom = 0;
                for (int t = 0 ; t < T - 1; t++){
                    if (((int) obsTemp[t]) == j){
                        numer = numer + gammaTemp[i][t];
                    }
                    denom = denom + gammaTemp[i][t];
                }
                bTemp[i][j] = numer/denom;
            }
        }

        List reestimatedLambda = new LinkedList();
        reestimatedLambda.add(aTemp);
        reestimatedLambda.add(bTemp);
        reestimatedLambda.add(piTemp);
        return reestimatedLambda;
    }

    public static void logChange() {
        logProb = 0;
        for (int t = 0 ; t < T ; t++){
            logProb = logProb + Math.log(1/((double) cArray[t]));
        }
        logProb = -logProb;
    }

    public static void viterbi() {
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
