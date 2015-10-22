package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class UndoButtonListener implements ActionListener, DequeListener {
	private BoardModel m;
	private JButton button;

	/**
	 *
	 * @param m undo対象となるBoardModel
	 * @param button undoボタン
	 */
	public UndoButtonListener(BoardModel m,JButton button) {
		super();
		this.m = m;
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
