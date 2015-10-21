package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class UndoButtonListener implements ActionListener, StackListener {
	private BoardModel m;
	private JButton button;

	/**
	 * コンストラクタ
	 * @param m undo対象となるBoardModel
	 * @param button undoボタン
	 */
	public UndoButtonListener(BoardModel m,JButton button) {
		super();
		this.m = m;
		this.button = button;
		button.setEnabled(false);
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		m.undo();
	}


	@Override
	public void stackUpdated() {
		if(m.isUndoable())
			button.setEnabled(true);
		else
			button.setEnabled(false);
	}




}
