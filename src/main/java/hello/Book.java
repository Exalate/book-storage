package hello;

import workDB.MainQueries;

import java.sql.SQLException;
import java.util.HashMap;

public class Book {

    private final HashMap<Integer, String> book;
    private final String errMessage;

    public Book(String text, String name, String description, int bookshelf_id, String token) throws SQLException {

        MainQueries mq = new MainQueries();

        //ПЕРЕДЕЛАТЬ НА ОДИН ЗАПРОС, А НЕ ПОЛУЧЕНИЕ СНАЧАЛА ИД, А ПОТОМ ОСТАЛЬНОЕ

        int user_id = mq.searchUserIdByToken(token);

        book = mq.newBook(name, description, bookshelf_id, user_id, text);

        if(book.size() == 0){
            this.errMessage = "books is not found";
        }
        else{
            this.errMessage = "";
        }
//        book = new HashMap<Integer, String>();
//        this.errMessage = "";
        mq.conClose();

    }

    public HashMap<Integer, String> getBooks() {
        return book;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
