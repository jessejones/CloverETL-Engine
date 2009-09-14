/* Generated By:JJTree: Do not edit this line. CLVFIfStatement.java */

package org.jetel.ctl.ASTnode;

import org.jetel.ctl.ExpParser;
import org.jetel.ctl.TransformLangParserVisitor;
import org.jetel.ctl.data.Scope;

public class CLVFIfStatement extends SimpleNode {

	private Scope thenScope;
	private Scope elseScope;
	
	
	public CLVFIfStatement(int id) {
		super(id);
	}

	public CLVFIfStatement(ExpParser p, int id) {
		super(p, id);
	}

	public CLVFIfStatement(CLVFIfStatement node) {
		super(node);
		this.thenScope = node.thenScope;
		this.elseScope = node.elseScope;
	}

	/** Accept the visitor. * */
	public Object jjtAccept(TransformLangParserVisitor visitor, Object data) {
		return visitor.visit(this, data);
	}
	
	public void setThenScope(Scope thenScope) {
		this.thenScope = thenScope;
	}
	
	public Scope getThenScope() {
		return thenScope;
	}
	
	public void setElseScope(Scope elseScope) {
		this.elseScope = elseScope;
	}
	
	public Scope getElseScope() {
		return elseScope;
	}
	
	@Override
	public SimpleNode duplicate() {
		return new CLVFIfStatement(this);
	}
}