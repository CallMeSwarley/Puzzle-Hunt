package models;

import org.jongo.MongoCollection;
import org.jongo.MongoCursor;

import java.util.ArrayList;
import java.util.List;

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


    public MongoCollection puzzles() {
        MongoCollection locationCollection = jongo.getCollection("puzzles");
        return locationCollection;
    }

    public Puzzle getPuzzle(String id) {
        return puzzles().findOne("{_id: #}", id).as(Puzzle.class);
    }

    public void insert(Puzzle puzzle) {
        puzzles().save(puzzle);
    }

    public void update(Puzzle puzzle) {
        puzzles().update("{_id: #}", puzzle.id).with(copyPuzzle(puzzle));
    }

    public Puzzle copyPuzzle(Puzzle puzzle) {
        Puzzle copy = new Puzzle(puzzle.id, puzzle.piecesCountHorizontal, puzzle.piecesCountVertical);
        return copy;
    }

    public Puzzle[] getAll() {
        MongoCursor<Puzzle> othersQuery = puzzles().find().as(Puzzle.class);
        List<Puzzle> puzzleList = new ArrayList<>();
        for (Puzzle puzzle : othersQuery)
            puzzleList.add(puzzle);
        return puzzleList.toArray(new Puzzle[0]);
    }
}
