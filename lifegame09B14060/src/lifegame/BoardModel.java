package lifegame;

import java.util.ArrayList;

public class BoardModel {
	private boolean[][] cells;
	int cols;
	int rows;
	private ArrayList<BoardListener> listeners;

	// コンストラクタ
	public BoardModel(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		cells = new boolean[rows][cols];
		this.listeners = new ArrayList<BoardListener>();
		addListener(new ModelPrinter());
	}

	public void addListener(BoardListener listener){
		this.listeners.add(listener);
	}

	private void fireUpdate(){
		for(BoardListener listener: this.listeners){
			listener.updated(this);
		}
	}

	public boolean isAlive(int c, int r){
		return (this.cells[c][r] == true) ? true : false;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	public void printForDebug(){
		for (int i = 0; i < this.cols; i++) {
			for (int j = 0; j < this.rows; j++) {
				System.out.printf("%c", (isAlive(i, j)) ? '※' : '-');
			}
			System.out.println();
		}
	}

	public void changeCellsState(int c, int r){
		this.cells[c][r] = !this.cells[c][r];
		fireUpdate();
	}

	private int countSuvivorsAround(int c, int r){
		//具体的な実装をここに

	}

	public void next(){
		int[][] numOfSurvivorsAround = new int[this.getCols()][this.getRows()];

		for (int i = 0; i < this.getCols(); i++) {
			for (int j = 0; j < this.getRows(); j++) {
				numOfSurvivorsAround[i][j] = this.countSuvivorsAround(i, j);
			}
		}
	}


}
