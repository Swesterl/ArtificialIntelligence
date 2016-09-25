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


        int winOrLoseInRound = -1;
        int bestChoice = 0;
        int count = 0;
        for (GameState s : nextStates){
            if (s.isEOG()) {
                if (s.isXWin()) {
                    winOrLoseInRound = count;
                    break;
                }
            }
            //TODO break win and lose into two separate loops so we can break earlier!
            else {
                if(otk(s)){
                    winOrLoseInRound = count;
                }
            }
            count++;
        }
        if (winOrLoseInRound == -1) {
            Random random = new Random();
            bestChoice = random.nextInt(nextStates.size());
        }
        else {
            bestChoice = winOrLoseInRound;
        }
        return nextStates.elementAt(bestChoice)
;




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

}
