import java.util.*;

// Johan Och Simon

public class Player {
    /**
     * Performs a move
     *
     * @param gameState
     *            the current state of the board
     * @param deadline
     *            time before which we must have returned
     * @return the next state the board is in after our move
     */
    int maxDepth = 7;


    public GameState play(final GameState gameState, final Deadline deadline) {

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        /*
        if(nextStates.size() % 2 == 1 || nextStates.size() > 12){
            Random random = new Random();
            return nextStates.elementAt(random.nextInt(nextStates.size()));
        }
        */


        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        int AorB = gameState.getNextPlayer();
        int stupidInt;
        boolean nextPlayer;

        if (AorB == 1){
            nextPlayer = false;
            stupidInt = -1;
        }
        else {
            nextPlayer = true;
            stupidInt = 1;
        }

        int[] moveValue = new int[nextStates.size()];
        int alpha = -1000000;
        int beta = 1000000;

        //alpha beta pruning
        int winOrLoseInRound = -1;
        int bestChoice = 0;
        int count = 0;
        for (GameState s : nextStates){
            if (s.isEOG()) {
                if (s.isXWin()) {
                    winOrLoseInRound = count;
                }
            }
            else {
                Move moveThatGotUsHere = s.getMove();
                int indexOnBoard = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
                int col = s.cellToCol(indexOnBoard);
                int row = s.cellToRow(indexOnBoard);
                int rowValScoreFulRow = 0;
                int colValScoreFulCol = 0;
                int diaValScore1FulDia = 0;
                int diaValScore2FulDia = 0;

                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(row, i);
                    if(thingInCell == 2){
                        rowValScoreFulRow--;
                    }
                }

                //Eval current col
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, col);
                    if(thingInCell == 2){
                        colValScoreFulCol--;
                    }

                }

                if(row == col){
                    for(int i  = 0 ; i < 4 ; i++ ) {
                        int thingInCell = s.at(i, i);
                        if(thingInCell == 2){
                            diaValScore1FulDia--;
                        }
                    }
                }
                if(row + col == 3){
                    for(int i  = 0 ; i < 4 ; i++ ) {
                        int thingInCell = s.at(i, 3-i);
                        if(thingInCell == 2){
                            diaValScore2FulDia--;
                        }
                    }
                }

