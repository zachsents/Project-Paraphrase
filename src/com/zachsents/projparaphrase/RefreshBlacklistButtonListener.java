package com.zachsents.projparaphrase;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RefreshBlacklistButtonListener implements ActionListener
{
	@Override
	public void actionPerformed(ActionEvent arg0) {
		WordOmitter.loadBlacklist();
	}
}