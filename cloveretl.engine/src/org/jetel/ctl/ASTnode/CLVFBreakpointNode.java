/* Generated By:JJTree: Do not edit this line. CLVFBreakpointNode.java */

package org.jetel.ctl.ASTnode;

import org.jetel.ctl.ExpParser;
import org.jetel.ctl.TransformLangParserVisitor;

public class CLVFBreakpointNode extends SimpleNode {
	public CLVFBreakpointNode(int id) {
		super(id);
	}

	public CLVFBreakpointNode(ExpParser p, int id) {
		super(p, id);
	}

	public CLVFBreakpointNode(CLVFBreakpointNode node) {
		super(node);
	}

	/** Accept the visitor. * */
	public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
	
	
	
	@Override
	public SimpleNode duplicate() {
		return new CLVFBreakpointNode(this);
	}
}