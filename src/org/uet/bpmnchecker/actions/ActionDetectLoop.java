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

public class ActionDetectLoop extends ActionDetectError {
	
	public ActionDetectLoop() {
		
	}
	
	public void performAction(IPluginAction pluginAction) {
		super.performAction(pluginAction);
		List<String> objNames = new ArrayList<String>();
		try {
			Value value = fSystemApi.evaluate("FlowNode.allInstances()->select(a | Sequence{a}->closure(outgoing.targetRef)->exists(b | b<>a and Sequence{b}->closure(outgoing.targetRef)->includes(a)))");
			for (Value obj : ((SetValue) value)) {
				MObject o = ((ObjectValue) obj).value();
				objNames.add(o.name());
			}
			if (objNames.isEmpty()) {
				JOptionPane.showMessageDialog(fMainWindow, "No loops were found.", "Result", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String objs = String.join(", ", objNames);
				String msg = String.format("Found one or more loops between %s.\nShow object diagram?" , objs);
				int show = JOptionPane.showOptionDialog(fMainWindow, msg, 
						"Loop(s) detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
				if (show == JOptionPane.OK_OPTION) {
					showObjectDiagram(objNames, "Loop", "/resources/lp.png");
				}
			
			}
		} catch (UseApiException e1) {
			fLogWriter.print(e1.getMessage());
		}
	}
}
