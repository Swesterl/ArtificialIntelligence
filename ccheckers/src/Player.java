import java.util.*;
import java.util.Arrays;

public class Player {
    /**
     * Performs a move
     *
     * @param pState
     *            the current state of the board
     * @param pDue
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */

    public ArrayList<Integer> myAwesomeList;
    public int maxDepth;
    public int hashDepth;
    HashMap cashedStates = new HashMap();

    public GameState play(final GameState pState, final Deadline pDue) {

        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        int currentPlayer = pState.getNextPlayer();
        //int currentPlayer;
        boolean redsTurn; //TODO do we get the players mixed up?

        /*
        if(nextPlayer == Constants.CELL_RED){
            currentPlayer = Constants.CELL_RED;
        } else {
            currentPlayer = Constants.CELL_WHITE;
        }
        */

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }

        int[] moveValue = new int[lNextStates.size()];
        myAwesomeList = new ArrayList<Integer>();
        int alpha = -1000000;
        int beta = 1000000;
        int maxDepth = 14;
        hashDepth = 10;

        //TODO two for loops instead of two ifs, saves a little time.
        if(currentPlayer == Constants.CELL_RED){
            for (GameState s : lNextStates){
                if (s.isRedWin()) {
                    return s;
                }
            }
        }
        else {
            for (GameState s : lNextStates){
                if (s.isWhiteWin()) {
                    return s;
                }
            }

        }
        //System.err.println("hej");
        int bestScore = alphaBeta(pState, pState,maxDepth,1,alpha,beta,currentPlayer, pDue); //TODO is this correct? Can we just send in whos turn it is?

