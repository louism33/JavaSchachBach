package chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static chess.ChessBoard.*;

class StandAloneSimpleGraphics extends JPanel {

    private static int WIDTH = 600, HEIGHT = 600, xoffset, yoffset;
    private ChessBoard board;
    private double scaleFac;
    private int xyfactor;

    private BufferedImage whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing;
    private BufferedImage blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing;
    private BufferedImage chessboard, bigChessboard;

    StandAloneSimpleGraphics(){

        this.board = new ChessBoard();
        xyfactor = 59;
        xoffset = 0;
        yoffset = 0;
        scaleFac = 2.3;

        JFrame jFrame = new JFrame();
        jFrame.add(this);
        jFrame.setSize(WIDTH, HEIGHT);
        jFrame.setLocationRelativeTo(null);
        jFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        jFrame.setVisible(true);

        loadPics();

    }

    private void loadPics(){
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

            chessboard = ImageIO.read(getClass().getResource("/res/chessboard.png"));
            AffineTransform at = new AffineTransform();
            at.scale(scaleFac, scaleFac);
            AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
            bigChessboard = scaleOp.filter(chessboard, bigChessboard);

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }



    @Override
    protected void paintComponent(java.awt.Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g2d = (Graphics2D) graphics;
        draw(this.board, g2d);
    }

    private void draw(ChessBoard board, Graphics2D g2d) {
        this.board = board;
        g2d.drawImage(bigChessboard, xoffset, yoffset, null);
        g2d.drawString("      a             b             c" +
                "             d             e             f" +
                "             g             h", 0, HEIGHT-100);

        String nums = "      8             7             6             5             4" +
                "             3             2             1";
        int angle = 90;
        int numoffset = 64*8;

        g2d.translate(numoffset,0);
        g2d.rotate(Math.toRadians(angle));
        g2d.drawString(nums,0,0);
        g2d.rotate(-Math.toRadians(angle));
        g2d.translate(-numoffset,-0);

        for (int y=7; y>=0; y--) {
            for (int x=0; x<8; x++) {
                ChessBoard.SquareDesc sq = this.board.getSquare(x, y);
                switch (sq.type) {
                    case PAWN:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackPawn, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whitePawn, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        break;
                    case KNIGHT:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackKnight, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whiteKnight, xyfactor * sq.x, xyfactor * (7-sq.y), null);
                        }
                        break;
                    case BISHOP:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackBishop, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whiteBishop, xyfactor * sq.x, xyfactor * (7-sq.y), null);
                        }
                        break;
                    case ROOK:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackRook, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whiteRook, xyfactor * sq.x, xyfactor * (7-sq.y), null);
                        }
                        break;
                    case QUEEN:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackQueen, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whiteQueen, xyfactor * sq.x, xyfactor * (7-sq.y), null);
                        }
                        break;
                    case KING:
                        if (sq.color == BLACK) {
                            g2d.drawImage(blackKing, xyfactor * sq.x, xyfactor * (7 - sq.y), null);
                        }
                        else {
                            g2d.drawImage(whiteKing, xyfactor * sq.x, xyfactor * (7-sq.y), null);
                        }
                        break;
                }
            }
        }
    }


    public void setBoard(ChessBoard board) {
        this.board = board;
        repaint();
    }
}



