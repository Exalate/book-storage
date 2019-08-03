package hello;

import workDB.MainQueries;
import java.sql.SQLException;
import java.util.HashMap;

public class Books {

    private final HashMap<Integer, String> books;
    private final String errMessage;

    public Books(int bookshelf_id, String token) throws SQLException {

        MainQueries mq = new MainQueries();

        books = mq.searchBooks_byBookshelfIDAndToken(bookshelf_id, token);

        if(books.size() == 0){
            this.errMessage = "books is not found";
        }
        else{
            this.errMessage = "";
        }

        mq.conClose();

    }

    public HashMap<Integer, String> getBooks() {
        return books;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
