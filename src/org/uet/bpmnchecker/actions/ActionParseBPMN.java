package org.uet.bpmnchecker.actions;

import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.uet.bpmnchecker.gui.ParserWindow;

public class ActionParseBPMN implements IPluginActionDelegate{
	private ParserWindow fDialog;
	
	public ActionParseBPMN() {
		
	}
	
	public void performAction(IPluginAction pluginAction) {
		Session fSession = pluginAction.getSession();
		MainWindow fMainWindow = pluginAction.getParent();
		fDialog = new ParserWindow(fSession, fMainWindow);
		fDialog.setVisible(true);
	}
}
