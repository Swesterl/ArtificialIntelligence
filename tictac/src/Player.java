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



    public GameState play(final GameState gameState, final Deadline deadline) {

        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        int AorB = gameState.getNextPlayer();
        boolean playerAsMove = true;
        if(AorB == 2){playerAsMove = true;System.err.println("It is player A with X's move");}
        if(AorB == 1){playerAsMove = false;System.err.println("It is player B with O's move");}


        /*
        if(nextStates.size() % 2 == 1 || nextStates.size() > 10){
            Random random = new Random();
            return nextStates.elementAt(random.nextInt(nextStates.size()));
        }
        */


        if (nextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(gameState, new Move());
        }

        int maxDepth = 9;
        int[] moveValue = new int[nextStates.size()];
        int i = 0 ;
        int alpha = -10000;
        int beta = 10000;

        // minmax
        /*
        for (GameState s : nextStates){
            int move = minimax(s, true, maxDepth);
            moveValue[i] = move;
            i++;
        }
        */

        //alpha beta pruning
        for (GameState s : nextStates){
            int move = alphaBeta(s,maxDepth,alpha,beta,playerAsMove);
            moveValue[i] = move;
            i++;
        }

        int bestChoice = 0;
        int count = 0;
        if(playerAsMove){
            for (int j = 0 ; j < moveValue.length ; j++){
                if(moveValue[bestChoice] <= moveValue[j]){
                    bestChoice = j;
                    count++;
                }
            }
        }
        else{
            for (int j = 0 ; j < moveValue.length ; j++){
                if(moveValue[bestChoice] > moveValue[j]){
                    bestChoice = j;
                    count++;
                }
            }
        }

        /*
        if (count == moveValue.length){
            int[] moveValue2 = new int[nextStates.size()];
            i = 0 ;

            for (GameState s : nextStates){
                Move moveThatGotUsHere = s.getMove();
                int moveIndexThatGotUsHere = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
                moveValue2[i] = eval2(s, moveIndexThatGotUsHere, playerATurn);

            }
            bestChoice = 0;
            for (int j = 0 ; j < moveValue2.length ; j++){
                if(moveValue2[bestChoice] <= moveValue2[j]){
                    bestChoice = j;
                }
                System.err.println("move alternative " + nextStates.elementAt(j).toMessage() + " yields : "  + moveValue2[j]);
            }
            return nextStates.elementAt(bestChoice);

        }
        */

        return nextStates.elementAt(bestChoice);
    }

    public int minimax(GameState state, boolean playerATurn, int md) {

        if (state.isEOG()) {
            //This is gamma
            md = 1;
            if (state.isXWin()) {
                return 1 * md;
            }
            else if (state.isOWin()) {
                return -1 * md;
            }
            else {
                return 0;
            }
        }

        else if(md == 0) {
            Move moveThatGotUsHere = state.getMove();
            int moveIndexThatGotUsHere = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
            int leafScore = evalDontLose(state, moveIndexThatGotUsHere, playerATurn);
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

            } else if (!playerATurn) {
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

        return 123215412;
    }

    public int alphaBeta(GameState state, int md, int alpha, int beta, boolean playerATurn) {
        int v = 0;
        if (state.isEOG()) {
            //This is gamma
            if (state.isXWin()) {
                v = 10 * (md+1);
            }
            else if (state.isOWin()) {
                v =  -10 * (md+1);
            }
            else {
                v = 0;
            }
        }

        else if(md == 0) {
            Move moveThatGotUsHere = state.getMove();
            int moveIndexThatGotUsHere = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
            int leafScore = evalDontLose(state, moveIndexThatGotUsHere, playerATurn);
            v = leafScore;
        }

        else {

            if (playerATurn) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                v = -100000;

                md--;

                for (GameState s : possibleStates){
                    v = Math.max(v, alphaBeta(s,md,alpha,beta,false));
                    alpha = Math.max(alpha,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            } else if (!playerATurn) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                v = 100000;

                md--;

                for (GameState s : possibleStates){
                    v = Math.min(v, alphaBeta(s,md,alpha,beta,true));
                    beta = Math.min(beta,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
        }

        return v;
    }

    public int evalHomeMade(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore1 = 0;
        int diaValScore2 = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);d

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

        //System.err.println(diaValScore + rowValScore + colValScore);
        return (diaValScore1 + diaValScore2 + rowValScore + colValScore);
    }

    public int evalTypLabb(GameState s, int indexOnBoard, boolean pAt){

        int rowValScore = 0;
        int colValScore = 0;
        int diaValScore1 = 0;
        int diaValScore2 = 0;

        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);

        //System.err.println("column: " + col);
        //System.err.println("row: " + row);


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



        return (diaValScore1+diaValScore2+rowValScore+colValScore);
        //return (int) (Math.pow((double)diaValScore,3) + Math.pow((double)rowValScore,3) + Math.pow((double)colValScore,3));
    }


    public int evalLabb(GameState s, int indexOnBoard, boolean pAt){

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
                        diaValScore1++;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore2++;
                    }

                }
            }

        }

        else{
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
                        diaValScore1--;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore2--;
                    }

                }
            }

        }

        return (diaValScore1 + diaValScore2 + rowValScore + colValScore);
    }

     public int evalDontLose(GameState s, int indexOnBoard, boolean pAt){

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
                        diaValScore1++;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 1){
                        diaValScore2++;
                    }

                }
            }
            if(rowValScore == 4){rowValScore=100;}
            if(colValScore == 4){rowValScore=100;}
            if(diaValScore1 == 4){rowValScore=100;}
            if(diaValScore2 == 4){rowValScore=100;}
        }

        else{
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
                        diaValScore1--;
                    }

                }
            }
            if(row + col == 3){
                for(int i  = 0 ; i < 4 ; i++ ) {
                    int thingInCell = s.at(i, 3-i);
                    if(thingInCell == 2){
                        diaValScore2--;
                    }

                }
            }
            if(rowValScore == -4){rowValScore=-100;}
            if(colValScore == -4){rowValScore=-100;}
            if(diaValScore1 == -4){rowValScore=-100;}
            if(diaValScore2 == -4){rowValScore=-100;}
        }

        return (diaValScore1 + diaValScore2 + rowValScore + colValScore);
    }
}