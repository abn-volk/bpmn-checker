package org.uet.bpmnchecker.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import org.tzi.use.api.UseApiException;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

public class ActionDetectImplicitGateway extends ActionDetectError {
	
	public ActionDetectImplicitGateway() {
		
	}
	
	public void performAction(IPluginAction pluginAction) {
		super.performAction(pluginAction);
		List<String> objNames = new ArrayList<String>();
		try {
			Value value = fSystemApi.evaluate("Task.allInstances()->select(a | a.incoming->size() > 1 or a.outgoing->size() > 1)");
			for (Value obj : ((SetValue) value)) {
				MObject o = ((ObjectValue) obj).value();
				objNames.add(o.name());
			}
			Value value2 = fSystemApi.evaluate("Event.allInstances()->select(a | a.incoming->size() > 1 or a.outgoing->size() > 1)");
			for (Value obj : ((SetValue) value2)) {
				MObject o = ((ObjectValue) obj).value();
				objNames.add(o.name());
			}
			if (objNames.isEmpty()) {
				JOptionPane.showMessageDialog(fMainWindow, "No implicit gateways were found.", "Result", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String objs = String.join(", ", objNames);
				String msg = String.format("Found %d implicit gateway(s) at %s.\nShow object diagram?" , objNames.size(), objs);
				int show = JOptionPane.showOptionDialog(fMainWindow, msg, 
						"Implicit gateway(s) detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
				if (show == JOptionPane.OK_OPTION) {
					showObjectDiagram(objNames, "Implicit gateway", "/resources/ig.png");
				}
			
			}
		} catch (UseApiException e1) {
			fLogWriter.print(e1.getMessage());
		}
	}
	

}
