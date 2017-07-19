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

public class ActionDetectCombinedGateway extends ActionDetectError {
	
	public ActionDetectCombinedGateway() {
		
	}
	
	public void performAction(IPluginAction pluginAction) {
		super.performAction(pluginAction);
		List<String> objNames = new ArrayList<String>();
		try {
			Value value = fSystemApi.evaluate("Gateway.allInstances()->select(a | a.incoming->size() > 1 and a.outgoing->size() > 1)");
			for (Value obj : ((SetValue) value)) {
				MObject o = ((ObjectValue) obj).value();
				objNames.add(o.name());
			}
			if (objNames.isEmpty()) {
				JOptionPane.showMessageDialog(fMainWindow, "No combined gateways were found.", "Result", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String objs = String.join(", ", objNames);
				String msg = String.format("Found %d combined gateway(s) at %s.\nShow object diagram?" , objNames.size(), objs);
				int show = JOptionPane.showOptionDialog(fMainWindow, msg, 
						"Combined gateway(s) detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
				if (show == JOptionPane.OK_OPTION) {
					showObjectDiagram(objNames, "Combined gateway", "/resources/cg.png");
				}
			
			}
		} catch (UseApiException e1) {
			fLogWriter.print(e1.getMessage());
		}
	}
	

}
