package com.ssplugins.shadow;

import com.ssplugins.shadow.Stepper.StepAction;
import com.ssplugins.shadow.common.ClassFinder;
import com.ssplugins.shadow.common.NamedReference;
import com.ssplugins.shadow.common.Range;
import com.ssplugins.shadow.common.ShadowIterator;
import com.ssplugins.shadow.def.*;
import com.ssplugins.shadow.element.*;
import com.ssplugins.shadow.exceptions.ShadowException;
import com.ssplugins.shadow.exceptions.ShadowExecutionException;
import com.ssplugins.shadow.exceptions.ShadowParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.*;

public class ShadowCommons extends ShadowAPI {
    
    private ReplacerDef replacerExec = replacerExec();
	
	@Override
	public List<ClassFinder> registerClassFinders() {
		List<ClassFinder> out = new ArrayList<>();
		out.add(finderStandard());
		return out;
	}
	
	@Override
	public List<ExpressionDef> registerExpressions() {
		List<ExpressionDef> out = new ArrayList<>();
		out.add(expAdd());
		out.add(expSubtract());
		out.add(expMultiply());
		out.add(expDivide());
		out.add(expMod());
		out.add(expLessThan());
		out.add(expLessThanEqual());
		out.add(expGreaterThan());
		out.add(expGreaterThanEqual());
		out.add(expEquals());
		out.add(expNotEquals());
		out.add(expOr());
		out.add(expAnd());
		return out;
	}
	
	@Override
	public List<KeywordDef> registerKeywords() {
		List<KeywordDef> out = new ArrayList<>();
		out.add(keywordLog());
		out.add(keywordSet());
        out.add(keywordUnset());
		out.add(keywordCall());
		out.add(keywordStore());
		out.add(keywordBreak());
        out.add(keywordBreakAll());
        out.add(keywordRepeatIf());
        out.add(keywordReturn());
        out.add(keywordExec());
		return out;
	}
	
	@Override
	public List<BlockDef> registerBlocks() {
		List<BlockDef> out = new ArrayList<>();
		out.add(blockMain());
        out.add(blockBlock());
		out.add(blockIf());
        out.add(blockElseIf());
		out.add(blockElse());
		out.add(blockCount());
		out.add(blockWhile());
		out.add(blockForEach());
        out.add(blockDefine());
		return out;
	}
	
	@Override
	public List<ReplacerDef> registerReplacers() {
		List<ReplacerDef> out = new ArrayList<>();
		out.add(replacerToString());
		out.add(replacerEval());
        out.add(replacerUpper());
        out.add(replacerLower());
        out.add(replacerArr());
        out.add(replacerLen());
        out.add(replacerExec);
        out.add(replacerInput());
		return out;
	}
	
	@Override
	public List<EvalSymbolDef> registerEvalSymbols() {
		List<EvalSymbolDef> out = new ArrayList<>();
		out.add(evalVar());
		out.add(evalString());
		out.add(evalCast());
		out.add(evalMethod());
		out.add(evalConstruct());
		out.add(evalField());
		out.add(evalParse());
		return out;
	}
	
	private ClassFinder finderStandard() {
		return input -> {
			boolean arr = input.endsWith("[]");
			if (arr) input = input.substring(0, input.length() - 2);
			try {
				Class<?> type = Class.forName(input);
				return Optional.of(type);
			} catch (ClassNotFoundException ignored) {}
			for (Package p : Package.getPackages()) {
				try {
					Class<?> type = Class.forName(arr ? "[L" + p.getName() + "." + input + ";" : p.getName() + "." + input);
					return Optional.of(type);
				} catch (ClassNotFoundException ignored) {}
			}
			return Optional.empty();
		};
	}
	
	private ExpressionDef expAdd() {
		return new ExpressionDef("+", Expressions::add);
	}
	
	private ExpressionDef expSubtract() {
		return new ExpressionDef("-", Expressions::subtract);
	}
	
	private ExpressionDef expMultiply() {
		return new ExpressionDef("*", Expressions::multiply);
	}
	
