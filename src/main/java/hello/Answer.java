package hello;

public abstract class Answer {

    private final String errMessage;

    public Answer(String errMessage){
        this.errMessage = errMessage;
    }

    public String getErrMessage() {
        return errMessage;
    }

}
