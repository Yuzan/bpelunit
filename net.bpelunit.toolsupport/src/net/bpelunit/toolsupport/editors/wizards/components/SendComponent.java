/**
* This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
package net.bpelunit.toolsupport.editors.wizards.components;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.bpelunit.framework.xml.suite.XMLAnyElement;
import net.bpelunit.framework.xml.suite.XMLSendActivity;
import net.bpelunit.toolsupport.editors.formwidgets.HyperlinkField;
import net.bpelunit.toolsupport.editors.formwidgets.HyperlinkField.IHyperLinkFieldListener;
import net.bpelunit.toolsupport.editors.wizards.NamespaceWizard;
import net.bpelunit.toolsupport.editors.wizards.TransportOptionWizard;
import net.bpelunit.toolsupport.editors.wizards.fields.DialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.IDialogFieldListener;
import net.bpelunit.toolsupport.editors.wizards.fields.LayoutUtil;
import net.bpelunit.toolsupport.editors.wizards.fields.MessageEditor;
import net.bpelunit.toolsupport.editors.wizards.fields.SelectionButtonDialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.StringDialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.TextDialogField;
import net.bpelunit.toolsupport.editors.wizards.pages.OperationWizardPage;
import net.bpelunit.toolsupport.util.schema.nodes.Element;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlOptions;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ExtendedModifyEvent;
import org.eclipse.swt.custom.ExtendedModifyListener;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
/**
 * The SendComponent allows the user to enter XML data to be sent.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */

