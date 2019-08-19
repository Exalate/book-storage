package hello;

import workDB.MainQueries;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Page {

    private final String pageText;
    private final int pageNumber;
    private final String errMessage;

    public Page(int book_id, String token) throws SQLException {

        MainQueries mq = new MainQueries();

        int user_id = mq.searchUserIdByToken(token);

        if(user_id == 0){
            pageText = "";
            pageNumber = 0;
            errMessage = "user invalid";
        }
        else{
            HashMap<Integer,String> hm = mq.searchPageByBookID(book_id, user_id);
            String pageTextBuf = "";
            int numberPageBuf = 0;
            for(Map.Entry<Integer, String> pair : hm.entrySet()) {
                numberPageBuf= pair.getKey();
                pageTextBuf = pair.getValue();
            }
            pageText = pageTextBuf;
            pageNumber = numberPageBuf;
            errMessage = "";
        }

        mq.conClose();

    }

    public Page(int book_id, String token, int newPageNumber) throws SQLException {

        MainQueries mq = new MainQueries();

        //ПЕРЕДЕЛАТЬ НА ОДИН ЗАПРОС, А НЕ ПОЛУЧЕНИЕ СНАЧАЛА ИД, А ПОТОМ ОСТАЛЬНОЕ

        int user_id = mq.searchUserIdByToken(token);

        if(user_id == 0){
            pageText = "";
            pageNumber = 0;
            errMessage = "user invalid";
        }
        else{ //ПЕРЕДЕЛАТЬ ПОЛУЧЕНИЕ ДАННЫХ ИЗ ХАШМАПА (БЕЗ ЦИКЛА)
            String pageTextBuf = mq.searchPageByBookIDAndPageNumber(book_id, newPageNumber);

            if(pageTextBuf == null){
                int numberPageBuf = 0;
                HashMap<Integer,String> hm = mq.searchLastPageByBookID(book_id);
                for(Map.Entry<Integer, String> pair : hm.entrySet()) {
                    System.out.println();
                    numberPageBuf= pair.getKey();
                    pageTextBuf = pair.getValue();
                }
                pageText = pageTextBuf;
                pageNumber = numberPageBuf;
            }
            else{
                pageText = pageTextBuf;
                pageNumber = newPageNumber;

            }

            errMessage = "";

        }

        mq.conClose();

    }


    public String getPageText() {
        return pageText;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
