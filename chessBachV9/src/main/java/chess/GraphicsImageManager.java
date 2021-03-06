package chess;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static chess.ChessBoard.*;

class GraphicsImageManager {

    private Color black, white;
    private BufferedImage whitePawn, whiteKnight, whiteBishop, whiteRook, whiteQueen, whiteKing;
    private BufferedImage blackPawn, blackKnight, blackBishop, blackRook, blackQueen, blackKing;

    GraphicsImageManager (){
        loadPics();
        pickColors();
    }

    ImageIcon iconFinder(ChessBoard.SquareDesc sq){
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

    private void pickColors(){
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

    private void loadPics(){
        try {
            blackPawn = ImageIO.read(getClass().getResource("/blackPawn.png"));
            blackKnight = ImageIO.read(getClass().getResource("/blackKnight.png"));
            blackBishop = ImageIO.read(getClass().getResource("/blackBishop.png"));
            blackRook = ImageIO.read(getClass().getResource("/blackRook.png"));
            blackQueen = ImageIO.read(getClass().getResource("/blackQueen.png"));
            blackKing = ImageIO.read(getClass().getResource("/blackKing.png"));

            whitePawn = ImageIO.read(getClass().getResource("/whitePawn.png"));
            whiteKnight = ImageIO.read(getClass().getResource("/whiteKnight.png"));
            whiteBishop = ImageIO.read(getClass().getResource("/whiteBishop.png"));
            whiteRook = ImageIO.read(getClass().getResource("/whiteRook.png"));
            whiteQueen = ImageIO.read(getClass().getResource("/whiteQueen.png"));
            whiteKing = ImageIO.read(getClass().getResource("/whiteKing.png"));

        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    Color getBlack() {
        return black;
    }

    Color getWhite() {
        return white;
    }
}
