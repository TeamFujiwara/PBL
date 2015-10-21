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
		JButton NewGamebutton = new JButton("New Game");
		JButton undobutton = new JButton("Undo");
		JButton nextbutton = new JButton("Next");


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
		JPanel buttons = new JPanel();

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
		base.add(buttons,BorderLayout.SOUTH);
		buttons.setLayout(new FlowLayout());
		buttons.add(NewGamebutton);
		buttons.add(undobutton);
		buttons.add(nextbutton);

		NewGamebutton.addActionListener(new NewGameListener(m));
		NewGamebutton.addActionListener(new NewGameListener(m));
		nextbutton.addActionListener(new NextButtonListener(m));
		UndoButtonListener undoListener = new UndoButtonListener(m, undobutton);
		undobutton.addActionListener(undoListener);



		// 盤面のBoardListenerにviewを追加
		m.addListener(view);

		// 盤面のhistoryのlistenerにundoListerを追加
		m.BoardHistories.addListener(undoListener);

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
				+ buttons.getSize().height
				+ 2));
		frame.setVisible(true);
	}

}
