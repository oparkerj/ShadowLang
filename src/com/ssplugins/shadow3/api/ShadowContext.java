package com.ssplugins.shadow3.api;

import com.ssplugins.shadow3.def.BlockType;
import com.ssplugins.shadow3.def.KeywordType;
import com.ssplugins.shadow3.def.OperatorAction;

import java.util.*;
import java.util.function.Predicate;

public class ShadowContext {
    
    private Map<String, OperatorMap> operators = new HashMap<>();
    private Map<String, BlockType> blocks = new HashMap<>();
    private Map<String, KeywordType> keywords = new HashMap<>();
    private Map<String, ShadowContext> modules = new HashMap<>();
    
    private <T> Optional<T> search(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).findFirst();
    }
    
    public void clean() {
        operators.forEach((s, map) -> map.clean());
        operators.clear();
    }
    
    //region Operators
    
    private OperatorMap getOpMap(OperatorAction action) {
        return operators.computeIfAbsent(action.getToken(), s -> new OperatorMap(action.getOrder()));
    }
    
    public boolean addOperator(OperatorAction operator) {
        return getOpMap(operator).insert(operator);
    }
    
    public boolean containsOperator(String token) {
        return operators.containsKey(token);
    }
    
    public Optional<OperatorAction> findOperator(String token, Class<?> left, Class<?> right) {
        OperatorMap map = operators.get(token);
        if (map == null) return Optional.empty();
        return map.find(left, right);
    }
    
    public Set<String> operators() {
        return operators.keySet();
    }
    
    //endregion
    //region Blocks
    
    public boolean addBlock(BlockType block) {
        if (blocks.containsKey(block.getName())) return false;
        blocks.put(block.getName(), block);
        return true;
    }
    
    public Optional<BlockType> findBlock(String name) {
        return Optional.ofNullable(blocks.get(name));
    }
    
    //endregion
    //region Keywords
    
    public boolean addKeyword(KeywordType keyword) {
        if (keywords.containsKey(keyword.getName())) return false;
        keywords.put(keyword.getName(), keyword);
        return true;
    }
    
    public Optional<KeywordType> findKeyword(String name) {
        return Optional.ofNullable(keywords.get(name));
    }
    
    //endregion
    //region Modules
    
    public boolean addModule(String name, ShadowContext context) {
        if (!modules.containsKey(name)) return false;
        modules.put(name, context);
        return true;
    }
    
    public Optional<ShadowContext> findModule(String name) {
        return Optional.ofNullable(modules.get(name));
    }
    
    //endregion
    
}
