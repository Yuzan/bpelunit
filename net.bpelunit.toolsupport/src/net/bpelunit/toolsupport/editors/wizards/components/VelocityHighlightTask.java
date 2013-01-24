package net.bpelunit.toolsupport.editors.wizards.components;

import java.io.StringReader;

import net.bpelunit.toolsupport.editors.wizards.pages.ActivityWizardPage;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.Parser;
import org.apache.velocity.runtime.parser.ParserVisitor;
import org.apache.velocity.runtime.parser.TokenMgrError;
import org.apache.velocity.runtime.parser.node.ASTAddNode;
import org.apache.velocity.runtime.parser.node.ASTAndNode;
import org.apache.velocity.runtime.parser.node.ASTAssignment;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTComment;
import org.apache.velocity.runtime.parser.node.ASTDirective;
import org.apache.velocity.runtime.parser.node.ASTDivNode;
import org.apache.velocity.runtime.parser.node.ASTEQNode;
import org.apache.velocity.runtime.parser.node.ASTElseIfStatement;
import org.apache.velocity.runtime.parser.node.ASTElseStatement;
import org.apache.velocity.runtime.parser.node.ASTEscape;
import org.apache.velocity.runtime.parser.node.ASTEscapedDirective;
import org.apache.velocity.runtime.parser.node.ASTExpression;
import org.apache.velocity.runtime.parser.node.ASTFalse;
import org.apache.velocity.runtime.parser.node.ASTFloatingPointLiteral;
import org.apache.velocity.runtime.parser.node.ASTGENode;
import org.apache.velocity.runtime.parser.node.ASTGTNode;
import org.apache.velocity.runtime.parser.node.ASTIdentifier;
import org.apache.velocity.runtime.parser.node.ASTIfStatement;
import org.apache.velocity.runtime.parser.node.ASTIntegerLiteral;
import org.apache.velocity.runtime.parser.node.ASTIntegerRange;
import org.apache.velocity.runtime.parser.node.ASTLENode;
import org.apache.velocity.runtime.parser.node.ASTLTNode;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.ASTMethod;
import org.apache.velocity.runtime.parser.node.ASTModNode;
import org.apache.velocity.runtime.parser.node.ASTMulNode;
import org.apache.velocity.runtime.parser.node.ASTNENode;
import org.apache.velocity.runtime.parser.node.ASTNotNode;
import org.apache.velocity.runtime.parser.node.ASTObjectArray;
import org.apache.velocity.runtime.parser.node.ASTOrNode;
import org.apache.velocity.runtime.parser.node.ASTReference;
import org.apache.velocity.runtime.parser.node.ASTSetDirective;
import org.apache.velocity.runtime.parser.node.ASTStop;
import org.apache.velocity.runtime.parser.node.ASTStringLiteral;
import org.apache.velocity.runtime.parser.node.ASTSubtractNode;
import org.apache.velocity.runtime.parser.node.ASTText;
import org.apache.velocity.runtime.parser.node.ASTTrue;
import org.apache.velocity.runtime.parser.node.ASTWord;
import org.apache.velocity.runtime.parser.node.ASTprocess;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class VelocityHighlightTask implements Runnable, ParserVisitor {
	
	private static final int WAIT_MS = 1000;
	private StyledText styledText;
	private ActivityWizardPage titledialogerror;
	//http://www.wikilengua.org/index.php/Lista_de_colores
	private static final Color C_REFERENCE = new Color(null, 255, 191, 0);//Ambar
	private static final Color cifsetstop = new Color(null,102,2,60);//Purpura de Tiro
	private static final Color cstringtextinteger = new Color(null,135,206,255);//Celeste
	private static final Color ccomment = new Color(null,0,168,107);//Jade
	
	
	
	private int endL=-1;
	
	public VelocityHighlightTask(StyledText styledText, ActivityWizardPage titledialogerror) {
		this.styledText = styledText;
		this.titledialogerror = titledialogerror;
		
	}
	
	public void run() {
		
		
		final Thread currentThread = Thread.currentThread();
		
		while (!currentThread.isInterrupted() && !styledText.isDisposed()) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					try {
						styledText.setStyleRanges(0, styledText.getText().length(), null, null);
						System.out.println("Esto Comienza\n\n");
						parse(styledText.getText(),-1);
						
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						
					}
				}
			});
			
			synchronized(this) {
				try {
					this.wait(WAIT_MS);
				} catch (InterruptedException e) {
					// interrupted wait
					break;
				}
			}
		}
	}
	
	
	private void parse(String Texto,int num) throws ParseException {
		
		endL=num;
		
		
		RuntimeServices rs = RuntimeSingleton.getRuntimeServices();
		try {
			rs.init();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Parser p = new Parser(rs);
		try{
		SimpleNode node = p.parse(new StringReader(Texto), "prueba");
		
		titledialogerror.setErrorMessage(null);
		node.jjtAccept((ParserVisitor) this, null);		
		}catch(ParseException e){
			System.out.print("Error de sintaxis\n" + e.getMessage() + 
					"\n Encuentras:"+e.getMessage().lastIndexOf("Encountered")
					+"Longitud:"+e.getMessage().length());
			
			String numberL;
			String numberC;
			String message;
			int pL;
			int pC;
			
			if(e.getMessage().indexOf("Lexical")==-1){
				
				pL= e.currentToken.beginLine;
				pC= e.currentToken.beginColumn;
				message = "There is a syntax error on line "+ pL +" and column "+ pC+" of the Velocity template";
			
			}else{
				
				numberL= e.getMessage().substring(e.getMessage().lastIndexOf("line ")+5,e.getMessage().indexOf(", column"));
				numberC= e.getMessage().substring(e.getMessage().lastIndexOf("column ")+7,e.getMessage().indexOf(".",e.getMessage().lastIndexOf("column ")+7));
				
				message = "There is a lexical error on line "+ numberL +" and column "+ numberC+" of the Velocity template";
				pL = Integer.parseInt(numberL);
			}
			
			
			
			
			StyleRange styleRangeO = new StyleRange();
			
			System.out.println("Line:"+e.getMessage().lastIndexOf("line ")
					+"LineF "+e.getMessage().indexOf(", column"));
			//		+"Number"+numberL
			//		+"NumberC"+numberC);
			
			System.out.println("Mensaje:"+message+
				//	"Number:"+numberL+
					"pL"+pL);
			
			if((pL-1)>0){
				
				endL=styledText.getOffsetAtLine(pL-1)-1;
				System.out.println(" pL:"+pL+"   "+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
			}
			styleRangeO.start =endL+1;
			if(styledText.getLineCount()==pL){
				
				System.out.println("CountLine: "+styledText.getLineCount()+" Longitud:"+styledText.getText().length());
				styleRangeO.length = styledText.getText().length()-(endL+1);		
		    }else{
		    	System.out.println("Final de Linea:"+styledText.getOffsetAtLine(pL));
		    	styleRangeO.length = styledText.getOffsetAtLine(pL)-endL;
		    }
			
			
			styleRangeO.underline=true;
			styledText.setStyleRange(styleRangeO);
			
			
			titledialogerror.setErrorMessage(message);
			System.out.print("Error de sintaxis\n" + e.getMessage());
		}catch(TokenMgrError tme){
			System.out.print("ESTAMOS EN EL OTRO\n" + tme.getMessage());
		}
		catch(Exception e){
			System.out.print("NI UNO NI OTRO\n" + e.getMessage());
		}
		
		
		
	}

	public Object visit(SimpleNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en SimpleNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept((ParserVisitor) this, arg1);
	}

	@Override
	public Object visit(ASTprocess arg0, Object arg1) {
		// procesamiento propio
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTprocess" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTEscapedDirective arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTEscapedDirective" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
		
	}

	@Override
	public Object visit(ASTEscape arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTEscape" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTComment arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTComment" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		
		
		if(styledText.getLineCount()==pL){
			System.out.println("CountLine: "+styledText.getLineCount()+" Longitud:"+styledText.getText().length());
			styleRangeO.length = styledText.getText().length()-(endL+pC);		
	    }else{
	    	System.out.println("Final de Linea:"+styledText.getOffsetAtLine(pL));
	    	styleRangeO.length = styledText.getOffsetAtLine(pL)-(endL+pC);
	    }
		styleRangeO.fontStyle = SWT.ITALIC;
		styleRangeO.foreground = ccomment;
		styledText.setStyleRange(styleRangeO);
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTFloatingPointLiteral arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTFloatingPointLiteral" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTIntegerLiteral arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTIntegerLiteral" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = fC-pC+1;
		styleRangeO.fontStyle = SWT.ITALIC;
		styleRangeO.foreground = cstringtextinteger;
		styledText.setStyleRange(styleRangeO);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTStringLiteral arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTStringLiteral" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = fC-pC+1;
		styleRangeO.fontStyle = SWT.ITALIC;
		styleRangeO.foreground = cstringtextinteger;
		styledText.setStyleRange(styleRangeO);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTIdentifier arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTIdentifier" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTWord arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTWord" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTDirective arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTDirective" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = fC-pC+1;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTBlock arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTBlock" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTMap arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTMap" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTObjectArray arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTObjectArray" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTIntegerRange arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTIntegerRange" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTMethod arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTMethod" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTReference arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		;
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTReference" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = fC-pC+1;
		styleRangeO.foreground = C_REFERENCE;
		styledText.setStyleRange(styleRangeO);	
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTTrue arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTTrue" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTFalse arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTFalse" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTText arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTText" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = fC-pC+1;
		styleRangeO.fontStyle = SWT.ITALIC;
		styleRangeO.foreground = cstringtextinteger;
		styledText.setStyleRange(styleRangeO);

				
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTIfStatement arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTIfStatement" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = 3;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		
		
		StyleRange styleRange1 = new StyleRange();
		
		
		pL=arg0.getLastToken().beginLine;
		fL=arg0.getLastToken().endLine;
		pC=arg0.getLastToken().beginColumn;
		fC=arg0.getLastToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTIfStatement" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
        if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}
		styleRange1.start = endL+pC;
		styleRange1.length = 4;
		styleRange1.fontStyle = SWT.BOLD;
		styleRange1.foreground = cifsetstop;
		styledText.setStyleRange(styleRange1);
		
		
		
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTElseStatement arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTElseStatement" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = 5;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTElseIfStatement arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTElseIfStatement" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = 7;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTSetDirective arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTSetDirective" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		StyleRange styleRangeO = new StyleRange();
		
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			styleRangeO.start = endL+pC;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}else if(pL-1==0){
			styleRangeO.start =endL+fC-4;
		}

		
		
		
		styleRangeO.length = 4;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		
		
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTStop arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTStop" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		if((pL-1)>0){
			
			endL=styledText.getOffsetAtLine(pL-1)-1;
			System.out.println(" pL:"+styledText.getOffsetAtLine(pL-1)+" ENDL:"+endL);
		}

		StyleRange styleRangeO = new StyleRange();
		
		styleRangeO.start = endL+pC;
		styleRangeO.length = 5;
		styleRangeO.fontStyle = SWT.BOLD;
		styleRangeO.foreground = cifsetstop;
		styledText.setStyleRange(styleRangeO);
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTExpression arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTExpression" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTAssignment arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTAssignment" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTOrNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTOrNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTAndNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTAndNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTEQNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTEQNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTNENode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTNENode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTLTNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTLTNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTGTNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTGTNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTLENode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTLENode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTGENode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTGENode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTAddNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTAddNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTSubtractNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTSubtractNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTMulNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTMulNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTDivNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTDivNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTModNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTModNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}

	@Override
	public Object visit(ASTNotNode arg0, Object arg1) {
		int pL,fL,pC,fC;
		pL=arg0.getFirstToken().beginLine;
		fL=arg0.getFirstToken().endLine;
		pC=arg0.getFirstToken().beginColumn;
		fC=arg0.getFirstToken().endColumn;
		
		System.out.println("1º "+arg0.getFirstToken().toString()+"2º "+arg0.getLastToken().toString()+" Estamos en ASTNotNode" +
				"Principio de Linea: "+pL+" " +
				"Final de linea: "+fL+" " +
				"Principio de Columna: "+pC+" " +
				"Final de Columna: "+fC);
		
		return arg0.childrenAccept(this, arg1);
	}
   

}