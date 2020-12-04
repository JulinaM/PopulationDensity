package algorithm;

public interface BaseAlgo {
    public enum ReturnType {
        INVALID_ENTRY(0),
        EXIT(1),
        DONE(2);

        private int value;
        ReturnType(int value){
            this.value = value;
        }
    }

    public ReturnType takeInput();

    public void findUSRectangle();

    public void findPopulation();
}
