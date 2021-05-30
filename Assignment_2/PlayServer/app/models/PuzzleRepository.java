package models;

import com.mongodb.WriteResult;

import org.jongo.MongoCollection;

import javax.inject.Inject;
import javax.inject.Singleton;

import uk.co.panaxiom.playjongo.PlayJongo;

@Singleton
public class PuzzleRepository {
    @Inject
    private PlayJongo jongo;

    private static PuzzleRepository instance = null;

    public PuzzleRepository() {
        instance = this;
    }

    public static PuzzleRepository getInstance() {
        return instance;
    }

    public MongoCollection puzzle() {
        MongoCollection locationCollection = jongo.getCollection("puzzle");
        return locationCollection;
    }

    public Puzzle getPuzzle(String id) {
        return puzzle().findOne("{_id: #}", id).as(Puzzle.class);
    }

    public void insert(Puzzle puzzle)  {
        WriteResult result = puzzle().save(puzzle);
        result.getUpsertedId().toString();
    }

    public void update(Puzzle puzzle) {
        puzzle().update("{_id: #}", puzzle.id).with(copyPuzzle(puzzle));
    }

    public Puzzle copyPuzzle(Puzzle puzzle) {
        Puzzle copy = new Puzzle(puzzle.id,puzzle.piecesCountHorizontal,puzzle.piecesCountVertical);
        return copy;
    }
}
