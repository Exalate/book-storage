package hello;

import workDB.MainQueries;
import java.sql.SQLException;
import java.util.HashMap;

public class Bookshelves {

    private final HashMap<Integer, String> bookshelves;
    private final String errMessage;

    public Bookshelves(String token) throws SQLException {

        MainQueries mq = new MainQueries();

        bookshelves = mq.searchBookshelves_byToken(token);

        if(bookshelves.size() == 0){
            this.errMessage = "bookshelves is not found";
        }
        else{
            this.errMessage = "";
        }

        mq.conClose();

    }

    public Bookshelves(String name, String token) throws SQLException {

        MainQueries mq = new MainQueries();

        int user_id = mq.searchUserIdByToken(token);

        bookshelves = mq.newBookshelf(name, user_id);

        if(bookshelves.size() == 0){
            this.errMessage = "bookshelves is not create";
        }
        else{
            this.errMessage = "";
        }

        mq.conClose();

    }

    public HashMap<Integer, String> getBookshelves() {
        return bookshelves;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