public class SendComponent extends DataComponent implements 
		MessageChangeListener, StringValueListener {

	protected TextDialogField fSendField;
	protected XMLSendActivity fSendData;
	protected StringDialogField fDelayStringField;
	protected SelectionButtonDialogField fDelaySelectionField;
	
	protected boolean fDelaySelected;
	
	protected MessageEditor messageEditor;
	
	
	private int option;
	/*private Thread threadA;
	private boolean observer= true;*/
	public TemplateVelocityComponent fieldTemplate;
	private Button TemplateCheckBox;
	private Text browserFolder;
	
	public SendComponent(IWizardPage wizard, FontMetrics metrics) {
		super(wizard, metrics);
	}
	
	public void SaveData(){
		XMLAnyElement xmlAny = XMLAnyElement.Factory.newInstance();
		final XmlCursor xmlCursor = xmlAny.newCursor();
		try {
			if(option==0){
				xmlCursor.insertChars(messageEditor.getMessageAsXML());
				fSendData.setData(xmlAny);
				fSendData.setTemplate(null);
			}else if(option==1){
				xmlCursor.insertChars(fSendField.getText());
				fSendData.setData(xmlAny);
				fSendData.setTemplate(null);
			}else{
				
				if(TemplateCheckBox.getSelection()){
					fSendData.setTemplate(null);
					fSendData.getTemplate().setSrc(browserFolder.getText());
					SaveFile(browserFolder.getText(),fieldTemplate.getText());
					
				}else{
					xmlCursor.insertChars(fieldTemplate.getText());
					fSendData.setTemplate(xmlAny);
				}
				fSendData.setData(null);
				
			}
		
		} finally {
			xmlCursor.dispose();
		}
		
	}
	public void SaveFile(String FilePath, String FileContent)
	{
	    	
	    	FileWriter file;
	        BufferedWriter writer;
	    	
	    	try 
	        {
	            file = new FileWriter(FilePath, false);
	            writer = new BufferedWriter(file);
	            writer.write(FileContent,
	                    0,
	                    FileContent.length());
	                    
	            writer.close();
	            file.close();
	        } 
	        catch (IOException ex) 
	        {
	            ex.printStackTrace();
	        }
	 }

	public void init(XMLSendActivity sendData) {
		
		this.fSendData = sendData;
		
		//
		// fSendField
		this.fSendField = new TextDialogField();
		this.fSendField.setLabelText(null);
		this.fSendField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				SendComponent.this.fireValueChanged(field);
			}
		});

		//
		// fDelayStringField
		this.fDelayStringField = new StringDialogField(true);
		this.fDelayStringField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				if (SendComponent.this.fDelaySelected) {
					SendComponent.this.fireValueChanged(field);
				}
			}
		});

		//
		// fDelaySelectionField
		this.fDelaySelectionField = new SelectionButtonDialogField(SWT.CHECK);
		this.fDelaySelectionField.attachDialogField(this.fDelayStringField);
		this.fDelaySelectionField.setLabelText("Vary send delay");
		this.fDelaySelectionField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				SendComponent.this.fDelaySelected = SendComponent.this.fDelaySelectionField
						.isEnabled();
				SendComponent.this.fDelayStringField.setText("");
				SendComponent.this.fireValueChanged(field);
			}
		});

		
		
		this.initValues();
	}

	private void initValues() {

		XMLAnyElement data = this.fSendData.getData();
		if (data == null) {
			data = this.fSendData.addNewData();
		}

		if (!data.newCursor().toFirstChild()) {
			// no children!
			this.fSendField.setText("");
		} else {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			Map<?, ?> ns = new HashMap<Object, Object>();
			this.getTestSuite().newCursor().getAllNamespaces(ns);
			opts.setSaveImplicitNamespaces(ns);

			this.fSendField.setText(this.fSendData.getData().xmlText(opts));
		}

		String delaySequence = this.fSendData.getDelaySequence();
		
		if (delaySequence == null || "".equals(delaySequence)) {
			delaySequence = "";
			this.fDelaySelected = false;
		} else {
			this.fDelaySelected = true;
		}

		this.fDelaySelectionField.setSelection(this.fDelaySelected);
		this.fDelayStringField.setText(delaySequence);

	}
	
	
	@Override
	public Composite createControls(Composite composite, int nColumns){
		
		final Group group = new Group(composite,SWT.None);
		group.setText("Data to be sent");
		
		GridData gd = new GridData();
		gd.minimumHeight = 200;
		gd.widthHint = 300;
		
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = nColumns;
		
		
		
		final StackLayout stackLayout = new StackLayout();
		group.setLayout(stackLayout);
		
		final Group[] groupOptions = new Group[3];
		group.setLayoutData(gd);
		GridLayout gridLayout = new GridLayout();
	    gridLayout.numColumns = 4;
	  
		for (int k = 0; k < groupOptions.length; k++){ 
		    groupOptions[k] = new Group(group, SWT.BORDER_DOT);
			groupOptions[k].setLayout(gridLayout);
		}
		
		
		stackLayout.topControl = groupOptions[0];
		
		this.messageEditor = new MessageEditor(groupOptions[0], SWT.NULL, this.getTestSuite());
		if (this.getWizardPage() instanceof OperationWizardPage) {
			OperationWizardPage comp = (OperationWizardPage) this.getWizardPage();
			comp.getOperationDataComponent().addMessageListener(this);
		}
		this.messageEditor.setXML(this.fSendField.getText());
		
		
		
		fieldTemplate = new TemplateVelocityComponent(groupOptions[2], SWT.MULTI| SWT.V_SCROLL | SWT.H_SCROLL | SWT.WRAP);
		fieldTemplate.setLayoutData(gd);
		fieldTemplate.addListener(SWT.CHANGED, new Listener(){
			public void handleEvent(Event e){
								
				SendComponent.this.fireValueChanged(null);
			}
			
		});
		
		
		browserFolder = new Text(groupOptions[2], SWT.LINE_CUSTOM | SWT.BORDER);
		
		
		
			
		final Button browserButton = new Button(groupOptions[2], SWT.BUTTON1);
		browserButton.setText("Browse...");
		browserButton.setEnabled(false);
		browserButton.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent event) {
				    Shell prueba = new Shell(Display.getDefault());
					FileDialog fc = new FileDialog(prueba);
					fc.setText("Selection file");
					fc.open();
					browserFolder.setText(fc.getFilterPath()+"/"+fc.getFileName());
					try{
						FileReader fr = new FileReader(browserFolder.getText());
						BufferedReader bf = new BufferedReader(fr);
						String sCadena = new String();
						while ((sCadena = bf.readLine())!=null) {
							fieldTemplate.setText(sCadena);
							
						}
						fSendData.getTemplate().setSrc(browserFolder.getText());
					}catch(Exception exection){
							
							System.out.println("Fail file");
					}
						
						
			}			
		});
		
		TemplateCheckBox=new Button(groupOptions[2],SWT.CHECK);
		TemplateCheckBox.setText("Import Template");
		TemplateCheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (TemplateCheckBox.getSelection()){
					browserButton.setEnabled(true);
					browserFolder.setEnabled(true);
					
				}else{
					browserFolder.setEnabled(false);
					browserButton.setEnabled(false);
					browserFolder.setText("");
				}
			}			
		});
		//System.out.println("Nombre: "+fSendData.documentProperties().getSourceName()
		//		+" "+fSendData.getTemplate().getSrc());
		
		
		if(fSendData.isSetTemplate()){
			XMLAnyElement template = fSendData.getTemplate();
			if (template.isSetSrc()) {
				
				
				
				File file =new File(template.getSrc().toString());
				
				if(!file.exists()){
					String path = fSendData.documentProperties().getSourceName().substring(fSendData.documentProperties().getSourceName().indexOf("/"), fSendData.documentProperties().getSourceName().lastIndexOf("/")+1);
					file = new File(path + template.getSrc().toString());
					browserFolder.setText(path + template.getSrc().toString());
				}
				
				if(!file.exists()){
					getWizardPage().setErrorMessage("File not found");
				}else{
				browserFolder.setText(template.getSrc().toString());
				TemplateCheckBox.setSelection(true);
				browserFolder.setEnabled(true);
				browserButton.setEnabled(true);
					try{
						FileReader fr = new FileReader(file.getAbsolutePath());
						BufferedReader bf = new BufferedReader(fr);
						String sCadena = new String();
						while ((sCadena = bf.readLine())!=null) {
						fieldTemplate.setText(sCadena);
					
						}
					}catch(Exception exection){
					
						System.out.println("Fail file");
					}
				}
			} else {
				TemplateCheckBox.setSelection(false);
				browserButton.setEnabled(false);
				browserFolder.setEnabled(false);
				fieldTemplate.setText(template.toString());
			}
		}
		
		
		
		final Combo ComboBox = new Combo (composite,SWT.READ_ONLY | SWT.DROP_DOWN | SWT.BORDER | SWT.VERTICAL);
		String items[]={"Document","Xml Literal", "Template"};
		ComboBox.setItems(items);
		ComboBox.setText("Document");
		messageEditor.setEditable(true);
		option=0;
		
		ComboBox.addSelectionListener(new SelectionAdapter(){
					public void widgetSelected(SelectionEvent e){
						if (ComboBox.getText().equals("Document")){
							stackLayout.topControl = groupOptions[0];
							group.layout();
							fSendField.getTextControl(null).setEditable(false);
							messageEditor.setEditable(true);
							//if(threadA.isAlive()){
							//	    observer=true;
								    option=0;
							/*		threadA.interrupt();
									
									try {
										threadA.join();
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
							}*/
							
							
						}else if(ComboBox.getText().equals("Xml Literal")){
							
							fSendField.getTextControl(null).setEditable(true);
							messageEditor.setEditable(false);
							stackLayout.topControl = groupOptions[1];
							group.layout();
							/*if(threadA.isAlive()){
									observer=true;*/
									option=1;
									/*
									threadA.interrupt();
									try {
										threadA.join();
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
									
							}*/
							
						}else{
							
							stackLayout.topControl = groupOptions[2];
							option=2;
							group.layout();
							/*if(observer){
							threadA = new Thread(new VelocityHighlightTask(fieldTemplate, getWizardPage()), "HiloA");
							threadA.start();
							observer=false;
							}*/
							
						}
					}
		});
				

		
		this.fSendField.doFillIntoGrid(groupOptions[1], nColumns);
		
		Text text = this.fSendField.getTextControl(null);
		text.setEditable(false);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (ComboBox.getText().equals("Xml Literal")) {
					String xml = SendComponent.this.getXmlText();
					SendComponent.this.messageEditor.setXML(xml);
				}
			}
		});
		text.setFocus();
		
		DialogField.createEmptySpace(composite, 7);

		this.fDelaySelectionField.doFillIntoGrid(composite, 1);
		Button selectionButton = this.fDelaySelectionField.getSelectionButton(null);
		LayoutUtil.setVerticalAlign(selectionButton, GridData.CENTER);

		this.fDelayStringField.doFillIntoGrid(composite, 1);

		Text textControl = this.fDelayStringField.getTextControl(null);
		LayoutUtil.setWidthHint(textControl, this.getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(textControl);

		HyperlinkField namespacePrefixField = new HyperlinkField("Configure Namespace Prefixes...");
		namespacePrefixField.setHyperLinkFieldListener(new IHyperLinkFieldListener() {
			public void hyperLinkActivated() {
				WizardDialog d = new WizardDialog(getShell(), new NamespaceWizard(getTestSuite()));
				if (d.open() == Window.OK) {
					fireValueChanged(fSendField);
					// Message Editor does not take part in fireValueChanged Listeners
					// as those update far too often
					messageEditor.updateItems();
				}
			}
		});
		namespacePrefixField.createControl(group, nColumns, GridData.BEGINNING);

		HyperlinkField transportOptionField = new HyperlinkField("Add HTTP Header...");
		transportOptionField.setHyperLinkFieldListener(new IHyperLinkFieldListener() {
			public void hyperLinkActivated() {
				WizardDialog d = new WizardDialog(getShell(), new TransportOptionWizard(fSendData));
				if (d.open() == Window.OK) {
					fireValueChanged(fSendField);
					// Message Editor does not take part in fireValueChanged Listeners
					// as those update far too often
					messageEditor.updateItems();
				}
			}
		});
		transportOptionField.createControl(group, nColumns, GridData.BEGINNING);
		
		// If the WSDL contains only one service with one port and one
		// operation, theses values are preselected. If this is the case, the
		// InputElement of the Operation must be displayed from the
		// MessageEditor
		if (this.getWizardPage() instanceof OperationWizardPage) {
			// This can only happen, if WizardPage is a OperationWizardPage. In
			// the other cases, the operation is already saved and displayed
			// automatically.
			OperationWizardPage wizardPage = (OperationWizardPage) this.getWizardPage();
			Element element;
			try {
				element = wizardPage.getElementForOperation();
				if (this.fSendField.getText().isEmpty()) {
					this.messageEditor.displayElement(element, true);
				}
			} catch (Exception e1) {
				// no (existing) operation selected. Error is shown elsewhere.
			}
		}

		return group;
	}


	
	
	
	public String getXmlText() {
		return this.fSendField.getText();
	}

	public void setXmlText(String xml) {
		this.fSendField.setTextWithoutUpdate(xml);
	}

	public String getDelaySequence() {
		return fDelaySelected ? this.fDelayStringField.getText() : "";
	}

	@Override
	public void messageChanged(Element input) {
		this.setOperationMessage(input, true);
	}

	public void setOperationMessage(Element inputElement, boolean notifyListener) {
		this.messageEditor.displayElement(inputElement, notifyListener);
	}

	@Override
	public void valueChanged(String newValue) {
		this.fSendField.setText(newValue);
	}

}

