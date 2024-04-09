package mydraw;

public class SizeException  extends Exception{
    public SizeException(){
        super("Invalid Size!");

        //TO DO Fallunterscheidungen zwischen negativen Werten und Mindestgröße!
    }
}
