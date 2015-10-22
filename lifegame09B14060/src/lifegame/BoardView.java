package lifegame;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

public class BoardView extends JPanel implements BoardListener,MouseListener,MouseMotionListener {
	private final int rows;
	private final int cols;
	// セルの1辺の長さの最小値
	private static final int MIN_CELL_SIZE = 30;
	//セルのサイズ
	private int cellSize;
	private BoardModel board;

	// プレス・ドラッグ時に直前に変化させたセルの行番号、セル番号
	private int CellsRowPressedJustBefore = -1;
	private int CellsColPressedJustBefore = -1;

	/**
	 * ボードを作成する。
	 * @param b 盤面情報
	 */
	public BoardView(BoardModel b) {
		super();
		this.board = b;
		this.rows = b.getRows();
		this.cols = b.getCols();
		if(this.cellSize < MIN_CELL_SIZE) this.cellSize = MIN_CELL_SIZE;
		board.addListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * ウィンドウサイズに合わせたセルのサイズを決定する
	 */
	public void setCellSize() {
		this.cellSize = Math.min((getHeight() - 2)/getRows(), (getWidth() - 2)/getCols());
		/*
		 * 中央揃えにするためにここに余白設定をかく
		 */
		if(getHeight() > this.getWidth()){
		}
	}

	/**
	 * rows(行数)のgetter
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * cols(列数)のgetter
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * セルの1辺の長さ(cellSize)のgetter
	 */
	public int getCellSize() {
		return this.cellSize;
	}


	@Override
	public void paint(Graphics g) {
		setCellSize();
		// 縦の長さ
		final int maxFieldHieght = getCellSize()*getRows();
		// 横の長さ
		final int maxFieldWidth = getCellSize()*getCols();
		super.paint(g);	//superクラス(JPanel)の背景を塗りつぶすメソッド

		// 縦線を引く
		for (int i = 1; i <= this.getCols(); i++) {
			g.drawLine(this.getCellSize()*i, 0, this.getCellSize()*i, maxFieldHieght);
		}

		// 横線を引く
		for (int i = 1; i <= this.getRows(); i++) {
			g.drawLine(0, this.getCellSize()*i, maxFieldWidth, this.getCellSize()*i);
		}

		// 生きているセルを塗りつぶす
		for (int i = 0; i < this.board.getRows(); i++) {
			for(int j = 0; j < this.board.getCols(); j++){
				if(board.isAlive(i, j))
					g.fillRect(this.getColsX(j) + 1, this.getRowsY(i) + 1, cellSize - 1, cellSize - 1);
			}
		}


	}

	/**
	 * 指定したy座標を含むセルの縦方向の要素を返す
	 * @param y 縦方向の座標(タイトルバーを除く)
	 * @return セルの縦の要素番号
	 */
	private int getRowlements(int y){
		return y / this.cellSize;
	}

	 /** 指定したx座標を含むセルの横方向の要素を返す
	 * @param x 横方向の座標
	 * @return セルの横の要素番号
	 */
	private int getColsElements(int x){
		return x / this.cellSize;
	}

	/**
	 * 指定した行の上端のx座標を返す
	 * @param rows 行
	 * @return y座標
	 */
	private int getRowsY(int rows){
		return rows * getCellSize();
	}

	/**
	 * 指定した列の左端のY座標を返す
	 * @param cols 列
	 * @return x座標
	 */
	private int getColsX(int cols){
		return cols * getCellSize();
	}




	/**
	 *	serialVersionUIDの設定(Eclipseのエラー対策で、実際にこのフィールドは使用しない)
	 */
	private static final long serialVersionUID = 4584313266405816505L;

	/**
	 * セルの状態を一括変更する。
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		/*
		 * 注 同じセルにいても1ピクセル動くごとにセルの状態が変わってしまう
		 */
		changeCellsColor(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {

	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	/**
	 * セルの状態を変化させる。
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		changeCellsColor(e);
	}

	private void changeCellsColor(MouseEvent e){
        int row = getRowlements(e.getY());
	    int col = getColsElements(e.getX());

	    // クリックされた位置がセル内の時だけchangeCellsStateを呼び出す
	    if(row < this.getRows() && col < this.getCols()){
	    	if(row != CellsRowPressedJustBefore || col != CellsColPressedJustBefore){
	    		this.board.changeCellsState(
                    this.getRowlements(e.getY()),
                    this.getColsElements(e.getX()));
	    	}
	    }

	    CellsRowPressedJustBefore = row;
	    CellsColPressedJustBefore = col;

		board.fireUpdate();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		CellsRowPressedJustBefore = -1;
		CellsColPressedJustBefore = -1;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// do nothing

	}


	@Override
	public void mouseExited(MouseEvent e) {
		// do nothing

	}

	/**
	 * セルの状態が変化した時に呼び出される。この場合は各セルの状態を取得して塗りつぶす。
	 */
	@Override
	public void updated(BoardModel m) {
		// TODO 自動生成されたメソッド・スタブ
		repaint();
	}


}
