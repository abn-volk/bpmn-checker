package org.uet.bpmnchecker.actions;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.tzi.use.api.UseApiException;
import org.tzi.use.main.shell.Shell;
import org.tzi.use.runtime.gui.IPluginAction;
import org.tzi.use.uml.ocl.value.ObjectValue;
import org.tzi.use.uml.ocl.value.SetValue;
import org.tzi.use.uml.ocl.value.Value;
import org.tzi.use.uml.sys.MObject;

public class ActionDetectDeadlock extends ActionDetectError {
	
	public ActionDetectDeadlock() {
		
	}
	
	public void performAction(IPluginAction pluginAction) {
		super.performAction(pluginAction);
		Shell.getInstance().processLineSafely("! for fe in FlowElement.allInstances do fe.optional := Sequence{} end");
		Shell.getInstance().processLineSafely("! for node in FlowNode.allInstances do node.isVisited := false end");
		Shell.getInstance().processLineSafely("! for se in StartEvent.allInstances do se.optional := Sequence{false} end");
		Shell.getInstance().processLineSafely("! for se in StartEvent.allInstances do for fl in se.outgoing do fl.label() end end");
		List<String> objNames = new ArrayList<String>();
		try {
			Value value = fSystemApi.evaluate("ParallelGateway.allInstances()->select(a | a.incoming->size() > 1 and a.incoming->exists(flow | flow.optional->at(flow.optional->size())))");
			for (Value obj : ((SetValue) value)) {
				MObject o = ((ObjectValue) obj).value();
				objNames.add(o.name());
			}
			if (objNames.isEmpty()) {
				JOptionPane.showMessageDialog(fMainWindow, "No deadlocks were found.", "Result", JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				String objs = String.join(", ", objNames);
				String msg = String.format("Found %d deadlock(s) at %s.\nShow object diagram?" , objNames.size(), objs);
				int show = JOptionPane.showOptionDialog(fMainWindow, msg, 
						"Deadlock(s) detected", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, 0);
				if (show == JOptionPane.OK_OPTION) {
					showObjectDiagram(objNames, "Deadlock", "/resources/dl.png");
				}
			
			}
		} catch (UseApiException e1) {
			fLogWriter.print(e1.getMessage());
		}
	}
}
