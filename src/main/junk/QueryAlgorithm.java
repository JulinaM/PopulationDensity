import java.util.InputMismatchException;
import java.util.Scanner;

abstract public class QueryAlgorithm {
	
	public static int INVALID_ENTRY = 0;
	public static int EXIT = 1;
	public static int DONE = 2;
	
	float westBoundary;
	float southBoundary;
	float eastBoundary;
	float northBoundary;

	
	private static Scanner scanner;
	
	int x;
	int y;
	CensusData censusData;
	
	Long totalPopulation = new Long(0);
	
	Rectangle usRectangle;
	Rectangle inputRecBoundary;
	
	int takeInput() {
		System.out.println("Please give west, south, east, north coordinates of your query rectangle: ");
		scanner = new Scanner(System. in);

		try {
			westBoundary = scanner.nextInt();
			if(westBoundary < 1 || westBoundary > x) {
				System.out.println("Invalid Western Boundary");
				return INVALID_ENTRY;
			}

			southBoundary = scanner.nextInt();
			if(southBoundary < 1 || southBoundary > y) {
				System.out.println("Invalid Southern Boundary");
				return INVALID_ENTRY;
			}

			eastBoundary = scanner.nextInt();
			if(eastBoundary < westBoundary || eastBoundary > x) {
				System.out.println("Invalid Eastern Boundary");
				return INVALID_ENTRY;
			}

			northBoundary = scanner.nextInt();
			if(northBoundary < southBoundary || northBoundary > y) {
				System.out.println("Invalid Northern Boundary");
				return INVALID_ENTRY;
			}

			inputRecBoundary = new Rectangle(westBoundary, eastBoundary, northBoundary, southBoundary);
		} catch (InputMismatchException e) {
			return EXIT;
		}
		return DONE;
	}


	public void findUSRectangle();
	
	public void findPopulation();
	
	
}
