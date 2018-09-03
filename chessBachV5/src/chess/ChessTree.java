package chess;

import java.util.List;

public class ChessTree<T> extends Tree<ChessBoard> {

    private typeOfNode typeOfNode;
    private int depth, score, rippleScore;
    private Evaluator evaluator;
    private Move creatorMove;


    public ChessTree(ChessBoard data) {
        super(data);
        this.evaluator = new Evaluator();
        this.depth = 0;
        this.typeOfNode = typeOfNode.ROOT;
        scoreMe();
    }

    void scoreMe(){
        if (this.typeOfNode == typeOfNode.ROOT) {
            this.score = evaluator.eval(this.getData());
        } else {
            this.score = evaluator.deltaEval(
                    this.getData(),
                    ((ChessBoard) this.getParent().getData()),
                    this.creatorMove,
                    ((ChessTree<ChessBoard>) this.getParent()).getScore());
        }
        this.rippleScore = this.score;
    }

    void makeChildren(){
        List<Move> moveList =  this.getData().generateMoves();
        for (Move move : moveList) {
            ChessBoard newBoard = new ChessBoard(this.getData());
            newBoard.makeMove(move);
            ChessTree childTree = new ChessTree(newBoard);
            childTree.setCreatorMove(move);
            childTree.setParent(this);
            childTree.setDepth(this.depth+1);
            if ((this.depth + 1) % 2 == 0){
                childTree.setRippleScore(childTree.getRippleScore()*(-1));
            }
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
//                + "\nScore of the current board (the bigger the better):\n" +this.getScore()
//                + "\nRipple score of the current board (the bigger the better):\n" +this.getRippleScore()
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

    public int getDepth() {
        return depth;
    }

    private int getRippleScore() {
        return rippleScore;
    }

    private void setTypeOfNode(ChessTree.typeOfNode typeOfNode) {
        this.typeOfNode = typeOfNode;
    }

    public void setRippleScore(int rippleScore) {
        this.rippleScore = rippleScore;
    }

    public ChessTree.typeOfNode getTypeOfNode() {
        return typeOfNode;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public Move getCreatorMove() {
        return creatorMove;
    }

    public void setCreatorMove(Move creatorMove) {
        this.creatorMove = creatorMove;
    }

    enum typeOfNode {
        ROOT, NODE, LEAF
    }
}
