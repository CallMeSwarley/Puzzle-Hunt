package models;

import com.mongodb.WriteResult;

import org.jongo.MongoCollection;

import javax.inject.Inject;

import uk.co.panaxiom.playjongo.PlayJongo;

public class PieceRepository {

    @Inject
    private PlayJongo jongo;

    private static PieceRepository instance = null;

    public PieceRepository() {
        instance = this;
    }

    public static PieceRepository getInstance() {
        return instance;
    }

    public MongoCollection piece() {
        MongoCollection pieceCollection = jongo.getCollection("piece");
        return pieceCollection;
    }

    public Piece getPiece(String id) {
        return piece().findOne("{_id: #}", id).as(Piece.class);
    }

    public void insert(Piece piece)  {
        WriteResult result = piece().save(piece);
        result.getUpsertedId().toString();
    }

    public void update(Piece piece) {
        piece().update("{_id: #}", piece.id).with(copyPiece(piece));
    }

    public Piece copyPiece(Piece piece) {
        Piece copy = new Piece(piece.id);
        return copy;
    }
}
