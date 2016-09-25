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
        int md = 3;
        int alpha = 0;
        int beta = 0;
        int bestChoiceIndex = 0;
        /*
        if(nextStates.size() % 2 == 1 || nextStates.size() > 10){
            Random random = new Random();
            return nextStates.elementAt(random.nextInt(nextStates.size()));
        }
        */
        int[] moveScores = new int[nextStates.size()];
        boolean playerXturn;
        if(gameState.getNextPlayer() == 2){
            System.err.println("player Os turn");
            playerXturn = false;
        } else {
            System.err.println("player Xs turn");
            playerXturn = true;
        }



        for(int i = 0 ; i < nextStates.size() ; i++){
            moveScores[i] = alphaBeta(nextStates.elementAt(i), md, alpha, beta, playerXturn);
        }
        for(int i = 0 ; i < moveScores.length ; i++){
            if(moveScores[i] > moveScores[bestChoiceIndex] && !playerXturn){
                bestChoiceIndex = i;
            }
            if(moveScores[i] < moveScores[bestChoiceIndex] && playerXturn){
                bestChoiceIndex = i;
            }
        }

        Random r = new Random();
        return nextStates.elementAt(bestChoiceIndex);
    }
    public int alphaBeta(GameState s, int maxDepth, int alpha, int beta, boolean xTurn){
        int v = 0;

        if(s.isEOG()){
            if(s.isXWin()){v = 100;}
            else if(s.isOWin()){v = -100;}
            else{v = 0;}
        }
        else if(maxDepth == 0){
            v = utility(s, xTurn);
        }

        else if(xTurn){
            v = -1000000;
            Vector<GameState> possibleStates = new Vector<GameState>();
            s.findPossibleMoves(possibleStates);
            for(int i = 0 ; i < possibleStates.size(); i++){
                v = Math.max(v, alphaBeta(possibleStates.elementAt(i), maxDepth-1, alpha, beta, false)); //TODO ska det vara maxDept-1?
                alpha = Math.max(alpha, v);
                if(beta <= alpha){
                    break; //Todo: Vad ska breakas igentligen?
                }
            }
        }
        else{
            v = 1000000;
            Vector<GameState> possibleStates = new Vector<GameState>();
            s.findPossibleMoves(possibleStates); //TODO: Är detta alla/bara O-spelarens possible moves?
            for(int i = 0 ; i < possibleStates.size(); i++){
                v = Math.min(v, alphaBeta(possibleStates.elementAt(i), maxDepth-1, alpha, beta, true));
                beta = Math.min(beta, v);
                if(beta <= alpha){
                    break; //Todo: Vad ska breakas igentligen?
                }
            }
        }

        return v;

    }
    public int utility(GameState s, boolean pAt){
        int value = 0;
        for (int i = 0 ; i < 16 ; i++){
            System.err.println(i/4 + " : " + i%4);
            if(s.at(i/4, i%4) == 1){
                value++;
            }
        }
        return value;


    }

}