/**
 * This file belongs to the BPELUnit utility and Eclipse plugin set. See enclosed
 * license file for more information.
 * 
 */
/**
package net.bpelunit.toolsupport.editors.wizards.components;

import java.util.HashMap;
import java.util.Map;

import net.bpelunit.framework.xml.suite.XMLAnyElement;
import net.bpelunit.framework.xml.suite.XMLSendActivity;
import net.bpelunit.toolsupport.ToolSupportActivator;
import net.bpelunit.toolsupport.editors.formwidgets.HyperlinkField;
import net.bpelunit.toolsupport.editors.formwidgets.HyperlinkField.IHyperLinkFieldListener;
import net.bpelunit.toolsupport.editors.wizards.NamespaceWizard;
import net.bpelunit.toolsupport.editors.wizards.TransportOptionWizard;
import net.bpelunit.toolsupport.editors.wizards.fields.DialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.IDialogFieldListener;
import net.bpelunit.toolsupport.editors.wizards.fields.LayoutUtil;
import net.bpelunit.toolsupport.editors.wizards.fields.MessageEditor;
import net.bpelunit.toolsupport.editors.wizards.fields.SelectionButtonDialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.StringDialogField;
import net.bpelunit.toolsupport.editors.wizards.fields.TextDialogField;
import net.bpelunit.toolsupport.editors.wizards.pages.OperationWizardPage;
import net.bpelunit.toolsupport.util.schema.nodes.Element;

import org.apache.xmlbeans.XmlOptions;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
*/
/**
 * The SendComponent allows the user to enter XML data to be sent.
 * 
 * @version $Id$
 * @author Philip Mayer
 * 
 */
