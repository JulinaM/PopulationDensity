import base.Rectangle;

public class RectangleTest {

    public static void main(String[] args) {
        Rectangle rectangle = new Rectangle(0, 10, 10, 0);
        System.out.println(rectangle.toString());;


        Rectangle that = new Rectangle(4, 8, 8, 4);
        Rectangle newRec = rectangle.encompass(that);
        System.out.println(newRec.toString());;
    }
}
