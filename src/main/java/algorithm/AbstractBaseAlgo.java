package algorithm;

import base.CensusData;
import base.Rectangle;

import java.util.InputMismatchException;
import java.util.Scanner;

abstract public class AbstractBaseAlgo implements BaseAlgo{

    protected int x;
    protected int y;
    protected CensusData censusData;

    protected Long totalPopulation = 0L;

    protected Rectangle usRectangle;
    protected Rectangle inputRecBoundary;

    AbstractBaseAlgo(Integer x, Integer y, CensusData censusData) {
        this.x = x;
        this.y = y;
        this.censusData = censusData;
    }

    public ReturnType takeInput() {
        System.out.println("Please give west, south, east, north coordinates of your query rectangle: ");
        Scanner scanner = new Scanner(System.in);

        try {
            float westBoundary = scanner.nextInt();
            if(westBoundary < 1 || westBoundary > x) {
                System.out.println("Invalid Western Boundary");
                return ReturnType.INVALID_ENTRY;
            }

            float southBoundary = scanner.nextInt();
            if(southBoundary < 1 || southBoundary > y) {
                System.out.println("Invalid Southern Boundary");
                return ReturnType.INVALID_ENTRY;
            }

            float eastBoundary = scanner.nextInt();
            if(eastBoundary < westBoundary || eastBoundary > x) {
                System.out.println("Invalid Eastern Boundary");
                return ReturnType.INVALID_ENTRY;
            }

            float northBoundary = scanner.nextInt();
            if(northBoundary < southBoundary || northBoundary > y) {
                System.out.println("Invalid Northern Boundary");
                return ReturnType.INVALID_ENTRY;
            }

            inputRecBoundary = new Rectangle(westBoundary, eastBoundary, northBoundary, southBoundary);
        } catch (InputMismatchException e) {
            return ReturnType.EXIT;
        }
        return ReturnType.DONE;
    }


    long queryPopulation(){
        double westBound = (usRectangle.left + (inputRecBoundary.left - 1) * (usRectangle.right - usRectangle.left) / x);
        double eastBound = (usRectangle.left + (inputRecBoundary.right) * (usRectangle.right - usRectangle.left) / x);
        double northBound = (usRectangle.bottom + (inputRecBoundary.top) * (usRectangle.top - usRectangle.bottom) / y);
        double southBound = (usRectangle.bottom + (inputRecBoundary.bottom - 1) * (usRectangle.top - usRectangle.bottom) / y);
        return calculatePopulation(westBound, eastBound, northBound, southBound);
    }

    abstract public long calculatePopulation(double westBound, double eastBound, double northBound, double southBound);


    abstract public void findUSRectangle();

    abstract public void findPopulation();

    class Result {
        Rectangle rec;
        int population;
        Result(Rectangle rec, int pop) {
            this.rec = rec;
            population = pop;
        }
    }
}
