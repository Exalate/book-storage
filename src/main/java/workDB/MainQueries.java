package workDB;

import hello.PetitionEntry;

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

    //конструктор
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

    //закрытие соединения
    public void conClose() throws SQLException{
        if(isConnectionExists()) {
            connect.close();
        }
    }

    //Проверка - коннект существует/открыт
    public boolean isConnectionExists() throws SQLException {
        if ( connect == null || connect.isClosed() == true ) {
            return false;
        }
        return true;
    }

    //Создание нового пользователя
    public String newUser(String email, String password) throws SQLException{

        int newUserID = 0;

        String newToken = getNewToken();

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO users (email, token, password) VALUES (?, ?, ?)", new String[] {"user_id"});
        stmt.setString(1, email);
        stmt.setString(2, newToken);
        stmt.setString(3, password);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            newUserID = gk.getInt("user_id");
        }
        stmt.close();

        if(newUserID != 0) {
            newBookshelf("default_bookshelf", newUserID);
        }

        return newToken;
    }

    //Создание новой полки
    public HashMap<Integer, String> newBookshelf(String name, int user_id) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO bookshelves (name, user_id) VALUES (?, ?)", new String[] {"bookshelf_id"});
        stmt.setString(1, name);
        stmt.setInt(2, user_id);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            hm.put(gk.getInt("bookshelf_id"), name);
        }

        gk.close();
        stmt.close();

        return hm;
    }

    //Проверка - email уже существует
    public boolean isEmailAlreadyExists(String email) throws SQLException{

        boolean result = false;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select * from users where (email = '" + email + "')"
        );

        if (rs.next()){
            result = true;
        }
        rs.close();
        st.close();

        return result;

    }

    //Поиск токена
    public String searchTokenByEmailAndPassword(String email, String password) throws SQLException{

        String token = "";

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select token from users where (email = '" + email + "' and password = '" + password + "')"
        );

        if (rs.next()){
            token = rs.getString("token");
        }
        rs.close();
        st.close();

        return token;

    }

    //Создание новой книги, создание страниц
    public HashMap<Integer, String> newBook(String name, String description, int bookshelf_id, int user_id, String text) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();
        int newBookID = 0;

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO books (name, user_id, description, bookshelf_id) VALUES (?, ?, ?, ?)", new String[] {"book_id"});
        stmt.setString(1, name);
        stmt.setInt(2, user_id);
        stmt.setString(3, description);
        stmt.setInt(4, bookshelf_id);
        stmt.executeUpdate();

        ResultSet gk = stmt.getGeneratedKeys();
        if(gk.next()) {
            newBookID = gk.getInt("book_id");
        }
        gk.close();
        stmt.close();

        hm.put(newBookID, name);

        List<String> pages = splitPages(text);

        newPages(newBookID, pages);

        return hm;

    }

    //Поиск ИД пользователя
    public int searchUserIdByToken(String token) throws SQLException{

        int user_id = 0;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select * from users where (token = '" + token + "')"
        );

        if (rs.next()){
            user_id = rs.getInt("user_id");
        }
        rs.close();
        st.close();

        return user_id;

    }

    //Поиск полки
    public HashMap<Integer, String> searchBookshelves_byToken(String token) throws SQLException{

        HashMap<Integer, String> hM = new HashMap<Integer, String>();

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select bookshelves.bookshelf_id, bookshelves.name " +
                        "from users, bookshelves " +
                        "where users.user_id = bookshelves.user_id and users.token = '" + token + "'"
        );

        while (rs.next()){
            hM.put(rs.getInt("bookshelf_id"),rs.getString("name"));
        }
        rs.close();
        st.close();

        return hM;
    }

    //Поиск книг ДОДЕЛАТЬ?
    public HashMap<Integer, String> searchBooksByBookshelfIDAndToken(int bookshelf_id, String token) throws SQLException{

        HashMap<Integer, String> hm = new HashMap<>();

        Statement st = connect.createStatement();

        ResultSet rs = st.executeQuery(
                "select books.book_id, books.name " +
                        "from users, books " +
                        "where users.token = '" + token + "' and books.bookshelf_id = '" + bookshelf_id + "' and users.user_id = books.user_id"
        );

        while (rs.next()) {
            hm.put(rs.getInt("book_id"),rs.getString("name"));
        }
        rs.close();
        st.close();

        return hm;

    }

    //Поиск страницы, если закладки нет - открывается первая страница, иначе страница с закладкой
    public HashMap<Integer, String> searchPageByBookID(int book_id, int user_id) throws SQLException {

        HashMap<Integer, String> result = new HashMap<>();
        int pageNumberDefault = 1;

        Statement st = connect.createStatement();

//        ResultSet rs = st.executeQuery(
//            "select page_number, content, main_bookmark  from pages " +
//                    "where (book_id = " + book_id + " " +
//                    "and (main_bookmark = true or page_number = " + pageNumberDefault + ")) " +
//                    "order by main_bookmark desc limit 1"
//        );
        ResultSet rs = st.executeQuery(
            "select books.main_bookmark, pages.content, books.user_id from books, pages " +
                    "where books.book_id = " + book_id + " and books.book_id = pages.book_id " +
                    "and books.main_bookmark = pages.page_number"
        );

        while(rs.next()){
            if(rs.getInt(user_id) == user_id) {
                result.put(rs.getInt("main_bookmark"), rs.getString("content"));
                break;
            }
        }

        rs.close();
        st.close();

        return result;

    }

    //Поиск страницы
    public String searchPageByBookIDAndPageNumber(int book_id, int PageNumber) throws SQLException{

        String result = null;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select content from pages where (book_id = " + book_id + " and page_number = " + PageNumber + ")"
        );

        if (rs.next()){
            result = rs.getString("content");
        }
        rs.close();
        st.close();

        return result;
    }

    //Поиск последней страницы книги
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

    //Изменение закладки
    public void addNewMainBookmark(int pageNamber, int book_id) throws SQLException{

        Statement st = connect.createStatement();

        st.executeUpdate(
                "update books set main_bookmark = " + pageNamber +
                        " where book_id = " + book_id
        );

        st.close();

    }

    //НОВОЕ _______________________________________________________________________

    public void addPetition(int petitioner_id, int book_id, String text) throws SQLException{

        int owner_id;

        owner_id = searchUserIDByBookID(book_id);

        if(owner_id == 0){
            return;
        }

        PreparedStatement stmt = connect.prepareStatement("INSERT INTO petitions (petitioner_id, owner_id, book_id, petition_text) VALUES (?, ?, ?, ?)");
        stmt.setInt(1, petitioner_id);
        stmt.setInt(2, owner_id);
        stmt.setInt(3, book_id);
        stmt.setString(4, text);
        stmt.executeUpdate();

        stmt.close();

    }

    public List<PetitionEntry> searchPetitions(int user_id) throws SQLException {

        List<PetitionEntry> result = new ArrayList<>();

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select " +
                "petitions.petitioner_id," +
                "petitions.petition_id," +
                "petitions.book_id," +
                "petitions.petition_text," +
                "users.user_id," +
                "users.email," +
                "books.name " +
                "from " +
                "petitions " +
                "left outer join users " +
                "on petitions.owner_id = users.user_id " +
                "left outer join books " +
                "on petitions.book_id = books.book_id " +
                "where (petitions.owner_id = " + user_id + " and petitions.owner_text is null)"
        );

        while (rs.next())
        {
            result.add(
                    new PetitionEntry(
                            rs.getInt("petition_id"),
                            rs.getInt("petitioner_id"),
                            rs.getString("email"),
                            rs.getInt("book_id"),
                            rs.getString("book_name"),
                            rs.getString("petition_text")
                    )
            );
        }
        rs.close();
        st.close();

        return result;

    }

    public void addResultPetition(String text, boolean result, int petition_id) throws SQLException {

        Statement st = connect.createStatement();

        st.executeUpdate(
                "update petitions set owner_text = '" + text + "', result = '" + result + "'" +
                        " where petition_id = " + petition_id
        );

        st.close();

    }

    //=============================================================================

    //поиск ид пользователя по ид книги
    private int searchUserIDByBookID(int book_id) throws SQLException{

        int result = 0;

        Statement st = connect.createStatement();
        ResultSet rs = st.executeQuery(
                "select user_id from books where (book_id = " + book_id + ") limit 1"
        );

        if (rs.next()){
            result = rs.getInt("user_id");
        }
        rs.close();
        st.close();

        return result;

    }

    //Создание страниц книги
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

    //разбивка текста на страницы (блоки текста по n символов) ПЕРЕДЕЛАТЬ НА ПРИВАТ
    private List<String> splitPages(String text){

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
