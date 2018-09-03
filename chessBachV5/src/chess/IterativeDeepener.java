package chess;

public class IterativeDeepener {

    private int maxDepthReached;

    // should alpha beta go here ?

    // the purpose of alpha beta would be NOT TO PURSUE certain branches
    // It seems that it works if eval is packaged into tree, as it currently is.
    // look up alpha beta with infinite trees

    void expandTree (ChessTree<ChessBoard> tree, long startTime, long timeLimitMillis){
        iterativeDeepeningSearch (tree,  startTime,  timeLimitMillis);
        }


    private void iterativeDeepeningSearch (ChessTree tree, long startTime, long timeLimitMillis) {

        boolean timeUp = false;

        int depthToSearchTo = 1;
        while (!timeUp) {

            depthFirstSearch(tree, depthToSearchTo, startTime, timeLimitMillis);

            depthToSearchTo++;

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            long currentTime = System.currentTimeMillis();
            timeUp = (currentTime - startTime > timeLimitMillis);
        }
    }




    private void depthFirstSearch (ChessTree<ChessBoard> tree, int howDeep, long startTime, long timeLimitMillis){
        if (tree.getChildren().size() == 0) {
            tree.makeChildren();
        }
        if (howDeep <= 1){
            return;
        }
        for (Tree<ChessBoard> childTree : tree.getChildren()) {

            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime > timeLimitMillis){
                return;
            }
            depthFirstSearch((ChessTree) childTree, howDeep - 1, startTime, timeLimitMillis);
        }
    }

    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
