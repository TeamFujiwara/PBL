/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.awt.BorderLayout;
import java.awt.Dimension;

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

		// ウィンドウを作成する
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Lifegame");

		JPanel base = new JPanel();
		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(400,300));	//最大サイズの設定
		frame.setMinimumSize(new Dimension(300, 200));	//最小サイズの設定

		base.setLayout(new BorderLayout());
		BoardView view = new BoardView(10,10);
		base.add(view,BorderLayout.CENTER);

		frame.pack();
		frame.setVisible(true);
	}

}
