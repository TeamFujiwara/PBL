/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main implements Runnable{

	public static void main(String[] args) {
		BoardModel tested = new BoardModel(10, 10);

		tested.changeCellsState(2, 2);
		tested.changeCellsState(3, 3);
		tested.changeCellsState(4, 1);
		tested.changeCellsState(4, 2);
		tested.changeCellsState(4, 3);

		tested.next();
		tested.printForDebug();
		System.out.println(tested.isUndoable());
		tested.undo();
		tested.printForDebug();
		System.out.println(tested.isUndoable());

		SwingUtilities.invokeLater(new Main());
	}

	/**
	 * GUIを描画する
	 */
	public void run(){
		final int minWindowSize;
		BoardModel m = new BoardModel(10, 10);
		JButton NewGameBottun = new JButton("New Game");
		JButton undoBottun = new JButton("Undo");
		JButton nextBottun = new JButton("Next");


		m.changeCellsState(3, 4);

		// ウィンドウを作成する
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Lifegame");

		JPanel base = new JPanel();
		BoardView view = new BoardView(m);
		JPanel Bottuns = new JPanel();



		// 最小ウィンドウサイズを計算する
		minWindowSize = view.getCellSize()*Math.max(m.getRows(), m.getCols());

		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(500,500));	//最大サイズの設定

		base.setLayout(new BorderLayout());
		// 中央揃えしたいけどできない
		base.add(view,BorderLayout.CENTER);
		base.add(Bottuns, BorderLayout.SOUTH);
		Bottuns.setLayout(new FlowLayout());
		Bottuns.add(NewGameBottun);
		Bottuns.add(undoBottun);
		Bottuns.add(nextBottun);
		m.addListener(view);

		/*
		 * ここにNewGame,Undo,Nextボタンをそれぞれ配置する(演習資料を見ること)
		 */

		frame.pack();
		// タイトルバーや境界を含めた最小値を設定する(packした後でないとこの値が取得できない)
		frame.setMinimumSize(new Dimension(minWindowSize
				+ frame.getInsets().left
				+ frame.getInsets().right,
				minWindowSize
				+ frame.getInsets().top
				+ frame.getInsets().bottom));
		frame.setVisible(true);
	}

}
