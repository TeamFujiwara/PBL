/**
 * Nextボタンが押されたときの動作を記述
 */
package lifegame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NextButtonListener implements ActionListener {

	BoardModel m;

	public NextButtonListener(BoardModel m) {
		super();
		this.m = m;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		m.next();
	}

}
