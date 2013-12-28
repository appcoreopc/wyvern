package wyvern.tools.bytecode.visitors;

import java.util.List;

import wyvern.targets.Common.WyvernIL.Def.ClassDef;
import wyvern.targets.Common.WyvernIL.Def.Def;
import wyvern.targets.Common.WyvernIL.Def.Def.Param;
import wyvern.targets.Common.WyvernIL.Def.Definition;
import wyvern.targets.Common.WyvernIL.Def.TypeDef;
import wyvern.targets.Common.WyvernIL.Def.ValDef;
import wyvern.targets.Common.WyvernIL.Def.VarDef;
import wyvern.targets.Common.WyvernIL.Stmt.Statement;
import wyvern.targets.Common.WyvernIL.visitor.DefVisitor;
import wyvern.tools.bytecode.core.BytecodeContext;
import wyvern.tools.bytecode.core.BytecodeContextImpl;
import wyvern.tools.bytecode.values.BytecodeClass;
import wyvern.tools.bytecode.values.BytecodeClassDef;
import wyvern.tools.bytecode.values.BytecodeFunction;
import wyvern.tools.bytecode.values.BytecodeRef;
import wyvern.tools.bytecode.values.BytecodeValue;

public class BytecodeDefVisitor implements DefVisitor<BytecodeContext> {

	private final BytecodeContext context;
	
	/**
	 * sets up the visitor with a context to work with
	 * @param visContext
	 * 		the context of the program at this point
	 */
	public BytecodeDefVisitor(BytecodeContext visContext) {
		context = visContext;
	}
	
	@Override
	public BytecodeContext visit(VarDef varDef) {
		String name = varDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(context);
		BytecodeValue value = varDef.getExn().accept(visitor);
		BytecodeValue refValue = new BytecodeRef(value);
		context.addToContext(name, refValue);
		return context;
	}

	@Override
	public BytecodeContext visit(ValDef valDef) {
		String name = valDef.getName();
		BytecodeExnVisitor visitor = new BytecodeExnVisitor(context);
		BytecodeValue value = valDef.getExn().accept(visitor);
		context.addToContext(name, value);
		return context;
	}

	@Override
	public BytecodeContext visit(TypeDef typeDef) {
		/*
		 *  currently unused
		 */
		throw new RuntimeException("got a TypeDef in the IL");
	}

	@Override
	public BytecodeContext visit(Def def) {
		List<Statement> body = def.getBody();
		String name = def.getName();
		List<Param> params = def.getParams();
		BytecodeValue val = new BytecodeFunction(params, body, context, name);
		context.addToContext(name, val);
		return context;
	}

	@Override
	public BytecodeContext visit(ClassDef classDef) {
		BytecodeContext newContext = new BytecodeContextImpl(context);
		List<Definition> classDefs = classDef.getClassDefinitions();
		List<Definition> defs = classDef.getDefinitions();
		for(Definition def : classDefs) {
			newContext = def.accept(new BytecodeDefVisitor(newContext));
		}
		BytecodeContext fullContext = new BytecodeContextImpl(newContext);
		for(Definition def : defs) {
			fullContext = def.accept(new BytecodeDefVisitor(fullContext));
		}
		BytecodeValue val = new BytecodeClassDef(newContext,fullContext);
		context.addToContext(classDef.getName(), val);
		return context;
	}
}
