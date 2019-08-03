package hello;

import workDB.MainQueries;
import java.sql.SQLException;

public class Login {

    private final String token;
    private final String errMessage;

    public Login(String email, String pwd) throws SQLException {

        MainQueries mq = new MainQueries();

        token = mq.searchTokenByEmailAndPassword(email, pwd);

        if(token == ""){
            this.errMessage = "user is not found";
        }
        else{
            this.errMessage = "";
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
