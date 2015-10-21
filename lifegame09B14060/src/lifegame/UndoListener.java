package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

public class UndoListener implements ActionListener, StackListener {
	private BoardModel m;
	private JButton button;

	public UndoListener(BoardModel m,JButton button) {
		super();
		this.m = m;
		this.button = button;
		button.setEnabled(false);
	}



	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		m.undo();
		if(!m.isUndoable())
			button.setEnabled(false);
	}



	@Override
	public void stackUpdated() {
		// TODO 自動生成されたメソッド・スタブ
		if(m.isUndoable())
			button.setEnabled(true);
		else
			button.setEnabled(false);
	}




}
