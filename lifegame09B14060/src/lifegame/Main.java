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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Main implements Runnable{

	BoardModel m;

	/**
	 * ボードモデルが既にある場合のコンストラクタ。主にファイルを開く際に用いる。
	 */
	public Main(BoardModel m) {
		super();
		this.m = m;
	}


	public Main() {
		super();
	}

	class StartButtonAction implements ActionListener{
		int rows;
		int cols;
		JFrame parent;	//親フレーム
		JTextField rField;	//行数を入力するフィールド
		JTextField cField;	//列数を入力するフィールド

		public StartButtonAction(JFrame parent, JTextField rField, JTextField cField) {
			super();
			this.parent = parent;
			this.rField = rField;
			this.cField = cField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(rField.getText() != null && cField.getText() != null){
				//TODO  エラーあり
				final int MaxRowsNum = BoardView.getMaximamRows();
				final int MaxColsNum = BoardView.getMaximumCols();

				rows = Integer.parseInt(rField.getText());
				cols = Integer.parseInt(cField.getText());
				if(rows > 0 && cols > 0){
					m = new BoardModel(rows, cols);
					parent.dispose();
					showGameFrame();
				}else{
					//TODO エラーメッセージ
					JLabel message = new JLabel("正しい値を入力してください");
					JOptionPane.showMessageDialog(parent, message);
				}

			}
		}
	}

	class OpenButtonAction implements ActionListener {
		JFrame frame;



		public OpenButtonAction(JFrame frame) {
			super();
			this.frame = frame;
		}



		/**
		 * Openボタンがクリックされた時はファイル選択ダイアログを表示して、開くファイルを指定する。
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int selected = fileChooser.showOpenDialog(this.frame);

			/*
			 * selectedにはファイルの選択状態が入る
			 * openStatusには指定したファイルが正しい形式になっているかどうかが入る
			 */
			if(selected == JFileChooser.APPROVE_OPTION){
				// for debug
				System.out.println("approved");

				int openStaus = LifegameFile.openFromFile(fileChooser.getSelectedFile());

				if(openStaus == LifegameFile.OPEN_SUCCESSFUL){
					frame.dispose();
				}else if(openStaus == LifegameFile.IMCOMPATIBLE_FILE){
					JLabel message = new JLabel("ファイル形式が正しくありません。詳しくはドキュメントを参照してください。");
					JOptionPane.showMessageDialog(this.frame, message);
				}else if(openStaus == LifegameFile.FILE_NOT_FOUND){
					JLabel message = new JLabel("ファイルが見つかりません。");
					JOptionPane.showMessageDialog(this.frame, message);
				}else if(openStaus == LifegameFile.IO_ERROR){
					JLabel message = new JLabel("IOエラーが発生しました。");
					JOptionPane.showMessageDialog(this.frame, message);
				}
			}else if(selected == JFileChooser.CANCEL_OPTION){
				// for debug
				System.out.println("canseled");
			}else{
				// for debug
				System.out.println("error");
			}
		}

	}

	class SaveButtonAction implements ActionListener {
		JFrame frame;
		BoardModel m;


		public SaveButtonAction(JFrame frame,BoardModel m) {
			super();
			this.frame = frame;
			this.m = m;
		}



		/**
		 * Openボタンがクリックされた時はファイル選択ダイアログを表示して、開くファイルを指定する。
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			JFileChooser fileChooser = new JFileChooser();
			int selected = fileChooser.showSaveDialog(this.frame);

			/*
			 * selectedにはファイルの選択状態が入る
			 * openStatusには指定したファイルが正しい形式になっているかどうかが入る
			 */
			if(selected == JFileChooser.APPROVE_OPTION){
				// for debug
				System.out.println("approved");

				int saveStatus = LifegameFile.saveBoardFile(m, fileChooser.getSelectedFile());

				if(saveStatus == LifegameFile.SAVE_SUCCESSFUL){
					JLabel message = new JLabel("保存されました。");
					JOptionPane.showMessageDialog(frame, message);
				}else{
					JLabel message = new JLabel("正しく保存されませんでした。");
					JOptionPane.showMessageDialog(this.frame, message);
				}
			}else if(selected == JFileChooser.CANCEL_OPTION){
				// for debug
				System.out.println("canseled");
			}else{
				// for debug
				System.out.println("error");

				// 追加
			}
		}

	}


	class NewGameAction implements ActionListener {

		public NewGameAction() {
			super();
		}


		/**
		 * 新しいゲームを開始(別スレッドで開始)
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			GameStart gameStart = new GameStart();
			gameStart.start();
		}

	}


	class NextButtonAction implements ActionListener {

		public NextButtonAction() {
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

	class UndoButtonAction implements ActionListener, DequeListener {
		private JButton button;

		/**
		 *
		 * @param m undo対象となるBoardModel
		 * @param button undoボタン
		 */
		public UndoButtonAction(JButton button) {
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
			// ここでマルチスレッドを作成
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
		StartButton.addActionListener(new StartButtonAction(frame, getRowsField, getColsField));
		OpenButton.addActionListener(new OpenButtonAction(frame));

		frame.getRootPane().setDefaultButton(StartButton);

		//TODO 画面に収まりきる最大の列数、行数を把握して制限する
		return newGameFramePanel;
	}


	private void showGameFrame(){
		int minWindowSize;
		// 盤面を作成する
		BoardView view = new BoardView(m);


		// ウィンドウを作成する
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setTitle("Lifegame");

		// メニューバーを表示
		JMenuBar mb = new JMenuBar();

		JMenu FileMenu = new JMenu("File");
		mb.add(FileMenu);
		JMenu GameMenu = new JMenu("Game");
		mb.add(GameMenu);

		JMenuItem NewGameMenu = new JMenuItem("Newgame");
		JMenuItem OpenMenu = new JMenuItem("Open");
		JMenuItem SaveMenu = new JMenuItem("Save");

		NewGameMenu.addActionListener(new NewGameAction());
		OpenMenu.addActionListener(new OpenButtonAction(frame));
		SaveMenu.addActionListener(new SaveButtonAction(frame, this.m));

		FileMenu.add(OpenMenu);
		FileMenu.add(SaveMenu);

		GameMenu.add(NewGameMenu);

		frame.setJMenuBar(mb);


		// 各ボタンを作成する
		JButton NewGamebutton = new JButton("New Game");
		JButton undobutton = new JButton("Undo");
		JButton nextbutton = new JButton("Next");



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
		NewGamebutton.addActionListener(new NewGameAction());
		nextbutton.addActionListener(new NextButtonAction());
		UndoButtonAction undoListener = new UndoButtonAction(undobutton);
		undobutton.addActionListener(undoListener);


		// 盤面のBoardListenerにviewを追加
		m.addListener(view);

		// 盤面のhistoryのlistenerにundoListerを追加
		m.BoardHistories.addListener(undoListener);


		frame.pack();
		// タイトルバーや境界を含めた最小値を設定する
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
