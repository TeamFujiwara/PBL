/**
 * メイン処理を担当するクラス
 * @author Wataru Hirota
 * @version 1.0
 */
package lifegame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main implements Runnable{

	/**
	 * ボードモデルが既にある場合のコンストラクタ。主にファイルを開く際に用いる。
	 */
	public Main(BoardModel m) {
		super();
		this.m = m;
	}


	public Main() {
		// TODO 自動生成されたコンストラクター・スタブ
	}


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

	class OpenButton implements ActionListener {
		JFrame frame;



		public OpenButton(JFrame frame) {
			super();
			this.frame = frame;
		}



		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int selected = fileChooser.showOpenDialog(this.frame);

			if(selected == JFileChooser.APPROVE_OPTION){
				System.out.println("approved");
				BoardModel.openFromFile(fileChooser.getSelectedFile());
			}else{
				System.out.println("error in opening file");
			}
		}

	}

	class NewGameListener implements ActionListener {

		public NewGameListener() {
			super();
		}


		/**
		 * 新しいゲームを開始(同一プロセスで実行
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			/*
			 * 要修正
			 */
			Main.main(null);
		}

	}

	class NextButtonListener implements ActionListener {

		public NextButtonListener() {
			super();
		}

		/**
		 * 次の盤面にボードを進める
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			m.next();
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Main());
	}

	class UndoButtonListener implements ActionListener, DequeListener {
		private JButton button;

		/**
		 *
		 * @param m undo対象となるBoardModel
		 * @param button undoボタン
		 */
		public UndoButtonListener(JButton button) {
			super();
			this.button = button;
			// はじめはUndoボタンを無効にする
			button.setEnabled(false);
		}


		@Override
		public void actionPerformed(ActionEvent e) {
			m.undo();
		}


		/**
		 * Undoできるかできないかでボタンの有効・無効を切り替える
		 */
		@Override
		public void dequeUpdated() {
			if(m.isUndoable())
				button.setEnabled(true);
			else
				button.setEnabled(false);
		}




	}


	/**
	 * GUIを描画する
	 */
	public void run(){
		// NewGameWindowを表示して、行数と列数を指定する。
		if(m == null){
			JFrame newGameFrame = new JFrame("New LifeGame");
			newGameFrame.setBounds(30, 30, 0, 0);
			newGameFrame.setSize(new Dimension(200, 200));


			newGameFrame.setContentPane(makeNewGamePanel(newGameFrame));

			newGameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			newGameFrame.pack();
			newGameFrame.setVisible(true);
		}else{
			showGameFrame();
		}
	}

	private JPanel makeNewGamePanel(JFrame frame)	{
		JPanel newGameFramePanel = new JPanel();
        newGameFramePanel.setLayout(new BoxLayout(newGameFramePanel, BoxLayout.PAGE_AXIS));

        // 1行目は行の設定パネル
		JPanel rowsSetLine = new JPanel();
		rowsSetLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		rowsSetLine.add(new JLabel("行数"));
		JTextField getRowsField = new JTextField(3);
		rowsSetLine.add(getRowsField);
		newGameFramePanel.add(rowsSetLine);

		// 2行目は列の設定パネル
		JPanel colsSetLine = new JPanel();
		colsSetLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		colsSetLine.add(new JLabel("列数"));
		JTextField getColsField = new JTextField(3);
		colsSetLine.add(getColsField);
		newGameFramePanel.add(colsSetLine);

		// 3行目はStartボタンとOpenボタンを作成
		JPanel ButtonsLine = new JPanel();
		ButtonsLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton StartButton = new JButton("Start");
		ButtonsLine.add(StartButton);
		JButton OpenButton = new JButton("Open");
		ButtonsLine.add(OpenButton);
		newGameFramePanel.add(ButtonsLine);
		StartButton.addActionListener(new StartButton(frame, getRowsField, getColsField));
		OpenButton.addActionListener(new OpenButton(frame));

		frame.getRootPane().setDefaultButton(StartButton);

		return newGameFramePanel;
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

		frame.setContentPane(base);
		base.setPreferredSize(new Dimension(500,500));	//初期サイズの設定

		base.setLayout(baseLayout);

		// baseにセルとボタンを配置する
		base.add(view,BorderLayout.CENTER);
		base.add(buttons,BorderLayout.SOUTH);
		buttons.setLayout(new FlowLayout());
		buttons.add(NewGamebutton);
		buttons.add(undobutton);
		buttons.add(nextbutton);

		// 各ボタンのActionListenerを追加する(Listenerに動作を記述してある)
		NewGamebutton.addActionListener(new NewGameListener());
		nextbutton.addActionListener(new NextButtonListener());
		UndoButtonListener undoListener = new UndoButtonListener(undobutton);
		undobutton.addActionListener(undoListener);


		//viewの領域を枠線表示
		// for debug
		// view.setBorder(new BevelBorder(BevelBorder.RAISED));

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
				+ 2*BoardView.BORDER_WIDTH,
				minWindowSize
				+ frame.getInsets().top
				+ frame.getInsets().bottom
				+ buttons.getSize().height
				+ 2*BoardView.BORDER_WIDTH));
		frame.setVisible(true);

	}


}