                if ( rowValScoreFulRow < -2 || colValScoreFulCol < -2 || diaValScore1FulDia < -2 || diaValScore2FulDia < -2 ){

                    winOrLoseInRound = count;
                }
            }
            count++;
        }
        if (winOrLoseInRound == -1) {
            count = 0;
            int i = 0 ;
            for (GameState s : nextStates){
                int move = alphaBeta(s,maxDepth,alpha,beta,nextPlayer);
                moveValue[i] = move*stupidInt;
                i++;
            }
            /*
            for (GameState s : nextStates){
                int move = minimax(s,nextPlayer,maxDepth);
                moveValue[i] = move*stupidInt;
                i++;
            }
            */
            bestChoice = 0;
            count = 0;
            for (int j = 0 ; j < moveValue.length ; j++){
                if(moveValue[bestChoice] < moveValue[j]){
                    bestChoice = j;
                    count++;
                }
            }
        }
        else {
            bestChoice = winOrLoseInRound;
        }

        return nextStates.elementAt(bestChoice);
    }

    public int minimax(GameState state, boolean playerATurn, int md) {

        if (state.isEOG()) {
            //This is gamma
            md = 1;
            if (state.isXWin()) {
                return 10 * md;
            }
            else if (state.isOWin()) {
                return -10 * md;
            }
            else {
                return 0;
            }
        }

        else if(md == 0) {
            Move moveThatGotUsHere = state.getMove();
            int moveIndexThatGotUsHere = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
            int leafScore = eval4(state, moveIndexThatGotUsHere, playerATurn);
            return leafScore;
        }
        else {

            if (playerATurn) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                int bestPossible = -100000;

                md--;

                for (GameState s : possibleStates){
                    int v = minimax(s, false, md);
                    bestPossible = Math.max(v, bestPossible);
                }

                return bestPossible;

            }
            else {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                int bestPossible = 100000;
                md -= 1;
                for (GameState s : possibleStates){
                    int v = minimax(s, true, md);
                    bestPossible = Math.min(v, bestPossible);
                }
                return bestPossible;

            }
        }

    }

    public int alphaBeta(GameState state, int md, int alpha, int beta, boolean playerATurn) {
        int v = -100000000;
        if (state.isEOG()) {
            //This is gamma
            if (state.isXWin()) {
                v = 50 * (md+2);
                if (maxDepth == md){
                    v = 1000;
                }
            }
            else if (state.isOWin()) {
                v =  -50 * (md+2);
                if (maxDepth == md){
                    v = -1000;
                }
            }
            else {
                v = 0;
            }
        }

        else if(md == 0) {
            Move moveThatGotUsHere = state.getMove();
            int moveIndexThatGotUsHere = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
            int leafScore = eval4(state, moveIndexThatGotUsHere, playerATurn);
            v = leafScore;
        }

        else {

            if (playerATurn) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                v = -100000;

                md--;

                for (GameState rS : possibleStates){
                    v = Math.max(v, alphaBeta(rS,md,alpha,beta,false));
                    alpha = Math.max(alpha,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
            else {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                v = 100000;

                md--;

                for (GameState rS : possibleStates){
                    v = Math.min(v, alphaBeta(rS,md,alpha,beta,true));
                    beta = Math.min(beta,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
        }

        return v;
    }

    public int eval(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore1 = 0;
        int diaValScore2 = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);

        if(pAt){
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 1){
                    rowValScore++;
                }
                else if (thingInCell == 2){
                    rowValScore = 0;
                    break;
                }

            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 1){
                    colValScore++;
                }
                else if (thingInCell == 2){
                    colValScore = 0;
                    break;
                }

            }
            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 1){
                        diaValScore1++;
                    }
                    else if (thingInCell == 2){
                        diaValScore1 = 0;
                        break;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore2++;
                    }
                    else if (thingInCell == 2){
                        diaValScore2 = 0;
                        break;
                    }

                }
            }

        }
        else {
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 2){
                    rowValScore--;
                }
                else if (thingInCell == 1){
                    rowValScore = 0;
                    break;
                }

            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 2){
                    colValScore--;
                }
                else if (thingInCell == 1){
                    colValScore = 0;
                    break;
                }

            }
            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 2){
                        diaValScore1--;
                    }
                    else if (thingInCell == 1){
                        diaValScore1 = 0;
                        break;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore2--;
                    }
                    else if (thingInCell == 1){
                        diaValScore2 = 0;
                        break;
                    }

                }
            }

        }

        //System.err.println(diaValScore + rowValScore + colValScore);
        return (int) (Math.pow((double)diaValScore1,3) + Math.pow((double)diaValScore2,3) + Math.pow((double)rowValScore,3) + Math.pow((double)colValScore,3));
        //return (diaValScore1 + diaValScore2 + rowValScore + colValScore);
    }

    public int eval2(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore1 = 0;
        int diaValScore2 = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);

        if(pAt){
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 1){
                    rowValScore++;
                }
                if(thingInCell == 2){
                    rowValScore -= 1;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 1){
                    colValScore++;
                }
                if(thingInCell == 2){
                    colValScore -= 1;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 1){
                        diaValScore1++;
                    }
                    if(thingInCell == 2){
                        diaValScore1 -= 1;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore2++;
                    }
                    if(thingInCell == 2){
                        diaValScore2 -= 1;
                    }

                }
            }

        }

        else{

            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 2){
                    rowValScore--;
                }
                if(thingInCell == 1){
                    rowValScore += 1;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 2){
                    colValScore--;
                }
                if(thingInCell == 1){
                    colValScore += 1;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 2){
                        diaValScore1--;
                    }
                    if(thingInCell == 1){
                        diaValScore1 += 1;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore2--;
                    }
                    if(thingInCell == 1){
                        diaValScore2 += 1;
                    }

                }
            }

        }
        //System.err.println("-----------------------------");
        //System.err.println((int) (Math.pow((double)diaValScore1,3) + Math.pow((double)diaValScore2,3) + Math.pow((double)rowValScore,3) + Math.pow((double)colValScore,3)));
        return (int) (Math.pow((double)diaValScore1,3) + Math.pow((double)diaValScore2,3) + Math.pow((double)rowValScore,3) + Math.pow((double)colValScore,3));
    }

    public int eval3(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);

        if(pAt){
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 1){
                    rowValScore++;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 1){
                    colValScore++;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 1){
                        diaValScore++;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore++;
                    }

                }
            }

        }

        else {
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 2){
                    rowValScore--;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 2){
                    colValScore--;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 2){
                        diaValScore--;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore--;
                    }

                }
            }

        }

        return (diaValScore + rowValScore + colValScore);
    }

    public int eval4(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore1 = 0;
        int diaValScore2 = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);

        if(pAt){
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                for(int j  = 0 ; j < 4 ; j++ ) {
                    int thingInCell = s.at(j, i);
                    if(thingInCell == 1){
                        rowValScore++;
                    }
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                for(int j  = 0 ; j < 4 ; j++ ) {
                    int thingInCell = s.at(i, j);
                    if(thingInCell == 1){
                        colValScore++;
                    }

                }
            }

            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, i);
                if(thingInCell == 1){
                    diaValScore1++;
                }

            }


            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, 3-i);
                if(thingInCell == 1){
                    diaValScore2++;
                }

            }


        }

        else{
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                for(int j  = 0 ; j < 4 ; j++ ) {
                    int thingInCell = s.at(j, i);
                    if(thingInCell == 2){
                        rowValScore--;
                    }
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                for(int j  = 0 ; j < 4 ; j++ ) {
                    int thingInCell = s.at(i, j);
                    if(thingInCell == 2){
                        colValScore--;
                    }
                }

            }

            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, i);
                if(thingInCell == 2){
                    diaValScore1--;
                }

            }

            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, 3-i);
                if(thingInCell == 2){
                    diaValScore2--;
                }

            }

        }

        int rowValScoreFulRow = 0;
        int colValScoreFulCol = 0;
        int diaValScore1FulDia = 0;
        int diaValScore2FulDia = 0;


        if(pAt){
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 1){
                    rowValScoreFulRow++;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 1){
                    colValScoreFulCol++;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 1){
                        diaValScore1FulDia++;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore2FulDia++;
                    }

                }
            }

        }

        else {
            //Eval current row
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(row, i);
                if(thingInCell == 2){
                    rowValScoreFulRow--;
                }
            }

            //Eval current col
            for(int i  = 0 ; i < 4 ; i++ ) {
                int thingInCell = s.at(i, col);
                if(thingInCell == 2){
                    colValScoreFulCol--;
                }

            }

            if(row == col){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, i);
                    if(thingInCell == 2){
                        diaValScore1FulDia--;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore2FulDia--;
                    }

                }
            }

        }
        /*
        if ( rowValScoreFulRow > 2 || colValScoreFulCol > 2 || diaValScore1FulDia > 2 || diaValScore2FulDia > 2 ){
            if(pAt){
                colValScore += 25;
            }
            else {
                colValScore -= 25;
            }

        }
        */




        //System.err.println("-----------------------------");
        //System.err.println((int) (Math.pow((double)diaValScore1,3) + Math.pow((double)diaValScore2,3) + Math.pow((double)rowValScore,3) + Math.pow((double)colValScore,3)));
        return (diaValScore1 + diaValScore2 + rowValScore + colValScore);
    }

}