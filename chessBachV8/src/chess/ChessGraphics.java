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
    private final JLabel message = new JLabel(
            "Louis' Chess");
    private static final String COLS = "ABCDEFGH";

    private Engine engine;

    private ChessBoard board;

    private BufferedImage whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing;
    private BufferedImage blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing;

    private Color black, white;

    private List<Integer> takenPieces;

    ChessGraphics(ChessBoard board, Engine engine) {
        this.board = board;
        this.possibleMove = new PossibleMove(board);
        this.engine = engine;
        takenPieces = new ArrayList<>();
        loadPics();
        pickColors();
        initializeGui();
    }

    void pickColors(){
        boolean style1 = false;
        boolean style2 = false;
        boolean style3 = true;

        if (style1) {
            black = Color.black;
            white = Color.white;
        } else if (style2) {
            black = new Color(218, 165, 32);
            white = new Color(222, 184, 135);
        } else if (style3) {
            black = new Color(139, 69, 19);
            white = new Color(205, 133, 63);
        }
    }



    void initializeGui() {

        // set up the main GUI
        gui.setBorder(new EmptyBorder(5, 5, 5, 5));
        JToolBar tools = new JToolBar();
        tools.setFloatable(false);
        gui.add(tools, BorderLayout.PAGE_START);

        JButton newB = new JButton("posmove");
        newB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println("possibleMove is: " + possibleMove);
            }
        });

        tools.add(newB);

        JButton spotty = new JButton("boards");
        spotty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(board);
            }
        });
        tools.add(spotty);

        JButton takenPiece = new JButton("TakenPieces");
        takenPiece.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.out.println(takenPieces);
            }
        });
        tools.add(takenPiece);
        tools.addSeparator();


        JButton aiMove = new JButton("AI Move");

        aiMove.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                Move m = IterativeDeepener.expandBoard(board, System.currentTimeMillis(), 1000);
                dealWithMove(m);
            }
        });
        tools.add(aiMove); // TODO - add functionality!
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

                ImageIcon imageIcon = iconFinder(sq);

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

        board.makeMove(move);
        setBoard(board);

    }

    void updateArt(Move move){

        int srcx = move.srcx;
        int srcy = move.srcy;
        int destx = move.destx;
        int desty = move.desty;

        ChessBoard.SquareDesc readDestFromBoard = board.getSquare(destx, desty);
        Icon icon = iconFinder(readDestFromBoard);
        chessBoardSquares[srcx][7-srcy].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(null);
        chessBoardSquares[destx][7-desty].setIcon(icon);

        if (readDestFromBoard.type == PAWN){ // dealing with enpassant like a boss

            ChessBoard.SquareDesc destPlusOne = board.getSquare(destx, desty+1);
            Icon icon1 = iconFinder(destPlusOne);

            ChessBoard.SquareDesc destMinusOne = board.getSquare(destx, desty-1);
            Icon icon2 = iconFinder(destMinusOne);

            chessBoardSquares[destx][7-destPlusOne.y].setIcon(icon1);
            chessBoardSquares[destx][7-destMinusOne.y].setIcon(icon2);

        } else if (readDestFromBoard.type == KING &&
                (readDestFromBoard.x == 1 || readDestFromBoard.x == 6)){ // castling
            System.out.println("woo");

            ChessBoard.SquareDesc destLeftOne = board.getSquare(destx-1, desty);
            Icon icon1 = iconFinder(destLeftOne);

            ChessBoard.SquareDesc destRightOne = board.getSquare(destx+1, desty);
            Icon icon2 = iconFinder(destRightOne);

            chessBoardSquares[destx-1][7-desty].setIcon(icon1);
            chessBoardSquares[destx+1][7-desty].setIcon(icon2);
        }
    }


    private ImageIcon iconFinder(ChessBoard.SquareDesc sq){
        ImageIcon imageIcon = new ImageIcon();
        switch (sq.type) {
            case PAWN:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackPawn);
                } else {
                    imageIcon = new ImageIcon(whitePawn);
                }
                break;
            case KNIGHT:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackKnight);
                } else {
                    imageIcon = new ImageIcon(whiteKnight);
                }
                break;
            case BISHOP:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackBishop);
                } else {
                    imageIcon = new ImageIcon(whiteBishop);
                }
                break;
            case ROOK:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackRook);
                } else {
                    imageIcon = new ImageIcon(whiteRook);
                }
                break;
            case QUEEN:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackQueen);
                } else {
                    imageIcon = new ImageIcon(whiteQueen);
                }
                break;
            case KING:
                if (sq.color == BLACK) {
                    imageIcon = new ImageIcon(blackKing);
                } else {
                    imageIcon = new ImageIcon(whiteKing);
                }
                break;
        }
        return imageIcon;
    }

    void loadPics(){
        try {
            blackPawn = ImageIO.read(getClass().getResource("/res/blackPawn.png"));
            blackKnight = ImageIO.read(getClass().getResource("/res/blackKnight.png"));
            blackBishop = ImageIO.read(getClass().getResource("/res/blackBishop.png"));
            blackRook = ImageIO.read(getClass().getResource("/res/blackRook.png"));
            blackQueen = ImageIO.read(getClass().getResource("/res/blackQueen.png"));
            blackKing = ImageIO.read(getClass().getResource("/res/blackKing.png"));

            whitePawn = ImageIO.read(getClass().getResource("/res/whitePawn.png"));
            whiteKnight = ImageIO.read(getClass().getResource("/res/whiteKnight.png"));
            whiteBishop = ImageIO.read(getClass().getResource("/res/whiteBishop.png"));
            whiteRook = ImageIO.read(getClass().getResource("/res/whiteRook.png"));
            whiteQueen = ImageIO.read(getClass().getResource("/res/whiteQueen.png"));
            whiteKing = ImageIO.read(getClass().getResource("/res/whiteKing.png"));

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

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
