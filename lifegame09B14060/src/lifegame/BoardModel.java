/**
 * Board情報を管理するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.util.ArrayList;
import java.util.ArrayDeque;

public class BoardModel {
	private boolean[][] cells;
	private int cols;
	private int rows;
	private ArrayList<BoardListener> listeners;
	// (APIより) ArrayDequeは16個まで要素を格納できる
	private ArrayDeque<BoardModel> BoardHistories = new ArrayDeque<BoardModel>();

	/**
	 * @param rows 行数
	 * @param cols 列数
	 */
	public BoardModel(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		cells = new boolean[rows][cols];
		this.listeners = new ArrayList<BoardListener>();
		addListener(new ModelPrinter());
	}

	/**
	 * Listerを追加する
	 * @param listener 追加するリスナー
	 */
	public void addListener(BoardListener listener){
		this.listeners.add(listener);
	}

	/**
	 * 盤面の更新をBoardListerに通知する
	 */
	private void fireUpdate(){
		for(BoardListener listener: this.listeners){
			listener.updated(this);
		}
	}

	/**
	 * (r.c)セルが生きているかどうかを判別する
	 * @return 生きていればtrue,死んでいればfalse
	 */
	public boolean isAlive(int r, int c){
		return (this.cells[r][c] == true) ? true : false;
	}

	public int getCols() {
		return cols;
	}

	public int getRows() {
		return rows;
	}

	/**
	 * (テスト用)現在のボードの状態をコンソールに出力する
	 */
	public void printForDebug(){
		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				System.out.printf("%c", (isAlive(i, j)) ? '※' : '-');
			}
			System.out.println();
		}
	}

	/**
	 * (c,r)マスの状態を変化させる
	 */
	public void changeCellsState(int c, int r){
		this.cells[c][r] = !this.cells[c][r];
		fireUpdate();
	}

	/**
	 * 新しいボードに変更する
	 * @param b 変更後のボード
	 */
	private void changeToNewBoard(boolean[][] b){
		for(int i=0; i < this.getRows(); i++){
			for(int j=0; j < this.getCols(); i++){
				this.cells[i][j] = b[i][j];
			}
		}
	}

	/**
	 * 周囲にあるマスのうち生きているマスの個数を数えて返す
	 * yet tested
	 */
	private int countSuvivorsAround(int r, int c){
		int SuvivorNum = 0;
		if(c != 0){
			for(int i=0;i < 2 && (r + i) <= this.getRows();i++){
				if(isAlive(c-1, r + i))
					++SuvivorNum;
			}
			if(r != 0)
				if(isAlive(c, r-1))
					++SuvivorNum;

		}

		if(c != this.getCols()){
			for(int i=0;i < 2 && (r + i) <= this.getRows();i++){
				if(isAlive(c+1, r + i))
					++SuvivorNum;
			}
			if(r != 0)
				if(isAlive(c, r-1))
					++SuvivorNum;

		}

		if(r != 0)
			if(isAlive(c, r-1))
				++SuvivorNum;

		if(r != this.getRows())
			if(isAlive(c, r+1))
				++SuvivorNum;

		return SuvivorNum;
	}

	/**
	 * ボードを次の状態に推移させる
	 */
	public void next(){
		int numOfSuvivor;
		boolean nextBoard[][] = new boolean[this.getRows()][this.getRows()];

		for (int i = 0; i < this.getRows(); i++) {
			for (int j = 0; j < this.getCols(); j++) {
				numOfSuvivor = countSuvivorsAround(i, j);
				/* 今生きているなら周りの生存者が2または3で生き続けられる
				 * もし死んでいるなら周りの生存者が3で生き返る
				 */
				if(isAlive(i, j)){
					if(numOfSuvivor == 2 || numOfSuvivor == 3)
						nextBoard[i][j] = true;
					else
						nextBoard[i][j] = false;
				}else{
					if(numOfSuvivor == 3)
						nextBoard[i][j] = true;
					else
						nextBoard[i][j] = false;
				}
			}
		}

		try{
			BoardHistories.push(this);
		}catch(IllegalAccessError e){
			BoardHistories.removeLast();
			BoardHistories.push(this);
		}

		changeToNewBoard(nextBoard);
		fireUpdate();

	}



}
