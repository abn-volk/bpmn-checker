package org.uet.bpmnchecker.gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Paths;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTextField;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.InclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.tzi.use.api.UseApiException;
import org.tzi.use.api.UseSystemApi;
import org.tzi.use.config.Options;
import org.tzi.use.gui.main.MainWindow;
import org.tzi.use.gui.util.ExtFileFilter;
import org.tzi.use.main.ChangeEvent;
import org.tzi.use.main.ChangeListener;
import org.tzi.use.main.Session;
import org.tzi.use.main.shell.Shell;

public class ParserWindow extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6414918347108644115L;
	private Session fSession;
	private PrintWriter fLogWriter;
	private UseSystemApi fSystemApi;
	private JTextField fFileName;
	private File selectedFile;
	
	public ParserWindow(Session session, MainWindow parent) {
		super(parent, "BPMN parser");
		fSession = session;
		fSession.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		fLogWriter = parent.logWriter();
		fSystemApi = UseSystemApi.create(fSession);
		
		fFileName = new JTextField();
		JButton btnPath = new JButton("Browse");
		btnPath.addActionListener(new ActionListener() {
			private JFileChooser fChooser;

			public void actionPerformed(ActionEvent e) {
				String path;
				if (fChooser == null) {
					path = Options.getLastDirectory().toString();
					fChooser = new JFileChooser(path);
					ExtFileFilter filter = new ExtFileFilter("bpmn",
							"Business Process Model Notation");
					fChooser.setFileFilter(filter);
					fChooser.setDialogTitle("Open BPMN file");
				}
				int returnVal = fChooser
						.showOpenDialog(ParserWindow.this);
				if (returnVal != JFileChooser.APPROVE_OPTION)
					return;

				path = fChooser.getCurrentDirectory().toString();
				Options.setLastDirectory(new File(path).toPath());

				fFileName.setText(Paths.get(path,
						fChooser.getSelectedFile().getName()).toString());
				selectedFile = fChooser.getSelectedFile();

			}
		});
		
		JButton btnParse = new JButton("Parse");
		btnParse.setMnemonic('P');
		btnParse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				parse();
				setVisible(false);
				dispose();
			}
		});
		JComponent contentPane = (JComponent) getContentPane();
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));
		contentPane.add(Box.createHorizontalGlue());
		contentPane.add(fFileName);
		contentPane.add(Box.createRigidArea(new Dimension(10, 0)));
		contentPane.add(btnPath);
		contentPane.add(Box.createRigidArea(new Dimension(10, 0)));
		contentPane.add(btnParse);
		getRootPane().setDefaultButton(btnParse);
		pack();
		setSize(new Dimension(400, 80));
		setResizable(true);
		setLocationRelativeTo(parent);
	}
	
	private void parse() {
		if (selectedFile == null) {
			fLogWriter.println("Please select a file.");
		} 
		else {
			Shell.getInstance().processLineSafely("reset");
			fLogWriter.println("Parsing " + selectedFile.getName() + "...");
			BpmnModelInstance model = Bpmn.readModelFromFile(selectedFile);
			Collection <StartEvent> startInstances = model.getModelElementsByType(StartEvent.class);
			for (StartEvent start : startInstances) {
				newElement(start, "StartEvent");
			}
			Collection <EndEvent> endInstances = model.getModelElementsByType(EndEvent.class);
			for (EndEvent end : endInstances) {
				newElement(end, "EndEvent");
			}
			Collection<Task> tasks = model.getModelElementsByType(Task.class);
			for (Task task : tasks) {
				newElement(task, "Task");
			}
			Collection<ExclusiveGateway> eGateways = model.getModelElementsByType(ExclusiveGateway.class);
			for (ExclusiveGateway e : eGateways) {
				newElement(e, "ExclusiveGateway");
			}
			Collection<InclusiveGateway> iGateways = model.getModelElementsByType(InclusiveGateway.class);
			for (InclusiveGateway i : iGateways) {
				newElement(i, "InclusiveGateway");
			}
			Collection<ParallelGateway> pGateways = model.getModelElementsByType(ParallelGateway.class);
			for (ParallelGateway p : pGateways) {
				newElement(p, "ParallelGateway");
			}
			Collection<SequenceFlow> flows = model.getModelElementsByType(SequenceFlow.class);
			for (SequenceFlow flow : flows) {
				newFlow(flow);
			}
			fLogWriter.println("Done.");
		}
	}
	
	

	private void newElement(ModelElementInstance element, String type) {
		String id =  element.getAttributeValue("id");
		try {
			fSystemApi.createObject(type, id);
			fSystemApi.setAttributeValue(id, "id", "'" + id + "'");	
			String name = element.getAttributeValue("name");
			if (name != null)
				fSystemApi.setAttributeValue(id, "name", "'" + name + "'");
			}
		catch (UseApiException e) {
			fLogWriter.println("Failed to create " + type + " " + id + ": " + e.getMessage());
		}
	}
	
	private void newFlow(SequenceFlow flow) {
		newElement(flow, "SequenceFlow");
		FlowNode source = flow.getSource();
		FlowNode target = flow.getTarget();
		try {
			fSystemApi.createLink("IncomingFlow", new String[] {flow.getId(), target.getId()});
			fSystemApi.createLink("OutgoingFlow", new String[] {source.getId(), flow.getId()});
		}
		catch (UseApiException e) {
			fLogWriter.println("Failed to create " + flow.getId() + "'s associations: " + e.getMessage());
		}
	}
	
}
