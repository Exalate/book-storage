package hello;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RestController
public class RestApiController {


    // private final StorageService storageService;
    //private final Path rootLocation =

    Path rootLocation = FileSystems.getDefault().getPath("C://client//");

    //this.rootLocation = Paths.get(properties.getLocation());

    @PostMapping("/register")
    public Register postRegister(
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "pwd", defaultValue = "") String pwd
    ) {
        return new Register(email, pwd);
    }

//    @GetMapping("/register")
//    public Register getRegister(
//            @RequestParam(value = "email", defaultValue = "") String email,
//            @RequestParam(value = "pwd", defaultValue = "") String pwd
//    ) {
//        return new Register(email, pwd);
//    }

    @PostMapping("/upload")
    public String postUpload(
            @RequestParam("file") MultipartFile file) {

        String fileName = file.getOriginalFilename();
        try {
            InputStream inputStream = file.getInputStream();
            Files.createFile(this.rootLocation.resolve(fileName));
            Files.copy(inputStream, this.rootLocation.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING);
        }
        catch (Exception e){
        }

        return this.rootLocation.resolve(fileName).toString();//.toString();//file.getOriginalFilename();
    }

}