	private ExpressionDef expDivide() {
		return new ExpressionDef("/", Expressions::divide);
	}
	
	private ExpressionDef expMod() {
		return new ExpressionDef("%", Expressions::mod);
	}
	
	private ExpressionDef expLessThan() {
		return new ExpressionDef("<", Expressions::lessThan);
	}
	
	private ExpressionDef expLessThanEqual() {
		return new ExpressionDef("<=", Expressions::lessThanEqual);
	}
	
	private ExpressionDef expGreaterThan() {
		return new ExpressionDef(">", Expressions::greaterThan);
	}
	
	private ExpressionDef expGreaterThanEqual() {
		return new ExpressionDef(">=", Expressions::greaterThanEqual);
	}
	
	private ExpressionDef expEquals() {
		return new ExpressionDef("==", Expressions::equals);
	}
	
	private ExpressionDef expNotEquals() {
		return new ExpressionDef("!=", Expressions::notEquals);
	}
	
	private ExpressionDef expOr() {
		return new ExpressionDef("||", Expressions::or);
	}
	
	private ExpressionDef expAnd() {
		return new ExpressionDef("&&", Expressions::and);
	}
	
	private KeywordDef keywordLog() {
        KeywordDef def = new KeywordDef("log", (def1, args, scope, stepper) -> {
            System.out.println(ShadowTools.sectionsToString(args, scope));
        });
        return def;
	}
	
	private KeywordDef keywordSet() {
        KeywordDef def = new KeywordDef("set", (def1, args, scope, stepper) -> {
            String name = args.get(0).asPrimitive().asString();
            scope.setVar(name, ShadowTools.executeEval(args.get(1).asEvalGroup(), scope).asReference().getValue());
        });
        
        def.setArgumentCount(Range.lowerBound(2));
        
        def.setSplitter((content, context) -> {
            int i = content.indexOf(' ');
            if (i == -1) return new String[0];
            return new String[] {content.substring(0, i), content.substring(i + 1)};
        });
        
        def.setSectionParser((sections, context) -> {
            ShadowTools.verifyArgs(sections, 2, context);
            List<ShadowSection> out = new ArrayList<>();
            out.add(Primitive.string(sections[0]));
            out.add(SectionParser.parseAsEval(sections[1], context));
            return out;
        });
        return def;
	}
	
