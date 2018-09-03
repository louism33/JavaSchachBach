package chess;

import java.util.List;

public class IterativeDeepenerTwo {



    private int MATE = Evaluator.MATE;

    private int maxDepthReached;

    // TODO:
    /*
    fix timeUp
    decide if should run alpha beta on original tree, or on its children (current version is latter)
    parrallelise
    optimise performance
    include killer

     */
    public static void main (String[] args){
        IterativeDeepener id = new IterativeDeepener();
    }

    IterativeDeepenerTwo(){
        ChessBoard c = new ChessBoard();
        List<Move> moves = c.generateMoves();
//        c.makeMove(moves.get(0));
        ChessTree<ChessBoard> tree = new ChessTree<>(c);
        long st = System.currentTimeMillis();
        long alloted = 200000;


        Move mmm = expandChildren(tree, st, alloted);

//        ChessBoard zzz = new ChessBoard(c);
//        zzz.makeMove(mmm);

        System.out.println(mmm);
    }


    Move expandChildren (ChessTree<ChessBoard> tree, long startTime, long timeLimitMillis){

        // here we can say where to start looking, eg Killer Heuristic
        // eventually multi thread, so that no time div is necessary. Make thread safe
        tree.makeChildren();

        Move moveToPlay = null;

        int maxScore = Integer.MAX_VALUE;
        int numberOfKids = tree.getChildren().size();


        ChessTree<ChessBoard> winnerKid = null;
        int winnerNum = 0;


        for (Tree<ChessBoard> childTree : tree.getChildren()) {
            Move plop = ((ChessTree) childTree).getCreatorMove();

            long timeLimitForEachMove = timeLimitMillis / numberOfKids;

            int score = iterativeDeepeningSearch(((ChessTree) childTree), startTime, tree.getPlayer(), timeLimitForEachMove);


            if (score < maxScore){ // chagning max score to MIN  and this to > gives inverse answers
                maxScore = score;
                moveToPlay = ((ChessTree) childTree).getCreatorMove();
                winnerKid = (ChessTree<ChessBoard>) childTree;
                winnerNum = tree.getChildren().indexOf(winnerKid);
            }

        }

        System.out.println(winnerKid + "\n\n"+winnerNum);
        return moveToPlay;
    }



    int iterativeDeepeningSearch (ChessTree tree, long startTime, int originalPlayer, long timeLimitMillis) {
        long endTime = startTime + timeLimitMillis;
        int scoreOfBestMove = 0;
        int depthToSearchTo = 1;

        int initialAlpha = Integer.MIN_VALUE;
        int initialBeta = Integer.MAX_VALUE;

        while (depthToSearchTo < 5) {
            long currentTime = System.currentTimeMillis();
            if (currentTime >= endTime) {
                break;
            }

            int result = depthFirstSearch(tree, depthToSearchTo, initialAlpha, initialBeta, originalPlayer, currentTime, endTime - currentTime);


            scoreOfBestMove = result;

            if (depthToSearchTo > maxDepthReached) maxDepthReached = depthToSearchTo;
            depthToSearchTo++;
        }
        ChessTree<ChessBoard> debug = tree;

        return scoreOfBestMove;
    }



    private int depthFirstSearch (ChessTree<ChessBoard> tree, int howDeep, int alpha, int beta, int originalPlayer, long startTime, long timeLimitMillis){
        long currentTime = System.currentTimeMillis();
        long timeUsed = currentTime - startTime;

        boolean outOfTime = timeUsed >= timeLimitMillis;

        if (tree.getChildren().size() == 0) {
            tree.makeChildren();
        }

        if (howDeep <= 1 || outOfTime){
            return tree.getScore();
        }

        if (tree.getPlayer() == originalPlayer) {

            for (Tree<ChessBoard> childTree : tree.getChildren()) {
                alpha = Math.max(alpha,
                        -depthFirstSearch((ChessTree) childTree, howDeep - 1, alpha, beta, (originalPlayer), startTime, timeLimitMillis));

                if (beta <= alpha){
                    break;
                }
            }
            return alpha;
        }

        else {
            for (Tree<ChessBoard> childTree : tree.getChildren()) {
                beta = Math.min(beta,
                        -depthFirstSearch((ChessTree) childTree, howDeep - 1, alpha, beta, (originalPlayer), startTime, timeLimitMillis));

                if (beta <= alpha){
                    break;
                }
            }
            return beta;
        }
    }





    public int getMaxDepthReached() {
        return maxDepthReached;
    }
}
