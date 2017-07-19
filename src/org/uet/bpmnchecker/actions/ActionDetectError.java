package org.uet.bpmnchecker.actions;

import java.awt.BorderLayout;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import org.tzi.use.api.UseSystemApi;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.main.ViewFrame;
import org.tzi.use.gui.util.StatusBar;
import org.tzi.use.main.Session;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.runtime.gui.IPluginActionDelegate;
import org.uet.bpmnchecker.views.diagrams.objectdiagram.NewObjectDiagramView;

public abstract class ActionDetectError implements IPluginActionDelegate {
	
	public MainWindow fMainWindow;
	public Session fSession;
	public PrintWriter fLogWriter;
	public UseSystemApi fSystemApi;
	

	@Override
	public void performAction(IPluginAction pluginAction) {
		fMainWindow = pluginAction.getParent();
		fSession = pluginAction.getSession();
		fLogWriter = fMainWindow.logWriter();
		fSystemApi = UseSystemApi.create(fSession);
	}
	
	public void showObjectDiagram(List<String> errObjs, String diagramName, String iconPath) {
		NewObjectDiagramView odv = new NewObjectDiagramView(fMainWindow, fSession.system(), errObjs);
        ViewFrame f = new ViewFrame(diagramName, odv, "");
        URL url = getClass().getResource(iconPath);
        ImageIcon icon = new ImageIcon(url);
		f.setFrameIcon(icon);
        StatusBar fStatusBar = fMainWindow.statusBar();
        
        // give some help information
        f.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
			public void internalFrameActivated(InternalFrameEvent ev) {
                fStatusBar.showTmpMessage("Use left mouse button to move "
                        + "objects, right button for popup menu.");
            }

            @Override
			public void internalFrameDeactivated(InternalFrameEvent ev) {
                fStatusBar.clearMessage();
            }
            
            @Override
			public void internalFrameClosed(InternalFrameEvent e) {
			}
        });
        
        int OBJECTS_LARGE_SYSTEM = 100;
        
        // Many objects. Ask user if all objects should be hidden
        if (fSession.system().state().allObjects().size() > OBJECTS_LARGE_SYSTEM) {
        	
        	int option = JOptionPane.showConfirmDialog(new JPanel(),
                    "The current system state contains more than " + OBJECTS_LARGE_SYSTEM + " instances." +
        	"This can slow down the object diagram.\r\nDo you want to start with an empty object diagram?",
                    "Large system state", JOptionPane.YES_NO_OPTION);
            
        	if (option == JOptionPane.YES_OPTION) {
                odv.getDiagram().hideAll();
            }
        }
        
        JComponent c = (JComponent) f.getContentPane();
        c.setLayout(new BorderLayout());
        c.add(odv, BorderLayout.CENTER);
		fMainWindow.addNewViewFrame(f);
	}

}
