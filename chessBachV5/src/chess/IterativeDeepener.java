package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private final int MATE = Evaluator.MATE;

//    void plop (){
//
//        tree.makeChildren();
//
//        List<Tree<ChessBoard>> kids = tree.getChildren();
//
//        System.out.println(tree.getChildrenScores());
//
//        tree.rankChildren();
//
//        System.out.println(tree.getChildrenScores());
//
//
//        System.out.println();
//    }

    public static void main(String[] args) {
        IterativeDeepener id = new IterativeDeepener();
    }

    IterativeDeepener() {

        long t1 = System.currentTimeMillis();

        ChessBoard c = new ChessBoard();

        ChessTree<ChessBoard> tree = new ChessTree<>(c);

        long startTime = System.currentTimeMillis();

        long timeLimitMillis = 1000000;



        expandTree(tree, startTime, timeLimitMillis);

        long t2 = System.currentTimeMillis();

        System.out.println(t2 - t1);


    }

    Move dfsWinningMove;
    Move winningMove;
    Move killerMove;

    void expandTree(ChessTree<ChessBoard> tree, long startTime, long timeLimitMillis) {
        winningMove = ((List<Move>) tree.getData().generateMoves()).get(0);
        dfsWinningMove = ((List<Move>) tree.getData().generateMoves()).get(0);


        Move bestMove = iterativeDeepeningSearch(tree, startTime, timeLimitMillis);

        System.out.println(bestMove);
        System.out.println();
    }


    private Move iterativeDeepeningSearch(ChessTree tree, long startTime, long timeLimitMillis) {

        Move bestMove = new Move();

        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int valueOfBoard = 0;


        int depthToSearchTo = 1;

        while (depthToSearchTo <= 6) {

            int player = ((ChessBoard) tree.getData()).turn;

            // possibly start this with previous winner board = (killer H within a move search?)
            int score = depthFirstSearch(tree, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            valueOfBoard = score;
            bestMove.copyMove(dfsWinningMove);


            System.out.println("---" + depthToSearchTo + "---");
            System.out.println("-- Score is : " + score + "--");
            System.out.println("- Move is : " + bestMove + "-");
            System.out.println();


            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;
        }
        return bestMove;
    }


    Move moveBeingExpanded = null;


    private int depthFirstSearch(ChessTree<ChessBoard> tree, int originalPlayer, int howDeep, int alpha, int beta, long startTime, long timeLimitMillis) {

        if (tree.getChildren().size() == 0) {
            tree.makeChildren();
            tree.rankChildren();
        }
        if (howDeep <= 1) {
            int col = (tree.getData().turn == originalPlayer) ? 1 : -1;
            return tree.getScore() * col;
        }





        if (tree.getData().turn == originalPlayer) {
            int value = Integer.MIN_VALUE;
            for (Tree<ChessBoard> childTree : tree.getChildren()) {

                ChessTree babyTree = ((ChessTree) childTree);

                if (babyTree.getParent().getParent() == null) {
                    moveBeingExpanded = ((ChessTree) childTree).getCreatorMove();
                }


                value = Math.max(value,
                        depthFirstSearch(((ChessTree) childTree), originalPlayer, howDeep - 1, alpha, beta, startTime, timeLimitMillis));

                if (value > alpha) {
                    alpha = value;
//                    System.out.println("   "+babyTree.getCreatorMove());
//                    dfsWinningMove.copyMove(moveBeingExpanded);


                    //if node is root ??
                    if (tree.getTypeOfNode() == ChessTree.typeOfNode.ROOT) {
                        dfsWinningMove.copyMove(babyTree.getCreatorMove());
                    }
                }

                if (alpha >= beta) {
                    break; // beta break off
                }
            }
            return value;
        }


        else {
            int value = Integer.MAX_VALUE;
            for (Tree<ChessBoard> childTree : tree.getChildren()) {
                ChessTree babyTree = ((ChessTree) childTree);
                value = Math.min(value,
                        depthFirstSearch(((ChessTree) childTree), originalPlayer, howDeep - 1, alpha, beta, startTime, timeLimitMillis));

                if (value < beta) {
                    beta = value;
                    if (tree.getTypeOfNode() == ChessTree.typeOfNode.ROOT) {
                        dfsWinningMove.copyMove(babyTree.getCreatorMove());
                    }
                }

                if (alpha >= beta) {
                    break; // alpha break off
                }
            }
            return value;
        }
    }

    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
