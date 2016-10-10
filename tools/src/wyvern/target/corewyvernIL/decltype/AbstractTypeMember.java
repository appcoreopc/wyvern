package wyvern.target.corewyvernIL.decltype;

import java.io.IOException;

import wyvern.target.corewyvernIL.EmitOIR;
import wyvern.target.corewyvernIL.Environment;
import wyvern.target.corewyvernIL.astvisitor.ASTVisitor;
import wyvern.target.corewyvernIL.support.TypeContext;
import wyvern.target.corewyvernIL.support.View;
import wyvern.target.oir.OIREnvironment;


public class AbstractTypeMember extends DeclType implements EmitOIR {

	public AbstractTypeMember(String name) {
		super(name);
	}

	@Override
	public <T, E> T acceptVisitor(ASTVisitor <T, E> emitILVisitor,
			E env, OIREnvironment oirenv) {
		return emitILVisitor.visit(env, oirenv, this);
	}

	@Override
	public boolean isSubtypeOf(DeclType dt, TypeContext ctx) {
        return this.getName().equals(dt.getName());
	}

	@Override
	public DeclType adapt(View v) {
        return this;
	}

	@Override
	public void checkWellFormed(TypeContext ctx) {
		// always well-formed!
	}

	@Override
	public DeclType doAvoid(String varName, TypeContext ctx, int count) {
		return this;
	}
	
	public void doPrettyPrint(Appendable dest, String indent) throws IOException {
		dest.append(this.getName());
	}
}
