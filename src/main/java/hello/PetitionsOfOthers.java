package hello;

import workDB.MainQueries;
import hello.PetitionEntry;

import java.sql.SQLException;
import java.util.List;

public class PetitionsOfOthers {

    private final String errMessage;
    private final List<PetitionEntry> entries;

    public PetitionsOfOthers(String token) throws SQLException {

        MainQueries mq = new MainQueries();

        int user_id = mq.searchUserIdByToken(token);
        if(user_id == 0) {
            this.errMessage = "user is not found";
            entries = null;
        }
        else {
            entries = mq.searchPetitions(user_id);
            this.errMessage = "";
        }
    }


    public String getErrMessage() {
        return errMessage;
    }

}
