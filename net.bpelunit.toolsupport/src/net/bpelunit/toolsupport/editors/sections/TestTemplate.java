package net.bpelunit.toolsupport.editors.sections;


import net.bpelunit.toolsupport.editors.TestSuitePage;
import net.bpelunit.toolsupport.editors.formwidgets.ContextPart;






import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapLayout;


public class TestTemplate extends BPELUnitSection implements ContextPart, IPropertyListener, IHyperlinkListener {

	public TestTemplate(Composite parent, TestSuitePage page, FormToolkit toolkit,int style) {

		super(page, parent, toolkit,style);
		createClient(getSection(), toolkit);
	}

	protected void createClient(Section section, FormToolkit toolkit) {

		section.setText("Test Template");
		section.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		section.setDescription("Enter Template or Data.");

		Composite content= toolkit.createComposite(section);
		
		final Text FieldTemplate = new Text(content, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		final Button CheckBox=new Button(content,SWT.CHECK);
		CheckBox.setText("Template");
		CheckBox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (CheckBox.getSelection()){
					FieldTemplate.setText("EscribePlantilla");
					FieldTemplate.setEnabled(true);
				}else{
					FieldTemplate.setText("EscribeNormal");
					FieldTemplate.setEnabled(false);
				}
			}			
		});
		
		FieldTemplate.setEnabled(false);
		FieldTemplate.setBackground(new org.eclipse.swt.graphics.Color(null,255,0,0));
		FieldTemplate.setSize(200, 200);
		
				
		TableWrapLayout layout= new TableWrapLayout();
		layout.leftMargin= layout.rightMargin= toolkit.getBorderStyle() != SWT.NULL ? 0 : 2;
		
		content.setLayout(layout);
				
		
		section.setClient(content);
		
		toolkit.paintBordersFor(content);
		
		
	}

	@Override
	public void linkActivated(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkEntered(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void linkExited(HyperlinkEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChanged(Object arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fireSaveNeeded() {
		// TODO Auto-generated method stub
		
	}
}