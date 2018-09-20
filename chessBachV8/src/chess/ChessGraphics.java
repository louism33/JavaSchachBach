package chess;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.*;

import static chess.ChessBoard.*;

public class ChessGraphics {

    private final JPanel gui = new JPanel(new BorderLayout(3, 3));
    private JButton[][] chessBoardSquares = new JButton[8][8];
    private JPanel chessBoard;
    private final JLabel message = new JLabel("Louis' Chess");
    private static final String COLS = "ABCDEFGH";
    private Engine engine;
    private GraphicsImageManager gim;
    private ChessBoard board;
    private List<ChessBoard> pastBoards, futureBoards;
    private Color black, white;
    private List<Integer> takenPieces;

    ChessGraphics(ChessBoard board, Engine engine) {
        initializeGraphicsHelper();
        pastBoards = new ArrayList<>();
        futureBoards = new ArrayList<>();
        this.board = board;
        this.possibleMove = new PossibleMove(board);
        this.engine = engine;
        takenPieces = new ArrayList<>();
        initializeGui();
    }

    void initializeGraphicsHelper(){
        this.gim = new GraphicsImageManager(this);
        black = gim.getBlack();
        white = gim.getWhite();
    }

    void initializeGui() {
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        JButton list = new JButton("List Previous Boards");
        list.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfPastBoards = pastBoards.size();
                if (numberOfPastBoards > 0) {
                    for (int n = 0; n < numberOfPastBoards; n++){
                        System.out.println(pastBoards.get(n));
                    }
                } else {
                    System.out.println("No past boards.");
                }
            }
        });
        tools.add(list);

        JButton prevBoard = new JButton("Prev Board");
        prevBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfBoards = pastBoards.size();
                if (numberOfBoards > 0){
                    ChessBoard oldBoard = pastBoards.get(numberOfBoards - 1);
                    pastBoards.remove(numberOfBoards - 1);

                    System.out.println(board.turn);

                    futureBoards.add(board);
                    setBoard(oldBoard);
                    System.out.println(board.turn);
                } else {
                    System.out.println("No past boards.");
                }
            }
        });
        tools.add(prevBoard);



        JButton nextBoard = new JButton("Next Board");
        nextBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfBoards = futureBoards.size();
                if (numberOfBoards > 0){
                    ChessBoard futureBoard = futureBoards.get(numberOfBoards - 1);
                    futureBoards.remove(numberOfBoards - 1);

                    System.out.println(futureBoards);

                    pastBoards.add(board);
                    setBoard(futureBoard);
                }
                else {
                    System.out.println("No next boards.");
                }
            }
        });
        tools.add(nextBoard);

        JButton takenPiece = new JButton("TakenPieces");
        takenPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                int numberOfBoards = takenPieces.size();
                if (numberOfBoards > 0) {
                    System.out.println(takenPieces);
                }
                else {
                    System.out.println("No pieces have been taken.");
                }
            }
        });
        tools.add(takenPiece);
        tools.addSeparator();

        JButton aiMove =
                new JButton("AI Move");
        aiMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Move m = IterativeDeepener.expandBoard(board, System.currentTimeMillis(), 1000);
                dealWithMove(m);
            }
        });
        tools.add(aiMove);
        tools.addSeparator();
        tools.add(message);




        gui.add(new JLabel("?"), BorderLayout.LINE_START);
        chessBoard = new JPanel(new GridLayout(0, 9));
        chessBoard.setBorder(new LineBorder(Color.BLACK));
        gui.add(chessBoard);
        Insets buttonMargin = new Insets(0,0,0,0);
        for (int ii = 0; ii < chessBoardSquares.length; ii++) {
            for (int jj = 0; jj < chessBoardSquares[ii].length; jj++) {
                JButton b = new JButton();
                b.setMargin(buttonMargin);

                ChessBoard.SquareDesc sq = board.getSquare((jj), (7-ii));

                ImageIcon imageIcon = gim.iconFinder(sq);

                b.setIcon(imageIcon);
                if ((jj + ii) % 2 == 0) {
                    b.setBackground(white);
                } else {
                    b.setBackground(black);
                }

                int x = jj;
                int y = 7-ii;
                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        ChessBoard.SquareDesc sq = board.getSquare((x), (y));
                        translateMove(sq);
                    }
                });
                chessBoardSquares[jj][ii] = b;

            }
        }

        chessBoard.add(new JLabel(""));
        for (int ii = 0; ii < 8; ii++) {
            chessBoard.add(
                    new JLabel(COLS.substring(ii, ii + 1),
                            SwingConstants.CENTER));
        }
        for (int ii = 0; ii < 8; ii++) {
            for (int jj = 0; jj < 8; jj++) {
                switch (jj) {
                    case 0:
                        chessBoard.add(new JLabel("" + ((8-ii)),
                                SwingConstants.CENTER));
                    default:
                        chessBoard.add(chessBoardSquares[jj][ii]);
                }
            }
        }
    }


    PossibleMove possibleMove;

    void translateMove(ChessBoard.SquareDesc square){
        possibleMove.addSquare(square);
    }

    class PossibleMove{
        ChessBoard board;
        String a, b;
        ChessBoard.SquareDesc source, destination;

        PossibleMove(ChessBoard board){
            this.board = board;
        }

        void addSquare(ChessBoard.SquareDesc square){
            if (source == null){
                source = square;
                destination = null;
            }
            else if (source != null && destination == null){
                destination = square;
                checkMove(source, destination);
            }
            else {
                reset();
                System.out.println("error");
            }

        }

        void checkMove(ChessBoard.SquareDesc source, ChessBoard.SquareDesc destination){
            int srcx = source.x;
            int srcy = source.y;
            int destx = destination.x;
            int desty = destination.y;

            boolean capture = destination.type != EMPTY;

            Move possibleMove = new Move(srcx, srcy, destx, desty, capture);

            if (movesAsStrings().contains(possibleMove.toString())){
                dealWithMove(possibleMove);
                reset();
            } else {
                reset();
            }
        }

        private void reset(){
            a = null;
            b = null;
            source = null;
            destination = null;
        }

        private List<String> movesAsStrings (){
            List<String> movesStrings = new ArrayList<>();
            List<Move> moves = board.generateMoves();
            for (Move move : moves){
                movesStrings.add(move.toString());
            }
            return movesStrings;
        }

        @Override
        public String toString() {
            return (a + " " + b);
        }
    }

    void dealWithMove(Move m){
        updateBoard(m);
        updateArt(m);
    }

    void updateBoard(Move move){

        if (move.capture) {
            int destx = move.destx;
            int desty = move.desty;
            ChessBoard.SquareDesc s = board.getSquare(destx, desty);
            takenPieces.add(s.type);
            System.out.println(takenPieces);
        }

        ChessBoard recordBoard = new ChessBoard(board);
        pastBoards.add(recordBoard);
        board.makeMove(move);
        futureBoards = new ArrayList<>();
        setBoard(board);

    }

    void updateArt(Move move){

        int srcx = move.srcx;
        int srcy = move.srcy;
        int destx = move.destx;
        int desty = move.desty;

        ChessBoard.SquareDesc readDestFromBoard = board.getSquare(destx, desty);
        Icon icon = gim.iconFinder(readDestFromBoard);
        chessBoardSquares[srcx][7-srcy].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(icon);

        if (readDestFromBoard.type == PAWN){

            ChessBoard.SquareDesc destPlusOne = board.getSquare(destx, desty+1);
            Icon icon1 = gim.iconFinder(destPlusOne);

            ChessBoard.SquareDesc destMinusOne = board.getSquare(destx, desty-1);
            Icon icon2 = gim.iconFinder(destMinusOne);

            chessBoardSquares[destx][7-destPlusOne.y].setIcon(icon1);
            chessBoardSquares[destx][7-destMinusOne.y].setIcon(icon2);

        } else if (readDestFromBoard.type == KING && readDestFromBoard.x == 2){

            ChessBoard.SquareDesc destLeftOne = board.getSquare(destx-2, desty);
            Icon icon1 = gim.iconFinder(destLeftOne);

            ChessBoard.SquareDesc destRightOne = board.getSquare(destx+1, desty);
            Icon icon2 = gim.iconFinder(destRightOne);

            chessBoardSquares[destx-2][7-desty].setIcon(icon1);
            chessBoardSquares[destx+1][7-desty].setIcon(icon2);

        }   else if (readDestFromBoard.type == KING && readDestFromBoard.x == 6){

            ChessBoard.SquareDesc destLeftOne = board.getSquare(destx-1, desty);
            Icon icon1 = gim.iconFinder(destLeftOne);

            ChessBoard.SquareDesc destRightOne = board.getSquare(destx+1, desty);
            Icon icon2 = gim.iconFinder(destRightOne);

            chessBoardSquares[destx-1][7-desty].setIcon(icon1);
            chessBoardSquares[destx+1][7-desty].setIcon(icon2);

        }
    }


