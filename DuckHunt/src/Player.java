
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