/**
public class SendComponent extends DataComponent implements 
		MessageChangeListener, StringValueListener {

	protected TextDialogField fSendField;
	protected XMLSendActivity fSendData;
	protected StringDialogField fDelayStringField;
	protected SelectionButtonDialogField fDelaySelectionField;
	protected SelectionButtonDialogField enterLiteralXMLCheckBox;

	protected boolean fDelaySelected;
	protected MessageEditor messageEditor;
	protected TabItem literalXMLTab;
	private TabItem messageEditorTab;
	private TabFolder tabFolder;

	public SendComponent(IWizardPage wizard, FontMetrics metrics) {
		super(wizard, metrics);
	}

	public void init(XMLSendActivity sendData) {

		this.fSendData = sendData;

		//
		// fSendField
		this.fSendField = new TextDialogField();
		this.fSendField.setLabelText(null);
		this.fSendField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				SendComponent.this.fireValueChanged(field);
			}
		});

		//
		// fDelayStringField
		this.fDelayStringField = new StringDialogField(true);
		this.fDelayStringField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				if (SendComponent.this.fDelaySelected) {
					SendComponent.this.fireValueChanged(field);
				}
			}
		});

		//
		// fDelaySelectionField
		this.fDelaySelectionField = new SelectionButtonDialogField(SWT.CHECK);
		this.fDelaySelectionField.attachDialogField(this.fDelayStringField);
		this.fDelaySelectionField.setLabelText("Vary send delay");
		this.fDelaySelectionField.setDialogFieldListener(new IDialogFieldListener() {

			public void dialogFieldChanged(DialogField field) {
				SendComponent.this.fDelaySelected = SendComponent.this.fDelaySelectionField
						.isEnabled();
				SendComponent.this.fDelayStringField.setText("");
				SendComponent.this.fireValueChanged(field);
			}
		});

		//
		// enterLiteralXMLCheckBox
		this.enterLiteralXMLCheckBox = new SelectionButtonDialogField(SWT.CHECK);
		this.enterLiteralXMLCheckBox.setLabelText("Enter XML literal");
		this.enterLiteralXMLCheckBox.setSelection(false);
		this.enterLiteralXMLCheckBox.setDialogFieldListener(new IDialogFieldListener() {

			@Override
			public void dialogFieldChanged(DialogField field) {
				SendComponent.this.setInputType();

			}
		});

		this.initValues();
	}

	protected void setInputType() {
		boolean selected = this.enterLiteralXMLCheckBox.isSelected();
		Image image = ToolSupportActivator.getImage(ToolSupportActivator.IMAGE_LOCK);
		if (selected) {

			this.messageEditorTab.setImage(image);
			this.literalXMLTab.setImage(null);
		} else {
			if (!this.messageEditor.isXMLValid()) {
				boolean reset = MessageDialog
						.openQuestion(this.getShell(), "Reset XML?",
								"Continuing will reset the XML to the default message of the selected Operation. Continue anyway?");
				if (!reset) {
					this.enterLiteralXMLCheckBox.setSelection(true);
					return;
				}
			}
			this.messageEditorTab.setImage(null);
			this.literalXMLTab.setImage(image);
		}
		this.fSendField.getTextControl(null).setEditable(selected);
		this.messageEditor.setEditable(!selected);
	}

	private void initValues() {

		XMLAnyElement data = this.fSendData.getData();
		if (data == null) {
			data = this.fSendData.addNewData();
		}

		if (!data.newCursor().toFirstChild()) {
			// no children!
			this.fSendField.setText("");
		} else {
			XmlOptions opts = new XmlOptions();
			opts.setSavePrettyPrint();
			opts.setSavePrettyPrintIndent(4);

			Map<?, ?> ns = new HashMap<Object, Object>();
			this.getTestSuite().newCursor().getAllNamespaces(ns);
			opts.setSaveImplicitNamespaces(ns);

			this.fSendField.setText(this.fSendData.getData().xmlText(opts));
		}

		String delaySequence = this.fSendData.getDelaySequence();
		if (delaySequence == null || "".equals(delaySequence)) {
			delaySequence = "";
			this.fDelaySelected = false;
		} else {
			this.fDelaySelected = true;
		}

		this.fDelaySelectionField.setSelection(this.fDelaySelected);
		this.fDelayStringField.setText(delaySequence);

	}

	@Override
	public Composite createControls(Composite composite, int nColumns) {
		Group group = this.createGroup(composite, "Data to be sent", nColumns, new GridData(
				SWT.FILL, SWT.FILL, true, true));

		this.tabFolder = new TabFolder(group, SWT.TOP);
		this.tabFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (e.item == SendComponent.this.literalXMLTab) {
					SendComponent.this.fSendField.getTextControl(null).setFocus();
				}
			}
		});
		GridData gd = new GridData();
		gd.minimumHeight = 200;
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
		gd.grabExcessHorizontalSpace = true;
		gd.grabExcessVerticalSpace = true;
		gd.horizontalSpan = nColumns;
		this.tabFolder.setLayoutData(gd);

		this.messageEditor = new MessageEditor(this.tabFolder, SWT.NULL, this.getTestSuite());
		if (this.getWizardPage() instanceof OperationWizardPage) {
			OperationWizardPage comp = (OperationWizardPage) this.getWizardPage();
			comp.getOperationDataComponent().addMessageListener(this);
		}
		this.messageEditor.setXML(this.fSendField.getText());
		this.messageEditor.addStringValueListener(this);
		this.messageEditorTab = new TabItem(this.tabFolder, SWT.NULL);
		this.messageEditorTab.setControl(this.messageEditor);
		this.messageEditorTab.setText("Message Editor");

		this.fSendField.doFillIntoGrid(this.tabFolder, nColumns);
		this.literalXMLTab = new TabItem(this.tabFolder, SWT.NULL);
		this.literalXMLTab.setImage(ToolSupportActivator.getImage(ToolSupportActivator.IMAGE_LOCK));
		Text text = this.fSendField.getTextControl(null);
		text.setEditable(false);
		text.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				if (SendComponent.this.enterLiteralXMLCheckBox.isSelected()) {
					String xml = SendComponent.this.getXmlText();
					SendComponent.this.messageEditor.setXML(xml);
				}
			}
		});
		text.setFocus();
		this.literalXMLTab.setControl(text);
		this.literalXMLTab.setText("XML to be sent");

		LayoutUtil.setHeightHint(text, Dialog
				.convertHeightInCharsToPixels(this.getFontMetrics(), 6));
		LayoutUtil.setWidthHint(text, this.getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);

		Composite inner = new Composite(group, SWT.NULL);
		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		inner.setLayoutData(gridData);
		inner.setLayout(new GridLayout(10, false));

		this.enterLiteralXMLCheckBox.doFillIntoGrid(inner, 1);
		if (!this.messageEditor.isXMLValid() && !this.fSendField.getText().isEmpty()) {
			this.enterLiteralXMLCheckBox.setSelection(true);
			this.setInputType();
			this.tabFolder.setSelection(1);
		}
		DialogField.createEmptySpace(inner, 7);

		this.fDelaySelectionField.doFillIntoGrid(inner, 1);
		Button selectionButton = this.fDelaySelectionField.getSelectionButton(null);
		LayoutUtil.setVerticalAlign(selectionButton, GridData.CENTER);

		this.fDelayStringField.doFillIntoGrid(inner, 1);

		Text textControl = this.fDelayStringField.getTextControl(null);
		LayoutUtil.setWidthHint(textControl, this.getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(textControl);

		HyperlinkField namespacePrefixField = new HyperlinkField("Configure Namespace Prefixes...");
		namespacePrefixField.setHyperLinkFieldListener(new IHyperLinkFieldListener() {
			public void hyperLinkActivated() {
				WizardDialog d = new WizardDialog(getShell(), new NamespaceWizard(getTestSuite()));
				if (d.open() == Window.OK) {
					fireValueChanged(fSendField);
					// Message Editor does not take part in fireValueChanged Listeners
					// as those update far too often
					messageEditor.updateItems();
				}
			}
		});
		namespacePrefixField.createControl(group, nColumns, GridData.BEGINNING);

		HyperlinkField transportOptionField = new HyperlinkField("Add HTTP Header...");
		transportOptionField.setHyperLinkFieldListener(new IHyperLinkFieldListener() {
			public void hyperLinkActivated() {
				WizardDialog d = new WizardDialog(getShell(), new TransportOptionWizard(fSendData));
				if (d.open() == Window.OK) {
					fireValueChanged(fSendField);
					// Message Editor does not take part in fireValueChanged Listeners
					// as those update far too often
					messageEditor.updateItems();
				}
			}
		});
		transportOptionField.createControl(group, nColumns, GridData.BEGINNING);
		
		// If the WSDL contains only one service with one port and one
		// operation, theses values are preselected. If this is the case, the
		// InputElement of the Operation must be displayed from the
		// MessageEditor
		if (this.getWizardPage() instanceof OperationWizardPage) {
			// This can only happen, if WizardPage is a OperationWizardPage. In
			// the other cases, the operation is already saved and displayed
			// automatically.
			OperationWizardPage wizardPage = (OperationWizardPage) this.getWizardPage();
			Element element;
			try {
				element = wizardPage.getElementForOperation();
				if (this.fSendField.getText().isEmpty()) {
					this.messageEditor.displayElement(element, true);
				}
			} catch (Exception e1) {
				// no (existing) operation selected. Error is shown elsewhere.
			}
		}

		return group;
	}

	public String getXmlText() {
		return this.fSendField.getText();
	}

	public void setXmlText(String xml) {
		this.fSendField.setTextWithoutUpdate(xml);
	}

	public String getDelaySequence() {
		return fDelaySelected ? this.fDelayStringField.getText() : "";
	}

	@Override
	public void messageChanged(Element input) {
		this.setOperationMessage(input, true);
	}

	public void setOperationMessage(Element inputElement, boolean notifyListener) {
		this.messageEditor.displayElement(inputElement, notifyListener);
	}

	@Override
	public void valueChanged(String newValue) {
		this.fSendField.setText(newValue);
	}

}*/
