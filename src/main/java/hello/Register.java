package hello;

import workDB.MainQueries;
import java.sql.SQLException;

public class Register {

    private final String token;
    private final String errMessage;

    public Register(String email, String pwd) throws SQLException {

        MainQueries mq = new MainQueries();

        if(mq.isEmailAlreadyExists(email)) {
            this.errMessage = "email already exists";
            this.token = "";
        }
        else{
            this.errMessage = "";
            this.token = mq.newUser(email, pwd);
        }

        mq.conClose();

    }

    public String getToken() {
        return token;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