	private KeywordDef keywordStore() {
		KeywordDef def = new KeywordDef("store", (def1, args, scope, stepper) -> {
			String name = args.get(0).asPrimitive().asString();
			Optional<Object> op = ShadowTools.asObject(args.get(1), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Parameter could not be converted to object.");
			scope.setVar(name, op.get());
		});
		def.setArgumentCount(Range.lowerBound(2));
		def.setSectionParser((sections, context) -> {
			ShadowTools.verifyArgs(sections, 2, context);
			List<ShadowSection> out = new ArrayList<>();
			out.add(Primitive.string(sections[0]));
			out.addAll(SectionParser.standard().getSections(Arrays.copyOfRange(sections, 1, sections.length), context));
			return out;
		});
		return def;
	}
	
	private KeywordDef keywordUnset() {
		KeywordDef def = new KeywordDef("unset", (def1, args, scope, stepper) -> {
			args.forEach(section -> scope.unset(section.asPrimitive().asString()));
		});
		def.setArgumentCount(Range.lowerBound(1));
		def.setSectionParser(SectionParser.allString());
		return def;
	}
	
	private KeywordDef keywordBreak() {
		KeywordDef def = new KeywordDef("break", (def1, args, scope, stepper) -> {
			stepper.next(StepAction.BREAK);
		});
		return def;
	}
	
	private KeywordDef keywordBreakAll() {
		KeywordDef def = new KeywordDef("breakall", (def1, args, scope, stepper) -> {
			stepper.next(StepAction.BREAK_ALL);
		});
		return def;
	}
	
	private KeywordDef keywordCall() {
		KeywordDef def = new KeywordDef("call", (def1, args, scope, stepper) -> {
			ShadowTools.executeEval(args.get(0).asEvalGroup(), scope);
		});
		def.setArgumentCount(Range.lowerBound(1));
		def.setSplitter(Splitter.evalSplit());
		def.setSectionParser(SectionParser.evalParser());
		return def;
	}
	
	private KeywordDef keywordRepeatIf() {
		KeywordDef def = new KeywordDef("repeatif", (def1, args, scope, stepper) -> {
            boolean r = scope.getContext()
                 .findBlock("if")
                 .map(BlockDef::getEntryCondition)
                 .map(cond -> cond.trigger(null, args, scope, stepper))
                 .orElse(false);
            if (r) {
                stepper.next(StepAction.RESTART);
            }
		});
        def.setArgumentCount(Range.single(1));
		return def;
	}
	
	private KeywordDef keywordReturn() {
        KeywordDef def = new KeywordDef("return", (def1, args, scope, stepper) -> {
            Object o = ShadowTools.asObject(args.get(0), scope).orElseThrow(ShadowException.sectionConvert(scope.getContext()));
            stepper.getReturnable().ifPresent(block -> block.setReturned(o));
            stepper.next(StepAction.BREAK_ALL);
        });
        def.setArgumentCount(Range.single(1));
        return def;
    }
    
    private KeywordDef keywordExec() {
	    KeywordDef def = new KeywordDef("exec", (def1, args, scope, stepper) -> {
            replacerExec.getAction().apply(args, scope);
        });
	    def.setArgumentCount(Range.lowerBound(1));
	    def.setSectionParser(replacerExec.getSectionParser());
	    return def;
    }
	
	private BlockDef blockMain() {
		BlockDef def = new BlockDef("main");
		return def;
	}
	
	private BlockDef blockBlock() {
        BlockDef def = new BlockDef("block");
        return def;
    }
	
	private BlockDef blockIf() {
		BlockDef def = new BlockDef("if");
		def.setModifierCount(Range.single(1));
		def.setEntryCondition((def1, mods, scope, stepper) -> {
			Optional<Boolean> op = ShadowTools.asBoolean(mods.get(0), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Modifiers could not be parsed as boolean.");
			return op.get();
		});
		return def;
	}
	
	private BlockDef blockElseIf() {
        BlockDef def = new BlockDef("elseif");
        def.setModifierCount(Range.single(1));
        def.setEntryCondition((def1, mods, scope, stepper) -> {
            if (!(stepper.followsBlock("elseif") || stepper.followsBlock("if"))) {
                throw new ShadowExecutionException("Elseif must be preceded by \"if\" or \"elseif\" block.");
            }
            if (stepper.lastElementRan()) {
                stepper.next(StepAction.BREAK_BLOCK_CHAIN);
            }
            else {
                Optional<Boolean> op = ShadowTools.asBoolean(mods.get(0), scope);
                if (!op.isPresent()) throw new ShadowExecutionException("Modifiers could not be parsed as boolean.");
                return op.get();
            }
            return false;
        });
        return def;
    }
	
	private BlockDef blockElse() {
		BlockDef def = new BlockDef("else");
		def.setEntryCondition((def1, mods, scope, stepper) -> {
            if (!(stepper.followsBlock("elseif") || stepper.followsBlock("if"))) {
                throw new ShadowExecutionException("Else must be preceded by \"if\" or \"elseif\" block.");
            }
            if (stepper.lastElementRan()) {
                stepper.next(StepAction.BREAK_BLOCK_CHAIN);
            }
            else {
                return !stepper.lastElementRan();
            }
			return false;
		});
		return def;
	}
	
	private BlockDef blockCount() {
		BlockDef def = new BlockDef("count");
		def.setModifierCount(Range.from(2, 3));
		def.setParameterCount(Range.single(1));
		def.setSectionParser(SectionParser.allPrimitive());
		def.setEntryCondition((def1, mods, scope, stepper) -> {
			Optional<Integer> start = mods.get(0).asPrimitive().asInt();
			Optional<Integer> end = mods.get(1).asPrimitive().asInt();
			if (start.isPresent() && end.isPresent()) {
				if (mods.size() < 3) return true;
				if (mods.get(2).asPrimitive().asInt().isPresent()) return true;
			}
			throw new ShadowExecutionException("All parameters must be numbers.");
		});
		def.setEnterEvent((def1, mods, parameters, scope, stepper) -> {
			scope.setParamVar(parameters.get(0), mods.get(0).asPrimitive().asInt().orElse(0));
		});
		def.setEndEvent((def1, mods, parameters, scope, stepper) -> {
			Optional<NamedReference<Object>> var = scope.getVar(parameters.get(0));
			if (!var.isPresent()) stepper.next(StepAction.BREAK);
			else {
				int start = mods.get(0).asPrimitive().asInt().orElse(0);
				int end = mods.get(1).asPrimitive().asInt().orElse(1);
				int step = ShadowTools.get(mods).filter(sections -> sections.size() > 2).flatMap(sections -> sections.get(2).asPrimitive().asInt()).orElse(1);
				NamedReference<Object> ref = var.get();
				int n = (int) ref.get();
				if (start < end) {
					ref.set(n + step);
					if (n + step > end) return;
				}
				if (end < start) {
					ref.set(n - step);
					if (n - step < end) return;
				}
				stepper.next(StepAction.RESTART);
			}
		});
		return def;
	}
	
	private BlockDef blockWhile() {
		BlockDef def = new BlockDef("while");
		def.setModifierCount(Range.single(1));
		def.setEntryCondition((def1, mods, scope, stepper) -> {
			Optional<Boolean> op = ShadowTools.asBoolean(mods.get(0), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Modifier is not boolean.");
			return op.get();
		});
		def.setEndEvent((def1, mods, parameters, scope, stepper) -> {
			Optional<Boolean> op = ShadowTools.asBoolean(mods.get(0), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Modifier is not boolean.");
			if (op.get()) {
				stepper.next(StepAction.RESTART);
			}
		});
		return def;
	}
	
	private BlockDef blockForEach() {
		BlockDef def = new BlockDef("foreach");
		def.setModifierCount(Range.single(1));
		def.setParameterCount(Range.from(1, 2));
		def.setEntryCondition((def1, mods, scope, stepper) -> {
			Optional<Object> op = ShadowTools.asObject(mods.get(0), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Modifier could not be converted to object.");
			return ShadowIterator.isIterable(op.get());
		});
		def.setEnterEvent((def1, mods, parameters, scope, stepper) -> {
			Optional<Object> op = ShadowTools.asObject(mods.get(0), scope);
			if (!op.isPresent()) throw new ShadowExecutionException("Modifier could not be converted to object.");
			Optional<Iterator> oi = ShadowIterator.getIterator(scope, op.get());
			if (!oi.isPresent()) throw new ShadowExecutionException("Modifier could not be converted to iterator.");
			if (!oi.get().hasNext()) {
				stepper.next(StepAction.BREAK);
				return;
			}
			scope.setVar(parameters.get(0), oi.get().next());
            if (parameters.size() > 1) {
                scope.setVar(parameters.get(1), 0);
            }
		});
		def.setEndEvent((def1, mods, parameters, scope, stepper) -> {
			if (!ShadowIterator.hasIterator(scope)) return;
			Optional<Iterator> oi = ShadowIterator.getIterator(scope, null);
			if (oi.isPresent() && oi.get().hasNext()) {
                scope.setVar(parameters.get(0), oi.get().next());
                if (parameters.size() > 1) {
                    Integer next = scope.getVar(parameters.get(1)).filter(ref -> ref.get() instanceof Integer).map(ref -> (Integer) ref.get() + 1).orElse(0);
                    scope.setVar(parameters.get(1), next);
                }
				stepper.next(StepAction.RESTART);
			}
		});
		return def;
	}
	
	private BlockDef blockDefine() {
        BlockDef def = new BlockDef("define");
        def.setModifierCount(Range.single(1));
        def.setSectionParser((sections, context) -> {
            ShadowTools.verifyArgs(sections, 1, context);
            if (!sections[0].matches("^[\\w\\d]+")) throw new ShadowParseException("Invalid function name.", context);
            return Collections.singletonList(Primitive.string(sections[0]));
        });
        def.setReturnable(true);
        return def;
    }
	
	private ReplacerDef replacerToString() {
		ReplacerDef def = new ReplacerDef("", (sections, scope) -> {
			ShadowTools.verifySections(sections, Range.lowerBound(1));
			String var = sections.get(0).toString();
			Optional<NamedReference<Object>> op = scope.getVar(var);
			if (!op.isPresent()) throw new ShadowExecutionException("Var " + var + " not found in scope.");
			return Primitive.string(ShadowTools.get(op.get().get()).map(Object::toString).orElse("null"));
		});
		def.setSplitter(Splitter.singleArg());
		def.setSectionParser(SectionParser.allString());
		return def;
	}
	
	private ReplacerDef replacerEval() {
		ReplacerDef def = new ReplacerDef("%", (sections, scope) -> {
			ShadowTools.verifySections(sections, Range.lowerBound(1));
			return ShadowTools.executeEval(sections.get(0).asEvalGroup(), scope);
		});
		def.setSplitter(Splitter.evalSplit());
		def.setSectionParser(SectionParser.evalParser());
		return def;
	}
	
	private ReplacerDef replacerUpper() {
	    ReplacerDef def = new ReplacerDef("upper", (shadowSections, scope) -> {
            String s = ShadowTools.asObject(shadowSections.get(0), scope).map(Object::toString).orElse(ShadowTools.asString(shadowSections));
            return new Primitive(s.toUpperCase());
        });
	    def.setSectionParser(SectionParser.standard());
	    return def;
    }
    
    private ReplacerDef replacerLower() {
        ReplacerDef def = new ReplacerDef("lower", (shadowSections, scope) -> {
            String s = ShadowTools.asObject(shadowSections.get(0), scope).map(Object::toString).orElse(ShadowTools.asString(shadowSections));
            return new Primitive(s.toLowerCase());
        });
        def.setSectionParser(SectionParser.standard());
        return def;
    }
    
    private ReplacerDef replacerArr() {
	    ReplacerDef def = new ReplacerDef("arr", (shadowSections, scope) -> {
            Object value = ShadowTools.getArray(shadowSections, 0, scope);
            Object index = ShadowTools.asObject(shadowSections.get(1), scope).orElseThrow(ShadowException.sectionConvert(scope.getContext()));
            if (!(index instanceof Integer)) throw new ShadowExecutionException("Second parameter must be integer index.", scope.getContext());
            return new Reference(Array.get(value, (Integer) index));
        });
	    def.setSplitter((content, context) -> content.split(","));
	    def.setSectionParser((sections, context) -> {
	        if (sections.length != 2) throw new ShadowParseException("Invalid arguments. Required 2, found " + sections.length, context);
            List<ShadowSection> s = new ArrayList<>(2);
            s.add(new ScopeVar(sections[0]));
            s.addAll(SectionParser.standard().getSections(Arrays.copyOfRange(sections, 1, 2), context));
            return s;
        });
	    return def;
    }
    
    private ReplacerDef replacerLen() {
        ReplacerDef def = new ReplacerDef("len", (shadowSections, scope) -> {
            Object value = ShadowTools.getArray(shadowSections, 0, scope);
            return Primitive.integer(Array.getLength(value));
        });
        def.setSectionParser((sections, context) -> {
            ShadowTools.verifyArgs(sections, 1, context);
            return Collections.singletonList(new ScopeVar(sections[0]));
        });
        return def;
    }
    
    private ReplacerDef replacerExec() {
	    replacerExec = new ReplacerDef("exec", (sections, scope) -> {
            Optional<Block> b = scope.getShadow()
                                     .streamBlocks()
                                     .filter(block -> block.getName().equals("define"))
                                     .filter(block -> block.getModifiers().get(0).asPrimitive().asString().equals(sections.get(0).asPrimitive().asString()))
                                     .filter(block -> block.getParameters().size() == sections.size() - 1)
                                     .findFirst();
            if (!b.isPresent()) throw new ShadowExecutionException("Could not find matching '" + sections.get(0).asPrimitive().asString() + "' function.", scope.getContext());
            Object[] params = sections.stream().skip(1).map(sec -> ShadowTools.asObject(sec, scope).orElseThrow(ShadowException.sectionConvert(scope.getContext()))).toArray();
            Block block = b.get();
            scope.getShadow().run(block, null, params);
            Object returned = block.getReturned();
            return new Reference(returned);
        });
	    replacerExec.setSplitter((content, context) -> content.split("(?<!\\\\),| "));
	    replacerExec.setSectionParser((sections, context) -> {
	        if (sections.length < 1) throw new ShadowParseException("Enter a function as a parameter.", context);
            List<ShadowSection> s = new ArrayList<>();
            s.add(Primitive.string(sections[0]));
            if (sections.length > 1) {
                s.addAll(SectionParser.standard().getSections(Arrays.copyOfRange(sections, 1, sections.length), context));
            }
            return s;
        });
	    return replacerExec;
    }
    
    private ReplacerDef replacerInput() {
	    ReplacerDef def = new ReplacerDef("input", (shadowSections, scope) -> {
            String query = ShadowTools.get(shadowSections)
                                       .filter(ss -> ss.size() > 0)
                                       .flatMap(ss -> ShadowTools.asObject(ss.get(0), scope))
                                       .map(Object::toString).orElse("");
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                System.out.print(query);
                return Primitive.string(reader.readLine());
            } catch (IOException e) {
                return new Empty();
            }
        });
	    def.setSectionParser(SectionParser.standard());
	    return def;
    }
	
	private EvalSymbolDef evalCast() {
		EvalSymbolDef def = new EvalSymbolDef(">", (reference, section, scope) -> {
			Optional<Class<?>> op = scope.getContext().findClass(section.getName());
			if (!op.isPresent()) throw new ShadowExecutionException("Could not find class: " + section.getName(), scope.getContext());
			reference.setType(op.get());
			return reference;
		});
		return def;
	}
	
	private EvalSymbolDef evalMethod() {
        EvalSymbolDef def = new EvalSymbolDef(":", (reference, section, scope) -> {
            Reflect.of(reference, scope).method(section.getName(), section.getParams());
            return reference;
        });
        return def;
	}
	
	private EvalSymbolDef evalString() {
		EvalSymbolDef def = new EvalSymbolDef("#", (reference, section, scope) -> {
			reference.set(section.getName());
			return reference;
		});
		return def;
	}
	
	private EvalSymbolDef evalVar() {
		EvalSymbolDef def = new EvalSymbolDef("", (reference, section, scope) -> {
			Optional<NamedReference<Object>> var = scope.getVar(section.getName());
			if (!var.isPresent()) throw new ShadowExecutionException("No variable defined named " + section.getName());
			reference.set(var.get().get());
			return reference;
		});
		return def;
	}
	
	private EvalSymbolDef evalConstruct() {
        EvalSymbolDef def = new EvalSymbolDef("=", (reference, section, scope) -> {
            Reflect.of(reference, scope).construct(section.getName(), section.getParams());
            return reference;
        });
        return def;
	}
	
	private EvalSymbolDef evalField() {
		EvalSymbolDef def = new EvalSymbolDef("~", (reference, section, scope) -> {
			Reflect.of(reference, scope).field(section.getName());
			return reference;
		});
		return def;
	}
	
	private EvalSymbolDef evalParse() {
		EvalSymbolDef def = new EvalSymbolDef("->", (reference, section, scope) -> {
			String s = section.getName();
			try {
				if (s.contains(".")) reference.set(Double.parseDouble(s));
				else reference.set(Integer.parseInt(s));
				return reference;
			} catch (NumberFormatException ignored) {}
			if (s.equalsIgnoreCase("true")) reference.set(true);
			else if (s.equalsIgnoreCase("false")) reference.set(false);
			else if (s.equalsIgnoreCase("null")) reference.set(null);
			reference.set(s);
			return reference;
		});
		return def;
	}
	
}
