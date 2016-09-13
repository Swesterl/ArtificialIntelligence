
import javafx.util.Pair;
import java.lang.Math;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.AbstractMap.SimpleEntry;

class Player {
    static double[][] delta;
    static double[][] deltaIDX;


    static double[][] aRM;
    static double[][] bRM;
    static double[][] piRM;
    static double[] obsRM;
    static double[][] deltaRM;
    static double[][] deltaIDXRM;
    static double logProbRM;
    static double oldLogProbRM;
    static double[][] alphaNollRM;
    static double[] cArrayRM;
    static double[][] betaRM;
    static double[][][] diGammaRM;
    static double[][] gammaRM;
    static double[][] alphaRM;

    static double[][] aMR;
    static double[][] bMR;
    static double[][] piMR;
    static double[][] deltaMR;
    static double[][] deltaIDXMR;
    static double logProbMR;
    static double oldLogProbMR;
    static double[][] alphaNollMR;
    static double[][] betaMR;
    static double[][][] diGammaMR;
    static double[][] gammaMR;
    static double[][] alphaMR;


    static double[][] aFM;
    static double[][] bFM;
    static double[][] piFM;
    static double[] obsFM;
    static double[][] deltaFM;
    static double[][] deltaIDXFM;
    static double logProbFM;
    static double oldLogProbFM;
    static double[][] alphaNollFM;
    static double[] cArrayFM;
    static double[][] betaFM;
    static double[][][] diGammaFM;
    static double[][] gammaFM;
    static double[][] alphaFM;

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

    static int nrOfModels = 5;
    static int nrOfRorelser = 9;


    static int timestep;


