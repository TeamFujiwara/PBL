package lifegame;

public class BoardModel {
	private boolean[][] cells;
	int cols;
	int rows;

	// コンストラクタ
	public BoardModel(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		cells = new boolean[rows][cols];
	}

	public boolean isAlive(int c, int r){
		return (this.cells[c][r] == true) ? true : false;
	}

	public void printForDebug(){
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				System.out.printf("%c", (isAlive(i, j)) ? '※' : '-');
			}
		}
	}

	public void changeCellsState(int c, int r){
		this.cells[c][r] = !this.cells[c][r];
	}




}
