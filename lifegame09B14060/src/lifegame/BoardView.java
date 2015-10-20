package lifegame;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class BoardView extends JPanel {
	private final int rows;
	private final int cols;
	// セルの1辺の長さの最小値
	private static final int MIN_CELL_SIZE = 30;
	//セルのサイズ
	private int cellSize;

	public void setCellSize() {
	}

	/**
	 * rows(行数)のgetter
	 * @return
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * cols(列数)のgetter
	 * @return
	 */
	public int getCols() {
		return cols;
	}

	/**
	 * セルの1辺の長さのgetter
	 * @return
	 */
	public int getCellSize() {
		return this.cellSize;
	}

	/**
	 * ボードを作成する。
	 * @param rows 行数
	 * @param cols 列数
	 */
	public BoardView(int rows, int cols) {
		super();
		this.rows = rows;
		this.cols = cols;
		this.cellSize = Math.min((this.getHeight() - 2)/this.getRows(), (this.getWidth() - 2)/this.getCols());
		if(this.cellSize < MIN_CELL_SIZE) this.cellSize = MIN_CELL_SIZE;
	}

	@Override
	public void paint(Graphics g) {
		this.setCellSize();
		final int maxFieldHieght = this.getCellSize()*this.getRows();
		final int maxFieldWidth = this.getCellSize()*this.getCols();
		super.paint(g);	//superクラス(JPanel)の背景を塗りつぶすメソッド


		for (int i = 1; i <= this.getRows(); i++) {
			g.drawLine(this.getCellSize()*i, 0, this.getCellSize()*i, maxFieldHieght);
		}

		for (int i = 1; i <= this.getCols(); i++) {
			g.drawLine(0, this.getCellSize()*i, maxFieldWidth, this.getCellSize()*i);
		}
	}


	/**
	 *	serialVersionUIDの設定(Eclipseのエラー対策で、実際にこのフィールドは使用しない)
	 */
	private static final long serialVersionUID = 4584313266405816505L;

}
