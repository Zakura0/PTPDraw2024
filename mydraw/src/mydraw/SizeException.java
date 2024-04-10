package mydraw;
/*
 * @authors Giahung Bui 7557640 , Ben Woller 
 */
public class SizeException  extends Exception{
    public SizeException(){
        super("Invalid Size!");

        //TO DO Fallunterscheidungen zwischen negativen Werten und Mindestgröße!
    }
}
