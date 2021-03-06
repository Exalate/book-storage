package hello;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

import workDB.MainQueries;

@RestController
public class RestApiController {

    @PostMapping("/users/register")
    public Register postRegister(
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "pwd", defaultValue = "") String pwd
    ) throws SQLException {
        return new Register(email, pwd);
    }

    @PostMapping("/users/login")
    public Login postLogin(
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "pwd", defaultValue = "") String pwd
    ) throws SQLException {
        return new Login(email, pwd);
    }

    @GetMapping("/bookshelves")
    public Bookshelves getBookshelves(
            @RequestHeader(value = "x-access-token", defaultValue = "") String token
    ) throws SQLException {
        return new Bookshelves(token);
    }

    @PostMapping("/bookshelves")
    public Bookshelves createBookshelf(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestHeader(value = "x-access-token", defaultValue = "") String token
    ) throws SQLException {
        //System.out.println("ИМЯ !!!!!" + name);
        return new Bookshelves(name, token);
    }

    @GetMapping("/books")
    public Books getBooks(
            @RequestHeader(value = "x-access-token", defaultValue = "") String token,
            @RequestParam(value = "bookshelfId") int bookshelf_id
    ) throws SQLException {
        return new Books(bookshelf_id, token);
    }

    @GetMapping("/books/{id}")
    public Page getBook(
            @RequestHeader(value = "x-access-token", defaultValue = "") String token,
            @PathVariable(value = "id") int book_id
    ) throws SQLException {
        return new Page(book_id, token);
    }

    @GetMapping("/books/{bookId}/page/{pageNumber}")
    public Page getPage(
            @RequestHeader(value = "x-access-token", defaultValue = "") String token,
            @PathVariable(value = "bookId") int book_id,
            @PathVariable(value = "pageNumber") int pageNumber
    ) throws SQLException {

        return new Page(book_id, token, pageNumber);
    }

    @GetMapping("/books/{bookId}/page/{pageNumber}/addBookmark")
    public Object setPageBookmark(
            @RequestHeader(value = "x-access-token", defaultValue = "") String token,
            @PathVariable(value = "bookId") int book_id,
            @PathVariable(value = "pageNumber") int pageNumber
    ) throws SQLException {

        MainQueries mq = new MainQueries();
        int user_id = mq.searchUserIdByToken(token);
        if(user_id != 0){
            mq.addNewMainBookmark(pageNumber, book_id);
        }
        mq.conClose();

        return null;
    }

    @PostMapping("/books/upload")
    public Book postUpload(
            @RequestParam("file") MultipartFile file,
            @RequestHeader(value = "x-access-token", defaultValue = "") String token,
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "description", defaultValue = "") String description,
            @RequestParam(value = "bookshelf_id", defaultValue = "") int bookshelf_id
            ) throws SQLException, IOException {


        String text = new String(file.getBytes(), "UTF-8");

        return new Book(text, name, description, bookshelf_id,token);

    }

//    //ЗАПРОС НА РАЗРЕШЕНИЕ  /// ПЕРЕДЕЛАТЬ ВОЗВРАТ НУЛЛ?
//    @PostMapping           //ВОПРОС: как возвращать "ничего" ?
//    public Object postPetition(
//    @RequestParam(value = "x-access-token", defaultValue = "") String token,
//    @RequestParam(value = "bookId") int book_id,
//    @RequestParam(value = "text", defaultValue = "") String text
//    )throws SQLException, IOException {
//
//        MainQueries mq = new MainQueries();
//
//        int user_id = mq.searchUserIdByToken(token);
//
//        if(user_id != 0){
//            mq.addPetition(user_id, book_id, text);
//        }
//
//        mq.conClose();
//        return null;
//
//    }

//    @GetMapping("/bookshelves")
//    public PetitionsOfOthers getPetitions(
//            @RequestHeader(value = "x-access-token", defaultValue = "") String token
//    ) throws SQLException {
//        return new PetitionsOfOthers(token);
//    }
//
//    @PostMapping           //ВОПРОС: как возвращать "ничего" ?
//    public Object postResultPetition(
//            @RequestParam(value = "x-access-token", defaultValue = "") String token,
//            @RequestParam(value = "petition_id") int petition_id,
//            @RequestParam(value = "result") boolean result,
//            @RequestParam(value = "text", defaultValue = "") String text
//    )throws SQLException, IOException {
//
//        MainQueries mq = new MainQueries();
//
//        int user_id = mq.searchUserIdByToken(token);
//
//        if(user_id != 0){
//            mq.addResultPetition(text, result, petition_id);
//        }
//
//        mq.conClose();
//        return null;
//
//    }



}