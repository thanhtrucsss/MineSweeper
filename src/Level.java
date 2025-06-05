package src;

public class Level{
	private static int numRows =7;
	private static int numCols =17;
	private static int mineCount =10;
	private static boolean winGame = false;

	private static int level =1;

	public static void setNumRows(int n){
		numRows = n;
	}
	public static int getNumRows(){
		return numRows;
	}
	public static void setNumCols(int n){
		numCols = n;
	}
	public static int getNumCols(){
		return numCols;
	}

	public static void setLevelInNumber(int n){
		level = n;
	}
	public static int getLevelInNumber(){
		return level;
	}

	public static void setMineCount(int n){
		mineCount = n;
	}
	public static int getMineCount(){
		return mineCount;
	}	

	public static void setWinGame(boolean n){
		winGame = n;
	}
	public static boolean getWinGame(){
		return winGame;
	}
	public static void setLevelEasy(){								
		System.out.println("Console: Easy level selected");
		Level.setMineCount(10);
		Level.setNumRows(7);
		Level.setNumCols(16);
		setLevelInNumber(1);

	}			
	public static void setLevelMedium(){							
		System.out.println("Console: Medium level selected");
		Level.setMineCount(20);
		Level.setNumRows(8);
		Level.setNumCols(17);
		setLevelInNumber(2);
	}
	public static void setLevelHard(){								
		System.out.println("Console: Hard level selected");
		Level.setMineCount(40);
		Level.setNumRows(9);
		Level.setNumCols(18);
		setLevelInNumber(3);
	}

	public static void setLevel(int mines,int rows, int cols){    			
		System.out.println("Console: Custom level");
		Level.setMineCount(mines);
		Level.setNumRows(rows);
		Level.setNumCols(cols);
		setLevelInNumber(0);
	}


}
