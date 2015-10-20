/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Main implements Runnable{

	public static void main(String[] args) {
		BoardModel tested = new BoardModel(10, 10);


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
		int minWindowSize;
		// 盤面を作成する
		BoardModel m = new BoardModel(10, 10);
		BoardView view = new BoardView(m);
		// 各ボタンを作成する
		JButton NewGameBottun = new JButton("New Game");
		JButton undoBottun = new JButton("Undo");
		JButton nextBottun = new JButton("Next");


		m.changeCellsState(2, 2);
		m.changeCellsState(3, 3);
		m.changeCellsState(4, 1);
		m.changeCellsState(4, 2);
		m.changeCellsState(4, 3);

		// ウィンドウを作成する
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Lifegame");

		// baseコンポネントを作成し、レイアウトをBorderLayoutにする
		JPanel base = new JPanel();
		BorderLayout baseLayout = new BorderLayout();
		JPanel Bottuns = new JPanel();

		// 最小ウィンドウサイズを設定する
		minWindowSize = view.getCellSize()*Math.max(m.getRows(), m.getCols());
		view.setMinimumSize(new Dimension(minWindowSize, minWindowSize));

		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(500,500));	//初期サイズの設定

		/*
		 * 中央揃えしたいけどできていない
		 */
		if(base.getHeight() > base.getWidth()){
			baseLayout.setHgap(base.getHeight() - base.getWidth());
		}else{
			baseLayout.setVgap(base.getWidth() - base.getHeight());
		}
		base.setLayout(baseLayout);

		// baseにセルとボタンを配置する
		base.add(view,BorderLayout.CENTER);
		base.add(Bottuns,BorderLayout.SOUTH);
		Bottuns.setLayout(new FlowLayout());
		Bottuns.add(NewGameBottun);
		Bottuns.add(undoBottun);
		Bottuns.add(nextBottun);

		// 盤面のBoardListenerにviewを追加
		m.addListener(view);

		/*
		 * ここにNewGame,Undo,Nextボタンをそれぞれ配置する(演習資料を見ること)
		 */

		frame.pack();
		// タイトルバーや境界を含めた最小値を設定する(packした後でないとこの値が取得できない)
		frame.setMinimumSize(new Dimension(minWindowSize
				+ frame.getInsets().left
				+ frame.getInsets().right
				+ 2,
				minWindowSize
				+ frame.getInsets().top
				+ frame.getInsets().bottom
				+ Bottuns.getSize().height
				+ 2));
		frame.setVisible(true);
	}

}
