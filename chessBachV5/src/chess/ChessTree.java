package chess;

import java.util.Collections;
import java.util.List;

public class ChessTree<T> extends Tree<ChessBoard> {

    private typeOfNode typeOfNode;
    private int score;
    private Evaluator evaluator;
    private Move creatorMove;


    ChessTree(ChessBoard data) {
        super(data);
        this.evaluator = new Evaluator();
        this.typeOfNode = typeOfNode.ROOT;
    }

    private void scoreMe(){
        if (this.typeOfNode == typeOfNode.ROOT) {
            this.score = evaluator.eval(this.getData());
        } else {
            this.score = evaluator.deltaEval(
                    this.getData(),
                    ((ChessBoard) this.getParent().getData()),
                    this.creatorMove,
                    ((ChessTree<ChessBoard>) this.getParent()).getScore());
        }
    }

    void makeChildren(){
        List<Move> moveList =  this.getData().generateMoves();
        for (Move move : moveList) {
            ChessBoard newBoard = new ChessBoard(this.getData());
            newBoard.makeMove(move);
            ChessTree childTree = new ChessTree(newBoard);
            childTree.setCreatorMove(move);
            childTree.setParent(this);
            childTree.setTypeOfNode(typeOfNode.LEAF);
            if (this.typeOfNode == typeOfNode.LEAF){
                this.typeOfNode = typeOfNode.NODE;
            }
            this.addChild(childTree);
            childTree.scoreMe();
        }
    }

    void rankChildren(){
        Collections.sort(this.getChildren(), (a, b) ->
                ((ChessTree<ChessBoard>) a).getScore() - ((ChessTree<ChessBoard>) b).getScore()
        );
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

    private void setTypeOfNode(ChessTree.typeOfNode typeOfNode) {
        this.typeOfNode = typeOfNode;
    }

    ChessTree.typeOfNode getTypeOfNode() {
        return typeOfNode;
    }

    int getScore() {
        return score;
    }

    Move getCreatorMove() {
        return creatorMove;
    }

    void setCreatorMove(Move creatorMove) {
        this.creatorMove = creatorMove;
    }

    enum typeOfNode {
        ROOT, NODE, LEAF
    }
}
