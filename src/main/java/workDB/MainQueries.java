package workDB;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainQueries {

    static final String DB_URL = "jdbc:postgresql://localhost:5432/books";
    static final String USER = "postgres";
    static final String PASS = "123123";

    private Connection connect = null;

    public MainQueries(){

        //подключение(?) драйвера
        try {
            //настройки подключения к бд
            Class.forName("org.postgresql.Driver");//
        }
        catch(ClassNotFoundException e){
            //СООБЩЕНИЕ "ДРАЙВЕР НЕ НАЙДЕН"

            e.printStackTrace();
            return;
        }


        //установка соединения
        try{
            connect = DriverManager.getConnection(DB_URL, USER, PASS);
        }
         catch (SQLException e) {
            //СООБЩЕНИЕ "СОЕДИНЕНИЕ НЕ УДАЛООСЬ"

            e.printStackTrace();
            return;
        }

        if (connect == null) {
//            try {
//                throw new SQLException();

        }

    }

    public void conClose() throws SQLException{
        if(CONNECTION_EXISTS()) {
            connect.close();
        }
    }

    //проверка соединение работает
    public boolean CONNECTION_EXISTS(){
        if(connect != null){
            return true;
        }
        return false;
    }

    //запись в таблицы users и bookshelves, если email не найдется в текущих записях таблицы users, Возвращает токен
    public String newUser(String email, String password) throws SQLException{

//        if (EMAIL_ALREADY_EXISTS(email)){
//            token = "email already exists";
//        }

        int newUserID = -1;

        String newToken = getNewToken();

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO users (email, token, password) VALUES (?, ?, ?)", new String[] {"user_id"});
        stmt.setString(1, email);
        stmt.setString(2, newToken);
        stmt.setString(3, password);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            newUserID = gk.getInt("user_id");//gk.getLong("user_id");
        }
        stmt.close();

        newBookshelf("default_bookshelf", newUserID);

        return newToken;
    }

    //запись в таблицу bookshelves
    public HashMap<Integer, String> newBookshelf(String name, int UserID) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();

        int newBookshelvesID = 0;

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO bookshelves (name, user_id) VALUES (?, ?)", new String[] {"bookshelf_id"});
        stmt.setString(1, name);
        stmt.setInt(2, UserID);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            hm.put(gk.getInt("bookshelf_id"), name);//gk.getLong("user_id");
        }
        stmt.close();

        return hm;
    }

    //проверка, в таблице users существует определенный email
    public boolean EMAIL_ALREADY_EXISTS(String email) throws SQLException{

        boolean result = false;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select * from users where (email = '" + email + "')"
        );

        while (rs.next())
        {
            result = true;
            //System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();

        return result;

    }

    public String searchTokenByEmailAndPassword(String email, String password) throws SQLException{

        String token = "";

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select token from users where (email = '" + email + "' and password = '" + password + "')"
        );

        while (rs.next())
        {
            token = rs.getString("token");
            //System.out.println(rs.getString(1));
        }
        rs.close();
        st.close();

        return token;

    }

    //запись в таблицу books(с разделением текста на страницы, записью в таблицу pages в отдельных методах)
    public HashMap<Integer, String> newBook(String name, String description, int bookshelf_ID, int user_ID, String text) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();
        int newBookID = -1;

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO books (name, user_id, description, bookshelf_id) VALUES (?, ?, ?, ?)", new String[] {"book_id"});
        stmt.setString(1, name);
        stmt.setInt(2, user_ID);
        stmt.setString(3, description);
        stmt.setInt(4, bookshelf_ID);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            newBookID = gk.getInt("book_id");
        }
        stmt.close();

        hm.put(newBookID, name);

        List<String> pages = splitPages(text);

        newPages(newBookID, pages);

        return hm;

    }

    //поиск ИД пользователя по токену(ЛИШНЕЕ?)
    public int searchUserIdByToken(String token) throws SQLException{

        int user_ID = 0;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select * from users where (token = '" + token + "')"
        );

        while (rs.next())
        {
            user_ID = rs.getInt("user_id");
            //result = true;
            //System.out.println(rs.getString(1));

        }
        rs.close();
        st.close();

        return user_ID;

    }

    //поиск полки по токену, возврат ИД полки и имя полки
    public HashMap<Integer, String> searchBookshelves_byToken(String token) throws SQLException{

        HashMap<Integer, String> hM = new HashMap<Integer, String>();

        Statement st = connect.createStatement();

        ResultSet rs = st.executeQuery(
                "select bookshelves.bookshelf_id, bookshelves.name " +
                        "from users, bookshelves " +
                        "WHERE users.user_id = bookshelves.user_id and users.token = '" + token + "'"
        );

        while (rs.next())
        {
            hM.put(rs.getInt("bookshelf_id"),rs.getString("name"));
        }
        rs.close();
        st.close();

        return hM;

    }

    //поиск книг по полке и токену, возврат книга ИД и имя книги, ДОДЕЛАТЬ
    public HashMap<Integer, String> searchBooks_byBookshelfIDAndToken(int bookshelf_ID, String token) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();

        Statement st = connect.createStatement();

        ResultSet rs = st.executeQuery(
                "select books.book_id, books.name " +
                        "from users, books " +
                        "WHERE users.token = '" + token + "' and books.bookshelf_id = '" + bookshelf_ID + "' and users.user_id = books.user_id"
        );

        while (rs.next())
        {
            hm.put(rs.getInt("book_id"),rs.getString("name"));
        }
        rs.close();
        st.close();

        return hm;

    }

    //ВОЗМОЖНО ПЕРЕДЕЛАТЬ НА ОДИН ЗАПРОС
    public HashMap<Integer, String> searchPageByBookID(int book_id) throws SQLException {

        HashMap<Integer, String> result = new HashMap<>();
        int pageNumberDefault = 1;

        Statement st = connect.createStatement();

        ResultSet rs = st.executeQuery(
                "select page_number, content from pages where (book_id = '" + book_id + "' and main_bookmark = true)"
        );

        if (rs.next()) {
            result.put(rs.getInt("page_number"), rs.getString("content"));
            rs.close();
        } else{
            rs.close();

        rs = st.executeQuery(
                "select page_number, content from pages where (book_id = '" + book_id + "' and page_number = '" + pageNumberDefault + "')"
        );

        while (rs.next()) {
            result.put(rs.getInt("page_number"), rs.getString("content"));
        }
        rs.close();
        }

        st.close();

        return result;

    }

    public String searchPageByBookIDAndPageNumber(int book_id, int PageNumber) throws SQLException{

        String result = null;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select content from pages where (book_id = '" + book_id + "' and page_number = '" + PageNumber + "')"
        );

        while (rs.next())
        {
            result = rs.getString("content");//result.put(rs.getInt("page_number"), rs.getString("content"));
        }
        rs.close();
        st.close();

        return result;
    }

    public HashMap<Integer, String> searchLastPageByBookID(int book_id) throws SQLException{

        HashMap<Integer, String> result = new HashMap<>();

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select page_number, content from pages where (book_id = " + book_id + ") order by page_number desc limit 1"
        );

        while (rs.next())
        {
            result.put(rs.getInt("page_number"), rs.getString("content"));
            //break;
        }
        rs.close();
        st.close();

        return result;
    }

    //проверка email и password, возврат токена
    public String searchToken_byEmailAndPassword(String email, String password) throws SQLException{

        String token = null;

        Statement st = connect.createStatement();

        ResultSet rs = st.executeQuery(
                "select token from users " +
                        "where email = '" + email + "' and password = '" + password + "'"
        );

        while (rs.next())
        {
            token = rs.getString("token");//hM.put(rs.getInt("bookshelf_id"),rs.getString("name"));
        }

        return token;

    }

    // ВОЗМОЖНО НУЖНО ПЕРЕДЕЛАТЬ, ЗАПРОС НА ИЗМЕНЕНИЯ ЗАПИСЕЙ
    public void addNewMainBookmark(int pageNamber, int book_id) throws SQLException{

        Statement st = connect.createStatement();

        st.executeUpdate(
                "update pages set main_bookmark = false " +
                        "where book_id = " + book_id + " and main_bookmark = true"
        );

        st.executeUpdate(
                "update pages set main_bookmark = true " +
                        "where book_id = " + book_id + " and page_number = " + pageNamber + ""
        );

        st.close();

    }

    //запись всех страниц книги в таблицу pages
    private void newPages(int book_ID, List<String> pages) throws SQLException{

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO pages (page_number, book_ID, content) VALUES (?, ?, ?)");

        for (int i = 0; i < pages.size(); i++) {
            // Заполняем параметры запроса
            stmt.setInt(1, i+1);
            stmt.setInt(2, book_ID);
            stmt.setString(3, pages.get(i));

            stmt.addBatch();
        }
        stmt.executeBatch();

        stmt.close();
    }

    //генерация нового токена
    private String getNewToken(){

        SecureRandom secureRandom = new SecureRandom();
        byte[] token = new byte[15];
        secureRandom.nextBytes(token);

        return new BigInteger(1, token).toString(16);

    }

    //разбивка текста на страницы (блоки текста по 1000 символов) ПЕРЕДЕЛАТЬ НА ПРИВАТ
    public List<String> splitPages(String text){

        //пока так, нужно переделать
        List<String> arrL = new ArrayList<>();

        int splitSize = 100;
        String str = "";

        while(true){

            str = "";

            if(text.length() >= splitSize){

                str = text.substring(0,splitSize);

                text = text.substring(splitSize,text.length());

            }
            else{
                str = text;
                text = "";
            }

            arrL.add(str);

            if (text.length() == 0)
                break;
        }

        return arrL;

    }

}
