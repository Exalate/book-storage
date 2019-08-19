package hello;

import workDB.MainQueries;

import java.sql.SQLException;

public class RunPages extends Thread{

    private int book_ID;
    private String text;

    public RunPages(int book_ID, String text){

        super("1");

        this.book_ID = book_ID;
        this.text = text;

        start();

    }

    @Override
    public void run(){

        MainQueries mq = new MainQueries();

        try {
            mq.newPages(book_ID, text);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            mq.conClose();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
