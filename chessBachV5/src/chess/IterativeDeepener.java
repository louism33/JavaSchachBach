package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private final int MATE = Evaluator.MATE;

    public static void main (String[] args){
        IterativeDeepener id = new IterativeDeepener();
    }

    IterativeDeepener (){

        ChessBoard c = new ChessBoard();

        ChessTree<ChessBoard> tree = new ChessTree<>(c);

        long startTime = System.currentTimeMillis();

        long timeLimitMillis = 1000000;

        expandTree(tree, startTime, timeLimitMillis);

    }

    Move dfsWinningMove;
    Move winningMove;

    void expandTree (ChessTree<ChessBoard> tree, long startTime, long timeLimitMillis){
        winningMove = ((List<Move>) tree.getData().generateMoves()).get(0);
        dfsWinningMove = ((List<Move>) tree.getData().generateMoves()).get(0);



        int magicNumber = iterativeDeepeningSearch (tree,  startTime,  timeLimitMillis);




        System.out.println(winningMove);


        System.out.println();
    }


    private int iterativeDeepeningSearch (ChessTree tree, long startTime, long timeLimitMillis) {

        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int valueOfBoard = 0;


        int depthToSearchTo = 1;

        while ( depthToSearchTo <= 3) {

//            long currentTime = System.currentTimeMillis();
//            timeUp = (currentTime - startTime > timeLimitMillis);


            int player = ((ChessBoard) tree.getData()).turn;

            // possibly start this with previous winner board = (killer H within a move search?)
            int score = depthFirstSearch(tree, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);



            valueOfBoard = score;
            winningMove.copyMove(dfsWinningMove);


            System.out.println("- Move is : "+ winningMove + "-");
            System.out.println("-- Score is : "+ score + "--");
            System.out.println("---" + depthToSearchTo + "---");
//            if (score == MATE) {
//                return score;
//            }


            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;



        }
        return valueOfBoard;
    }



    Move moveBeingExpanded = null;
    private int depthFirstSearch (ChessTree<ChessBoard> tree, int originalPlayer, int howDeep, int alpha, int beta, long startTime, long timeLimitMillis){
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - startTime > timeLimitMillis){
//                return;
//            }

        if (howDeep <= 1){
            int col = (tree.getData().turn == originalPlayer) ? -1 : 1;

            return tree.getScore() * -col;
        }

        if (tree.getChildren().size() == 0) {
            tree.makeChildren();
            tree.rankChildren();
        }


        if (tree.getData().turn == originalPlayer) {

//            System.out.println("            turn : "+tree.getData().turn);


            for (Tree<ChessBoard> childTree : tree.getChildren()) {
                ChessTree babyTree = ((ChessTree) childTree);

//                System.out.println("a is " + babyTree.getCreatorMove() + "    alpha  is "+ alpha + "    beta is "+beta);

                // moveBeingExpanded will remember which original move we are looking at
                if (babyTree.getParent().getParent() == null) {
                    moveBeingExpanded = ((ChessTree) childTree).getCreatorMove();
                }




                int tempA = alpha;
                int tempB = alpha;


                alpha = Math.max(alpha,
                        depthFirstSearch((ChessTree) childTree, originalPlayer, howDeep - 1,
                                alpha, beta, startTime, timeLimitMillis));

                tempB = alpha;

                if (tempA != tempB){ // do not change this
                    if (dfsWinningMove != moveBeingExpanded){
                        dfsWinningMove.copyMove(moveBeingExpanded);
                    }
                }

//                System.out.println(alpha);

//                if (alpha >= beta){
//                    System.out.println("---BREAK in a ---  a "+alpha+" b "+beta+  " ---------------------------");
//                    System.out.println(babyTree.getCreatorMove());
//                    break;
//                }

            }

            return alpha;

        }

        else {
//            System.out.println("            turn : "+tree.getData().turn);

//            Move moveBeingExpanded = null;

            for (Tree<ChessBoard> childTree : tree.getChildren()) {

                ChessTree babyTree = ((ChessTree) childTree);

//                System.out.println("     b is " +babyTree.getCreatorMove() + "    alpha is "+ alpha+ "       beta is "+beta);


//                System.out.println("beta "+ beta);

//                System.out.println("   " + moveBeingExpanded + " is pretty good.");

//
//
//                // moveBeingExpanded will remember which original move we are looking at
//                if (babyTree.getParent().getParent() == null) {
//                    moveBeingExpanded = ((ChessTree) childTree).getCreatorMove();
//                }
//
//
//                int tempA = beta;
//                int tempB = beta;


                beta = Math.min(beta,
                        depthFirstSearch((ChessTree) childTree, originalPlayer, howDeep - 1,
                                alpha, beta, startTime, timeLimitMillis));


//                System.out.println("    "+beta);
//                tempB = beta;
//
//                if (tempA != tempB && dfsWinningMove != moveBeingExpanded){
//                    dfsWinningMove.copyMove(moveBeingExpanded);
//                }
//
//                if (alpha >= beta){
//                    System.out.println("---BREAK in b ---  a "+alpha+" b "+beta+  " ---------------------------");
//                    System.out.println(babyTree.getCreatorMove());
//                    break;
//                }

            }
//            dfsWinningMove = tree.getCreatorMove();
            return beta;
        }
    }





    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
