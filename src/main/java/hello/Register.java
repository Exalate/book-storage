package hello;

//const users = [
//        {id: 1, email: 'email1@test.ru', pwd: 666},
//        {id: 2, email: 'email2@test.ru', pwd: 666},
//        {id: 3, email: 'email3@test.ru', pwd: 666}
//        ];

public class Register {

    private final int id;
    private final String email;
    private final String pwd;
    private final String token;

    public Register(String email, String pwd) {
        this.email = email;
        this.pwd = pwd;
        this.token = "sSdasadAD32EsdDSA";
        this.id = 666;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

}
