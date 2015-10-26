/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import static org.hamcrest.CoreMatchers.instanceOf;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import org.omg.CORBA.IMP_LIMIT;

public class Main implements Runnable{

	newWindowsReturnValue boardPropaties;
	BoardModel m;

	class StartButton implements ActionListener{
		int rows;
		int cols;
		JFrame frame;
		JTextField rField;
		JTextField cField;

		public StartButton(JFrame frame, JTextField rField, JTextField cField) {
			super();
			this.frame = frame;
			this.rField = rField;
			this.cField = cField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO 自動生成されたメソッド・スタブ
			if(rField.getText() != null && cField.getText() != null){
				rows = Integer.parseInt(rField.getText());
				cols = Integer.parseInt(cField.getText());

				m = new BoardModel(rows, cols);
				frame.dispose();
				showGameFrame();
			}
		}

	}

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Main());
	}

	/**
	 * GUIを描画する
	 */
	public void run(){
		// ここにNew Gameウィンドウを作成する

        JFrame newWindow = new JFrame("New LifeGame");
		newWindow.setBounds(30, 30, 0, 0);
		newWindow.setSize(new Dimension(200, 200));

		JPanel newWindowPanel = new JPanel();
		newWindowPanel.setLayout(new BoxLayout(newWindowPanel, BoxLayout.PAGE_AXIS));

		newWindow.setContentPane(newWindowPanel);

		JPanel rowsSetLine = new JPanel();
		rowsSetLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowsSetLine.add(new JLabel("行数"));
		JTextField getRowsField = new JTextField(5);
		rowsSetLine.add(getRowsField);
		newWindowPanel.add(rowsSetLine);

		JPanel colsSetLine = new JPanel();
		colsSetLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		colsSetLine.add(new JLabel("列数"));
		JTextField getColsField = new JTextField(5);
		colsSetLine.add(getColsField);
		newWindowPanel.add(colsSetLine);

		JPanel ButtonsLine = new JPanel();
		ButtonsLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton StartButton = new JButton("Start");
		ButtonsLine.add(StartButton);
		JButton OpenButton = new JButton("Open");
		ButtonsLine.add(OpenButton);
		newWindowPanel.add(ButtonsLine);

		StartButton.addActionListener(new StartButton(newWindow, getRowsField, getColsField));

		newWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		newWindow.pack();
		newWindow.setVisible(true);

			}


	private void showGameFrame(){
		int minWindowSize;
		// 盤面を作成する
		BoardView view = new BoardView(m);
		// 各ボタンを作成する
		JButton NewGamebutton = new JButton("New Game");
		JButton undobutton = new JButton("Undo");
		JButton nextbutton = new JButton("Next");



		// ウィンドウを作成する
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Lifegame");

		// baseコンポネントを作成し、レイアウトをBorderLayoutにする
		JPanel base = new JPanel();
		BorderLayout baseLayout = new BorderLayout();
		JPanel buttons = new JPanel();

		// 最小ウィンドウサイズを設定する
		minWindowSize = BoardView.MIN_CELL_SIZE*Math.max(m.getRows(), m.getCols());
		view.setMinimumSize(new Dimension(minWindowSize, minWindowSize));

		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(500,500));	//初期サイズの設定

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

		// 各ボタンのActionListenerを追加する(Listenerに動作を記述してある)
		NewGamebutton.addActionListener(new NewGameListener(m));
		nextbutton.addActionListener(new NextButtonListener(m));
		UndoButtonListener undoListener = new UndoButtonListener(m, undobutton);
		undobutton.addActionListener(undoListener);


		//viewの領域を枠線表示
		// for debug
		view.setBorder(new BevelBorder(BevelBorder.RAISED));

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

	private void makeNexGameFrame(){


	}

	class newWindowsReturnValue{
		int rows;
		int cols;

		public newWindowsReturnValue(int rows, int cols) {
			super();
			this.rows = rows;
			this.cols = cols;
		}

	}

}