//    private ImageIcon iconFinder(ChessBoard.SquareDesc sq){
//        ImageIcon imageIcon = new ImageIcon();
//        switch (sq.type) {
//            case PAWN:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackPawn);
//                } else {
//                    imageIcon = new ImageIcon(whitePawn);
//                }
//                break;
//            case KNIGHT:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackKnight);
//                } else {
//                    imageIcon = new ImageIcon(whiteKnight);
//                }
//                break;
//            case BISHOP:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackBishop);
//                } else {
//                    imageIcon = new ImageIcon(whiteBishop);
//                }
//                break;
//            case ROOK:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackRook);
//                } else {
//                    imageIcon = new ImageIcon(whiteRook);
//                }
//                break;
//            case QUEEN:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackQueen);
//                } else {
//                    imageIcon = new ImageIcon(whiteQueen);
//                }
//                break;
//            case KING:
//                if (sq.color == BLACK) {
//                    imageIcon = new ImageIcon(blackKing);
//                } else {
//                    imageIcon = new ImageIcon(whiteKing);
//                }
//                break;
//        }
//        return imageIcon;
//    }

//    void loadPics(){
//        try {
//            blackPawn = ImageIO.read(getClass().getResource("/res/blackPawn.png"));
//            blackKnight = ImageIO.read(getClass().getResource("/res/blackKnight.png"));
//            blackBishop = ImageIO.read(getClass().getResource("/res/blackBishop.png"));
//            blackRook = ImageIO.read(getClass().getResource("/res/blackRook.png"));
//            blackQueen = ImageIO.read(getClass().getResource("/res/blackQueen.png"));
//            blackKing = ImageIO.read(getClass().getResource("/res/blackKing.png"));
//
//            whitePawn = ImageIO.read(getClass().getResource("/res/whitePawn.png"));
//            whiteKnight = ImageIO.read(getClass().getResource("/res/whiteKnight.png"));
//            whiteBishop = ImageIO.read(getClass().getResource("/res/whiteBishop.png"));
//            whiteRook = ImageIO.read(getClass().getResource("/res/whiteRook.png"));
//            whiteQueen = ImageIO.read(getClass().getResource("/res/whiteQueen.png"));
//            whiteKing = ImageIO.read(getClass().getResource("/res/whiteKing.png"));
//
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
//    }

    public final JComponent getChessBoard() {
        return chessBoard;
    }

    public final JComponent getGui() {
        return gui;
    }

    public ChessBoard getBoard() {
        return board;
    }

    public void setBoard(ChessBoard board) {
        this.board = board;
    }
}
