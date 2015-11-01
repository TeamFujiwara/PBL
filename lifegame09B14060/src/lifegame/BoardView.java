package lifegame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;



public class BoardView extends JPanel implements BoardListener,MouseListener,MouseMotionListener {
	private final int rows;	// 行数
	private final int cols;	// 列数
	public static final int MIN_CELL_SIZE = 10; // セルの1辺の長さの最小値(見やすさ維持のため、ウィンドウサイズによらずこれ以上小さくすることができない)
	public static final int BORDER_WIDTH = 1;
	private int cellSize;	// セルの1辺の長さ
	private BoardModel board;	//盤面

	// プレス・ドラッグ時に直前に変化させたセルの行番号、セル番号(-1の場合設定されていないことを示す)
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

		// 盤面の変更、マウスの動作をこのインスタンスが受け取るように設定
		board.addListener(this);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
	}

	/**
	 * ウィンドウサイズに合わせたセルのサイズを決定する
	 */
	public void setCellSize() {
		cellSize = Math.min((getHeight() - 2)/getRows(), (getWidth() - 2)/getCols());
		if(cellSize < MIN_CELL_SIZE)
			cellSize = MIN_CELL_SIZE;

		setPreferredSize(new Dimension(cellSize*getCols(), cellSize*getRows()));
    }

	/**
	 * rows(行数)のgetter
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * cols(列数)のgetter
	 */
	public int getCols() {
		return this.cols;
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
			g.drawLine(getCellSize()*i, 0, getCellSize()*i, maxFieldHieght);
		}

		// 横線を引く
		for (int i = 1; i <= this.getRows(); i++) {
			g.drawLine(0, getCellSize()*i, maxFieldWidth, getCellSize()*i);
		}

		// 生きているセルを塗りつぶす
		for (int i = 0; i < board.getRows(); i++) {
			for(int j = 0; j < board.getCols(); j++){
				if(board.isAlive(i, j))
					// 上下左右の枠線以外の部分を塗りつぶす
					g.fillRect(getColsX(j) + BORDER_WIDTH
							, getRowsY(i) + BORDER_WIDTH
							, cellSize - BORDER_WIDTH
							, cellSize - BORDER_WIDTH);
			}
		}


	}

	/**
	 * 指定したy座標を含むセルの縦方向の要素を返す
	 * @param y 縦方向の座標(タイトルバーを除く)
	 * @return セルの縦の要素番号
	 */
	private int getRowlements(int y){
		return y / cellSize;
	}

	 /** 指定したx座標を含むセルの横方向の要素を返す
	 * @param x 横方向の座標
	 * @return セルの横の要素番号
	 */
	private int getColsElements(int x){
		return x / cellSize;
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
	 * セルの状態を一括変更する。
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		changeCellsColor(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// do nothing
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// do nothing
	}

	/**
	 * セルの状態を変化させる。
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		changeCellsColor(e);
	}

	/**
	 * セルの生死を変更する。マウスがクリックされた時、またはドラッグ中に上に乗った時のみ使用。
	 */
	private void changeCellsColor(MouseEvent e){
        int row = getRowlements(e.getY());
	    int col = getColsElements(e.getX());

	    // クリックされた位置がセル内の時だけchangeCellsStateを呼び出す
	    if(row < getRows() && col < getCols() && row >= 0 && cols >= 0){
	    	/*
	    	 * クリックされた位置が直前に状態を変更させたセルならchangeCellsStateを呼び出さない
	    	 * (この条件を追加しないと、セル内でも1ピクセル動くごとに状態が入れ替わるため目がチカチカする)
	    	 */
	    	if(row != CellsRowPressedJustBefore || col != CellsColPressedJustBefore){
	    		board.changeCellsState(
                    getRowlements(e.getY()),
                    getColsElements(e.getX()));
	    	}
	    }

	    // 直前に変更したセル番号を更新
	    CellsRowPressedJustBefore = row;
	    CellsColPressedJustBefore = col;

	    // 盤面通知を送信
		board.fireUpdate();
	}

	/**
	 * マウスが離された時は、直前に変更したセル情報をリセット
	 */
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
	 * 盤面が更新されたときは再描画する。
	 */
	@Override
	public void updated(BoardModel m) {
		repaint();
	}

	public static int getMaximamRows() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		// for debug
		System.out.println(env.getMaximumWindowBounds());
		return
				env.getMaximumWindowBounds().height / MIN_CELL_SIZE;
	}

	public static int getMaximumCols() {
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		return
				env.getMaximumWindowBounds().width / MIN_CELL_SIZE;
	}


	/**
	 *	serialVersionUIDの設定(Eclipseのエラー対策で、実際にはこのフィールドは使用しない)
	 */
	private static final long serialVersionUID = 4584313266405816505L;



}
