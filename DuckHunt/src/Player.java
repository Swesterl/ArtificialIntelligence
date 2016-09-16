import java.util.Arrays;
import java.lang.Math;

class Player {

    HMM[] HMMList;
    HMM[] HMMList2;
    HMM[] HMMListBirds;
    int timestep = 0;
    int oldRound = 0;
    int nrOfRorelser = 9;
    int numberBirdsPrevRound;
    int timeBeforeObs = 20;
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
        if (pState.getRound() > oldRound) {
            oldRound = pState.getRound();
            timestep = 0;
            System.err.println("---------------");
        }

        timestep ++;
        if(timestep == 1 ){ //&& pState.getRound() < 1){
            HMMList = new HMM[pState.getNumBirds()];
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                HMMList[i] = new HMM(5,nrOfRorelser);

            }
        }

        if(timestep >= 1){
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                if (!pState.getBird(i).isDead()){

                    HMMList[i].setO(pState.getBird(i).getLastObservation());
                    if(HMMList[i].obs.length > 10){
                        int obsIndex = HMMList[i].obs.length-1;
                        int direction = HMMList[i].obs[obsIndex];
                        for(int j = 0 ; j < 10 ; j++){
                            if(HMMList[i].obs[obsIndex-j] != direction){
                                break;
                            }
                            if(j == 4){
                                //shoot
                                return new Action(i, direction);
                            }
                        }

                    }

                }
            }
        }

        /*
        if(timestep == timeBeforeObs +1) {
            numberBirdsPrevRound = pState.getNumBirds();
        }
        */


        //if(timestep == 25 || timestep == 40 || timestep == 60 || timestep == 80){
        /*
        if(timestep > 59){
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                double[] observation;
                observation = observationSequence(pState,i);
                HMMList[i].hmm4(observation);
            }

        }
        */
        if(timestep > 70){
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                HMMList[i].hmm4();
            }

        }
        /*
        if(timestep == timeBeforeObs && pState.getRound() > 0) {
            HMMList2 = new HMM[pState.getNumBirds()];
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                double[] fitValue = new double[numberBirdsPrevRound];
                for (int j = 0 ; j < numberBirdsPrevRound ; j++) {
                    fitValue[j] = HMMList[j].alphaPass();
                }
                double largest = fitValue[0];
                int index = 0;
                for (int j = 0;j<fitValue.length;j++){
                    if (fitValue[j] > largest){
                        largest = fitValue[j];
                        index = j;
                    }
                }
                if (largest > 0){
                    HMMList2[i] = new HMM(HMMList[index].returnA(),HMMList[index].returnB(),HMMList[index].returnPi());
                }
                else{
                    HMMList2[i] = new HMM(3,nrOfRorelser);
                }

            }
            HMMList = HMMList2;

        }
        */

        if(timestep > 70 && pState.getRound() > 0) {
            //int[] latestMove = new int[pState.getNumBirds()];
            //for (int i = 0; i < pState.getNumBirds(); i++){
            //    latestMove[i] = pState.getBird(i).getLastObservation();
            //}

            double[][] indexProb = new double[2][pState.getNumBirds()];
            for (int i = 0; i < pState.getNumBirds(); i++){
                //double[][] piTemp = new double[1][nrOfRorelser];

                if (!pState.getBird(i).isDead()){
                    double[] returnArray;
                    returnArray = HMMList[i].delta();
                    indexProb[0][i] = ((int)returnArray[0]);
                    indexProb[1][i] = returnArray[1];

                }
                else {
                    indexProb[0][i] = -1;
                    indexProb[1][i] = -1;

                }
            }
            double mostProbDir = 0;
            int bird = -1;
            double secondMostProbDir = 0;
            int secondBird = -1;
            for (int i = 0;i<indexProb[0].length;i++){
                if (indexProb[1][i] > mostProbDir){
                    secondMostProbDir = mostProbDir;
                    secondBird = bird;
                    mostProbDir = indexProb[1][i];
                    bird = i;
                }
            }
            if (pState.getRound() == 0 && mostProbDir > 0.7){
                System.err.println("SHOT certenty: " + mostProbDir);
                System.err.println(((int) indexProb[0][(int) bird]));
                return new Action(bird,((int) indexProb[0][(int) bird]));

            }
            if (mostProbDir > 0.7) {
                double[] fitValue = new double[HMMListBirds.length];
                for (int j = 0 ; j < HMMListBirds.length ; j++) {
                    fitValue[j] = HMMListBirds[j].alphaPass(HMMList[bird].returnO());
                }
                double mostProbBird = fitValue[0];
                int birdType = 0;
                for (int j = 0;j<fitValue.length;j++){
                    if (fitValue[j] > mostProbBird){
                        mostProbBird = fitValue[j];
                        birdType = j;
                    }
                }

                if (birdType != 5){
                    System.err.println("SHOT certenty: " + mostProbDir);
                    System.err.println(((int) indexProb[0][(int) bird]));
                    return new Action(bird,((int) indexProb[0][(int) bird]));
                }
                else {
                    if (secondMostProbDir > 0.7) {
                        fitValue = new double[HMMListBirds.length];
                        for (int j = 0 ; j < HMMListBirds.length ; j++) {
                            fitValue[j] = HMMListBirds[j].alphaPass(HMMList[secondBird].returnO());
                        }
                        mostProbBird = fitValue[0];
                        birdType = 0;
                        for (int j = 0;j<fitValue.length;j++){
                            if (fitValue[j] > mostProbBird){
                                mostProbBird = fitValue[j];
                                birdType = j;
                            }
                        }

                        if (birdType != 5){
                            System.err.println("SHOT certenty: " + secondMostProbDir);
                            System.err.println(((int) indexProb[0][(int) secondBird]));
                            return new Action(secondBird,((int) indexProb[0][(int) secondBird]));
                        }
                        else {
                            return cDontShoot;
                        }
                    }
                }

            }



            /*
            double[][] indexProb = new double[2][pState.getNumBirds()];
            for (int i = 0; i < pState.getNumBirds(); i++){
               //double[][] piTemp = new double[1][nrOfRorelser];

                if (!pState.getBird(i).isDead()){
                    double[] returnArray;
                    returnArray = HMMList[i].delta();
                    indexProb[0][i] = ((int)returnArray[0]);
                    indexProb[1][i] = returnArray[1];

                }
                else {
                    indexProb[0][i] = -1;
                    indexProb[1][i] = -1;

                }
            }
            double largest = 0;
            int index = -1;
            for (int i = 0;i<indexProb[0].length;i++){
                if (indexProb[1][i] > largest){
                    largest = indexProb[1][i];
                    index = i;
                }
            }
            if (largest > 0.7) {
                System.err.println("SHOT");
                System.err.println(((int) indexProb[0][(int) index]));
                return new Action(index,((int) indexProb[0][(int) index]));
            }
            */
        }



        // This line chooses not to shoot.
        return cDontShoot;

        // This line would predict that bird 0 will move right and shoot at it.
        // return Action(0, MOVE_RIGHT);
    }

    public static double[] observationSequence(GameState pState,int i){
        int obsLength = 0;
        int g = 0;
        int tempDirection = 0;
        double[] obsMRTemp = new double[100];
        for(int j = 0 ; j < pState.getBird(i).getSeqLength() ; j++){
            tempDirection = ((int) pState.getBird(i).getObservation(j));
            if (((int) tempDirection) > -1){
                obsLength += 1;
                obsMRTemp[g] = ((int) pState.getBird(i).getObservation(j));
                g += 1;
            }
        }

        double[] obsMR = new double[obsLength];

        //double[] cArrayMR = new double[obsMR.length];


        g = 0;
        for(int j = 0 ; j < obsLength ; j++){
            obsMR[j] = obsMRTemp[j];
        }
        return obsMR;
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
        /*
        int[] lGuess = new int[pState.getNumBirds()];
        if(pState.getRound() == 0){ //&& pState.getRound() < 1){
            HMMListBirds = new HMM[6];
            for (int i = 0 ; i < 6 ; i ++) {
                HMMList[i] = new HMM(5,nrOfRorelser);
            }
            for (int i = 0; i < pState.getNumBirds(); ++i) {
                lGuess[i] = Constants.SPECIES_PIGEON;
            }
        }
        */
        int[] lGuess = new int[pState.getNumBirds()];

        /*
        if (pState.getRound() == 0){
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                lGuess[i] = 1;
            }
        }
        */

        if (pState.getRound() > 0){
            double[][] suggestedArt = new double[2][pState.getNumBirds()];
            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                double[] fitValue = new double[HMMListBirds.length];
                for (int j = 0 ; j < HMMListBirds.length ; j++) {
                    fitValue[j] = HMMListBirds[j].alphaPass(HMMList[i].returnO());
                }
                double largest = fitValue[0];
                int index = 0;
                for (int j = 0;j<fitValue.length;j++){
                    if (fitValue[j] > largest){
                        largest = fitValue[j];
                        index = j;
                    }
                }
                suggestedArt[0][i] = index;
                suggestedArt[1][i] = largest;
            }

            for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
                if (suggestedArt[1][i] > Math.pow(10 , -100) ){
                    lGuess[i] = ((int) suggestedArt[0][i]);
                }
                else {
                    lGuess[i] = -1;
                }
            }

        }

        //for (int i = 0; i < pState.getNumBirds(); ++i)
        //   lGuess[i] = Constants.SPECIES_UNKNOWN;
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
        System.err.println(Arrays.toString(pSpecies));
        if(pState.getRound() == 0){ //&& pState.getRound() < 1){
            HMMListBirds = new HMM[6];
            for (int i = 0 ; i < 6 ; i ++) {
                HMMListBirds[i] = new HMM(5,nrOfRorelser);
                HMMListBirds[i].art = i;
            }
        }
        System.err.println("Round: " + pState.getRound());
        for (int i = 0 ; i < pState.getNumBirds() ; i ++) {
            HMMList[i].art = pSpecies[i];
            HMMListBirds[pSpecies[i]].setOL(HMMList[i].returnO());
        }

        for (int i = 0 ; i < HMMListBirds.length ; i ++) {
            if(HMMListBirds[i].returnO() != null ){
                HMMListBirds[i].hmm4();
            }
        }



    }

    public static final Action cDontShoot = new Action(-1, -1);
}