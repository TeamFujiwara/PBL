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

/**
 * メイン処理を担当するクラス
 */
public class Main implements Runnable{

	BoardModel m;

	/**
	 * ボードモデルが既にある場合のコンストラクタ。主にファイルを開く際に用いる。
	 */
	public Main(BoardModel m) {
		super();
		this.m = m;
	}

	/**
	 * ボードモデルを新規作成する場合のコンストラクタ。主にゲームを新規開始するときに用いる。
	 */
	public Main() {
		super();
	}

	/**
	 * 新しく盤面を作成する機能。
	 */
	class NewBoard implements ActionListener{
		int rows;
		int cols;
		JFrame parent;	//親フレーム
		JTextField rField;	//行数を入力するフィールド
		JTextField cField;	//列数を入力するフィールド

		public NewBoard(JFrame parent, JTextField rField, JTextField cField) {
			super();
			this.parent = parent;
			this.rField = rField;
			this.cField = cField;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if(rField.getText() != null && cField.getText() != null){
				final int maxRowsNum = BoardView.getMaximamRows();
				final int maxColsNum = BoardView.getMaximumCols();

				rows = Integer.parseInt(rField.getText());
				cols = Integer.parseInt(cField.getText());
				if(rows > 0 && cols > 0 && rows < maxRowsNum && cols < maxColsNum){
					m = new BoardModel(rows, cols);
					parent.dispose();
					showGameFrame();
				}else{
					JLabel message = new JLabel("入力できる行数は1から" + maxRowsNum + "列数は1から" + maxColsNum + "までです");
					JOptionPane.showMessageDialog(parent, message);
				}

			}
		}
	}

	/**
	 * ファイルのオープン機能
	 */
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

	/**
	 * 盤面データの保存機能。
	 */
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


	/**
	 * 新規ゲーム開始機能。
	 */
	class NewGameAction implements ActionListener {

		public NewGameAction() {
			super();
		}


		/**
		 * 別スレッドを立ち上げて新しいゲームを開始
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			GameStart gameStart = new GameStart();
			gameStart.start();
		}

	}


	/**
	 * 盤面を次に進める機能
	 */
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
		private JMenuItem menuItem;

		public UndoButtonAction(JButton button, JMenuItem menuItem) {
			super();
			this.button = button;
			this.menuItem = menuItem;
			this.button.setEnabled(false);
			this.menuItem.setEnabled(false);
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
			if(m.isUndoable()){
				button.setEnabled(true);
				menuItem.setEnabled(true);
			}else{
				button.setEnabled(false);
				menuItem.setEnabled(false);
			}
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
		JPanel buttonLine = new JPanel();
		buttonLine.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton startButton = new JButton("Start");
		buttonLine.add(startButton);
		JButton openButton = new JButton("Open");
		buttonLine.add(openButton);
		newGameFramePanel.add(buttonLine);
		startButton.addActionListener(new NewBoard(frame, getRowsField, getColsField));
		openButton.addActionListener(new OpenButtonAction(frame));

		frame.getRootPane().setDefaultButton(startButton);

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

		JMenu fileMenu = new JMenu("File");
		mb.add(fileMenu);
		JMenu gameMenu = new JMenu("Game");
		mb.add(gameMenu);

		// fileメニューのアイテム
		JMenuItem openMenu = new JMenuItem("Open");
		JMenuItem saveMenu = new JMenuItem("Save");
		openMenu.addActionListener(new OpenButtonAction(frame));
		saveMenu.addActionListener(new SaveButtonAction(frame, this.m));

		// Gameメニューのアイテム
		JMenuItem newGameMenu = new JMenuItem("Newgame");
		JMenuItem nextMenu = new JMenuItem("Next");
		JMenuItem undoMenu = new JMenuItem("Undo");
		newGameMenu.addActionListener(new NewGameAction());
		nextMenu.addActionListener(new NextButtonAction());


		fileMenu.add(openMenu);
		fileMenu.add(saveMenu);

		gameMenu.add(newGameMenu);
		gameMenu.addSeparator();
		gameMenu.add(nextMenu);
		gameMenu.add(undoMenu);

		frame.setJMenuBar(mb);


		// 各ボタンを作成する
		JButton newGamebutton = new JButton("New Game");
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
		buttons.add(newGamebutton);
		buttons.add(undobutton);
		buttons.add(nextbutton);

		// 各ボタンのActionListenerを追加する(Listenerに動作を記述してある)
		newGamebutton.addActionListener(new NewGameAction());
		nextbutton.addActionListener(new NextButtonAction());
		UndoButtonAction undoListener = new UndoButtonAction(undobutton,undoMenu);
		undoMenu.addActionListener(undoListener);
		undobutton.addActionListener(undoListener);


		// 盤面のBoardListenerにviewを追加
		m.addListener(view);

		// 盤面のhistoryのlistenerにundoListerを追加
		m.boardHistories.addListener(undoListener);


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
