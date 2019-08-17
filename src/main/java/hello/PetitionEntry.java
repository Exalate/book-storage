package hello;

public class PetitionEntry {

    private final int petitioner_id;
    private final String petitioner_email;

    private final int book_id;
    private final String book_name;

    private final String petition_text;

    private final int petition_id;

    public PetitionEntry(int petition_id, int petitioner_id, String petitioner_email, int book_id, String book_name, String petition_text) {

        this.book_id = book_id;
        this.book_name = book_name;
        this.petition_text = petition_text;
        this.petitioner_email = petitioner_email;
        this.petitioner_id = petitioner_id;
        this.petition_id = petition_id;
    }

    public int getPetitioner_id(){
        return petitioner_id;
    }

    public String getPetitioner_email(){
        return petitioner_email;
    }

    public int getBook_id(){
        return book_id;
    }

    public String getBook_name(){
        return book_name;
    }

    public String getPetition_text(){
        return petition_text;
    }

    public int getPetition_id(){
        return petition_id;
    }
}
