/**
 * Board情報を管理するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class BoardModel {
	private boolean[][] cells;
	private final int cols;
	private final int rows;
	// 盤面更新を通知するlistenerリスト
	private ArrayList<BoardListener> listeners;
	// 盤面の履歴を保存するスタック
	public ArrayDequeWithListener<boolean[][]> BoardHistories = new ArrayDequeWithListener<boolean[][]>();

	public static final int OPEN_SUCCESSFUL = 1;
	public static final int FILE_NOT_FOUND = -1;
	public static final int IMCOMPATIBLE_FILE = -2;
	public static final int IO_ERROR = -3;

	public static final int SAVE_SUCCESSFUL = 1;
	public static final int SAVE_FAILED = -1;

	/**
	 * コンストラクタ
	 * @param rows 行数
	 * @param cols 列数
	 */
	public BoardModel(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		this.cells = new boolean[rows][cols];
		this.listeners = new ArrayList<BoardListener>();
	}

	/**
	 * Listerを追加する
	 * @param listener 追加するリスナー
	 */
	public void addListener(BoardListener listener){
		listeners.add(listener);
	}

	/**
	 * 盤面の更新をBoardListerに通知する
	 */
	public void fireUpdate(){
		for(BoardListener listener: listeners){
			listener.updated(this);
		}
	}

	/**
	 * 現在の盤面を複製する。(別のboolean[][]インスタンスを生成)
	 * テストでも用いるためpublic
	 * @return 複製した盤面
	 */
	public boolean[][] duplicateBoard(){
		boolean[][] b = new boolean[getRows()][getCols()];
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				// 注: b[i][j] = getCells()[i][j]としてしまうと、同じ参照をしてしまうので複製したことにならない
				b[i][j] = (getCells()[i][j]) ? true : false;
			}
		}
		return b;
	}

	/**
	 * (r.c)セルが生きているかどうかを判別する
	 * @return 生きていればtrue,死んでいればfalse
	 */
	public boolean isAlive(int r, int c){
		return (cells[r][c] == true) ? true : false;
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
		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
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
	}

	/**
	 * 新しいボードに変更する
	 * テストでも用いるためpublic
	 * @param b 変更後のボード
	 */
	public void changeToNewBoard(boolean[][] b){
		for(int i=0; i < getRows(); i++){
			for(int j=0; j < getCols(); j++){
				if(getCells()[i][j] != b[i][j])
					changeCellsState(i, j);
			}
		}

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
			if(r < getRows() - 1)
				if(isAlive(r+1, c-1))
					++SuvivorNum;
		}

		// 右端でない場合
		if(c < getCols() - 1){
			// まず右隣を計算
			if(isAlive(r, c+1))
				++SuvivorNum;
			//一番上でない場合は右斜め上を計算
			if(r > 0)
				if(isAlive(r-1, c+1))
					++SuvivorNum;
			//一番下でない場合は右斜め下を計算
			if(r < getRows() - 1)
				if(isAlive(r+1, c+1))
					++SuvivorNum;
		}

		// 一番上でない場合は上を計算
		if(r > 0)
			if(isAlive(r-1, c))
				++SuvivorNum;

		//一番下でない場合は下を計算
		if(r < getRows() - 1)
			if(isAlive(r+1,c))
				++SuvivorNum;

		return SuvivorNum;
	}

	/**
	 * ボードを次の状態に推移させる
	 */
	public void next(){
		int numOfSuvivor;	// 隣接する生きているセルの個数
        boolean nextBoard[][] = new boolean[getRows()][getCols()];	// 次の盤面を保持する配列

		for (int i = 0; i < getRows(); i++) {
			for (int j = 0; j < getCols(); j++) {
				numOfSuvivor = countSuvivorsAround(i, j);

				/* 今生きているなら周りの生存者が2または3で生き続けられる
				 * 今死んでいるなら周りの生存者が3で生き返る
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
		 * スタックの容量がいっぱいの場合は一番古い履歴を消してからpushする
		 */

		try{
			BoardHistories.push(this.duplicateBoard());
		}catch(IllegalAccessError e){
			// スタックがいっぱいの時は一番古い履歴を削除
			BoardHistories.removeLast();
			BoardHistories.push(this.duplicateBoard());
		}


		// 盤面を新しい状態にする
		this.changeToNewBoard(nextBoard);

		// 盤面の更新を通知する
		fireUpdate();
		// 盤面ヒストリーの更新を通知する
		BoardHistories.fireUpdate();
	}

	/**
	 * 盤面を1つ前の状態に戻す。このメソッドを呼び出す前にisUndoableメソッドでundo可能であることを判別することが推奨される。
	 * @throws NoSuchElementException 履歴スタックが空の場合
	 */
	public void undo() throws NoSuchElementException{
		try{
			changeToNewBoard(BoardHistories.pop());
		}catch(NoSuchElementException e){
			// スタックに要素がないときは例外をthrowする
			throw e;
		}

		// 盤面の更新を通知する
		fireUpdate();
		// 盤面ヒストリーの更新を通知する
		BoardHistories.fireUpdate();

	}

	/**
	 * 盤面が巻き戻せるかどうかを判別する。
	 */
	public boolean isUndoable(){
		return (this.BoardHistories.peek() != null) ? true : false;
	}

	/**
	 * ファイルから既存のボードを開く
	 * @param file 開くファイル
	 * @return
	 */
	public static int openFromFile(File file){

		try {
			BufferedReader buffer = new BufferedReader(new FileReader(file));

			String firstCSVLine = buffer.readLine();
			StringTokenizer stFirst = new StringTokenizer(firstCSVLine, ",");

			int rows = 0;
			int cols = 0;

			if(stFirst.countTokens() != 2){
				buffer.close();
				return IMCOMPATIBLE_FILE;
			}

			rows = Integer.parseInt(stFirst.nextToken());
			cols = Integer.parseInt(stFirst.nextToken());

			if(rows == 0 || cols == 0){
				buffer.close();
				return IMCOMPATIBLE_FILE;
			}

			BoardModel board = new BoardModel(rows, cols);
			String line = "";

			while((line = buffer.readLine()) != null){
				StringTokenizer st = new StringTokenizer(line, ",");
				while(st.hasMoreTokens()){
					// for debug
					System.out.println("changed");
					if(st.countTokens() == 2){
						board.changeCellsState(
								Integer.parseInt(st.nextToken()),
								Integer.parseInt(st.nextToken())
								);
					}else{
						buffer.close();
						return IMCOMPATIBLE_FILE;
					}
				}
			}

			buffer.close();

			// 新しいスレッドから新規ゲームを開始
			Thread ct = new Thread(new Main(board));
			ct.start();

			return OPEN_SUCCESSFUL;
		}catch(FileNotFoundException e){
			return FILE_NOT_FOUND;
		}catch (IOException e) {
			return IO_ERROR;
		}
	}

	// TODO 未完成
	public static int saveBoardFile(BoardModel m,File file) {
		try {
			FileWriter fw = new FileWriter(file);
			fw.write(m.getRows() + "," + m.getCols() + "\n");

			for(int i=0; i < m.getRows(); i++){
				for(int j=0; j < m.getCols(); j++){
					if(m.isAlive(i, j)){
						fw.write(i + "," + j + "\n");
					}
				}
			}
			fw.close();
			return SAVE_SUCCESSFUL;
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("ファイル書き込みエラー");
			return SAVE_FAILED;
		}

	}



}