        return lNextStates.elementAt(myAwesomeList.indexOf(bestScore));
    }

    public int alphaBeta(GameState state, GameState prevState, int md, int depth, int alpha, int beta, int currentPlayer, Deadline due) {
        int v = -100000000;

        if(depth > hashDepth){
            //Get the cashed value for a boardstate so we dont have to calculate it again!
            //TODO test for hash collitions?
            //TODO hash the value to achieve a smaller hashmap
            //1. Hash the gamestates boardvalue
            //2. Get the value from the Hashmap!
            String[] parts = state.toMessage().split(" ");
            Object value = cashedStates.get(parts[0] + "_" + parts[2]);
            if(value != null){
                v = (int)value;
                //System.err.println("We found a v! And it is: ---------------------------------------------------------------" + v);
                return v;
            }

            /*
            else {
                System.err.println("Sorry bro!");
            }
            */
        }

        if (state.isEOG()) {
            //This is gamma
            if (state.isRedWin()) {
                v = 500*(1+md-depth);
            }
            else if (state.isWhiteWin()) {
                v = -500*(1+md-depth);
            }
            else {
                v = 0;
            }
        }

        else if(depth == md) {
            int leafScore = evalKings(state) + evalValue(state);// + evalEndOfBord(state);
            v = leafScore;
        }

        else {

            if (currentPlayer == Constants.CELL_RED) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);
                int nrOfChildren = possibleStates.size();
                Pair[] childRankings = new Pair[nrOfChildren];
                childRankings = childRankanker(possibleStates);
                Arrays.sort(childRankings);

                v = -100000;

                for (int i = 0; i < nrOfChildren ; i++){
                    if(due.timeUntil() < 1){
                        //System.err.println("breaking off search due to timeconstraints");
                        break;
                    }
                    //Here we decide which child to attend to first.
                    //Some children will be more promising than others!
                    int childValue = alphaBeta(possibleStates.elementAt(childRankings[i].index), state, md, depth + 1,alpha,beta,Constants.CELL_WHITE, due);
                    v = Math.max(v, childValue);

                    if (depth == 1) {
                        myAwesomeList.add(childValue);
                    }

                    alpha = Math.max(alpha,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
            else {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);
                int nrOfChildren = possibleStates.size();
                Pair[] childRankings = new Pair[nrOfChildren];
                childRankings = childRankanker(possibleStates);
                Arrays.sort(childRankings);//TODO: Sortera descending i B kanske?


                v = 100000;

                for (int i = 0; i < nrOfChildren ; i++){
                    if(due.timeUntil() < 1){
                        //System.err.println("breaking off search due to timeconstraints");
                        break;
                    }
                    //Here we decide which child to attend to first.
                    //Some children will be more promising than others!
                    int childValue = alphaBeta(possibleStates.elementAt(childRankings[i].index), state, md, depth + 1,alpha,beta,Constants.CELL_RED, due);
                    v = Math.min(v, childValue);

                    if (depth == 1) {
                        myAwesomeList.add(childValue);
                    }

                    beta = Math.min(beta,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
        }
        if(depth > hashDepth){
            //Add the node to the hashmap for reuse;

            //1. Hash the boardstate to get a key
            //2. Put the value of v into the into the Hashmap using the key
            String[] parts = state.toMessage().split(" ");
            cashedStates.put(parts[0] + "_" + parts[2], v);
        }
        return v;
    }


    public Pair[] childRankanker(Vector<GameState> ps){
        Pair[] cr = new Pair[ps.size()];
        //int[] previousValues = evalValue(s,pAt);
        //int kingsPrev = evalKings(s,pAt);
        int valueOfChild;
        for(int i = 0; i < ps.size(); i++){
            valueOfChild = evalValue(ps.elementAt(i)) + evalKings(ps.elementAt(i));// + evalEndOfBord(ps.elementAt(i));
            cr[i] = new Pair(i, valueOfChild);

        }
        /*
        for(int i = 0; i < cr.length ; i++) {
            //System.err.println("Value" + cr[i].value + ", and index: " + cr[i].index );
        }
        */
        return cr;
    }



    public int evalValue(GameState state){
        //TODO incorpoarate kings somehow
        int red = 0;
        int white = 0;

        for (int i = 0; i < 32; i++){
            int inCell = state.get(i);
            if (isRed(inCell)){
                red++;
            }
            else if(isWhite(inCell)){
                white++;
            }
        }
        return 5*(red - white);
    }

    public boolean isKing(int posState)
    {
        int maskTry = 1<<2;
        if((maskTry & posState) == maskTry)
        {
            return true;
        }
        return false;
    }

    public boolean isRed(int posState)
    {
        int maskTry = 1<<0;
        if((maskTry & posState) == maskTry)
        {
            return true;
        }
        return false;
    }

    public boolean isWhite(int posState)
    {
        int maskTry = 1<<1;
        if((maskTry & posState) == maskTry)
        {
            return true;
        }
        return false;
    }

    public int evalKings(GameState state){
        //TODO incorpoarate kings somehow
        int red = 0;
        int white = 0;
        for (int i = 0; i < 32; i++){
            int inCell = state.get(i);
            if (isKing(inCell)){
                if (isRed(inCell)) {
                    red++;
                }
                else if (isWhite(inCell)) {
                    white++;
                }
            }
        }


        return 25*(red-white);
    }

    public int evalEndOfBord(GameState state){
        //TODO incorpoarate kings somehow
        int red = 0;
        int white = 0;
        for (int i = 24; i < 28; i++){
            int inCell = state.get(i);
            if (!isKing(inCell)){
                if (isRed(inCell)) {
                    red++;
                }
            }
        }

        for (int i = 4; i < 8; i++){
            int inCell = state.get(i);
            if (!isKing(inCell)){
                if (isWhite(inCell)) {
                    white++;
                }
            }
        }



        return 4*(red-white);
    }


    //---------------------------------------------------------------------------------

    public class Pair implements Comparable<Pair> {
        public final int index;
        public final int value;

        public Pair(int index, int value) {
            this.index = index;
            this.value = value;
        }

        @Override
        public int compareTo(Pair other) {
            //multiplied to -1 as the author need descending sort order
            return -1 * Integer.valueOf(this.value).compareTo(other.value);
        }
    }
}