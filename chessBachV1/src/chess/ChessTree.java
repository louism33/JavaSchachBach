package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChessTree<T> extends Tree<ChessBoard> {

    private int totalCount, totalLeafCount, depth, score, rippleScore, difficulty;
    private typeOfNode typeOfNode;
    private Evaluator evaluator;
    private List<Integer> childrenRippleScores;

    public ChessTree(ChessBoard data) {
        super(data);
        this.evaluator = new Evaluator();
        this.childrenRippleScores = new ArrayList<>();
        this.score = evaluator.eval(this.getData());
        this.depth = 0;
        this.rippleScore = 0;
        this.typeOfNode = typeOfNode.ROOT;

        // 1, 2 or 3
        difficulty = 3;

    }

    void findBestMoveInCertainTime (long timeAtStart) {
        deepChildren();
        this.extractScores();
        long t2 = System.currentTimeMillis();
        long ta = (t2 - timeAtStart)/1000;

        System.out.println("Making the tree and extracting the scores took "+ ta + " seconds.");

        totalCount = totalCount();
        totalLeafCount = totalLeafCount();
        System.out.println("This is approximately "+(this.totalCount / ta) + " boards per second.");
        System.out.println(this);

    }

    private void deepChildren(){
        this.makeChildren();
        if (this.depth < difficulty) {
            for (Tree<ChessBoard> childTree : this.getChildren()) {
                ((ChessTree<ChessBoard>) childTree).deepChildren();
            }
        }
    }

    Move bestMove(){
        Move mostDesirableMove;
        List<Move> moveList = this.getData().generateMoves();

        if (this.childrenRippleScores.size() > 0 ) {
            int mostDesirableScore;
            mostDesirableScore = Collections.max(this.childrenRippleScores);
            int indexOfMostDesirableMove = this.childrenRippleScores.indexOf(mostDesirableScore);
            mostDesirableMove = moveList.get(indexOfMostDesirableMove);

            return mostDesirableMove;
        }
        return moveList.get(0);
    }


    void extractScores (){
        if (this.typeOfNode == typeOfNode.LEAF){
            if (this.depth % 2 == 0) {
                this.rippleScore = this.score;
            }
            else {
                this.rippleScore = (-1) * this.score;
            }
        }
        else {
            for (Tree<ChessBoard> childTree : this.getChildren()) {
                ((ChessTree<ChessBoard>) childTree).extractScores();
                this.childrenRippleScores.add(((ChessTree<ChessBoard>) childTree).rippleScore);
            }
            this.rippleScore = Collections.max(this.childrenRippleScores);
        }
    }


    private void makeChildren(){
        List<Move> moveList =  this.getData().generateMoves();
        for (Move move : moveList) {
            ChessBoard newBoard = new ChessBoard(this.getData());
            newBoard.makeMove(move);
            ChessTree childTree = new ChessTree(newBoard);
            childTree.setParent(this);
            childTree.setDepth(this.depth+1);
            childTree.setTypeOfNode(typeOfNode.LEAF);
            if (this.typeOfNode == typeOfNode.LEAF){
                this.typeOfNode = typeOfNode.NODE;
            };
            this.addChild(childTree);
        }
    }

    int totalCount (){
        if (this.typeOfNode == typeOfNode.LEAF) {
            return 1;
        }
        int childrenNum = 1;
        for (Tree<ChessBoard> childTree : this.getChildren()) {
            childrenNum += ((ChessTree<ChessBoard>) childTree).totalCount();
        }
        return childrenNum ;
    }

    private int totalLeafCount (){
        if (this.typeOfNode == typeOfNode.LEAF) {
            return 1;
        }
        int childrenNum = 0;
        for (Tree<ChessBoard> childTree : this.getChildren()) {
            childrenNum += ((ChessTree<ChessBoard>) childTree).totalLeafCount();
        }
        return childrenNum;
    }

    @Override
    public String toString() {
        String ans = (""
//                + "Board:\n" + this.getData()
                + "\nScore of the current board (the bigger the better):\n" +this.score
                + "\nRipple score of the current board (the bigger the better):\n" +this.getRippleScore()
                + "\nNumber Of Children:\n"+this.getChildren().size()
                + "\nTotal Number of Boards in Tree:\n" + this.totalCount()
                + "\nOf which leaves:\n" + this.totalLeafCount()
        );
        return ans;
    }

    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            ChessTree ct = (ChessTree) o;
            return this.getData() == ct.getData();
        }
        return false;
    }


    private void setDepth(int depth) {
        this.depth = depth;
    }

    private int getRippleScore() {
        return rippleScore;
    }

    private void setTypeOfNode(ChessTree.typeOfNode typeOfNode) {
        this.typeOfNode = typeOfNode;
    }

    private enum typeOfNode {
        ROOT, NODE, LEAF
    }
}
