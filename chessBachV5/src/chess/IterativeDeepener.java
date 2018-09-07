package chess;

import java.util.List;

public class IterativeDeepener {

    private int maxDepthReached;
    private Move dfsWinningMove;

    Move expandTree(ChessTree<ChessBoard> tree, long startTime, long timeLimitMillis) {
        dfsWinningMove = ((List<Move>) tree.getData().generateMoves()).get(0);

        Move bestMove = iterativeDeepeningSearch(tree, startTime, timeLimitMillis);

        return bestMove;
    }


    private Move iterativeDeepeningSearch(ChessTree tree, long startTime, long timeLimitMillis) {

        Move bestMove = new Move();
        boolean timeUp = false;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        int depthToSearchTo = 1;

        while (!timeUp) {

            long currentTime = System.currentTimeMillis();

            long timeLeft = startTime + timeLimitMillis - currentTime;

            if (timeLeft < 0) {
                timeUp = true;
            }

            int player = ((ChessBoard) tree.getData()).turn;


            depthFirstSearch(tree, player, depthToSearchTo, alpha, beta, startTime, timeLimitMillis);

            bestMove.copyMove(dfsWinningMove);

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;
        }
        return bestMove;
    }


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

                long currentTime = System.currentTimeMillis();
                long timeLeft = startTime + timeLimitMillis - currentTime;
                if (timeLeft < 0) {
                    break;
                }

                ChessTree babyTree = ((ChessTree) childTree);

                value = Math.max(value,
                        depthFirstSearch(((ChessTree) childTree), originalPlayer, howDeep - 1, alpha, beta, startTime, timeLimitMillis));

                if (value > alpha) {
                    alpha = value;
                    if (tree.getTypeOfNode() == ChessTree.typeOfNode.ROOT) {
                        dfsWinningMove.copyMove(babyTree.getCreatorMove());
                    }
                }

                if (alpha >= beta) {
                    break;
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
                    break;
                }
            }
            return value;
        }
    }

    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
