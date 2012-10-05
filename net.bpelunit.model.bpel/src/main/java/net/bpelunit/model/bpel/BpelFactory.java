package net.bpelunit.model.bpel;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.oasis_open.docs.wsbpel._2_0.process.executable.TProcess;

public class BpelFactory {
	private static final JAXBContext ctx;
	private static final String NAMESPACE_BPEL_2_0 = "http://docs.oasis-open.org/wsbpel/2.0/process/executable";

	static {
		JAXBContext c = null;
		try {
			c = JAXBContext.newInstance("org.oasis_open.docs.wsbpel._2_0.process.executable");
		} catch (JAXBException e) {
		}
		ctx = c;
	}
	
	public static IProcess loadProcess(InputStream in) throws JAXBException {
		return new net.bpelunit.model.bpel._2_0.BpelFactory((TProcess)((JAXBElement<?>)ctx.createUnmarshaller().unmarshal(in)).getValue()).getProcess();
	}

	public static IProcess createProcess() {
		return createProcess(NAMESPACE_BPEL_2_0);
	}

	public static IProcess createProcess(String bpelNamespace) {
		if(NAMESPACE_BPEL_2_0.equals(bpelNamespace)) {
			return new net.bpelunit.model.bpel._2_0.BpelFactory().getProcess();
		} else {
			throw new IllegalArgumentException("No model found for namespace " + bpelNamespace);
		}
	}
	
}
