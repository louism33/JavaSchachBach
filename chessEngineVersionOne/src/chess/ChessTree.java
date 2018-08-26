package chess;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChessTree<T> extends Tree<ChessBoard> {

    private int totalCount, totalLeafCount;
    private int depth, score, rippleScore;
    private long timeLeft;
    private boolean success, failure;
    private typeOfNode typeOfNode;
    private Evaluator evaluator;
    private List<Integer> childrenScores, childrenRippleScores;


    private List<Object> deepChildrenScores;

    public ChessTree(ChessBoard data) {
        super(data);
        this.evaluator = new Evaluator();
        this.childrenScores = new ArrayList<>();
        this.childrenRippleScores = new ArrayList<>();
        this.deepChildrenScores = new ArrayList<>();
        this.score = evaluator.eval(this.getData());
        this.depth = 0;
        this.rippleScore = 0;
        this.typeOfNode = typeOfNode.ROOT;
        this.timeLeft = 1000000;
    }

    void findBestMoveInCertainTime (long timeAtStart, long allocatedTime) {

        this.timeLeft = allocatedTime;

        deepChildren(timeAtStart, this.timeLeft);

        this.extractScores();

        long t2 = System.currentTimeMillis();

        long ta = (t2 - timeAtStart)/1000;

        System.out.println("\nAllocated time was: "+ allocatedTime/1000);
        System.out.println("Making the tree and extracting the scores took "+ ta + " seconds.");
        totalCount = totalCount(0);
        totalLeafCount = totalLeafCount(0);


        System.out.println(this);

    }
// need to find a way to HALT EVERYTHING is time is ever up



    void deepChildren(long timeAtStart, long allocatedTime){

        this.timeLeft = 1; //timeAtStart + allocatedTime - System.currentTimeMillis();

        if (this.timeLeft > 0) {

            this.makeChildren();
        }
//        else {
//            System.out.println("time out:\nTL:"+this.timeLeft+"\n"+(System.currentTimeMillis()-timeAtStart));
//        }

        this.timeLeft = 1; // timeAtStart + allocatedTime - System.currentTimeMillis();
        if (this.timeLeft > 0) {

            if (this.depth < 1) {
                for (Tree<ChessBoard> childTree : this.getChildren()) {
                    ((ChessTree<ChessBoard>) childTree).deepChildren(timeAtStart, timeLeft);
                }
            }
        }
    }




    Move bestMove(){
        Move mostDesirableMove;
        List<Move> moveList = this.getData().generateMoves();

        if (this.childrenRippleScores.size() > 0 ) {
            int mostDesirableScore = 0;
            mostDesirableScore = Collections.max(this.childrenRippleScores);
            int indexOfMostDesirableMove = this.childrenRippleScores.indexOf(mostDesirableScore);
            mostDesirableMove = moveList.get(indexOfMostDesirableMove);

            return mostDesirableMove;
        }
        System.out.println("!!!");
        return moveList.get(0);
    }


    void extractScores (){
//        System.out.println(this.timeLeft);


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


    void makeChildren(){
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







    int totalCount (int acc){

        if (this.typeOfNode == typeOfNode.LEAF) {
            return 1;
        }
        int childrenNum = 1;
        for (Tree<ChessBoard> childTree : this.getChildren()) {
            childrenNum += ((ChessTree<ChessBoard>) childTree).totalCount(acc);
        }
        return childrenNum ;
    }

    int totalLeafCount (int acc){

        if (this.typeOfNode == typeOfNode.LEAF) {
            return 1;
        }
        int childrenNum = 0;
        for (Tree<ChessBoard> childTree : this.getChildren()) {
            childrenNum += ((ChessTree<ChessBoard>) childTree).totalLeafCount(acc);
        }
        return childrenNum;
    }



    List<Integer> childrenScores(){

        this.deepScoreMe();

        if (this.getChildren() != null && this.getChildren().size() > 0) {
            for (Tree<ChessBoard> childTree : this.getChildren()) {
                this.childrenScores.add(((ChessTree<ChessBoard>) childTree).score);
            }
        }

        return this.childrenScores;
    }

//    List<Object> deepChildrenScores(){
//
//        //this will create a repetition, change later
//        this.deepScoreMe();
//
//        if (this.getChildren() != null && this.getChildren().size() > 0) {
//            for (Tree<ChessBoard> childTree : this.getChildren()) {
//                this.deepChildrenScores.add(((ChessTree<ChessBoard>) childTree).deepChildrenScores);
//            }
//        }
//
//        return this.deepChildrenScores;
//    }
















    void scoreMe(){
        this.setScore(evaluator.eval(this.getData()) );
    }

    void deepScoreMe(){
//        this.score = evaluator.eval(this.getData());
        if (this.getChildren() != null && this.getChildren().size() > 0) {
            for (Tree<ChessBoard> childTree : this.getChildren()) {
                ((ChessTree<ChessBoard>) childTree).deepScoreMe();
            }
        }
    }


    @Override
    public String toString() {
        String ans = (""
//                + "Board:\n" + this.getData()
                + "\nScore of this board (the bigger the better):\n" +this.score
                + "\nRipple score of this board (the bigger the better):\n" +this.getRippleScore()
                + "\nNumber Of Children:\n"+this.getChildren().size()
                + "\nTotal Number of Boards in Tree:\n" + this.totalCount(0)
        );
        return ans;
    }

    public String deepToString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.toString());
        for (Tree<ChessBoard> childTree : this.getChildren()){
            sb.append(((ChessTree) childTree).deepToString());
        }
        return sb.toString();
    }


    @Override
    public boolean equals(Object o) {
        if (o.getClass() == this.getClass()) {
            ChessTree ct = (ChessTree) o;
            return this.getData() == ct.getData();
        }
        return false;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public boolean isFailure() {
        return failure;
    }

    public void setFailure(boolean failure) {
        this.failure = failure;
    }

    public int getRippleScore() {
        return rippleScore;
    }

    public void setRippleScore(int rippleScore) {
        this.rippleScore = rippleScore;
    }

    public ChessTree.typeOfNode getTypeOfNode() {
        return typeOfNode;
    }

    public void setTypeOfNode(ChessTree.typeOfNode typeOfNode) {
        this.typeOfNode = typeOfNode;
    }

    public Evaluator getEvaluator() {
        return evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public List<Integer> getChildrenScores() {
        return childrenScores;
    }

    public void setChildrenScores(List<Integer> childrenScores) {
        this.childrenScores = childrenScores;
    }

    public List<Integer> getChildrenRippleScores() {
        return childrenRippleScores;
    }

    public void setChildrenRippleScores(List<Integer> childrenRippleScores) {
        this.childrenRippleScores = childrenRippleScores;
    }

    public List<Object> getDeepChildrenScores() {
        return deepChildrenScores;
    }

    public void setDeepChildrenScores(List<Object> deepChildrenScores) {
        this.deepChildrenScores = deepChildrenScores;
    }

    public enum typeOfNode {
        ROOT, NODE, LEAF
    }
}
