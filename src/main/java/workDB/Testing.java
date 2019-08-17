package workDB;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Testing {

    public static void main(String [] args) throws SQLException {

        MainQueries mq = new MainQueries();

//        System.out.println(mq.CONNECTION_EXIST());
//

//        mq.newUser("podstavadlyalineage2@mail.ru", "hfpldfnhb");

//        boolean b =  mq.EMAIL_ALREADY_EXISTS("yaro84@mail.ru");
//        System.out.println(b);

//        mq.newBook("Белый ягуар - вождь араваков", "Начинается как Робинзон Крузо, потом про войну индейцев", 3, 3, "Текст книги");
//        mq.newBook("Xtk.cnb", "не попадает", 3, 3,
//
//
//
//                "Текст книги");

          //List<String> arrL = mq.splitPages("ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ТЕКСТА ");
//           for(int i = 0; i<arrL.size(); i++){
//
//               System.out.println(arrL.get(i));
//
//        }


//        int a = mq.searchUserID_byToken("ds34t4gsd4fsh481");
//        System.out.println(a);

//        HashMap<Integer, String> hM = mq.searchBookshelvesID_byToken("g43g37u4vyu3536g");
//        for(Map.Entry<Integer, String> pair : hM.entrySet()){
//            System.out.println("ИД полки = " + pair.getKey() + ", а имя = " + pair.getValue());
//        }

        //System.out.println(mq.getNewToken());

        //System.out.println(mq.searchToken_byEmailAndPassword("oct601@mail.ru", "4859662"));

//        System.out.println("Коннекшон екзистс: " + mq.CONNECTION_EXISTS());
//
//        mq.conClose();
//
//        System.out.println("Коннекшон екзистс: " + mq.CONNECTION_EXISTS());

       // mq.addPetition(2, 16, "позязя");

        mq.addResultPetition("ни за что!!!!11222", false, 1);


    }


}
