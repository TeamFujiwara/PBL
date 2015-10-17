/**
 * Board情報を管理するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.ArrayDeque;

public class BoardModel {
	private boolean[][] cells;
	private int cols;
	private int rows;
	private ArrayList<BoardListener> listeners;
	// (APIより) ArrayDequeは16個まで要素を格納できる
	private ArrayDeque<BoardModel> BoardHistories = new ArrayDeque<BoardModel>();

	/**
	 * (テスト用)
	 * @return
	 */
	public ArrayDeque<BoardModel> getBoardHistories() {
		return BoardHistories;
	}

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

	/**
	 * 現在の盤面を取得する
	 * @return 現在の盤面
	 */
	public boolean[][] getCells() {
		return cells;
	}

	/**
	 * 盤面の列数を取得する
	 * @return 盤面の列数(注: 1から始まる)
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * 盤面の行数を取得する
	 * @return 盤面の行数(注: 1から始まる)
	 */
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
			// 改行する
			System.out.println();
		}
		System.out.println();
	}

	/**
	 * (r,c)マスの生死を変化させる
	 */
	public void changeCellsState(int r, int c){
		this.cells[r][c] = !this.cells[r][c];
		fireUpdate();
	}

	/**
	 * 新しいボードに変更する
	 * @param b 変更後のボード
	 */
	private void changeToNewBoard(boolean[][] b){
		for(int i=0; i < this.getRows(); i++){
			for(int j=0; j < this.getCols(); j++){
				this.cells[i][j] = b[i][j];
			}
		}

		fireUpdate();
	}

	/**
	 * 周囲にあるマスのうち生きているマスの個数を数える
	 * @return 隣接する生きているセルの個数
	 */
	private int countSuvivorsAround(int r, int c){
		int SuvivorNum = 0;

		// 左端でない場合
		if(c > 0){
			//まず左隣を計算
			if(isAlive(r, c-1))
				++SuvivorNum;
			// 一番上でない場合は左斜め上を計算
			if(r > 0)
				if(isAlive(r-1,c-1))
					++SuvivorNum;
			//一番下でない場合は左斜め下を計算
			if(r < this.getRows() - 1)
				if(isAlive(r+1, c-1))
					++SuvivorNum;
		}

		// 右端でない場合
		if(c < this.getCols() - 1){
			// まず右隣を計算
			if(isAlive(r, c+1))
				++SuvivorNum;
			//一番上でない場合は右斜め上を計算
			if(r > 0)
				if(isAlive(r-1, c+1))
					++SuvivorNum;
			//一番下でない場合は右斜め下を計算
			if(r < this.getRows() - 1)
				if(isAlive(r+1, c+1))
					++SuvivorNum;
		}

		// 一番上でない場合は上を計算
		if(r > 0)
			if(isAlive(r-1, c))
				++SuvivorNum;

		//一番下でない場合は下を計算
		if(r < this.getRows() - 1)
			if(isAlive(r+1,c))
				++SuvivorNum;

		return SuvivorNum;
	}

	/**
	 * ボードを次の状態に推移させる
	 */
	public void next(){
		// 隣接する生きているセルの個数
		int numOfSuvivor;
		// 次の盤面を保持する配列
		boolean nextBoard[][] = new boolean[this.getRows()][this.getCols()];

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

		/*
		 * 今の状態を履歴スタックにpushする
		 * スタックの容量がいっぱいの場合は一番古い履歴を消す
		 * yet tested
		 */
		try{
			BoardHistories.push(this);
		}catch(IllegalAccessError e){
			BoardHistories.removeLast();
			BoardHistories.push(this);
		}

		// 盤面を新しい状態にする
		changeToNewBoard(nextBoard);
		// 盤面の更新を通知する
		fireUpdate();

	}

	/**
	 * 盤面を1つ前の状態に戻す。
	 * @throws NoSuchElementException 履歴スタックが空の場合
	 * yet tested
	 */
	public void undo() throws NoSuchElementException{
		try{
			changeToNewBoard(BoardHistories.pop().getCells());
		}catch(NoSuchElementException e){
			throw e;
		}

		fireUpdate();
	}



}