    public Player() {
        Random r = new Random();
        double sumThisRow = 0;


        aMR = new double[nrOfModels][nrOfModels];
        bMR = new double[nrOfModels][nrOfRorelser];
        piMR = new double[1][nrOfModels];
        //Give aMR random values
        for(int i = 0 ; i < aMR.length ; i++) {
            for (int j = 0; j < aMR[0].length; j++) {
                double randNr = r.nextDouble();
                aMR[i][j] = randNr;
                sumThisRow += randNr;
            }
            for (int k = 0; k < aMR[0].length; k++) {
                aMR[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }

        //Give bMR random values.
        for(int i = 0 ; i < bMR.length ; i++) {
            for (int j = 0; j < bMR[0].length; j++) {
                double randNr = r.nextDouble();
                bMR[i][j] = randNr;
                sumThisRow += randNr;
            }
            for (int k = 0; k < bMR[0].length; k++) {
                bMR[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }
        //Give piMR random values.
        for(int i = 0 ; i < piMR.length ; i++) {
            for (int j = 0; j < piMR[0].length; j++) {
                double randNr = r.nextDouble();
                piMR[i][j] = randNr;
                sumThisRow += randNr;
            }
            for (int k = 0; k < piMR[0].length; k++) {
                piMR[i][k] /= sumThisRow;
            }
            sumThisRow = 0;
        }

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

        if(pState.getRound() == 0){
            if(timestep == 97){
                for(int i = 0 ; i < pState.getNumBirds() ; i++){
                    double[] obsMR = new double[pState.getBird(i).getSeqLength()];

                    double[] cArrayMR = new double[obsMR.length];



                    for(int j = 0 ; j < pState.getBird(i).getSeqLength() ; j++){
                        obsMR[j] = pState.getBird(i).getObservation(j);
                    }


                    int maxIters = 400;
                    iters = 0;
                    double oldLogProbMR = 10000000;

                    SimpleEntry<double[], double[][]> cArrayAlpha = alphaForward(cArrayMR, aMR, bMR, piMR, obsMR);
                    cArrayMR = cArrayAlpha.getKey();
                    alphaMR = cArrayAlpha.getValue();

                    betaMR = betaBackwards(cArrayMR, aMR, bMR, piMR, obsMR);

                    SimpleEntry<double[][], double[][][]> gammaValues = gammaMerge(aMR, bMR, alphaMR, betaMR, piMR, obsMR);
                    gammaMR = gammaValues.getKey();
                    diGammaMR = gammaValues.getValue();

                    SimpleEntry<double[][], SimpleEntry<double[][], double[][]>> improvedLambdas = reEstimateing(gammaMR, diGammaMR, aMR, bMR, alphaMR, betaMR, piMR, obsMR);
                    piMR = improvedLambdas.getKey();
                    SimpleEntry<double[][], double[][]> improvedAAndB = improvedLambdas.getValue();
                    aMR = improvedAAndB.getKey();
                    bMR = improvedAAndB.getValue();
                    logProbMR = logChange(logProbMR, cArrayMR, obsMR);

                    while (iters < maxIters) {
                        oldLogProbMR = logProbMR;
                        //System.out.println(logProb);
                        cArrayAlpha = alphaForward(cArrayMR, aMR, bMR, piMR, obsMR);
                        cArrayMR = cArrayAlpha.getKey();
                        alphaMR = cArrayAlpha.getValue();

                        betaMR = betaBackwards(cArrayMR, aMR, bMR, piMR, obsMR);

                        gammaValues = gammaMerge(aMR, bMR, alphaMR, betaMR, piMR, obsMR);
                        gammaMR = gammaValues.getKey();
                        diGammaMR = gammaValues.getValue();

                        improvedLambdas = reEstimateing(gammaMR, diGammaMR, aMR, bMR, alphaMR, betaMR, piMR, obsMR);
                        piMR = improvedLambdas.getKey();
                        improvedAAndB = improvedLambdas.getValue();
                        aMR = improvedAAndB.getKey();
                        bMR = improvedAAndB.getValue();
                        logProbMR = logChange(logProbMR, cArrayMR, obsMR);


                        iters = iters + 1;
                        //System.out.println(logProb);
                    }
                }
                System.err.println("A");
                printMatrix(aMR);
                System.err.println("B");
                printMatrix(bMR);
                System.err.println("pi");
                printMatrix(piMR);
            }



        }

        timestep++;



        // This line chooses not to shoot.
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
    public static SimpleEntry alphaForward(double[] cArrayTemp, double[][] aTemp, double[][] bTemp, double[][] piTemp, double[] obsTemp) {
        int N = aTemp.length;
        int T = obsTemp.length;
        int M = bTemp[0].length;
        c = 0;

        double[][] alphaTemp = new double[N][T];

        for(int i = 0 ; i < N ;  i++){
            alphaTemp[i][0] = piTemp[0][i]*bTemp[i][(int) obsTemp[0]];
            c = c + alphaTemp[i][0];
        }
        c = 1/c;
        cArrayTemp[0] = c;
        for(int i = 0 ; i < N ;  i++){
            alphaTemp[i][0] = alphaTemp[i][0]*c;
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
            cArrayTemp[t] = c;

        }

        return new SimpleEntry<double[], double[][]>(cArrayTemp, alphaTemp);

    }

    public static double[][] betaBackwards(double[] cArrayTemp, double[][] aTemp, double[][] bTemp, double[][] piTemp, double[] obsTemp) {
        int N = aTemp.length;
        int T = obsTemp.length;
        int M = bTemp[0].length;
        double[][] betaTemp = new double[N][T];

        for (int i=0; i < N; i++){
            betaTemp[i][T-1] = c;
        }


        for (int t = T - 2 ; t >= 0 ; t--){ // ska det igentligen vara 0 eller -1?
            for (int i=0; i < N; i++){
                betaTemp[i][t] = 0;
                for(int j = 0 ; j < N ; j++){
                    betaTemp[i][t] = betaTemp[i][t] + aTemp[i][j]*bTemp[j][(int) obsTemp[t+1]]*betaTemp[j][t+1];
                }
                betaTemp[i][t] = betaTemp[i][t]*cArrayTemp[t];
            }
        }

        return betaTemp;
    }

    //Den borde inte needa en denom eftersom den nollställs direkt
    //TODO borde cArray returnas? Nej?
    public static SimpleEntry gammaMerge(double[][] aTemp, double[][] bTemp, double[][] alphaTemp, double[][] betaTemp, double[][] piTemp, double[] obsTemp) {
        int N = aTemp.length;
        int T = obsTemp.length;

        double[][] gammaTemp = new double[N][T];
        double[][][] diGammaTemp = new double[N][N][T];

        double evalSum = 0;
        for(int i  = 0 ; i < N ; i++){
            evalSum += alphaTemp[i][T-1];
        }


        for (int t=0 ; t < T - 1 ; t++) {
            denom = 0;
            for (int i = 0 ; i < N ; i++ ) {
                for (int j = 0 ; j < N ; j++ ) {
                    denom = denom + alphaTemp[i][t]*aTemp[i][j]*bTemp[j][(int) obsTemp[t+1]]*betaTemp[j][(int) obsTemp[t+1]];
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


        return new SimpleEntry<double[][], double[][][]>(gammaTemp, diGammaTemp);

    }

    //TODO Alla N kan behöva bli specifika N!
    public static SimpleEntry reEstimateing(double[][] gammaTemp, double[][][] diGammaTemp, double[][] aTemp, double[][] bTemp, double[][] alphaTemp, double[][] betaTemp, double[][] piTemp, double[] obsTemp) {
        int N = aTemp.length;
        int T = obsTemp.length;
        int M = bTemp[0].length;




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
        SimpleEntry<double[][], double[][]> aAndB= new SimpleEntry<double[][], double[][]>(aTemp, bTemp);
        return new SimpleEntry<double[][], SimpleEntry<double[][], double[][]>>(piTemp, aAndB);
    }
    //TODO Vi needar cArray
    public static double logChange(double logProbTemp, double[] cArrayTemp, double[] obsTemp) {
        int T = obsTemp.length;
        logProbTemp = 0;
        for (int t = 0 ; t < T ; t++){
            logProbTemp = logProbTemp + Math.log(1/((double) cArrayTemp[t]));
        }
        logProbTemp = -logProbTemp;
        return logProbTemp;
    }

    public static double[] viterbi(double[][] gammaTemp, double[][][] diGammaTemp, double[][] aTemp, double[][] bTemp, double[][] alphaTemp, double[][] betaTemp, double[][] piTemp, double[] obsTemp) {


        double[][] localDelta = new double[aTemp.length][obsTemp.length];
        double[][] localDeltaIDX = new double[aTemp.length][obsTemp.length];
        //Initialize delta
        for(int i = 0 ; i < aTemp.length  ; i++) {
            double pLoL = piTemp[0][i];
            double obsLoL = bTemp[i][(int) obsTemp[0]];
            localDelta[i][0] = pLoL * obsLoL;
            localDeltaIDX[i][0] = i;
        }
        //System.out.println("Delta efter första raden");
        //printMatrix(delta);

        for(int t = 1 ;  t < obsTemp.length ; t++){
            for(int i = 0 ;  i < aTemp.length ; i++){
                double[] list = new double[aTemp.length];
                for(int j = 0 ; j < aTemp.length ; j++){
                    list[j] = aTemp[j][i]*localDelta[j][t-1]*bTemp[i][(int)obsTemp[t]];
                }
                localDelta[i][t] = findMaxVal(list);
                localDeltaIDX[i][t] = findMaxIndex(list);
            }
        }

        //Backtracking most likely states-steps
        double[] mostLikelyState = new double[delta[0].length];
        double maxIndex = 0;
        for(int i = 1 ; i < aTemp.length ; i++){
            if(localDelta[(int)maxIndex][localDelta[0].length-1] < localDelta[i][localDelta[0].length-1]){
                maxIndex = i;
            }
        }

        mostLikelyState[mostLikelyState.length-1] = maxIndex;

        for(int i  = localDelta[0].length-1 ; i > 0 ; i--){
            mostLikelyState[i-1] = localDeltaIDX[(int)mostLikelyState[i]][i];
        }

        for(int i  = 0 ; i < mostLikelyState.length  ; i++){
            System.out.print((int)mostLikelyState[i] + " ");
        }
        return mostLikelyState;
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

        System.err.println("printing matrix------------------------------------");
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                System.err.print(m[i][j] + " ; ");
            }
            System.err.println();
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
