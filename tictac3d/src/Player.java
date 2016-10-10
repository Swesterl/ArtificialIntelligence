import java.util.*;

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
    public ArrayList<Integer> myAwesomeList;
    public int maxDepth;

    public GameState play(final GameState gameState, final Deadline deadline) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);


        /*
        if(nextStates.size() % 2 == 1 || nextStates.size() > 44){
            Random random = new Random();
            return nextStates.elementAt(random.nextInt(nextStates.size()));
        }
        */

        if (nextStates.size() == 0) {
            return new GameState(gameState, new Move());
        }


        int[] moveValue = new int[nextStates.size()];
        myAwesomeList = new ArrayList<Integer>();
        int alpha = -1000000;
        int beta = 1000000;
        int maxDepth = 6;

        for (GameState s : nextStates){
            if (s.isXWin()) {
                return s;
            }
        }

        for (GameState s : nextStates){
            if(otk(s)){
                return s;
            }
        }



        int score2 = alphaBeta(gameState,maxDepth,1,alpha,beta,true);
        return nextStates.elementAt(myAwesomeList.indexOf(score2));
    }



    public int alphaBeta(GameState state, int md, int depth, int alpha, int beta, boolean playerATurn) {
        int v = -100000000;
        if (state.isEOG()) {
            //This is gamma
            if (state.isXWin()) {
                v = 5000;
            }
            else if (state.isOWin()) {
                v =  -500;
            }
            else {
                v = 0;
            }
        }

        else if(depth == md) {
            int leafScore = eval5(state, playerATurn);
            v = leafScore;
        }

        else {

            if (playerATurn) {
                Vector<GameState> possibleStates = new Vector<GameState>();
                state.findPossibleMoves(possibleStates);

                v = -100000;

                for (GameState rS : possibleStates){
                    int childValue = alphaBeta(rS,md,depth + 1,alpha,beta,false);
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

                v = 100000;



                for (GameState rS : possibleStates){
                    v = Math.min(v, alphaBeta(rS,md,depth +1 ,alpha,beta,true));
                    beta = Math.min(beta,v);
                    if (beta <= alpha){
                        break;
                    }
                }

            }
        }

        return v;
    }

    public boolean otk(GameState s){
        Move moveThatGotUsHere = s.getMove();
        int indexOnBoard = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);
        int lay = s.cellToLay(indexOnBoard);
        if(nrOfMarksOnRow(s, row, lay, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnCol(s, col, lay, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnLay(s, row, col, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnRowDia(s, row, col, lay, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnColDia(s, row, col, lay, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnLayDia(s, row, col, lay, Constants.CELL_O) > 2){return true;}
        if(nrOfMarksOnDiaDia(s, row, col, lay, Constants.CELL_O) > 2){return true;}
        return false;
    }

    public int nrOfMarksOnRow(GameState s, int row, int lay, int mark){
        int score = 0;
        for(int i  = 0 ; i < 4 ; i++) {
            int thingInCell = s.at(row, i, lay);
            if(thingInCell == mark){
                score++;
            }

        }
        return score;
    }

    public int nrOfMarksOnCol(GameState s, int col, int lay, int mark){
        int score = 0;
        for(int i  = 0 ; i < 4 ; i++) {
            int thingInCell = s.at(i, col, lay);
            if(thingInCell == mark){
                score++;
            }

        }
        return score;
    }

    public int nrOfMarksOnLay(GameState s, int row, int col, int mark){
        int score = 0;
        for(int i  = 0 ; i < 4 ; i++) {
            int thingInCell = s.at(row, col, i);
            if(thingInCell == mark){
                score++;
            }

        }
        return score;
    }

    public int nrOfMarksOnRowDia(GameState s, int row, int col, int lay, int mark){
        int score = 0;
        if(col == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(row, i, i);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        if(3-col == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(row, 3-i, i);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        return score;

    }

    public int nrOfMarksOnColDia(GameState s, int row, int col, int lay, int mark){
        int score = 0;
        if(row == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(i, col, i);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        if(3-row == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(3-i, col, i);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        return score;

    }

    public int nrOfMarksOnLayDia(GameState s, int row, int col, int lay, int mark){
        int score = 0;
        if(row == col){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(i, i, lay);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        if(3-row == col){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(3-i, i, lay);
                if(thingInCell == mark){
                    score++;
                }

            }

        }
        return score;

    }

    public int nrOfMarksOnDiaDia(GameState s, int row, int col, int lay, int mark){
        int score = 0;

        if(row == col && col == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(i, i, i);
                if(thingInCell == mark){
                    score++;
                }

            }
        }

        if(3-row == col && col == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(3-i, i, i);
                if(thingInCell == mark){
                    score++;
                }

            }
        }
        //TODO remove uncessary arithmetic below!
        if(3-row == 3-col && 3-col == lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(3-i, 3-i, i);
                if(thingInCell == mark){
                    score++;
                }

            }
        }

        if(3-row == col && col == 3-lay){
            for(int i  = 0 ; i < 4 ; i++) {
                int thingInCell = s.at(3-i, i, 3-i);
                if(thingInCell == mark){
                    score++;
                }

            }
        }

        return score;
    }

    public int eval(GameState s, boolean pAt){

        Move moveThatGotUsHere = s.getMove();
        int indexOnBoard = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);
        int lay = s.cellToLay(indexOnBoard);


        int sum = 0;
        int constant = 0;
        int antiConstant = 0;
        int ab = 1;

        if (pAt){
            constant = Constants.CELL_X;
            antiConstant = Constants.CELL_O;
            ab = 1;
        }
        else {
            constant = Constants.CELL_O;
            antiConstant = Constants.CELL_X;
            ab = -1;
        }

        sum += Math.pow(nrOfMarksOnRow(s, row, lay, constant),2);
        sum += Math.pow(nrOfMarksOnCol(s, col, lay, constant),2);
        sum += Math.pow(nrOfMarksOnLay(s, row, col, constant),2);
        sum += Math.pow(nrOfMarksOnRowDia(s, row, col, lay, constant),2);
        sum += Math.pow(nrOfMarksOnColDia(s, row, col, lay, constant),2);
        sum += Math.pow(nrOfMarksOnLayDia(s, row, col, lay, constant),2);
        sum += Math.pow(nrOfMarksOnDiaDia(s, row, col, lay, constant),2);

        return sum*ab;
    }

    public int eval2(GameState s, boolean pAt){

        Move moveThatGotUsHere = s.getMove();
        int indexOnBoard = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);
        int lay = s.cellToLay(indexOnBoard);



        int constant = 0;
        int antiConstant = 0;
        int ab = 1;


        if (pAt){
            constant = Constants.CELL_X;
            antiConstant = Constants.CELL_O;
            ab = 1;
        }
        else {
            constant = Constants.CELL_O;
            antiConstant = Constants.CELL_X;
            ab = -1;
        }

        int score = 0;
        for(int i  = 0 ; i < 4 ; i++) {
            for(int j  = 0 ; j < 4 ; j++) {
                for(int k  = 0 ; k < 4 ; k++) {
                    int thingInCell = s.at(i,j,k);
                    if(thingInCell == constant){
                        score++;
                    }
                }
            }
        }

        score = score * 3;

        for(int i  = 0 ; i < 4 ; i++) {
            score += nrOfMarksOnRowDia(s, i, 0, 0, constant);
            score += nrOfMarksOnColDia(s, 0, i, 0, constant);
            score += nrOfMarksOnLayDia(s, 0, 0, i, constant);
            score += nrOfMarksOnRowDia(s, i, 0, 3, constant);
            score += nrOfMarksOnColDia(s, 3, i, 0, constant);
            score += nrOfMarksOnLayDia(s, 0, 3, i, constant);
        }

        score += 2*nrOfMarksOnDiaDia(s, 0, 0, 0, constant);
        score += 2*nrOfMarksOnDiaDia(s, 3, 0, 0, constant);
        score += 2*nrOfMarksOnDiaDia(s, 3, 3, 0, constant);
        score += 2*nrOfMarksOnDiaDia(s, 0, 3, 0, constant);




        return score*ab;
    }

    public int eval3(GameState s, boolean playerATurn){
        int constant = 0;
        int antiConstantEval3 = 0;
        int multi = 1;
        if (playerATurn){
            constant = Constants.CELL_X;
            antiConstantEval3 = Constants.CELL_O;
            multi = 1;
        }
        else {
            constant = Constants.CELL_O;
            antiConstantEval3 = Constants.CELL_X;
            multi = -1;
        }
        Move moveThatGotUsHere = s.getMove();
        int indexOnBoard = Integer.parseInt(moveThatGotUsHere.toMessage().split("_")[1]);
        int col = s.cellToCol(indexOnBoard);
        int row = s.cellToRow(indexOnBoard);
        int lay = s.cellToLay(indexOnBoard);
        int sum = 0;
        if(nrOfMarksOnRow(s, row, lay, constant) > 2){if(nrOfMarksOnRow(s, row, lay, antiConstantEval3) == 0){sum += 1;}}
        if(nrOfMarksOnCol(s, col, lay, constant) > 2){if(nrOfMarksOnCol(s, row, lay, antiConstantEval3) == 0){sum += 1;}}
        if(nrOfMarksOnLay(s, row, col, constant) > 2){if(nrOfMarksOnLay(s, row, lay, antiConstantEval3) == 0){sum += 1;}}
        if(nrOfMarksOnRowDia(s, row, col, lay, constant) > 2){if(nrOfMarksOnRowDia(s, row, col, lay, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnColDia(s, row, col, lay, constant) > 2){if(nrOfMarksOnColDia(s, row, col, lay, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnLayDia(s, row, col, lay, constant) > 2){if(nrOfMarksOnLayDia(s, row, col, lay, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnDiaDia(s, row, col, lay, constant) > 2){if(nrOfMarksOnDiaDia(s, row, col, lay, antiConstantEval3) == 0){sum += 2;}}

        return sum*multi;
    }

    public int eval4(GameState s, boolean playerATurn){
        int constant = 0;
        int antiConstantEval3 = 0;
        int multi = 1;
        int sum = 0;
        if (playerATurn){
            constant = Constants.CELL_X;
            antiConstantEval3 = Constants.CELL_O;
            multi = 1;
        }
        else {
            constant = Constants.CELL_O;
            antiConstantEval3 = Constants.CELL_X;
            multi = -1;
        }

        for(int i  = 0 ; i < 4 ; i++) {
            for(int j  = 0 ; j < 4 ; j++) {
                if(nrOfMarksOnRow(s, i, j, constant) > 2){if(nrOfMarksOnRow(s, i, j, antiConstantEval3) == 0){sum += 1;}}
                if(nrOfMarksOnCol(s, i, j, constant) > 2){if(nrOfMarksOnCol(s, i, j, antiConstantEval3) == 0){sum += 1;}}
                if(nrOfMarksOnLay(s, i, j, constant) > 2){if(nrOfMarksOnLay(s, i, j, antiConstantEval3) == 0){sum += 1;}}

            }
            if(nrOfMarksOnRowDia(s, i, 0, 0, constant) > 2){if(nrOfMarksOnRowDia(s, i, 0, 0, constant) == 0){sum += 1;}}
            if(nrOfMarksOnColDia(s, 0, i, 0, constant) > 2){if(nrOfMarksOnColDia(s, 0, i, 0, constant) == 0){sum += 1;}}
            if(nrOfMarksOnLayDia(s, 0, 0, i, constant) > 2){if(nrOfMarksOnLayDia(s, 0, 0, i, constant) == 0){sum += 1;}}
            if(nrOfMarksOnRowDia(s, i, 0, 3, constant) > 2){if(nrOfMarksOnRowDia(s, i, 0, 3, constant) == 0){sum += 1;}}
            if(nrOfMarksOnColDia(s, 3, i, 0, constant) > 2){if(nrOfMarksOnColDia(s, 3, i, 0, constant) == 0){sum += 1;}}
            if(nrOfMarksOnLayDia(s, 0, 3, i, constant) > 2){if(nrOfMarksOnLayDia(s, 0, 3, i, constant) == 0){sum += 1;}}
        }

        if(nrOfMarksOnDiaDia(s, 0, 0, 0, constant) > 2){if(nrOfMarksOnDiaDia(s, 0, 0, 0, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnDiaDia(s, 3, 0, 0, constant) > 2){if(nrOfMarksOnDiaDia(s, 3, 0, 0, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnDiaDia(s, 3, 3, 0, constant) > 2){if(nrOfMarksOnDiaDia(s, 3, 3, 0, antiConstantEval3) == 0){sum += 2;}}
        if(nrOfMarksOnDiaDia(s, 0, 3, 0, constant) > 2){if(nrOfMarksOnDiaDia(s, 0, 3, 0, antiConstantEval3) == 0){sum += 2;}}

        return sum*multi;
    }

    public int eval5(GameState s, boolean playerATurn){
        if (playerATurn){
            return 2*eval4(s,true) + eval4(s,false);
        }
        else {
            return eval4(s,true) + 2*eval4(s,false);
        }
    }

}