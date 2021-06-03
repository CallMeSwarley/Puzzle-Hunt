package models;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.jongo.MongoCollection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

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

    public static void main(String[] args) {
        //Load our image
        byte[] imageBytes = new byte[0];
        try {
            imageBytes = LoadImage("C:/Temp/bear.bmp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Connect to database
        Mongo mongo = new Mongo( "127.0.0.1" );
        String dbName = "GridFSTestJava";
        DB db = mongo.getDB( dbName );
        //Create GridFS object
        GridFS fs = new GridFS( db );

        //Save image into database
        GridFSInputFile in = fs.createFile(imageBytes);
        in.save();

        //Find saved image
        GridFSDBFile out = fs.findOne( new BasicDBObject( "_id" , in.getId() ) );

        //Save loaded image from database into new image file
        FileOutputStream outputImage = null;
        try {
            outputImage = new FileOutputStream("C:/Temp/bearCopy.bmp");
            out.writeTo( outputImage );
            outputImage.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] LoadImage(String filePath) throws Exception {
        File file = new File(filePath);
        int size = (int)file.length();
        byte[] buffer = new byte[size];
        FileInputStream in = new FileInputStream(file);
        in.read(buffer);
        in.close();
        return buffer;
    }

    public MongoCollection puzzle() {
        MongoCollection locationCollection = jongo.getCollection("puzzle");
        return locationCollection;
    }

    public Puzzle getPuzzle(String id) {
        return puzzle().findOne("{_id: #}", id).as(Puzzle.class);
    }

    public void insert(Puzzle puzzle)  {
        puzzle.id = new ObjectId().toHexString();
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
